package com.torrents_csv_android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TorrentViewModel : ViewModel() {
  private var _torrentList = mutableStateListOf<Torrent>()
  var errorMessage: String by mutableStateOf("")
  val torrentList: List<Torrent>
    get() = _torrentList

  fun fetchTorrentList(search: String) {
    viewModelScope.launch {
      val apiService = APIService.getInstance()
      val call = apiService.getTorrents(search = search)
      try {
        _torrentList.clear()
        _torrentList.addAll(call.body()!!)
      } catch (e: Exception) {
        errorMessage = e.message.toString()
      }
    }
  }
}
