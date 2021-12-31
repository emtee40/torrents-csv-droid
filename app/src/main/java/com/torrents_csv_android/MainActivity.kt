package com.torrents_csv_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.torrents_csv_android.ui.theme.MainTheme
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {

  private val vm: TorrentViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    setContent {
      MainTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          AppView(vm)
        }
      }
    }
  }
}

@Composable
fun AppView(vm: TorrentViewModel) {
  Scaffold {
    MainView(vm)
  }
}

@Composable
fun MainView(vm: TorrentViewModel) {
  var searchText by rememberSaveable { mutableStateOf("") }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  Column {
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
        })
    }
    Row {
      if (vm.errorMessage.isEmpty()) {
        TorrentListView(vm.torrentList, listState)
      } else {
        Text(vm.errorMessage)
      }
    }
  }
}


val defaultPadding = 12.dp

@Composable
fun RowScope.TableCell(
  text: String,
  weight: Float
) {
  Text(
    text = text,
    Modifier
      .weight(weight)
  )
}

@Composable
fun TorrentListView(torrents: List<Torrent>, listState: LazyListState) {
  Column(modifier = Modifier.padding(defaultPadding)) {
    LazyColumn(state = listState, modifier = Modifier.fillMaxHeight()) {
      items(torrents) { torrent ->
        TorrentView(torrent)
        Divider(Modifier.padding(vertical = defaultPadding))
      }
    }
  }
}

@Composable
fun TorrentView(torrent: Torrent) {
  val context = LocalContext.current
  val magnet = magnetLink(torrent.infohash, torrent.name)
  val intent =
    remember { Intent(Intent.ACTION_VIEW, Uri.parse(magnet)) }

  val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
  val created = sdf.format(Date(torrent.created_unix.toLong() * 1000))
//  val scraped = sdf.format(Date(torrent.scraped_date.toLong() * 1000))

  Column(
    Modifier.clickable {
      context.startActivity(intent)
    }
  ) {
    Row(
      modifier = Modifier.padding(0.dp, 0.dp, 0.dp, defaultPadding)
    ) {
      Text(
        torrent.name,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis
      )
    }

    // The table
    val column1Weight = .5f
    val column2Weight = .5f
    Column(
      Modifier
        .fillMaxSize()
        .padding(horizontal = defaultPadding)
    ) {
      Row(Modifier.fillMaxWidth()) {
        TableCell(text = "Seeds", weight = column1Weight)
        TableCell(text = torrent.seeders.toString(), weight = column2Weight)
      }
//      Row(Modifier.fillMaxWidth()) {
//        TableCell(text = "Leeches", weight = column1Weight)
//        TableCell(text = torrent.leechers.toString(), weight = column2Weight)
//      }
      Row(Modifier.fillMaxWidth()) {
        TableCell(text = "Size", weight = column1Weight)
        TableCell(text = formatSize(torrent.size_bytes), weight = column2Weight)
      }
      Row(Modifier.fillMaxWidth()) {
        TableCell(text = "Created", weight = column1Weight)
        TableCell(text = created, weight = column2Weight)
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
  onSubmit: (KeyboardActionScope) -> Unit
) {
  val focusRequester = remember { FocusRequester() }
  val isValid = text.count() >= 3

  OutlinedTextField(
    value = text,
    modifier = Modifier
      .fillMaxWidth()
      .focusRequester(focusRequester),
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
//    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(onDone = onSubmit),
    isError = !isValid,
  )

  DisposableEffect(Unit) {
    focusRequester.requestFocus()
    onDispose { }
  }
}


fun formatSize(v: Long): String {
  if (v < 1024) return "$v B"
  val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
  return String.format("%.1f %sB", v.toDouble() / (1L shl z * 10), " KMGTPE"[z])
}

val sampleTorrent1 = Torrent(
  completed = 6025,
  created_unix = 1639448700,
  infohash = "deb438c0879a9b94b5132309be4f73531867dddc",
  leechers = 52,
  name = "The.French.Dispatch.2021.1080p.AMZN.WEBRip.1400MB.DD5.1.x264-GalaxyRG[TGx]",
  scraped_date = 1639768311,
  seeders = 352,
  size_bytes = 1506821189
)

val sampleTorrent2 = Torrent(
  completed = 6025,
  created_unix = 1639448700,
  infohash = "deb438c0879a9b94b5132309be4f73531867dddc",
  leechers = 3,
  name = "A not real torrent",
  scraped_date = 1619768311,
  seeders = 26,
  size_bytes = 13068189
)

val sampleTorrentList: List<Torrent> = listOf(
  sampleTorrent1,
  sampleTorrent2
)

fun magnetLink(
  hash: String,
  name: String,
): String {
  return "magnet:?xt=urn:btih:${hash}&dn=${name}${trackerListToUrl(trackerList)}"
}

fun trackerListToUrl(trackerList: List<String>): String {
  return trackerList.joinToString(separator = "") { "&tr=$it" }
}

val trackerList = listOf(
  "udp://tracker.coppersurfer.tk:6969/announce",
  "udp://tracker.open-internet.nl:6969/announce",
  "udp://tracker.leechers-paradise.org:6969/announce",
  "udp://tracker.internetwarriors.net:1337/announce",
  "udp://tracker.opentrackr.org:1337/announce",
  "udp://9.rarbg.to:2710/announce",
  "udp://9.rarbg.me:2710/announce",
  "http://tracker3.itzmx.com:6961/announce",
  "http://tracker1.itzmx.com:8080/announce",
  "udp://exodus.desync.com:6969/announce",
  "udp://explodie.org:6969/announce",
  "udp://ipv4.tracker.harry.lu:80/announce",
  "udp://denis.stalker.upeer.me:6969/announce",
  "udp://tracker.torrent.eu.org:451/announce",
  "udp://tracker.tiny-vps.com:6969/announce",
  "udp://thetracker.org:80/announce",
  "udp://open.demonii.si:1337/announce",
  "udp://tracker4.itzmx.com:2710/announce",
  "udp://tracker.cyberia.is:6969/announce",
  "udp://retracker.netbynet.ru:2710/announce",
)


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

//@Preview(showBackground = true)
//@Composable
//fun FullViewPreview() {
//  MainTheme {
//    AppView()
//  }
//}
