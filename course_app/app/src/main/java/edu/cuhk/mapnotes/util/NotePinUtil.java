package edu.cuhk.mapnotes.util;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

import edu.cuhk.mapnotes.activities.MapsActivity;
import edu.cuhk.mapnotes.datatypes.NoteEntry;
import edu.cuhk.mapnotes.datatypes.NotePin;

public class NotePinUtil {
    private static final DecimalFormat df = new DecimalFormat("0.00000");

    public static NotePin MakeNewPinAtLocation(LatLng latLng) {
        NotePin newPin = new NotePin();
        newPin.pinName = "New Note";
        newPin.latitude = latLng.latitude;
        newPin.longitude = latLng.longitude;
        List<Long> insertionResponse = MapsActivity.noteDatabase.notePinDao().insertPins(newPin);
        // immediately write back to the object here, others may want to use the UID
        newPin.uid = (int) ((long) insertionResponse.get(0));

        // also give a default note for the pin
        NoteEntry noteEntry = new NoteEntry();
        String roundedLat = df.format(newPin.latitude);
        String roundedLng = df.format(newPin.longitude);
        noteEntry.noteText = "The location (" + roundedLat + ", " + roundedLng + ") is now marked. Perhaps there is something important here to be noted.";
        noteEntry.pinUid = newPin.uid;
        MapsActivity.noteDatabase.noteEntryDao().insertNoteEntries(noteEntry);

        return newPin;
    }
}
