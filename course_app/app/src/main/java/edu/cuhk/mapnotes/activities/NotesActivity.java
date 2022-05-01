package edu.cuhk.mapnotes.activities;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import edu.cuhk.mapnotes.databinding.ActivityNotesBinding;
import edu.cuhk.mapnotes.fragments.RecyclerViewFragment;
import edu.cuhk.mapnotes.R;

public class NotesActivity extends AppCompatActivity {

    private ActivityNotesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RecyclerViewFragment fragment = new RecyclerViewFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }
}