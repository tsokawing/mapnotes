package edu.cuhk.mapnotes.activities;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.databinding.ActivityMapsBinding;
import edu.cuhk.mapnotes.datatypes.NotePin;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private List<NotePin> notePins = new ArrayList<>();

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadNotePins();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawNotePins();
        initCamera();
    }

    private void drawNotePins() {
        for (NotePin notePin : notePins) {
            drawNotePin(notePin);
        }
    }

    private void drawNotePin(NotePin notePin) {
        LatLng latlng = new LatLng(notePin.latitude, notePin.longitude);
        mMap.addMarker(new MarkerOptions().position(latlng).title(notePin.pinName));
    }

    private void initCamera() {
        LatLng center = getCenterOfPins(notePins);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 13));
    }

    private void loadNotePins() {
        prepareTestPins();
    }

    private void prepareTestPins() {
        // Generate some pins for testing
        NotePin pin1 = new NotePin();
        pin1.pinName = "Shatin";
        pin1.pinDescription = "My first notes";
        pin1.latitude = 22.38670278162099;
        pin1.longitude = 114.1954333616334;
        notePins.add(pin1);

        NotePin pin2 = new NotePin();
        pin2.pinName = "Shek Mun";
        pin2.pinDescription = "My second notes";
        pin2.latitude = 22.386583738555515;
        pin2.longitude = 114.20890877918538;
        notePins.add(pin2);
    }

    private LatLng getCenterOfPins(List<NotePin> notePins) {
        List<Double> latitudes = notePins.stream().map(notePin -> notePin.latitude)
                .collect(Collectors.toList());
        List<Double> longitudes = notePins.stream().map(notePin -> notePin.longitude)
                .collect(Collectors.toList());

        double latCenter = (Collections.max(latitudes) + Collections.min(latitudes)) / 2;
        double lngCenter = (Collections.max(longitudes) + Collections.min(longitudes)) / 2;
        return new LatLng(latCenter, lngCenter);
    }
}