package com.example.quote_today;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String currentDisplayedText;
    private String currentDisplayedAuthor;
    private AppDatabase appDatabase;
    private TextView quoteText, quoteAuthor;
    private ImageView shareBtn;
    private ImageView likeBtn;
    private int currentDay;
    private Button logoutBtn,viewFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "quote_database").build();

        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//        Toast.makeText(this, String.valueOf(currentDay), Toast.LENGTH_SHORT).show();

        logoutBtn = findViewById(R.id.logoutBtn);
        likeBtn = findViewById(R.id.likeBtn);
        shareBtn = findViewById(R.id.shareBtn);
        viewFavorite = findViewById(R.id.viewFavourite);

        quoteText = findViewById(R.id.quoteText);
        quoteAuthor = findViewById(R.id.quoteAuthor);

        // Set a listener to handle state changes


        // Check for internet connection before fetching quotes
        if (isConnectedToInternet()) {
            // Replace with your API endpoint
            String apiUrl = "https://type.fit/api/quotes";

            // Execute AsyncTask to fetch quotes from the API
            new FetchQuotesTask().execute(apiUrl);
        } else {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show();
        }

        // Check if the quoteText, quoteAuthor, and userId combination exists in the database asynchronously
        long userId = getUserIdFromIntent();

//        Toast.makeText(this, String.valueOf(userId), Toast.LENGTH_SHORT).show();


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                toggleLikeState(userId); // You can define this method to change the appearance or perform an action
            }
        });

        viewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, viewFavorite.class);
                    intent.putExtra("USER_ID_EXTRA", userId);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                }
            }
        });


        shareBtn.setOnClickListener(v ->shareQuote(v));





        logoutBtn.setOnClickListener(v->{
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
            finish();
        });
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Handle UI mode changes if needed
        if ((newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Night mode is active
            // Apply specific changes for dark mode
        } else {
            // Day mode is active
            // Apply specific changes for light mode
        }
    }
    private boolean isConnectedToInternet() {
        // Add your logic to check for internet connectivity
        // Return true if connected, false otherwise
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        long userId = getUserIdFromIntent();
        // Check if the quote is in the database when the activity starts
        isQuoteInDatabaseAsync(currentDisplayedText, currentDisplayedAuthor, userId);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Your onResume logic here
        long userId = getUserIdFromIntent();
        // Check if the quote is in the database when the activity starts
        isQuoteInDatabaseAsync(currentDisplayedText, currentDisplayedAuthor, userId);
    }

    private class FetchQuotesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            try {
                // Create URL object
                URL url = new URL(apiUrl);

                // Open HTTP connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return response.toString();
                } finally {
                    // Disconnect after reading the response
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    // Parse the JSON array
                    JSONArray quotesArray = new JSONArray(result);

                    if (quotesArray.length() > 0) {
                        // Get a random index to display a random quote
                        Random random = new Random();
                        int randomIndex = random.nextInt(quotesArray.length());

                        // Display the random quote
                        JSONObject randomQuote = quotesArray.getJSONObject(randomIndex);

                        if (randomQuote.has("text") && randomQuote.has("author")) {
                            String text = randomQuote.getString("text");
                            String author = randomQuote.getString("author");

                            displayQuote(text, author);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void displayQuote(String text, String author) {
        // Display the quote
        currentDisplayedText = text;
        currentDisplayedAuthor = author.replace(", type.fit", "");
        long userId = getUserIdFromIntent();
        isQuoteInDatabaseAsync(currentDisplayedText, currentDisplayedAuthor, userId);
        author = author.replace(", type.fit", "");
        quoteText.setText("\"" + currentDisplayedText + "\"");
        quoteAuthor.setText("- " + currentDisplayedAuthor);
    }
    public void shareQuote(View view) {
        String text = currentDisplayedText;
        String author = currentDisplayedAuthor;

        String message = "Today's Quote: " + text + " \nAuthor: " + author+"\nFrom Quote App \nBy -Khan Omar";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

//    Like quote


    private long getUserIdFromIntent() {
//        Intent i = getIntent();
//        long userId = i.getLongExtra("USER_ID_EXTRA", 0L);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_ID_EXTRA")) {
            // Parse the string to long if it's a valid long value
            long userIdString = intent.getLongExtra("USER_ID_EXTRA",0L);
            try {
                return userIdString;
            } catch (NumberFormatException e) {
                // Handle the case when userId is not a valid long
                e.printStackTrace();
            }
        }

        // Handle the case when userId is not available or parsing fails
        return 0L; // or any other default value
    }


    private void isQuoteInDatabaseAsync(String quoteText, String quoteAuthor, long userId) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Query the database in the background
                    final FavoriteQuote favoriteQuote = appDatabase.favoriteQuoteDao().getFavoriteQuote(userId, quoteText, quoteAuthor);

                    Log.d("DatabaseQuery", "Favorite quote found: " + (favoriteQuote != null));

                    // Update UI on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleDatabaseResult(favoriteQuote);
                        }
                    });
                } catch (Exception e) {
                    // Handle any exceptions that occurred during the database query
                    Log.e("DatabaseQuery", "Exception: " + e.getMessage(), e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Error querying database", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void handleDatabaseResult(FavoriteQuote favoriteQuote) {
        if (favoriteQuote != null) {
            // Quote exists, update UI accordingly
            likeBtn.setImageResource(R.drawable.colorheart);
            likeBtn.setTag(true);
        } else {
            // Quote does not exist, update UI accordingly
            likeBtn.setImageResource(R.drawable.unlike);
            likeBtn.setTag(false);
        }
    }
//Like quote
    private void toggleLikeState(long userId) {
    // Change the image resource based on the current state
    if (likeBtn.getTag() == null || (boolean) likeBtn.getTag()) {
        likeBtn.setImageResource(R.drawable.unlike);
        likeBtn.setTag(false);
        deleteFavouriteQuoteAsync(userId, currentDisplayedText,currentDisplayedAuthor);
        // Perform any other actions you want when the button is unliked
    } else {
        likeBtn.setImageResource(R.drawable.colorheart);
        likeBtn.setTag(true);
        insertFavoriteQuoteAsync(userId, currentDisplayedText,currentDisplayedAuthor);
        // Perform any other actions you want when the button is liked
    }
}

    private void insertFavoriteQuoteAsync(long userId, String quoteText, String quoteAuthor) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Insert the favorite quote into the database
                    appDatabase.favoriteQuoteDao().insertFavouriteQuote(userId, quoteText, quoteAuthor);

                    // Retrieve the inserted quote (optional)
                    FavoriteQuote insertedQuote = appDatabase.favoriteQuoteDao().getFavoriteQuote(userId, quoteText, quoteAuthor);

                    if (insertedQuote != null) {
                        // The value is inserted successfully, you can handle this as needed
//                        showToast("Favorite quote inserted. ID: " + insertedQuote.getFqId());
                        showToast("Favourite quote added!!!");
                    } else {
                        // Handle the case where the value couldn't be retrieved
                        showToast("Error: Unable to retrieve inserted value.");
                    }
                } catch (Exception e) {
                    // Handle any exceptions that occurred during the database insertion
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }


    private void deleteFavouriteQuoteAsync(long userId, String quoteText, String quoteAuthor) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Delete the favorite quote from the database
                    appDatabase.favoriteQuoteDao().deleteFavoriteQuote(userId, quoteText, quoteAuthor);

                    // Check if the deletion was successful by attempting to retrieve the deleted quote
                    FavoriteQuote deletedQuote = appDatabase.favoriteQuoteDao().getFavoriteQuote(userId, quoteText, quoteAuthor);

                    if (deletedQuote == null) {
                        // The value is deleted successfully, you can handle this as needed
                        showToast("Favorite quote deleted.");
                    } else {
                        // Handle the case where the value couldn't be deleted
                        showToast("Error: Unable to delete the favorite quote.");
                    }

                } catch (Exception e) {
                    // Handle any exceptions that occurred during the database deletion
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }


    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}