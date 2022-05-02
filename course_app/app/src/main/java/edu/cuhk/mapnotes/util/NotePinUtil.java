package edu.cuhk.mapnotes.util;

import com.google.android.gms.maps.model.LatLng;

import edu.cuhk.mapnotes.activities.MapsActivity;
import edu.cuhk.mapnotes.datatypes.NotePin;

public class NotePinUtil {
    public static NotePin MakeNewPinAtLocation(LatLng latLng) {
        NotePin newPin = new NotePin();
        newPin.pinName = "New Note";
        newPin.latitude = latLng.latitude;
        newPin.longitude = latLng.longitude;
        MapsActivity.noteDatabase.notePinDao().insertPins(newPin);

        return newPin;
    }
}
