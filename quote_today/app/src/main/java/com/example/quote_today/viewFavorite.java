package com.example.quote_today;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class viewFavorite extends AppCompatActivity {

    private AppDatabase appDatabase;
    private List<FavoriteItem> favoriteItemList;
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite);


        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "quote_database").build();

        long userId = getUserIdFromIntent();
//        Toast.makeText(this, String.valueOf(userId), Toast.LENGTH_SHORT).show();

        // Populate your favoriteItemList with data
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FavoriteAdapter adapter = new FavoriteAdapter(getYourFavoriteItemList(userId), this, userId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // Example method to populate your list
    private List<FavoriteItem> getYourFavoriteItemList(long userId) {
        final List<FavoriteItem> itemList = new ArrayList<>();

        // Execute database query on a background thread
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<FavoriteQuote> favoriteQuotes = appDatabase.favoriteQuoteDao().getFavoriteQuotesByUserId(userId);

                for (FavoriteQuote favoriteQuote : favoriteQuotes) {
                    FavoriteItem favoriteItem = new FavoriteItem(favoriteQuote.getQuoteText(), favoriteQuote.getQuoteAuthor());
                    itemList.add(favoriteItem);
                }

                // Update UI with the retrieved items on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUIWithFavoriteItems(itemList);
                    }
                });
            }
        });

        return itemList; // Note: This will be an empty list until the background thread completes
    }

    private void updateUIWithFavoriteItems(List<FavoriteItem> itemList) {
        // Update your UI with the retrieved favorite items
        // For example, update your RecyclerView adapter with the new items
        // recyclerViewAdapter.setData(itemList);
    }


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
}