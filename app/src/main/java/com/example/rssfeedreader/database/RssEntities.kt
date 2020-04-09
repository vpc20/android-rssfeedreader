package com.example.rssfeedreader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_sites")
data class RssSites(
    @PrimaryKey
    @ColumnInfo(name = "rss_site_name")
    var rssSiteName: String,

    @ColumnInfo(name = "rss_url")
    val rssUrl: String

//    @ColumnInfo(name = "rss_path")
//    var rssPath: String
)
