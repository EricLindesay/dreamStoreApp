package com.android.androidTesting.db;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LinkTableDao {

    @Query("SELECT * FROM LinkTable")
    List<LinkTable> getAllLinks();

    @Query("SELECT nid FROM LinkTable WHERE tid == :tagID")
    List<Integer> getAllNotesWithTag(String tagID);

    @Query("SELECT tid FROM LinkTable WHERE nid == :noteID")
    List<String> getAllTagsForNote(int noteID);

    @Insert(onConflict=IGNORE)
    void insertLink(LinkTable... links);

    @Query("DELETE FROM linktable WHERE nid == :noteID")
    void deleteLinksToNote(int noteID);

    @Query("DELETE FROM linktable WHERE tid == :tagID")
    void deleteLinksToTag(String tagID);

    @Delete
    void delete(LinkTable link);

    @Query("SELECT COUNT(nid) FROM linktable WHERE tid == :tagID")
    int tagOccurrences(String tagID);
}
