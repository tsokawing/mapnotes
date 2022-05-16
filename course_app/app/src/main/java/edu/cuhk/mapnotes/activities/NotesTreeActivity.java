package edu.cuhk.mapnotes.activities;

import android.os.Bundle;
import android.widget.SearchView;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolderFactory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.adapters.NotesTreeViewHolder;
import edu.cuhk.mapnotes.databinding.ActivityNotesTreeBinding;
import edu.cuhk.mapnotes.datatypes.NoteEntry;
import edu.cuhk.mapnotes.datatypes.NotePin;

public class NotesTreeActivity extends AppCompatActivity {

    private ActivityNotesTreeBinding binding;

    private List<NotePin> notePins = new ArrayList<>();

    RecyclerView notesTreeRecyclerView;
    TreeViewAdapter treeViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotesTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load list of notes tree view
        notesTreeRecyclerView = findViewById(R.id.notes_tree_rv);
        notesTreeRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        notesTreeRecyclerView.setNestedScrollingEnabled(false);

        TreeViewHolderFactory factory = (v, layout) -> new NotesTreeViewHolder(v);

        treeViewAdapter = new TreeViewAdapter(factory);
        notesTreeRecyclerView.setAdapter(treeViewAdapter);

        loadAllNotes();
        setUpSearch();
    }

    private void setUpSearch() {
        SearchView searchView = findViewById(R.id.notestree_search_widget);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    filterNotes(newText);
                    treeViewAdapter.expandAll();
                } else {
                    loadAllNotes();
                }
                return false;
            }
        });
    }

    private void loadAllNotes() {
        List<TreeNode> roots = new ArrayList<>();

        notePins = MapsActivity.noteDatabase.notePinDao().getAllPins();
        for (NotePin notePin : notePins) {
            List<NoteEntry> noteEntries = MapsActivity.noteDatabase.noteEntryDao().getAllNoteEntries(notePin.uid);
            TreeNode root = new TreeNode(notePin, R.layout.list_item_file);
            for (NoteEntry noteEntry : noteEntries) {
                root.addChild(new TreeNode(noteEntry, R.layout.list_item_file));
            }
            roots.add(root);
        }

        treeViewAdapter.notifyDataSetChanged();
        treeViewAdapter.updateTreeNodes(roots);
    }

    private void filterNotes(String keyword) {
        List<TreeNode> roots = new ArrayList<>();

        for (NotePin notePin : notePins) {
            List<NoteEntry> noteEntries = MapsActivity.noteDatabase.noteEntryDao().getAllNoteEntries(notePin.uid);
            TreeNode root = new TreeNode(notePin, R.layout.list_item_file);

            // Filter here
            if (notePin.pinName.toLowerCase().contains(keyword.toLowerCase())) {
                // Pin contains keyword, add all notes of this pin
                for (NoteEntry noteEntry : noteEntries) {
                    root.addChild(new TreeNode(noteEntry, R.layout.list_item_file));
                }
                roots.add(root);
            } else {
                // Pin doesn't contain keyword, check notes inside
                for (NoteEntry noteEntry : noteEntries) {
                    if (noteEntry.noteTitle.toLowerCase().contains(keyword.toLowerCase())) {
                        root.addChild(new TreeNode(noteEntry, R.layout.list_item_file));
                    }
                }
                // Only add pin if there's at least one note contains keyword
                if (!root.getChildren().isEmpty()) {
                    roots.add(root);
                }
            }
        }

        treeViewAdapter.notifyDataSetChanged();
        treeViewAdapter.updateTreeNodes(roots);
    }
}