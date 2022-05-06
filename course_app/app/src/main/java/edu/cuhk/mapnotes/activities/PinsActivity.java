package edu.cuhk.mapnotes.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import edu.cuhk.mapnotes.adapters.PinsAdapter;
import edu.cuhk.mapnotes.databinding.ActivityPinsBinding;
import edu.cuhk.mapnotes.datatypes.NoteEntry;
import edu.cuhk.mapnotes.datatypes.NotePin;
import edu.cuhk.mapnotes.fragments.NotesRecyclerViewFragment;
import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.fragments.PhotosRecyclerViewFragment;
import edu.cuhk.mapnotes.util.HelpButtonOnClickListener;

public class PinsActivity extends AppCompatActivity {

    private ActivityPinsBinding binding;
    private AlertDialog.Builder builder;
    private NotesRecyclerViewFragment notesRecyclerViewFragment;
    private PhotosRecyclerViewFragment photosRecyclerViewFragment;

    private int pinUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPinsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        writePinInfoOnToolBar();

        // load the pin uid
        Intent invokerIntent = getIntent();
        if (invokerIntent != null) {
            // has invoker
            this.pinUid = invokerIntent.getIntExtra("pinUid", -1);
            if (this.pinUid < 0) {
                // well this cannot be good
                throw new RuntimeException("pinUid is invalid");
            }
            Log.d("TAG", "Pin notes from intent: UID " + this.pinUid);
        }
        if (savedInstanceState != null) {
//            this.pinUid = savedInstanceState.getInt("pinUid");
//            Log.d("TAG", "Pin notes: UID " + this.pinUid);
        }

        // Fab button for gallery
        FloatingActionButton fabViewGallery = binding.fabViewGallery;
        fabViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "View the gallery", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Fab button for adding new note
        binding.fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add a new note entry to this pin
                // TODO: Delete db then refresh may be slow as UI has to wait db finished.
                NoteEntry noteEntry = new NoteEntry();
                noteEntry.noteTitle = "New Note";
                noteEntry.noteText = "A new note for this location has been created. Click here to edit it.";
                noteEntry.pinUid = pinUid;
                // TODO: Should make db related as singleton outside of MapsActivity
                MapsActivity.noteDatabase.noteEntryDao().insertNoteEntries(noteEntry);

                PinsAdapter adapter = notesRecyclerViewFragment.getPinsAdapter();
                adapter.refreshNotePins();
                adapter.notifyItemInserted(adapter.getItemCount() - 1);

                Toast.makeText(getApplicationContext(), "A new note has been created.", Toast.LENGTH_LONG).show();
            }
        });

        binding.fabDeletePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage(R.string.deleting_pin_descr)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // delete it!
                                // we will do it in a slightly roundabout way
                                NotePin pin = MapsActivity.noteDatabase.notePinDao().getPinById(pinUid);
                                MapsActivity.noteDatabase.notePinDao().deletePin(pin);
                                // exit to the map
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setTitle(R.string.deleting_pin_title);
                dialog.show();
            }
        });

        // Show list of notes by default
        showPinPhotos();

        // help button
        builder = new AlertDialog.Builder(this);
        FloatingActionButton mapHelpButton = binding.fabHelpNotes;
        mapHelpButton.setOnClickListener(new HelpButtonOnClickListener(
                builder, R.string.notes_of_pin_title, R.string.notes_of_pin_descr));
    }

    private void showPinNotes() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        NotesRecyclerViewFragment fragment = new NotesRecyclerViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pinUid", this.pinUid);
        fragment.setArguments(bundle);
        notesRecyclerViewFragment = fragment;
        transaction.replace(R.id.pin_content_fragment, notesRecyclerViewFragment);
        transaction.commit();
    }

    private void showPinPhotos() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PhotosRecyclerViewFragment fragment = new PhotosRecyclerViewFragment();
        photosRecyclerViewFragment = fragment;
        transaction.replace(R.id.pin_content_fragment, photosRecyclerViewFragment);
        transaction.commit();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        // force the stuff to update the UI; we know of no other way
        if (notesRecyclerViewFragment != null) {
            PinsAdapter adapter = notesRecyclerViewFragment.getPinsAdapter();
            adapter.refreshNotePins();
            adapter.notifyDataSetChanged();
        }
    }

    private void writePinInfoOnToolBar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Pin");
    }
}