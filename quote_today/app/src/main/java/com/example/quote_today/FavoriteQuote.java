package com.example.quote_today;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// FavoriteQuote.java
@Entity(
        tableName = "favoriteQuotes",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        )
)
public class FavoriteQuote {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "fqId")
    private long fqId;

    @ColumnInfo(name = "userId")
    private long userId;

    @ColumnInfo(name = "quote_text")
    private String quoteText;

    @ColumnInfo(name = "quote_author")
    private String quoteAuthor;

    // Constructors, getters, and setters if needed...
    @NonNull
    public long getFqId() {
        return fqId;
    }

    public long getUserId() {
        return userId;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public String getQuoteAuthor() {
        return quoteAuthor;
    }

    // Setter methods
    public void setFqId(long fqId) {
        this.fqId = fqId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public void setQuoteAuthor(String quoteAuthor) {
        this.quoteAuthor = quoteAuthor;
    }
}
