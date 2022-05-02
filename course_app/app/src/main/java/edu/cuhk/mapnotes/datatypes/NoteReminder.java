package edu.cuhk.mapnotes.datatypes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NoteReminder {

    @PrimaryKey(autoGenerate = true)
    public int uid;

//    @ColumnInfo(name = "pin")
//    public NotePin pin;

    @ColumnInfo(name = "alarm_timestamp")
    public int alarmStamp;
}
