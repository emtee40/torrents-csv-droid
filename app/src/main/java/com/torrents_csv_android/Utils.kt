package com.torrents_csv_android

fun formatSize(v: Long): String {
    if (v < 1024) return "$v B"
    val z = (63 - java.lang.Long.numberOfLeadingZeros(v)) / 10
    return String.format("%.1f %sB", v.toDouble() / (1L shl z * 10), " KMGTPE"[z])
}

fun magnetLink(
    hash: String,
    name: String
): String {
    return "magnet:?xt=urn:btih:$hash&dn=${name}${trackerListToUrl(trackerList)}"
}

fun trackerListToUrl(trackerList: List<String>): String {
    return trackerList.joinToString(separator = "") { "&tr=$it" }
}
