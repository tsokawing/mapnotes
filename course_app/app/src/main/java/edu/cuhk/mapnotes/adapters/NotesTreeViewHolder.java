package edu.cuhk.mapnotes.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;

import edu.cuhk.mapnotes.R;

public class NotesTreeViewHolder extends TreeViewHolder {

    private TextView fileName;
    private ImageView fileStateIcon;
    private ImageView fileTypeIcon;

    public NotesTreeViewHolder(@NonNull View itemView) {
        super(itemView);
        initViews();
    }

    private void initViews() {
        fileName = itemView.findViewById(R.id.file_name);
        fileStateIcon = itemView.findViewById(R.id.file_state_icon);
        fileTypeIcon = itemView.findViewById(R.id.file_type_icon);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);
        // Here you can bind your node and check if it selected or not

        String fileNameStr = node.getValue().toString();
        fileName.setText(fileNameStr);


        if (node.getChildren().isEmpty()) {
            fileStateIcon.setVisibility(View.INVISIBLE);
        } else {
            fileStateIcon.setVisibility(View.VISIBLE);
            int stateIcon = node.isExpanded() ? R.drawable.ic_baseline_edit_24 : R.drawable.ic_baseline_check_24;
            fileStateIcon.setImageResource(stateIcon);
        }
    }
}
