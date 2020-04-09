package com.example.rssfeedreader.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rssfeedreader.database.RssSites
import com.example.rssfeedreader.database.RssSitesDao
import com.example.rssfeedreader.utils.RssXmlParser
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.StringReader
import java.util.concurrent.ConcurrentLinkedQueue

enum class RssApiStatus { LOADING, ERROR, DONE }

class RssViewModel(
    val database: RssSitesDao, application: Application
) : AndroidViewModel(application) {

    data class RssData(
        val title: String?,
        val link: String?,
        val pubDate: String?,
        var ogImg: String?,
        var siteName: String?
    )

    private var _rssDataList = MutableLiveData<List<RssData>>()
    val rssDataList: LiveData<List<RssData>>
        get() = _rssDataList

    private val _status = MutableLiveData<RssApiStatus>()
    val status: LiveData<RssApiStatus>
        get() = _status

    //    private lateinit var retrofitService: RssApiService
    private var listRssData = listOf<RssData>()
    private var linkedRssData = ConcurrentLinkedQueue<RssData>()
//    private var rssPath: String? = ""

    //    https://digg.com/rss/top.rss
    //    http://www.gearjunkies.com/feed
    //    https://feeds.feedburner.com/SonicstatecomNews
    //    https://feeds.feedburner.com/TechCrunch
    //    https://premierguitar.com/rss/articles
    //    https://feeds2.feedburner.com/androidcentral
    //    https://www.gearnews.com/feed
    init {
// test ---------------------------
//        database.clear()
//        database.insert(
//            RssSites(
//                "Android Central",
//                "https://feeds2.feedburner.com/androidcentral"
//            )
//        )
//        database.insert(
//            RssSites(
//                "Premier Guitar",
//                "https://premierguitar.com/rss/articles"
//            )
//        )
// test ---------------------------
        getRssFeeds()
    }

    fun getRssFeeds() {
        _status.value = RssApiStatus.LOADING
        val rssSitesList = database.getAll()
        linkedRssData.clear()

        CoroutineScope(Dispatchers.Main).launch {
            coroutineScope {
                rssSitesList.map {
                    CoroutineScope(Dispatchers.Main).launch {
                        processRssSitesList(it)
                    }
                }
            }.joinAll()
            if (linkedRssData.isNotEmpty()) {
                ogImgProcessing()
                _rssDataList.value = linkedRssData.map { it }
                _status.value = RssApiStatus.DONE
            } else {
                _rssDataList.value = emptyList()
                _status.value = RssApiStatus.ERROR
            }
        }
    }

    private suspend fun processRssSitesList(rssSite: RssSites) {
//        Log.i("RssFeedReader", "RssViewModel - processRssSitesList - start $rssSite")
        CoroutineScope(Dispatchers.Default).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(rssSite.rssUrl)
                .build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
//                    parseRssFeedXml(response.body()!!.string(), rssSite.rssSiteName)
                    listRssData =
                        RssXmlParser(rssSite.rssSiteName).parse(StringReader(response.body()!!.string()))
                            .take(16)
                    listRssData.map { linkedRssData.add(it) }
                } else
                    Log.i(
                        "RssFeedReader",
                        "Unsuccessful request for ${rssSite.rssUrl} - response code: ${response.code()}"
                    )
            } catch (e: Exception) {
                Log.i("RssFeedReader", "Exception: ${e.message}")
            }
        }.join()
    }

//    private fun parseRssFeedXml(rssFeedXml: String, rssSiteName: String) {
////        Log.i("RssFeedReader", "RssViewModel - parseRssFeedXml")
////        Log.i("RssFeedReader", rssFeedXml)
//        val inputReader = StringReader(rssFeedXml)
//        listRssData = RssXmlParser(rssSiteName).parse(inputReader).take(16)
//        listRssData.map { linkedRssData.add(it) }
//    }

    private suspend fun ogImgProcessing() {
        coroutineScope {
            linkedRssData.map {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.i("RssFeedReader", "getOgImage - start $it")
                    try {
                        val doc = Jsoup.connect(it.link).get()
                        it.ogImg = doc?.select("meta[property=og:image]")?.attr("content")
                    } catch (e: HttpStatusException) {
                        Log.i("RssFeedReader", "${e.message}")
                    }
                    Log.i("RssFeedReader", "getOgImage - end $it")
                }
            }
        }.joinAll()
    }
}
