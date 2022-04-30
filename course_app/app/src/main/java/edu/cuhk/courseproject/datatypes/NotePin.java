package edu.cuhk.courseproject.datatypes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotePin {

    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "name")
    public String pinName;

    @ColumnInfo(name = "description")
    public String pinDescription;

    @ColumnInfo(name = "latitude")
    public float latitude;

    @ColumnInfo(name = "longitude")
    public float longitude;
}
