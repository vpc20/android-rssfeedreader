package com.example.rssfeedreader.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rssfeedreader.database.RssSitesDao

//import com.example.sleeptrackerrecylerview.database.SleepDatabaseDao

class RssFeedViewModelFactory(
    private val dataSource: RssSitesDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RssViewModel::class.java)) {
            return RssViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

