package edu.cuhk.mapnotes.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.mapnotes.R;

public class NoteTagsAdapter extends RecyclerView.Adapter<NoteTagsAdapter.ViewHolder> {

    private static final String TAG = "TagAdaptor";

    private List<String> mNoteTags = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
//        private final TextView textViewNoteTitle;
//        private final TextView textViewNoteContent;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    NoteEntry entry = mNoteEntries.get(getAdapterPosition());
//                    Intent noteIntent = new Intent(v.getContext(), NotesActivity.class);
//                    noteIntent.putExtra("noteUid", entry.uid);
//                    Log.d(TAG, "Note entry " + entry.uid + " clicked.");
                    Log.d(TAG, "Somebody got clicked");
//                    v.getContext().startActivity(noteIntent);
                }
            });
//            textViewNoteTitle = (TextView) v.findViewById(R.id.textViewNoteTitle);
//            textViewNoteContent = (TextView) v.findViewById(R.id.textViewNoteText);
        }

//        public TextView getTitleTextView() {
//            return textViewNoteTitle;
//        }
//
//        public TextView getContentTextView() {
//            return textViewNoteContent;
//        }
    }

    public NoteTagsAdapter() {
        // dummy create some data
        for (int i = 0; i < 20; i++) {
            String numStr = "" + i;
            mNoteTags.add(numStr);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_tag_row_item, parent, false);

        return new NoteTagsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");
    }

    @Override
    public int getItemCount() {
        return mNoteTags.size();
    }

}
