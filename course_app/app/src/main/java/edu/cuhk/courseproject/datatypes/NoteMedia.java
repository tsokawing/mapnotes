package edu.cuhk.courseproject.datatypes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteMedia {

    @PrimaryKey
    public int uid;

    // todo media details, media types, etc
    public String todo;
}
