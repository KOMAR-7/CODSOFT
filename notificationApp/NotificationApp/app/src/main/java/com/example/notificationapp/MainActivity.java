package com.example.notificationapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private Button pickImageButton;
    private Bitmap notificationImage;
    private Uri notificationImageUri;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize notification manager
        notificationManager = NotificationManagerCompat.from(this);

        // Find views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextMessage = findViewById(R.id.editTextMessage);
        pickImageButton = findViewById(R.id.pickImageButton);
        imageView = findViewById(R.id.imageSelected);

        // Set click listener for pick image button
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Find the create notification button
        Button createNoti = findViewById(R.id.createNoti);

        // Set a click listener for the create notification button
        createNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    showNotification();
                } else {
                    requestPermission();
                }
            }
        });

        // Create notification channel
        createNotificationChannel();
    }

    // Method to check if permission is granted
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
    }

    // Method to request permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, PERMISSION_REQUEST_CODE);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show notification
                showNotification();
            } else {
                // Permission denied, show a toast message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to open image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                notificationImageUri = imageUri;
                // Convert URI to Bitmap
                notificationImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Handle selected image
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();

                // Update ImageView with selected image

                imageView.setImageBitmap(notificationImage);
                pickImageButton.setText("Reselect Image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Method to create and display the notification
    private void showNotification() {
        // Check if the VIBRATE permission is granted
        if (checkPermission()) {
            // Create intent for the activity to open when notification is clicked
            Intent intent = new Intent(this,notification_view.class);

            // Add title, message, and image URI as extras to the intent
            intent.putExtra("title", editTextTitle.getText().toString());
            intent.putExtra("message", editTextMessage.getText().toString());
            if (notificationImageUri != null) {
                intent.putExtra("imageUri", notificationImageUri.toString());
            }


            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            // Build notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(editTextTitle.getText().toString())
                    .setContentText(editTextMessage.getText().toString())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            // Add image to notification if available
            if (notificationImage != null) {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notificationImage).bigLargeIcon(null));
            }

            // Display notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());

            editTextMessage.setText("");
            editTextTitle.setText("");
            pickImageButton.setText("Select Image");
            imageView.setImageBitmap(null);

        } else {
            // Request permission if it's not granted
            requestPermission();
        }
    }

    // Method to create notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel";
            String channelDescription = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
