package edu.cuhk.mapnotes.datatypes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteTagDao {
    // todo add more queries as needed

    @Query("SELECT * FROM note_tag")
    List<NoteTag> getAllPossibleTags();

    @Query("SELECT * FROM note_tag WHERE uid = :tagUid")
    NoteTag getTag(int tagUid);

    @Query("SELECT * FROM note_tag WHERE name = :tagName")
    NoteTag getTag(String tagName);

    // for general usage, please use helper functions instead.
    @Insert
    long registerTag(NoteTag noteTag);

    // for general usage, please use helper functions instead.
    @Delete
    void deleteTag(NoteTag tag);

    // for general usage, please use helper functions instead.
    @Query("DELETE FROM note_tag WHERE name = :tagName")
    void deleteTag(String tagName);

}