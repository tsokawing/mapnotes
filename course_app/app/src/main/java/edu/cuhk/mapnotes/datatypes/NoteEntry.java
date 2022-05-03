package edu.cuhk.mapnotes.datatypes;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_entry",
        foreignKeys = {@ForeignKey(
        entity = NotePin.class,
        parentColumns = "uid",
        childColumns = "pin_uid",
        onDelete = CASCADE
)})
public class NoteEntry {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "pin_uid")
    public int pinUid;

    @ColumnInfo(name = "text")
    public String noteText;
}
