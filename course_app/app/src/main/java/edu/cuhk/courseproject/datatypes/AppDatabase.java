package edu.cuhk.courseproject.datatypes;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        NoteMedia.class,
        NotePin.class,
        NoteReminder.class,
        NoteTag.class,
        NoteTaggingInfo.class,
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NotePinDao notePinDao();
}
