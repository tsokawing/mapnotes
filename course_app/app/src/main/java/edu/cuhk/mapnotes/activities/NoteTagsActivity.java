package edu.cuhk.mapnotes.activities;

import android.content.Intent;
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
import edu.cuhk.mapnotes.util.NoteEntryUtil;

public class NoteTagsActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    private ActivityNoteEntryTagsBinding binding;

    private int noteEntryUid;

    // Declare Variables
    RecyclerView recycler;
    NoteTagsAdapter tagsAdapter;
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

        Intent invokerIntent = getIntent();
        if (invokerIntent != null) {
            this.noteEntryUid = invokerIntent.getIntExtra("noteUid", -1);
            if (this.noteEntryUid < 0) {
                Log.e("TAG", "Bad: received an invalid note UID");
            }
        }

        setupSearchControl();
        NoteEntryUtil.cleanUpUnusedTags();
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
        tagsAdapter.refreshTagsWithThisSearchString(text);
        return false;
    }

    private void setupSearchControl() {
        tagEditSearch = (SearchView) findViewById(R.id.tag_search_widget);
        tagEditSearch.setOnQueryTextListener(this);

        recycler = (RecyclerView) findViewById(R.id.recyclerView);
        tagsAdapter = new NoteTagsAdapter(noteEntryUid);
        recycler.setAdapter(tagsAdapter);
    }
}