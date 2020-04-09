package com.example.rssfeedreader.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rssfeedreader.database.RssSites
import com.example.rssfeedreader.database.RssSitesDao

class RssSitesViewModel (
    val database: RssSitesDao, application: Application
) : AndroidViewModel(application) {

    private var _rssSitesList = MutableLiveData<List<RssSites>>()
    val rssSitesList: LiveData<List<RssSites>>
        get() = _rssSitesList

    fun refreshRssSitesList(database: RssSitesDao) {
        _rssSitesList.value = database.getAll()
    }

    fun addRssSite(rssSite: RssSites) {
        database.insert(rssSite)
    }

//    fun updateRssSite(rssSite: RssSites) {
//        database.delete(rssSite)
//        database.insert(rssSite)
//    }

    fun deleteRssSite(rssSite: RssSites) {
        database.delete(rssSite)
    }

}
