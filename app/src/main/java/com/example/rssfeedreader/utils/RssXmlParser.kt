package com.example.rssfeedreader.utils

import com.example.rssfeedreader.ui.RssViewModel.RssData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class RssXmlParser(private val siteName: String) {
    fun parse(inputReader: StringReader): List<RssData> {
        val listRssData = mutableListOf<RssData>()
        val xmlPullParserFactory = XmlPullParserFactory.newInstance()
        val parser: XmlPullParser = xmlPullParserFactory.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputReader)
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                if (parser.name == "item")
                    listRssData.add(readItems(parser))
            }
        }
        return listRssData
    }

    private fun readItems(parser: XmlPullParser): RssData {
        var title: String? = null
        var link: String? = null
        var pubDate: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "title" -> title = readText(parser)
                    "link" -> link = readText(parser)
                    "pubDate" -> pubDate = readText(parser)
                    else -> skip(parser)
                }
            }
        }
        return RssData(title, link, pubDate, null, siteName)
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.next()
        }
        return result
    }

    private fun skip(parser: XmlPullParser) {
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> depth++
                XmlPullParser.END_TAG -> depth--
            }
        }
    }

//    private suspend fun getOgImg(link: String?): String {
//        val doc = Jsoup.connect(link).get()
//        return doc?.select("meta[property=og:image]")?.attr("content") as String
//    }

}

//import android.util.Xml
//import com.example.rssfeedreader.rssMain.RssViewModel.RssData
//import org.xmlpull.v1.XmlPullParser
//import org.xmlpull.v1.XmlPullParserException
//import java.io.IOException
//import java.io.Reader
//
//private val ns: String? = null
//
//class RssXmlParser {
//
//    @Throws(XmlPullParserException::class, IOException::class)
//    fun parse(inputReader: Reader): List<RssData> {
//        val parser: XmlPullParser = Xml.newPullParser()
//        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
//        parser.setInput(inputReader)
//        parser.nextTag()
//        parser.nextTag()
//        return readFeed(parser)
//    }
//
//    @Throws(XmlPullParserException::class, IOException::class)
//    private fun readFeed(parser: XmlPullParser): List<RssData> {
//        val entries = mutableListOf<RssData>()
//
//        parser.require(XmlPullParser.START_TAG, ns, "channel")
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            // Starts by looking for the entry tag
//            if (parser.name == "item") {
//                entries.add(readEntry(parser))
//            } else {
//                skip(parser)
//            }
//        }
//        return entries
//    }
//
////    data class Entry(val title: String?, val summary: String?, val link: String?)
//
//    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
//// to their respective "read" methods for processing. Otherwise, skips the tag.
//    @Throws(XmlPullParserException::class, IOException::class)
//    private fun readEntry(parser: XmlPullParser): RssData {
//        parser.require(XmlPullParser.START_TAG, ns, "item")
//        var title: String? = null
//        var link: String? = null
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            when (parser.name) {
//                "title" -> title = readTitle(parser)
//                "link" -> link = readLink(parser)
//                else -> skip(parser)
//            }
//        }
//        return RssData(title, link)
//    }
//
//    // Processes title tags in the feed.
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readTitle(parser: XmlPullParser): String {
//        parser.require(XmlPullParser.START_TAG, ns, "title")
//        val title = readText(parser)
//        parser.require(XmlPullParser.END_TAG, ns, "title")
//        return title
//    }
//
//    // Processes link tags in the feed.
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readLink(parser: XmlPullParser): String {
//        var link = ""
//        parser.require(XmlPullParser.START_TAG, ns, "link")
//        val tag = parser.name
//        val relType = parser.getAttributeValue(null, "rel")
//        if (tag == "link") {
//            if (relType == "alternate") {
//                link = parser.getAttributeValue(null, "href")
//                parser.nextTag()
//            }
//        }
//        parser.require(XmlPullParser.END_TAG, ns, "link")
//        return link
//    }
//
//    // For the tags title and summary, extracts their text values.
//    @Throws(IOException::class, XmlPullParserException::class)
//    private fun readText(parser: XmlPullParser): String {
//        var result = ""
//        if (parser.next() == XmlPullParser.TEXT) {
//            result = parser.text
//            parser.nextTag()
//        }
//        return result
//    }
//
//    @Throws(XmlPullParserException::class, IOException::class)
//    private fun skip(parser: XmlPullParser) {
//        if (parser.eventType != XmlPullParser.START_TAG) {
//            throw IllegalStateException()
//        }
//        var depth = 1
//        while (depth != 0) {
//            when (parser.next()) {
//                XmlPullParser.END_TAG -> depth--
//                XmlPullParser.START_TAG -> depth++
//            }
//        }
//    }
//}