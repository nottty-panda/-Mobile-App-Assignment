package com.example.cameragalleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_PICK_FOLDER = 102;

    private Uri selectedFolderUri = null;
    private GridView gridView;
    private ImageAdapter adapter;
    private ArrayList<DocumentFile> imageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnChooseFolder = findViewById(R.id.btnChooseFolder);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);
        gridView = findViewById(R.id.gridView);

        imageFiles = new ArrayList<>();
        adapter = new ImageAdapter(this, imageFiles);
        gridView.setAdapter(adapter);

        // a) Configure permissions: Request Camera permission when taking a photo
        btnTakePhoto.setOnClickListener(v -> {
            if (selectedFolderUri == null) {
                Toast.makeText(this, "Please choose a folder first to save images!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });

        // b) Choose a folder using Android's Storage Access Framework
        btnChooseFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_PICK_FOLDER);
        });

        // c) Click an image to open Image Details Page
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            DocumentFile file = imageFiles.get(position);
            Intent intent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            intent.putExtra("uri", file.getUri().toString());
            intent.putExtra("name", file.getName());
            intent.putExtra("size", file.length());
            intent.putExtra("date", file.lastModified());
            startActivity(intent);
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_FOLDER && data != null) {
                selectedFolderUri = data.getData();
                // Persist permissions so we can keep accessing this folder
                getContentResolver().takePersistableUriPermission(selectedFolderUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                loadImagesFromFolder();
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                // a) Take photos... and save it to a chosen folder
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data"); // Gets the camera thumbnail
                saveImageToFolder(imageBitmap);
            }
        }
    }

    private void loadImagesFromFolder() {
        imageFiles.clear();
        if (selectedFolderUri != null) {
            DocumentFile folder = DocumentFile.fromTreeUri(this, selectedFolderUri);
            if (folder != null && folder.isDirectory()) {
                for (DocumentFile file : folder.listFiles()) {
                    // Filter only image files
                    if (file.getType() != null && file.getType().startsWith("image/")) {
                        imageFiles.add(file);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void saveImageToFolder(Bitmap bitmap) {
        if (selectedFolderUri != null) {
            DocumentFile folder = DocumentFile.fromTreeUri(this, selectedFolderUri);
            String filename = "IMG_" + System.currentTimeMillis() + ".jpg";

            // Create the file in the selected directory
            DocumentFile newImage = folder.createFile("image/jpeg", filename);
            if (newImage != null) {
                try {
                    OutputStream out = getContentResolver().openOutputStream(newImage.getUri());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    Toast.makeText(this, "Image Saved to Folder!", Toast.LENGTH_SHORT).show();
                    loadImagesFromFolder(); // Refresh the grid
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh grid when coming back from Image Details (in case an image was deleted)
        if (selectedFolderUri != null) {
            loadImagesFromFolder();
        }
    }
}