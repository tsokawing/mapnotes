package edu.cuhk.mapnotes.activities;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.SearchView;

import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.adapters.NoteTagsAdapter;
import edu.cuhk.mapnotes.databinding.ActivityNoteEntryTagsBinding;

public class NoteTagsActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private ActivityNoteEntryTagsBinding binding;

    // Declare Variables
    RecyclerView recycler;
    SearchView tagEditSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteEntryTagsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("Manage tags");

        setupSearchControl();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        Log.d("SEARCH", text);
//        adapter.filter(text);
        return false;
    }

    private void setupSearchControl() {
        tagEditSearch = (SearchView) findViewById(R.id.tag_search_widget);
        tagEditSearch.setOnQueryTextListener(this);

        recycler = (RecyclerView) findViewById(R.id.recyclerView);
        recycler.setAdapter(new NoteTagsAdapter());
    }
}