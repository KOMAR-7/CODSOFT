package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class notification_view extends AppCompatActivity {

    TextView textViewTitle, textViewMessage;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewMessage = findViewById(R.id.textViewMessage);
        imageView = findViewById(R.id.imageView);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");
            String imageUriString = intent.getStringExtra("imageUri");

            // Set title and message
            textViewTitle.setText(title);
            textViewMessage.setText(message);

            // Set image if available
            if (imageUriString != null && !imageUriString.isEmpty()) {
                Uri imageUri = Uri.parse(imageUriString);
                imageView.setImageURI(imageUri);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Adjust image scaling
            }
        }
    }
}