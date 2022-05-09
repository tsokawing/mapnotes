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

import edu.cuhk.mapnotes.adapters.NotesTreeViewHolder;
import edu.cuhk.mapnotes.databinding.ActivityNotesTreeBinding;

public class NotesTreeActivity extends AppCompatActivity {

    private ActivityNotesTreeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNotesTreeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showTestData();
    }

    private void showTestData() {
        RecyclerView notesTreeRecyclerView = findViewById(R.id.notes_tree_rv);
        notesTreeRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        notesTreeRecyclerView.setNestedScrollingEnabled(false);

        TreeViewHolderFactory factory = (v, layout) -> new NotesTreeViewHolder(v);

        TreeViewAdapter treeViewAdapter = new TreeViewAdapter(factory);
        notesTreeRecyclerView.setAdapter(treeViewAdapter);

        TreeNode root1 = new TreeNode("Root1", R.layout.list_item_file);
        root1.addChild(new TreeNode("Child1", R.layout.list_item_file));
        root1.addChild(new TreeNode("Child2", R.layout.list_item_file));

        List<TreeNode> roots = new ArrayList<>();
        roots.add(root1);

        treeViewAdapter.updateTreeNodes(roots);
    }
}