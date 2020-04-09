package com.example.rssfeedreader.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rssfeedreader.database.RssDatabase
import org.junit.Test

import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RssViewModelTest {

//    @Before
//    fun setUp() {
//    }
//
//    @After
//    fun tearDown() {
//    }

//    @Test
//    fun getRssDataList() {
//    }
//
//    @Test
//    fun getStatus() {
//    }

    @Test
    fun testGetRssFeeds() {

        val rssSitesDao = RssDatabase.getInstance(ApplicationProvider.getApplicationContext()).rssDatabaseDao
        val rssViewModel = RssViewModel(rssSitesDao, ApplicationProvider.getApplicationContext())
    }

//    @Test
//    fun getDatabase() {
//    }
}