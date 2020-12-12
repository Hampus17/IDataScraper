package com.hampus.scraper;

public class IUser {
    private String ID, calltag, name;
    private boolean is_private, is_verified, is_business, bio_email, bio_number;
    private int followers, following;

    public IUser(String ID, String calltag, String name, int followers, int following,
                 boolean is_private, boolean is_verified, boolean is_business, boolean bio_email, boolean bio_number)
    {
        this.ID = ID;
        this.calltag = calltag;

    }

    public void exportCredentials() {

    }

}
