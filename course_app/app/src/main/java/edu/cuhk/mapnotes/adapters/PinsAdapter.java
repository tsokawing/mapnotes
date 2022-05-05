package edu.cuhk.mapnotes.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.activities.MapsActivity;
import edu.cuhk.mapnotes.activities.NotesActivity;
import edu.cuhk.mapnotes.datatypes.NoteEntry;

public class PinsAdapter extends RecyclerView.Adapter<PinsAdapter.ViewHolder> {
    private static final String TAG = "Adapter";

//    private String[] mDataSet;

    private int pinUid;
    private List<NoteEntry> mNoteEntries;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNoteTitle;
        private final TextView textViewNoteContent;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    v.getContext().startActivity(new Intent(v.getContext(), NotesActivity.class));
                }
            });
            textViewNoteTitle = (TextView) v.findViewById(R.id.textViewNoteTitle);
            textViewNoteContent = (TextView) v.findViewById(R.id.textViewNoteText);
        }

        public TextView getTitleTextView() {
            return textViewNoteTitle;
        }

        public TextView getContentTextView() {
            return textViewNoteContent;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public PinsAdapter(String[] dataSet) {
//        mDataSet = dataSet;
    }

    public PinsAdapter(int pinUid) {
        this.pinUid = pinUid;
        this.refreshNotePins();
    }

    public void refreshNotePins() {
        // for performance reasons, this will not call any notify-dataset-changed functions
        // only the calle knows what should be called
        mNoteEntries = MapsActivity.noteDatabase.noteEntryDao().getAllNoteEntries(pinUid);
    }

    public NoteEntry getNoteEntryAt(int position) {
        return mNoteEntries.get(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        NoteEntry noteEntry = mNoteEntries.get(position);
        viewHolder.getTitleTextView().setText(noteEntry.noteTitle);
        viewHolder.getContentTextView().setText(noteEntry.noteText);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mNoteEntries.size();
    }
}
