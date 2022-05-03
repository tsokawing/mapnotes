package edu.cuhk.mapnotes.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;

import edu.cuhk.mapnotes.databinding.ActivityPinsBinding;
import edu.cuhk.mapnotes.fragments.RecyclerViewFragment;
import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.util.HelpButtonOnClickListener;

public class PinsActivity extends AppCompatActivity {

    private ActivityPinsBinding binding;
    private AlertDialog.Builder builder;

    private int pinUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPinsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // Fab button for adding new note
        binding.fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add a new note to this pin", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        binding.fabDeletePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Delete this pin from map (with confirmation)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Display notes in a fragment recycler view
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RecyclerViewFragment fragment = new RecyclerViewFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("pinUid", this.pinUid);
            fragment.setArguments(bundle);
            transaction.replace(R.id.pin_content_fragment, fragment);
            transaction.commit();
        }

        // help button
        builder = new AlertDialog.Builder(this);
        FloatingActionButton mapHelpButton = binding.fabHelpNotes;
        mapHelpButton.setOnClickListener(new HelpButtonOnClickListener(
                builder, R.string.notes_of_pin_title, R.string.notes_of_pin_descr));
    }
}