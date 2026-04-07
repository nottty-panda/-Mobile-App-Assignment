package com.example.cameragalleryapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);

        File folder = new File(getExternalFilesDir(null), "MyImages");
        File[] files = folder.listFiles();
        if (files == null) {
            files = new File[0];
        }

        gridView.setAdapter(new ImageAdapter(this, files));
    }
}