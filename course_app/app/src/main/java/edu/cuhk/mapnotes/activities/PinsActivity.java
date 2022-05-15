package edu.cuhk.mapnotes.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import edu.cuhk.mapnotes.adapters.PinNotesAdapter;
import edu.cuhk.mapnotes.databinding.ActivityPinsBinding;
import edu.cuhk.mapnotes.datatypes.NoteEntry;
import edu.cuhk.mapnotes.datatypes.NotePin;
import edu.cuhk.mapnotes.fragments.NotesRecyclerViewFragment;
import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.fragments.PhotosRecyclerViewFragment;

public class PinsActivity extends AppCompatActivity {

    private ActivityPinsBinding binding;
    private AlertDialog.Builder builder;
    private NotesRecyclerViewFragment notesRecyclerViewFragment;
    private PhotosRecyclerViewFragment photosRecyclerViewFragment;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private boolean showingPhotos = false;

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
        this.refreshPinLocationDisplay();

        // Fab button for gallery
        FloatingActionButton fabViewGallery = binding.fabViewGallery;
        fabViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBetweenNotesAndPhotos();
            }
        });

        // floating button to modify location name
        FloatingActionButton fabEditTitle = binding.fabEditPinName;
        AlertDialog dialogEditPinName = this.makeEditTitleDialog();
        fabEditTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditPinName.show();
            }
        });

        // Fab buttons

        binding.fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add a new note entry to this pin
                // TODO: Delete db then refresh may be slow as UI has to wait db finished.
                NoteEntry noteEntry = new NoteEntry();
                noteEntry.noteTitle = "Untitled Note";
                noteEntry.noteText = "Click here to edit!";
                noteEntry.pinUid = pinUid;
                // TODO: Should make db related as singleton outside of MapsActivity
                MapsActivity.noteDatabase.noteEntryDao().insertNoteEntries(noteEntry);

                PinNotesAdapter adapter = notesRecyclerViewFragment.getPinNotesAdapter();
                adapter.refreshNotePins();
                adapter.notifyItemInserted(adapter.getItemCount() - 1);

                Toast.makeText(getApplicationContext(), "A new note has been created.", Toast.LENGTH_LONG).show();
            }
        });

        binding.fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
//                dispatchTakePictureIntent();
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
        showPinNotes();

        // help button
        builder = new AlertDialog.Builder(this);
    }

    private void toggleBetweenNotesAndPhotos() {
        if (showingPhotos) {
            showPinNotes();
        } else {
            showPinPhotos();
        }
        showingPhotos = !showingPhotos;
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

        FloatingActionButton galleryFab = findViewById(R.id.fab_view_gallery);
        galleryFab.setImageResource(R.drawable.ic_baseline_photo_24);
    }

    private void showPinPhotos() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PhotosRecyclerViewFragment fragment = new PhotosRecyclerViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pinUid", this.pinUid);
        fragment.setArguments(bundle);
        photosRecyclerViewFragment = fragment;
        transaction.replace(R.id.pin_content_fragment, photosRecyclerViewFragment);
        transaction.commit();

        FloatingActionButton galleryFab = findViewById(R.id.fab_view_gallery);
        galleryFab.setImageResource(R.drawable.ic_baseline_notes_24);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void captureImage() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pictureIntent, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                saveImage(imageBitmap);
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        String filename;
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = sdf.format(date);

        try {
            String path = this.getApplicationContext().getExternalFilesDir(null).toString();
            OutputStream outputStream = null;
            File file = new File(path, "/images/" + String.valueOf(pinUid) + "/" + filename + ".jpg");
            File root = new File(Objects.requireNonNull(file.getParent()));
            if (file.getParent() != null && !root.isDirectory()) {
                root.mkdirs();
            }
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            Log.e("PinsActivity", "saveImage: " + e);
            e.printStackTrace();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        // force the stuff to update the UI; we know of no other way
        if (notesRecyclerViewFragment != null) {
            PinNotesAdapter adapter = notesRecyclerViewFragment.getPinNotesAdapter();
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

    public Context getContext() {
        return this.getApplicationContext();
    }

    private void properlySetToolbarTitle(CharSequence charSequence) {
        CollapsingToolbarLayout ctl = findViewById(R.id.toolbar_layout);
        ctl.setTitle(charSequence);
    }

    private void refreshPinLocationDisplay() {
        if (this.pinUid >= 0) {
            // ok
            NotePin notePin = MapsActivity.noteDatabase.notePinDao().getPinById(pinUid);
            properlySetToolbarTitle(notePin.pinName);
        }
    }

    private AlertDialog makeEditTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.renaming_pin_name);
        EditText inputRenameTitle = new EditText(this);
        inputRenameTitle.setInputType(InputType.TYPE_CLASS_TEXT);
        // should have already loaded a valid note
        if (pinUid >= 0) {
            NotePin pin = MapsActivity.noteDatabase.notePinDao().getPinById(pinUid);
            inputRenameTitle.setText(pin.pinName);
        }
        builder.setView(inputRenameTitle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pinUid >= 0) {
                    // put the updated title back to the DB
                    NotePin notePin = MapsActivity.noteDatabase.notePinDao().getPinById(pinUid);
                    notePin.pinName = inputRenameTitle.getText().toString();
                    MapsActivity.noteDatabase.notePinDao().updatePin(notePin);
                    refreshPinLocationDisplay();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog properDialog = builder.create();
        return properDialog;
    }
}