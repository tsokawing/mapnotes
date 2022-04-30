package edu.cuhk.courseproject.datatypes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteTaggingInfo {

    @PrimaryKey
    public int uid;

    // todo
    public NotePin pin;

    // todo
    public NoteTag tag;
}
