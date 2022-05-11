package edu.cuhk.mapnotes.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cuhk.mapnotes.activities.MapsActivity;
import edu.cuhk.mapnotes.datatypes.NoteReminder;
import edu.cuhk.mapnotes.datatypes.NoteTag;
import edu.cuhk.mapnotes.datatypes.NoteTagDao;
import edu.cuhk.mapnotes.datatypes.NoteTaggingInfo;
import edu.cuhk.mapnotes.datatypes.NoteTaggingInfoDao;

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

    public static List<NoteTag> getEnabledTagsForNoteEntry(int noteEntryUid) {
        List<NoteTaggingInfo> noteTagInfo = MapsActivity.noteDatabase.noteTaggingInfoDao().getAllNoteTaggingInfo(noteEntryUid);
        List<NoteTag> noteTagList = new ArrayList<>();
        for (NoteTaggingInfo tagInfo : noteTagInfo) {
            noteTagList.add(MapsActivity.noteDatabase.noteTagDao().getTag(tagInfo.tagUid));
        }
        return noteTagList;
    }

    public static List<NoteTag> getTagsNotUsedByNoteEntry(int noteEntryUid) {
        // todo consider doing a join where not in

        Map<Integer, NoteTag> noteTagMapping = new HashMap<>();
        List<NoteTag> allPossibleTags = MapsActivity.noteDatabase.noteTagDao().getAllPossibleTags();
        for (NoteTag tag : allPossibleTags) {
            noteTagMapping.put(tag.uid, tag);
        }

        List<NoteTaggingInfo> noteTagInfo = MapsActivity.noteDatabase.noteTaggingInfoDao().getAllNoteTaggingInfo(noteEntryUid);
        for (NoteTaggingInfo tagInfo : noteTagInfo) {
//            noteTagList.add(MapsActivity.noteDatabase.noteTagDao().getTag(tagInfo.tagUid));
            noteTagMapping.remove(tagInfo.tagUid);
        }

        List<NoteTag> noteTagList = new ArrayList<>();
        for (int tagUid : noteTagMapping.keySet()) {
            noteTagList.add(noteTagMapping.get(tagUid));
        }
        return noteTagList;
    }

    public static void addTagForNoteEntry(int noteEntryUid, String tagName) {
        // ensure that the internal tag exists; and then associate it with the note entry
        NoteTagDao tagDao = MapsActivity.noteDatabase.noteTagDao();
        NoteTag tag = tagDao.getTag(tagName);
        if (tag == null) {
            tag = new NoteTag();
            tag.tagName = tagName;
            tag.uid = (int) tagDao.registerTag(tag);
        }

        NoteTaggingInfoDao taggingInfoDao = MapsActivity.noteDatabase.noteTaggingInfoDao();
        NoteTaggingInfo info = taggingInfoDao.getSpecificNoteTaggingInfo(noteEntryUid, tag.uid);
        if (info == null) {
            // not yet registered
            info = new NoteTaggingInfo();
            info.noteEntryUid = noteEntryUid;
            info.tagUid = tag.uid;
            taggingInfoDao.insertTaggingInformation(info);
        }
        // else: already registered... normally should not happen
    }

    public static void removeTagForNoteEntry(int noteEntryUid, String tagName) {
        // ensure that the internal tag exists; and then associate it with the note entry
        NoteTagDao tagDao = MapsActivity.noteDatabase.noteTagDao();
        NoteTag tag = tagDao.getTag(tagName);
        if (tag == null) {
            // nothing to remove
            return;
        }

        NoteTaggingInfoDao taggingInfoDao = MapsActivity.noteDatabase.noteTaggingInfoDao();
        NoteTaggingInfo info = taggingInfoDao.getSpecificNoteTaggingInfo(noteEntryUid, tag.uid);
        if (info == null) {
            // nothing to remove
            return;
        }
        taggingInfoDao.removeTaggingInformation(info);

        // check again: do we have no correlation left?
        List<NoteTaggingInfo> tagInfoWithTag = taggingInfoDao.getAllTaggingInfoWithTag(tag.uid);
        if (tagInfoWithTag.isEmpty()) {
            // no more remains; remove it
            tagDao.deleteTag(tag);
        }
    }
}
