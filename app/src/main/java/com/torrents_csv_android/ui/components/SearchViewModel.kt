package com.torrents_csv_android.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.torrents_csv_android.APIService
import com.torrents_csv_android.Torrent
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    var errorMessage: String by mutableStateOf("")
    var torrents: List<Torrent> by mutableStateOf(listOf())
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    fun fetchTorrentList(search: String) {
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                loading = true
                torrents = apiService.getTorrents(search = search)
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            } finally {
                loading = false
            }
        }
    }
}
