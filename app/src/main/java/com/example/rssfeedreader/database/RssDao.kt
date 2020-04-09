package com.example.rssfeedreader.database

import androidx.room.*

@Dao
interface RssSitesDao {

    @Insert
    fun insert(rssSite: RssSites)

    @Update
    fun update(rssSite: RssSites)

    @Delete
    fun delete(rssSite: RssSites)

    @Query("SELECT * from rss_sites order by rss_site_name")
    fun getAll(): List<RssSites>

    @Query("SELECT * from rss_sites WHERE rss_site_name = :key")
    fun get(key: String): List<RssSites>

    @Query("DELETE FROM rss_sites")
    fun clear()

}

