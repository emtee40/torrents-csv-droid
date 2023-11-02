package com.torrents_csv_android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun SearchActivity(vm: SearchViewModel) {
    var searchText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        content = { padding ->
            Column(
                modifier =
                Modifier
                    .padding(padding)
                    .imePadding(),
            ) {
                Row {
                    SearchField(
                        text = searchText,
                        onSearchChange = {
                            searchText = it
                        },
                        onSubmit = {
                            if (searchText.count() >= 3) {
                                vm.fetchTorrentList(searchText)
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        },
                    )
                }
                if (vm.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Row {
                    if (vm.errorMessage.isEmpty()) {
                        TorrentListView(vm.torrents, listState)
                    } else {
                        Text(vm.errorMessage)
                    }
                }
            }
        },
    )
}
