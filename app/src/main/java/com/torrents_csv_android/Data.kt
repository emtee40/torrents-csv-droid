package com.torrents_csv_android

val trackerList =
    listOf(
        "udp://open.tracker.cl:1337/announce",
        "udp://tracker.opentrackr.org:1337/announce",
        "udp://9.rarbg.com:2810/announce",
        "udp://exodus.desync.com:6969/announce",
        "udp://www.torrent.eu.org:451/announce",
        "udp://tracker1.bt.moack.co.kr:80/announce",
        "udp://tracker.torrent.eu.org:451/announce",
        "udp://tracker.tiny-vps.com:6969/announce",
        "udp://tracker.pomf.se:80/announce",
        "udp://tracker.openbittorrent.com:6969/announce",
        "udp://tracker.altrosky.nl:6969/announce",
        "udp://tracker.0x.tf:6969/announce",
        "udp://retracker.lanta-net.ru:2710/announce",
        "udp://open.stealth.si:80/announce",
        "udp://movies.zsw.ca:6969/announce",
        "udp://fe.dealclub.de:6969/announce",
        "udp://discord.heihachi.pw:6969/announce",
        "udp://bt2.archive.org:6969/announce",
        "udp://bt1.archive.org:6969/announce",
        "udp://6ahddutb1ucc3cp.ru:6969/announce",
    )

val sampleTorrent1 =
    Torrent(
        completed = 6025,
        created_unix = 1639448700,
        infohash = "deb438c0879a9b94b5132309be4f73531867dddc",
        leechers = 52,
        name = "The.French.Dispatch.2021.1080p.AMZN.WEBRip.1400MB.DD5.1.x264-GalaxyRG[TGx]",
        scraped_date = 1639768311,
        seeders = 352,
        size_bytes = 1506821189,
    )

val sampleTorrent2 =
    Torrent(
        completed = 6025,
        created_unix = 1639448700,
        infohash = "deb438c0879a9b94b5132309be4f73531867dddc",
        leechers = 3,
        name = "A not real torrent",
        scraped_date = 1619768311,
        seeders = 26,
        size_bytes = 13068189,
    )

val sampleTorrentList: List<Torrent> =
    listOf(
        sampleTorrent1,
        sampleTorrent2,
    )
