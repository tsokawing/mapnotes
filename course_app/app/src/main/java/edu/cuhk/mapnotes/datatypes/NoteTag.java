package edu.cuhk.mapnotes.datatypes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteTag {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "name")
    public String tagName;
}
