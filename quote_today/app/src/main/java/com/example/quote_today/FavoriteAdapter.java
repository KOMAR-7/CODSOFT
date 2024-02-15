package com.example.quote_today;

import static android.content.Intent.getIntent;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;
import java.util.concurrent.Executors;

// FavoriteAdapter.java
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<FavoriteItem> favoriteList;
    private AppDatabase appDatabase;
    private Context context;
    private long userId; // Add userId field


    public FavoriteAdapter(List<FavoriteItem> favoriteList, Context context, long userId) {
        this.favoriteList = favoriteList;
        this.context = context;
        this.userId = userId;
        this.appDatabase = Room.databaseBuilder(context, AppDatabase.class, "quote_database").build();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView favText;
        public TextView favAuthor;

        public ImageView removeFavBtn;
        public ViewHolder(View view) {
            super(view);
            favText = view.findViewById(R.id.favText);
            favAuthor = view.findViewById(R.id.favAuthor);
            removeFavBtn = view.findViewById(R.id.removeFavBtn);
            appDatabase = Room.databaseBuilder(view.getContext(), AppDatabase.class, "quote_database").build();

        }
    }



    public FavoriteAdapter(List<FavoriteItem> favoriteList,long userId) {
        this.favoriteList = favoriteList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem favoriteItem = favoriteList.get(position);
        holder.favText.setText("\""+favoriteItem.getQuoteText()+"\"");
        holder.favAuthor.setText("-"+favoriteItem.getQuoteAuthor());


//        Toast.makeText(context, String.valueOf(userId), Toast.LENGTH_SHORT).show();

        // Set an OnClickListener for the button
        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click (e.g., remove the item from favorites)
                deleteFavouriteQuoteAsync(userId, favoriteItem.getQuoteText(), favoriteItem.getQuoteAuthor(), holder);
//                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    // Method to remove an item from favorites
    private void deleteFavouriteQuoteAsync(long userId, String quoteText, String quoteAuthor, ViewHolder holder) {
        // Get the application context before running the background thread
        Context context = holder.itemView.getContext();

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

                        // Update the dataset in the UI thread
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Remove the item from the dataset
                                removeItemFromFavorites(holder.getAdapterPosition());
                            }
                        });
                    } else {
                        // Handle the case where the value couldn't be deleted
                        showToast("Error: Unable to delete the favorite quote.");
                    }

                } catch (Exception e) {
                    // Handle any exceptions that occurred during the database deletion
                    showToast(String.valueOf(e));
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to remove an item from favorites
    private void removeItemFromFavorites(int position) {
        // Implement the logic to remove the item from favorites based on the position
        favoriteList.remove(position);
        // Notify the adapter that the data set has changed after removal
        notifyDataSetChanged();
    }




    // Function to show a Toast message on the UI thread
    // Function to show a Toast message on the UI thread
    private void showToast(final String message) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}

