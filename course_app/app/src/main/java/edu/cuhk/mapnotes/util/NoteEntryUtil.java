package edu.cuhk.mapnotes.util;

import java.util.List;

import edu.cuhk.mapnotes.activities.MapsActivity;
import edu.cuhk.mapnotes.datatypes.NoteReminder;

public class NoteEntryUtil {
    public static NoteReminder getValidReminderOfNoteEntry(int noteEntryUid, long bufferTimeMs) {
        List<NoteReminder> reminderList = MapsActivity.noteDatabase.noteReminderDao().getAllNoteReminders(noteEntryUid);
        if (reminderList.isEmpty()) {
            return null;
        }
        NoteReminder reminder = reminderList.get(0);
        if (reminder.reminderTimestamp < System.currentTimeMillis() - bufferTimeMs) {
            return null;
        }
        return reminder;
    }
}
