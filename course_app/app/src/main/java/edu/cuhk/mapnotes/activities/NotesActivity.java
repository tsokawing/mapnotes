package edu.cuhk.mapnotes.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import edu.cuhk.mapnotes.databinding.ActivityNotesBinding;
import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.datatypes.NoteEntry;

public class NotesActivity extends AppCompatActivity {

    private ActivityNotesBinding binding;

    private boolean isEditing = false;

    private int noteEntryUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent invokerIntent = getIntent();
        if (invokerIntent != null) {
            // has invoker
            this.noteEntryUid = invokerIntent.getIntExtra("noteUid", -1);
            if (this.noteEntryUid < 0) {
                // well this cannot be good
                throw new RuntimeException("noteUid is invalid");
            }
            Log.d("TAG", "Pin notes from intent: UID " + this.noteEntryUid);
        }

        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        // load the notes
        NoteEntry noteEntry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(this.noteEntryUid);
        if (noteEntry != null) {
            toolBarLayout.setTitle(noteEntry.noteTitle);
        } else {
            toolBarLayout.setTitle(getTitle());
        }

        stopEditText(true);

        FloatingActionButton fabEditText = binding.fabEditText;
        fabEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditing) {
                    stopEditText(false);
                    Toast.makeText(getApplicationContext(), "New changes saved", Toast.LENGTH_LONG).show();
                } else {
                    startEditText();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.noteEntryUid >= 0) {
            // put the note content into the UI
            EditText noteTextContentEditText = findViewById(R.id.note_edittext);
            // load the notes
            NoteEntry noteEntry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(this.noteEntryUid);
            // todo check is text or audio
            noteTextContentEditText.setText(noteEntry.noteText);
        }
    }

    void startEditText() {
        isEditing = true;
        EditText editText = findViewById(R.id.note_edittext);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.requestFocus();

        // Show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

        FloatingActionButton editTextFab = findViewById(R.id.fab_edit_text);
        editTextFab.setImageResource(R.drawable.ic_baseline_check_24);
    }

    void stopEditText(boolean doNotSave) {
        isEditing = false;
        EditText editText = findViewById(R.id.note_edittext);
        editText.setFocusable(false);

        // Collapse keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        FloatingActionButton editTextFab = findViewById(R.id.fab_edit_text);
        editTextFab.setImageResource(R.drawable.ic_baseline_edit_24);

        // save it to the db
        if (!doNotSave) {
            NoteEntry entry = MapsActivity.noteDatabase.noteEntryDao().getNoteEntry(this.noteEntryUid);
            entry.noteText = editText.getText().toString();
            MapsActivity.noteDatabase.noteEntryDao().updateNoteEntry(entry);
        }
    }
}