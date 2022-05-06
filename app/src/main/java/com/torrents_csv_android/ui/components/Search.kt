package com.torrents_csv_android.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.torrents_csv_android.*
import com.torrents_csv_android.ui.theme.DEFAULT_PADDING
import com.torrents_csv_android.ui.theme.MainTheme
import java.util.*

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        Modifier.weight(weight),
        style = MaterialTheme.typography.body2,
        textAlign = textAlign,
    )
}

@Composable
fun TorrentListView(torrents: List<Torrent>, listState: LazyListState) {
    Column(modifier = Modifier.padding(DEFAULT_PADDING)) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxHeight()) {
            items(torrents) { torrent ->
                TorrentView(torrent)
                Divider(Modifier.padding(vertical = DEFAULT_PADDING))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TorrentView(torrent: Torrent) {
    val context = LocalContext.current
    val magnet = magnetLink(torrent.infohash, torrent.name)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(magnet))
    val localClipboardManager = LocalClipboardManager.current

    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val created = sdf.format(Date(torrent.created_unix.toLong() * 1000))
//  val scraped = sdf.format(Date(torrent.scraped_date.toLong() * 1000))

    Column(
        Modifier.combinedClickable(
            onClick = {
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context, "No torrent app installed",
                        Toast
                            .LENGTH_SHORT
                    ).show()
                }
            },
            onLongClick = {
                localClipboardManager.setText(AnnotatedString(magnet))
                Toast.makeText(
                    context, "Magnet link copied to clipboard",
                    Toast
                        .LENGTH_SHORT
                ).show()
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, DEFAULT_PADDING)
        ) {
            Text(
                torrent.name,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
            )
        }

        // The table
        val column1Weight = .5f
        val column2Weight = .5f
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = DEFAULT_PADDING)
        ) {
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = "Seeds", weight = column1Weight)
                TableCell(
                    text = torrent.seeders.toString(),
                    weight = column2Weight,
                    textAlign = TextAlign.End,
                )
            }
//      Row(Modifier.fillMaxWidth()) {
//        TableCell(text = "Leeches", weight = column1Weight)
//        TableCell(text = torrent.leechers.toString(), weight = column2Weight)
//      }
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = "Size", weight = column1Weight)
                TableCell(
                    text = formatSize(torrent.size_bytes),
                    weight = column2Weight,
                    textAlign = TextAlign.End,
                )
            }
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = "Created", weight = column1Weight)
                TableCell(
                    text = created,
                    weight = column2Weight,
                    textAlign = TextAlign.End,
                )
            }
//      Row(Modifier.fillMaxWidth()) {
//        TableCell(text = "Scraped", weight = column1Weight)
//        TableCell(text = scraped, weight = column2Weight)
//      }
        }
    }
}

@Composable
fun SearchField(
    text: String,
    onSearchChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val isValid = text.count() >= 3

    OutlinedTextField(
        value = text,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_ENTER) {
                    onSubmit()
                }
                false
            },
        onValueChange = onSearchChange,
        label = {
            Text("Torrents-csv")
        },
        placeholder = {
            Text("Search")
        },
        trailingIcon = {
            Icon(Icons.Filled.Search, "Search")
        },
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false,
        ),
        isError = !isValid,
    )
}

@Preview(showBackground = true)
@Composable
fun TorrentPreview() {
    MainTheme {
        TorrentView(sampleTorrent1)
    }
}

@Preview(showBackground = true)
@Composable
fun TorrentListPreview() {
    val listState = rememberLazyListState()

    MainTheme {
        TorrentListView(sampleTorrentList, listState)
    }
}
