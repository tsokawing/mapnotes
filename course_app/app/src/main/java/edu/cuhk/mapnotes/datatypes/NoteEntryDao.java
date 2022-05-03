package edu.cuhk.mapnotes.datatypes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteEntryDao {
    // todo add more queries as needed

    @Query("SELECT * FROM note_entry")
    List<NoteEntry> getAllNoteEntries();

    @Query("SELECT * FROM note_entry WHERE pin_uid = :pinUid")
    List<NoteEntry> getAllNoteEntries(int pinUid);

    @Insert
    void insertNoteEntries(NoteEntry... entries);

    @Delete
    void deleteNoteEntry(NoteEntry entry);
}
