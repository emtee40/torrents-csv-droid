package com.torrents_csv_android.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.torrents_csv_android.Torrent
import com.torrents_csv_android.formatSize
import com.torrents_csv_android.magnetLink
import com.torrents_csv_android.sampleTorrent1
import com.torrents_csv_android.sampleTorrentList
import com.torrents_csv_android.ui.theme.DEFAULT_PADDING
import com.torrents_csv_android.ui.theme.MainTheme
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        text = text,
        Modifier.weight(weight),
        style = MaterialTheme.typography.bodySmall,
        color = color,
        textAlign = textAlign
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
                        context,
                        "No torrent app installed",
                        Toast
                            .LENGTH_SHORT
                    ).show()
                }
            },
            onLongClick = {
                localClipboardManager.setText(AnnotatedString(magnet))
                Toast.makeText(
                    context,
                    "Magnet link copied to clipboard",
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
                style = MaterialTheme.typography.titleSmall
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
                val seeders = torrent.seeders
                TableCell(text = "Seeds", weight = column1Weight)
                TableCell(
                    text = seeders.toString(),
                    weight = column2Weight,
                    textAlign = TextAlign.End,
                    color = seederColor(seeders)
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
                    textAlign = TextAlign.End
                )
            }
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = "Created", weight = column1Weight)
                TableCell(
                    text = created,
                    weight = column2Weight,
                    textAlign = TextAlign.End
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
fun seederColor(seeders: Int): Color {
    return if (seeders in 1..5) {
        Color.Unspecified
    } else if (seeders > 5) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    text: String,
    onSearchChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val isValid = text.count() >= 3

    val focusRequester = remember { FocusRequester() }
    var focus by remember { mutableStateOf(false) }
    val kbController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(300)
        kbController?.show()
        focusRequester.requestFocus()
    }

    TextField(
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        ),
        value = text,
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .onFocusChanged {
                if (focus != it.isFocused) {
                    focus = it.isFocused
                } else {
                    kbController?.hide()
                }
            }
            .onKeyEvent {
                if (it.nativeKeyEvent.keyCode == NativeKeyEvent.KEYCODE_ENTER) {
                    onSubmit()
                }
                false
            },
        onValueChange = onSearchChange,
        placeholder = {
            Text("Search")
        },
        trailingIcon = {
            Icon(Icons.Filled.Search, "Search")
        },
        maxLines = 1,
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { onSubmit(); kbController?.hide() }),
        isError = !isValid
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
