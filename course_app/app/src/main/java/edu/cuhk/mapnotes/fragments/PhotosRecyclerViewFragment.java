package edu.cuhk.mapnotes.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.cuhk.mapnotes.R;
import edu.cuhk.mapnotes.activities.PinsActivity;
import edu.cuhk.mapnotes.adapters.PinPhotosAdapter;
import edu.cuhk.mapnotes.datatypes.Photo;

public class PhotosRecyclerViewFragment extends Fragment {
    private static final String TAG = "PhotosRecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected PinPhotosAdapter mAdapter;
    protected List<Photo> mDataset = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadPinPhotos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photos_rv_frag, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.photos_rv);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new PinPhotosAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void loadPinPhotos() {
        // Load from sdcard
        File path = new File(this.requireContext().getExternalFilesDir(null).toString(), "images");
        if(path.exists()) {
            String[] fileNames = path.list();
            for (String fileName : fileNames) {
                Bitmap bitmap = BitmapFactory.decodeFile(path.getPath() + "/" + fileName);
                mDataset.add(new Photo(bitmap));
            }
        } else {
            Log.d(TAG, "Path not exist: " + path.getPath());
        }
    }

    public PinPhotosAdapter getPinPhotosAdapter() {
        return mAdapter;
    }
}
