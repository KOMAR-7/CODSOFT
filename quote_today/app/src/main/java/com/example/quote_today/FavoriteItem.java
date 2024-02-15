package com.example.quote_today;

// FavoriteItem.java
public class FavoriteItem {
    private String quoteText;
    private String quoteAuthor;

    public FavoriteItem(String quoteText, String quoteAuthor) {
        this.quoteText = quoteText;
        this.quoteAuthor = quoteAuthor;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public String getQuoteAuthor() {
        return quoteAuthor;
    }
}
