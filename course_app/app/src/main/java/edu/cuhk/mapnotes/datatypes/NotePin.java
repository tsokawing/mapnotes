package edu.cuhk.mapnotes.datatypes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_pin")
public class NotePin {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "name")
    public String pinName;

    @ColumnInfo(name = "description")
    public String pinDescription;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;
}
