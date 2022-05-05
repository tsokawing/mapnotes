package edu.cuhk.mapnotes.activities;

import android.app.Activity;
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

import edu.cuhk.mapnotes.databinding.ActivityNotesBinding;
import edu.cuhk.mapnotes.R;

public class NotesActivity extends AppCompatActivity {

    private ActivityNotesBinding binding;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        stopEditText();

        FloatingActionButton fabViewGallery = binding.fabViewGallery;
        fabViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "View the gallery", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fabDeleteNote = binding.fabDeleteNote;
        fabDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Delete this note (with confirmation)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fabEditText = binding.fabEditText;
        fabEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditing) {
                    stopEditText();
                    Toast.makeText(getApplicationContext(), "New changes saved", Toast.LENGTH_LONG).show();
                } else {
                    startEditText();
                }
            }
        });
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

    void stopEditText() {
        isEditing = false;
        EditText editText = findViewById(R.id.note_edittext);
        editText.setFocusable(false);

        FloatingActionButton editTextFab = findViewById(R.id.fab_edit_text);
        editTextFab.setImageResource(R.drawable.ic_baseline_edit_24);
    }
}