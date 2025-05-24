package com.vsnt.user.payload;

public class Links {
    private String title;
    private String url;
    public Links(Links old)
    {
        this.title = old.title;
        this.url = old.url;
    }
}
