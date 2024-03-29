package com.torrents_csv_android.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.torrents_csv_android.R
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
fun TorrentListView(
    torrents: List<Torrent>,
    listState: LazyListState,
) {
    Column {
        Spacer(Modifier.size(DEFAULT_PADDING))
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
    val ctx = LocalContext.current
    val magnet = magnetLink(torrent.infohash, torrent.name)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(magnet))
    val localClipboardManager = LocalClipboardManager.current

    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val created = sdf.format(Date(torrent.created_unix.toLong() * 1000))
//  val scraped = sdf.format(Date(torrent.scraped_date.toLong() * 1000))

    val noTorrentAppInstalledStr = stringResource(R.string.no_torrent_app_installed)
    val magnetLinkCopiedStr = stringResource(R.string.magnet_link_copied)
    Column(
        Modifier.combinedClickable(
            onClick = {
                try {
                    ctx.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        ctx,
                        noTorrentAppInstalledStr,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
            onLongClick = {
                localClipboardManager.setText(AnnotatedString(magnet))
                Toast.makeText(
                    ctx,
                    magnetLinkCopiedStr,
                    Toast.LENGTH_SHORT,
                ).show()
            },
        ),
    ) {
        Row(
            modifier =
                Modifier.padding(
                    DEFAULT_PADDING,
                    0.dp,
                    DEFAULT_PADDING,
                    DEFAULT_PADDING,
                ),
        ) {
            Text(
                torrent.name,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DEFAULT_PADDING.times(1.5f)),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconAndText(
                text = torrent.seeders.toString(),
                icon = Icons.Outlined.FileUpload,
                iconText = stringResource(R.string.seeds),
                color = seederColor(torrent.seeders),
            )
            IconAndText(
                text = formatSize(torrent.size_bytes),
                icon = Icons.Outlined.DataUsage,
                iconText = stringResource(R.string.size),
            )
            IconAndText(
                text = created,
                icon = Icons.Outlined.CalendarToday,
                iconText = stringResource(R.string.created),
            )
        }
    }
}

@Composable
private fun IconAndText(
    text: String,
    icon: ImageVector,
    iconText: String,
    color: Color = Color.Unspecified,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            iconText,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
        )
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchField(
    text: String,
    onSearchChange: (String) -> Unit,
    onSubmit: () -> Unit,
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
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
            ),
        value = text,
        modifier =
            Modifier
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
            Text(stringResource(R.string.search))
        },
        trailingIcon = {
            Icon(Icons.Filled.Search, stringResource(R.string.search))
        },
        singleLine = true,
        keyboardActions =
            KeyboardActions(onDone = {
                onSubmit()
                kbController?.hide()
            }),
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
