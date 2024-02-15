package com.example.quote_today;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteQuoteDao {


    @Query("SELECT * FROM favoriteQuotes WHERE userId = :userId")
    List<FavoriteQuote> getFavoriteQuotesByUserId(long userId);

    @Query("SELECT * FROM favoriteQuotes WHERE userId = :userId AND quote_text = :quoteText AND quote_author = :quoteAuthor")
    FavoriteQuote isQuoteLikedByUser(long userId, String quoteText, String quoteAuthor);

    @Query("SELECT * FROM favoriteQuotes WHERE userId = :userId AND quote_text = :quoteText AND quote_author = :quoteAuthor LIMIT 1")
    FavoriteQuote getFavoriteQuote(long userId, String quoteText, String quoteAuthor);



    @Query("DELETE FROM favoriteQuotes WHERE userId = :userId AND quote_text = :quoteText AND quote_author = :quoteAuthor")
    void deleteFavoriteQuote(long userId, String quoteText, String quoteAuthor);

    @Query("INSERT INTO favoriteQuotes (userId, quote_text, quote_author) VALUES (:userId, :quoteText, :quoteAuthor)")
    void insertFavouriteQuote(long userId, String quoteText, String quoteAuthor);


}
