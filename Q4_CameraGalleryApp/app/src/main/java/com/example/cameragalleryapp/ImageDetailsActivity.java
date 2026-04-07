package com.example.cameragalleryapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        ImageView imageView = findViewById(R.id.detailImageView);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPath = findViewById(R.id.tvPath);
        TextView tvSize = findViewById(R.id.tvSize);
        TextView tvDate = findViewById(R.id.tvDate);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Retrieve data passed from MainActivity
        String uriString = getIntent().getStringExtra("uri");
        String name = getIntent().getStringExtra("name");
        long sizeBytes = getIntent().getLongExtra("size", 0);
        long dateModified = getIntent().getLongExtra("date", 0);

        Uri uri = Uri.parse(uriString);
        imageView.setImageURI(uri);

        // c.i) View the image name, path, size, date taken details
        tvName.setText("Name: " + name);
        tvPath.setText("Path/URI: " + uriString);
        tvSize.setText("Size: " + (sizeBytes / 1024) + " KB");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        tvDate.setText("Date Taken: " + sdf.format(new Date(dateModified)));

        // c.ii) Delete button with confirmation Dialog
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to permanently delete this image?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        DocumentFile file = DocumentFile.fromSingleUri(this, uri);
                        if (file != null && file.exists()) {
                            file.delete();
                            Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show();
                            // After image is deleted, user should be brought back to gallery view
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}