package edu.cuhk.mapnotes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import edu.cuhk.mapnotes.R;
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
        mDataset.add(new Photo(R.drawable.image1));
        mDataset.add(new Photo(R.drawable.image2));
        mDataset.add(new Photo(R.drawable.image3));
        mDataset.add(new Photo(R.drawable.image4));
        mDataset.add(new Photo(R.drawable.image5));
        mDataset.add(new Photo(R.drawable.image6));
        mDataset.add(new Photo(R.drawable.image7));
    }

    public PinPhotosAdapter getPinPhotosAdapter() {
        return mAdapter;
    }
}
