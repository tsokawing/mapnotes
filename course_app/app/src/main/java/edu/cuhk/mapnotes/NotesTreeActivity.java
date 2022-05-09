package edu.cuhk.mapnotes;

import android.os.Bundle;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.mapnotes.activities.MapsActivity;
import edu.cuhk.mapnotes.adapters.NotesTreeViewHolder;
import edu.cuhk.mapnotes.databinding.ActivityNotesTreeBinding;
import edu.cuhk.mapnotes.datatypes.NoteEntry;
import edu.cuhk.mapnotes.datatypes.NotePin;

public class NotesTreeActivity extends AppCompatActivity {

    private ActivityNotesTreeBinding binding;
    private List<NotePin> notePins = new ArrayList<>();
    List<TreeNode> roots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotesTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView notesTreeRecyclerView = findViewById(R.id.notes_tree_rv);
        notesTreeRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        notesTreeRecyclerView.setNestedScrollingEnabled(false);

        TreeViewHolderFactory factory = (v, layout) -> new NotesTreeViewHolder(v);

        TreeViewAdapter treeViewAdapter = new TreeViewAdapter(factory);
        notesTreeRecyclerView.setAdapter(treeViewAdapter);

        loadAllNotes();
        treeViewAdapter.updateTreeNodes(roots);
    }

    private void loadAllNotes() {
        notePins = MapsActivity.noteDatabase.notePinDao().getAllPins();
        for (NotePin notePin : notePins) {
            List<NoteEntry> noteEntries = MapsActivity.noteDatabase.noteEntryDao().getAllNoteEntries(notePin.uid);
            TreeNode root = new TreeNode(notePin.pinName, R.layout.list_item_file);
            for (NoteEntry noteEntry : noteEntries) {
                root.addChild(new TreeNode(noteEntry.noteTitle, R.layout.list_item_file));
            }
            roots.add(root);
        }
    }
}