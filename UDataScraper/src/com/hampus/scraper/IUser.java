package com.hampus.scraper;

public class IUser {
    private string ID, calltag, name;
    private bool is_private, is_verified, is_business, bio_email, bio_number;
    private int followers, following;

    public IUser(string ID, string calltag, string name, int followers, int following
                 bool is_private, bool is_verified, bool is_business, bool bio_email, bool bio_number)
    {
        this.ID = ID;
        this.calltag = calltag;


    }

    public void exportCredentials() {

    }

}
