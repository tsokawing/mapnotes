package edu.cuhk.mapnotes.datatypes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(indices = [Index("name")])
public class NoteTag {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String tagName;
}
