package com.partner.cinepulse.ui.screens.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.partner.cinepulse.data.local.dao.RecentSearchDao
import com.partner.cinepulse.data.local.dao.RecentViewDao
import com.partner.cinepulse.data.local.dao.SearchCacheDao
import com.partner.cinepulse.data.local.entity.RecentSearchEntity
import com.partner.cinepulse.data.local.entity.SearchCacheEntity
import com.partner.cinepulse.data.remote.models.searchItem
import com.partner.cinepulse.data.remote.models.searchResponse
import com.partner.cinepulse.data.repository.ContentRepository
import com.partner.cinepulse.utils.Constants
import com.partner.cinepulse.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SearchUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recentSearches: List<String> = emptyList(),
    val trendingSearches: List<searchItem> = emptyList(),
    val suggestions: List<searchItem> = emptyList(),
    val isTrendingLoading: Boolean = false,
    val isSuggestionsLoading: Boolean = false,
    val trendingErrorMessage: String? = null,
    val suggestionsErrorMessage: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val recentSearchDao: RecentSearchDao,
    private val recentViewDao: RecentViewDao,
    private val searchCacheDao: SearchCacheDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val gson = Gson()

    // SavedStateHandle flows for lightweight states
    val query = savedStateHandle.getStateFlow("query", "")
    val selectedTab = savedStateHandle.getStateFlow("selectedTab", "All")
    val selectedFilter = savedStateHandle.getStateFlow("selectedFilter", "Best Match")
    val currentPage = savedStateHandle.getStateFlow("currentPage", 1)
    val isLastPage = savedStateHandle.getStateFlow("isLastPage", false)
    val scrollIndex = savedStateHandle.getStateFlow("scrollIndex", 0)
    val scrollOffset = savedStateHandle.getStateFlow("scrollOffset", 0)

    private val _uistate = MutableStateFlow(SearchUiState())
    val uistate: StateFlow<SearchUiState> = _uistate.asStateFlow()

    // In-memory results flow for large lists
    private val _searchResult = MutableStateFlow<searchResponse?>(null)
    val searchResult: StateFlow<searchResponse?> = _searchResult.asStateFlow()

    init {
        // Load recent searches from DB
        observeRecentSearches()

        // Load offline cached trending, suggestions, and last search results immediately
        loadLocalCache()

        // Refresh lists from network in background
        refreshTrendingAndSuggestions()
    }

    private fun observeRecentSearches() {
        viewModelScope.launch {
            recentSearchDao.getRecentSearchesFlow(Constants.RECENT_SEARCHES_LIMIT).collect { entities ->
                _uistate.update {
                    it.copy(recentSearches = entities.map { entity -> entity.query })
                }
            }
        }
    }

    private fun loadLocalCache() {
        viewModelScope.launch {
            // 1. Trending
            val trendingCache = searchCacheDao.getCache("trending_cache")
            if (trendingCache != null) {
                try {
                    val listType = object : com.google.gson.reflect.TypeToken<List<searchItem>>() {}.type
                    val list: List<searchItem> = gson.fromJson(trendingCache.responseJson, listType)
                    _uistate.update { it.copy(trendingSearches = list) }
                } catch (e: Exception) {
                    // Cache corrupt/invalid format
                }
            }

            // 2. Suggestions
            val suggestionsCache = searchCacheDao.getCache("suggestions_cache")
            if (suggestionsCache != null) {
                try {
                    val listType = object : com.google.gson.reflect.TypeToken<List<searchItem>>() {}.type
                    val list: List<searchItem> = gson.fromJson(suggestionsCache.responseJson, listType)
                    _uistate.update { it.copy(suggestions = list) }
                } catch (e: Exception) {
                    // Cache corrupt/invalid format
                }
            }

            // 3. Search Results
            val resultsCache = searchCacheDao.getCache("last_search_results")
            if (resultsCache != null && query.value.isNotBlank()) {
                try {
                    val response = gson.fromJson(resultsCache.responseJson, searchResponse::class.java)
                    if (response.query.lowercase() == query.value.lowercase()) {
                        _searchResult.value = response
                    }
                } catch (e: Exception) {
                    // Cache corrupt/invalid format
                }
            }
        }
    }

    fun setQuery(q: String) {
        savedStateHandle["query"] = q
        if (q.isBlank()) {
            _searchResult.value = null
        }
    }

    fun setTab(tab: String) {
        savedStateHandle["selectedTab"] = tab
    }

    fun setFilter(filter: String) {
        savedStateHandle["selectedFilter"] = filter
    }

    fun saveScrollPosition(index: Int, offset: Int) {
        savedStateHandle["scrollIndex"] = index
        savedStateHandle["scrollOffset"] = offset
    }

    fun searchContent(q: String) {
        if (q.isBlank()) return
        viewModelScope.launch {
            // Trim and insert to Recent Searches locally (REPLACE reorders to top)
            recentSearchDao.addSearchWithLimit(q, Constants.RECENT_SEARCHES_LIMIT)

            contentRepository.searchContent(q).collect { result ->
                when (result) {
                    is Resource.Loading<*> -> {
                        _uistate.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        val data = result.data
                        _searchResult.value = data
                        _uistate.update { it.copy(isLoading = false) }

                        // Cache search result locally in Room
                        if (data != null) {
                            searchCacheDao.saveCache(
                                SearchCacheEntity(
                                    key = "last_search_results",
                                    responseJson = gson.toJson(data),
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    is Resource.Error<*> -> {
                        _uistate.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }

    fun refreshTrendingAndSuggestions() {
        viewModelScope.launch {
            val userRegion = Locale.getDefault().country
            
            // Check if network fetch is needed (cache invalidation)
            val nowMs = System.currentTimeMillis()
            val trendingCache = searchCacheDao.getCache("trending_cache")
            val suggestionsCache = searchCacheDao.getCache("suggestions_cache")

            val shouldFetchTrending = trendingCache == null || (nowMs - trendingCache.timestamp > Constants.CACHE_EXPIRY_MS)
            val shouldFetchSuggestions = suggestionsCache == null || (nowMs - suggestionsCache.timestamp > Constants.CACHE_EXPIRY_MS)

            if (shouldFetchTrending) {
                fetchTrendingFromNetwork(userRegion)
            }
            if (shouldFetchSuggestions) {
                fetchSuggestionsFromNetwork()
            }
        }
    }

    private suspend fun fetchTrendingFromNetwork(region: String) {
        _uistate.update { it.copy(isTrendingLoading = true, trendingErrorMessage = null) }
        contentRepository.getTrendingSearches(region).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val list = result.data ?: emptyList()
                    _uistate.update {
                        it.copy(trendingSearches = list, isTrendingLoading = false)
                    }
                    // Save to Room Cache
                    searchCacheDao.saveCache(
                        SearchCacheEntity(
                            key = "trending_cache",
                            responseJson = gson.toJson(list),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                is Resource.Error -> {
                    // Non-blocking: Keep displaying cached list but update UI message
                    _uistate.update {
                        it.copy(isTrendingLoading = false, trendingErrorMessage = result.message)
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun fetchSuggestionsFromNetwork() {
        _uistate.update { it.copy(isSuggestionsLoading = true, suggestionsErrorMessage = null) }

        // Fetch recent views from DB (ID list)
        val views = recentViewDao.getRecentViews(Constants.RECENT_VIEWS_LIMIT)
        val formattedViews = views.map { "${it.contentId}:${it.contentType}" }

        // Fetch recent searches
        val searchEntities = recentSearchDao.getRecentSearchesRaw()
        val queries = searchEntities.map { it.query }

        contentRepository.getSearchSuggestions(queries, formattedViews).collect { result ->
            when (result) {
                is Resource.Success -> {
                    val list = result.data ?: emptyList()
                    _uistate.update {
                        it.copy(suggestions = list, isSuggestionsLoading = false)
                    }
                    // Save to Room Cache
                    searchCacheDao.saveCache(
                        SearchCacheEntity(
                            key = "suggestions_cache",
                            responseJson = gson.toJson(list),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                is Resource.Error -> {
                    // Non-blocking: Keep displaying cached list but update UI message
                    _uistate.update {
                        it.copy(isSuggestionsLoading = false, suggestionsErrorMessage = result.message)
                    }
                }
                else -> {}
            }
        }
    }

    fun removeRecentSearch(query: String) {
        viewModelScope.launch {
            recentSearchDao.deleteByQuery(query)
        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch {
            recentSearchDao.clearAll()
        }
    }
}
