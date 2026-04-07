package com.example.cameragalleryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import androidx.documentfile.provider.DocumentFile;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DocumentFile> files;

    public ImageAdapter(Context context, ArrayList<DocumentFile> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() { return files.size(); }

    @Override
    public Object getItem(int position) { return files.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_image_adapter, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.gridImageView);
        imageView.setImageURI(files.get(position).getUri());

        return convertView;
    }
}