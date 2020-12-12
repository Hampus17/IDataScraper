package com.hampus.scraper;

import io.appium.java_client.android.nativekey.AndroidKey;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;

import static java.lang.Integer.parseInt;

public class UScraper {

    // Universal elements
    private String[] privateContainer = {"com.instagram.android:id/row_profile_header_empty_profile_notice_title", "Id"};
    private String[] navSearch = {"//android.widget.Button[@content-desc=\"Search and Explore\"]", "XPath"};
    private String[] inputSearch = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.EditText", "XPath"};
    private String[] searchTagButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout[3]", "XPath"};
    private String[] emailButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.view.ViewGroup/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.Button[3]", "XPath"};
    private String[] callButton = {""};
    private String[] contactButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.ViewGroup/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.Button[3]", "XPath"};
    private String[] contactNestedCall = {"\"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[1]/android.widget.TextView[2]", "XPath"};
    private String[] contactNestedEmail = {"\"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[2]/android.widget.TextView[2]", "XPath"};
    private String[] userFullname = {"com.instagram.android:id/profile_header_full_name", "Id"};
    private String[] userBio = {"com.instagram.android:id/profile_header_bio_text", "Id"};
    private String[] userCalltag = {"com.instagram.android:id/action_bar_title", "Id"};
    private String[] userFollowers = {"com.instagram.android:id/row_profile_header_textview_followers_count", "Id"};
    private String[] userFollowing = {"com.instagram.android:id/row_profile_header_textview_following_count", "Id"};
    private String[] businessCategory = {"com.instagram.android:id/profile_header_business_category", "Id"};

    private AndroidDriver driver;

    private int RESET_SCRAPER_TIME = 7200; // 2 hours

    public UScraper(boolean debug) throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("deviceName", "emulator-5554");
        desiredCapabilities.setCapability("platformName", "android");
        desiredCapabilities.setCapability("appPackage", "com.instagram.android");
        desiredCapabilities.setCapability("appActivity", ".activity.MainTabActivity");
        desiredCapabilities.setCapability("noReset", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    private MobileElement getElement(String[] elementObject) {

        MobileElement element = null;

        if (elementObject[1].equals("XPath")) {
            element = (MobileElement) driver.findElementByXPath(elementObject[0]);
        } else if (elementObject[1].equals("Id")) {
            element = (MobileElement) driver.findElementById(elementObject[0]);
        } else {
            System.out.println("[Error] Invalid element specifier.");
            return element;
        }

        return element;
    }

    public void appExit() {
        System.out.println("### BOT STOPPED ###");
        driver.quit();
    }

    // TODO after some time create copy of the updating data file from scraper
    public void backupUserData() {

    }

    // TODO
    public void userSearch(String username) {

    }

    public void emailAndNumberSearch(String h_tag, int max_query, int query_rate, int max_followers, int min_followers,
                                     int min_posts, int random_tick_speed, boolean recent_posts) throws InterruptedException {
        boolean SCRAPE_STOP = false;
        int column = 1;
        int row = 1;
        long startTime = System.nanoTime();
        long lastUpdateTime = System.nanoTime();

        boolean firstRun = true;

        // Scrape loop
        while (RESET_SCRAPER_TIME > (startTime - lastUpdateTime) && !SCRAPE_STOP) {
            lastUpdateTime = System.nanoTime();
            TimeUnit.SECONDS.sleep(2);

            // Begin by pressing the search button

            // set the clipboard to the hashtag, and paste it
            if (firstRun) {
                firstRun = false;
                getElement(navSearch).click();
                TimeUnit.SECONDS.sleep(2);

                getElement(inputSearch).click();
                driver.hideKeyboard();

                TimeUnit.SECONDS.sleep(2);
                getElement(searchTagButton).click();

                if (h_tag.contains("#"))
                    getElement(inputSearch).sendKeys(h_tag);
                else
                    getElement(inputSearch).sendKeys("#" + h_tag);

                var searchResults = driver.findElementsByClassName("android.widget.TextView");
                for (var el : searchResults)
                    if (((MobileElement) el).getText().equals("#" + h_tag) || ((MobileElement) el).getText().equals(h_tag))
                        ((MobileElement) el).click();

            }
        }

        // resetScraper(); here we can check if to continue or close

        // logic to decide which hashtag to click

        // every time loops runs, check if should reset column and row counter (like every 2 hours maybe)
        // Also logic around how often to randomize certain times depending on random tick speed (higher risk of slow interactions)
        // Maybe like do a random number 0 - random tick speed and add to the intervals between clicks


        // first check if profile is private

        // If email or number found (in either bio or button) (check if bio includes a more button (" ... more "), or just press bio everytime)
        // IUser user = new IUser(); (in the end)
        // Determine if business profile (best of our abilities)

        // check if their profile has either a contact, email or call button
        // also check

        // In the end export the credentials to a csv file
        // user.exportCredentials();



        //column and row
        // 1 row has 3 columns
        // so we can iterate 3 times then increase the row value and reset column value to 1 again
        //
        // if first post
    }

    public static void main(String args[]) throws MalformedURLException, InterruptedException {

        System.out.println("### BOT RUNNING... ###");

        // Variables
        UScraper scraper = new UScraper(true);

        int searchMode, maxSearches, waitInterval, resetInterval, randomTickSens;
        String hashtag, username, fileLocation;
        boolean recentPosts;

        if (args.length > 0) {
            if (args[0].equals("--help") || args[0].equals("help") || args[0].equals("-help")) {
                System.out.println("Help info");
                return;
            }

            // Parse first input
            try {
                searchMode = parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("[Error] " + e);
                return;
            }

            switch (searchMode) {
                case 1:
                    System.out.println("[Info] Search Mode: Hashtag");

                    break;
                default:
                    System.out.println("[Warning] Invalid mode");
                    break;
            }
        }

        scraper.emailAndNumberSearch("camping", 1000, 2, 20000, 100, 3, 6, true);

    }
}