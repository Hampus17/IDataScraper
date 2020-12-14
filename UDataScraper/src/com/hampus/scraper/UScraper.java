package com.hampus.scraper;

import io.appium.java_client.android.nativekey.AndroidKey;
import org.apache.tools.ant.taskdefs.Echo;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String[] recentPostsButton = {"//android.widget.TextView[@content-desc=\"Recent\"]", "XPath"};
    private String[] postUsername = {"com.instagram.android:id/row_feed_photo_profile_name", "Id"};
    private String[] emailButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.view.ViewGroup/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.Button[3]", "XPath"};
    private String[] callButton = {""};
    private String[] contactButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.ViewGroup/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.Button[3]", "XPath"};
    private String[] contactNestedCall = {"\"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout/android.widget.FrameLayout/android.view.ViewGroup/android.widget.FrameLayout/androidx.recyclerview.widget.RecyclerView/android.widget.LinearLayout[1]/android.widget.TextView[2]", "XPath"};
    private String[] contactNestedEmail = {"com.instagram.android:id/contact_option_sub_text", "Id"};
    private String[] userFullname = {"com.instagram.android:id/profile_header_full_name", "Id"};
    private String[] userBio = {"com.instagram.android:id/profile_header_bio_text", "Id"};
    private String[] userCalltag = {"com.instagram.android:id/action_bar_title", "Id"};
    private String[] userFollowers = {"com.instagram.android:id/row_profile_header_textview_followers_count", "Id"};
    private String[] userFollowing = {"com.instagram.android:id/row_profile_header_textview_following_count", "Id"};
    private String[] businessCategory = {"com.instagram.android:id/profile_header_business_category", "Id"};
    private String[] androidEmail = {"com.google.android.gm:id/to", "Id"};
    private String[] androidNumber = {"com.android.dialer:id/digits", "Id"};

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

    public void exportUserCredentials(String prefix, int ID, String calltag, int followers, int following, boolean is_business, String email) {

        System.out.println("Exporting credentials");
        try (FileWriter writer = new FileWriter(("exported_hashtag.csv"), true)) {


            StringBuilder sb = new StringBuilder();
            sb.append('\n');

            sb.append(calltag);
            sb.append(',');
            sb.append(followers);
            sb.append(',');
            sb.append(email);
            sb.append(',');
            sb.append(is_business);

            writer.write(sb.toString());

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO
    public void userSearch(String username) {

    }

    public void emailAndNumberSearch(String prefix, String h_tag, int max_query, int query_rate, int max_followers, int min_followers,
                                     int min_posts, int random_tick_speed, boolean recent_posts) throws InterruptedException, ParseException {
        boolean SCRAPE_STOP = false;
        int column = 1;
        int row = 1;
        int postCount = 0, following = 0, followers = 0;
        long startTime = System.nanoTime();
        long lastUpdateTime = System.nanoTime();
        String userEmail = "", userBioEmail = "", calltag = "";

        boolean firstRun = true;
        boolean userEmailFound = false, bioEmailFound = false, contactFound = false, emailFound = false;
        boolean businessAcc = false;

        // Begin by pressing the search button
        while (!SCRAPE_STOP) {
            // set the clipboard to the hashtag, and paste it
            if (firstRun) {
                firstRun = false;
                getElement(navSearch).click();
                Thread.sleep(1000);

                getElement(inputSearch).click();
                driver.hideKeyboard();

                getElement(searchTagButton).click();

                if (h_tag.contains("#"))
                    getElement(inputSearch).sendKeys(h_tag);
                else
                    getElement(inputSearch).sendKeys("#" + h_tag);

                Thread.sleep(3000);
                var searchResults = driver.findElementsByClassName("android.widget.TextView");
                for (var el : searchResults) {
                    if (((MobileElement) el).getText().equals("#" + h_tag) || ((MobileElement) el).getText().equals(h_tag)) {
                        ((MobileElement) el).click();
                        break;
                    }
                }

                Thread.sleep(6000);
                getElement(recentPostsButton).click();
            }

            // Decide which picture to click on
            while (postCount < max_query) {
                emailFound = false;
                bioEmailFound = false;
                contactFound = false;
                businessAcc = false;

                Thread.sleep(2000);
                var postsUnderHashtag = driver.findElementsById("com.instagram.android:id/image_button");

                MobileElement post = (MobileElement) postsUnderHashtag.get(postCount);
                String postAccessID = post.getAttribute("content-desc");

                Thread.sleep(2000);
                if (row % 2 == 0) {
                    System.out.println("[Action] Scrolling...");
                    (new TouchAction(driver)).press(PointOption.point(544, 544)).moveTo(PointOption.point(544, 80)).perform();
                }


                Thread.sleep(2000);
                if (postAccessID.contains("Row " + row) && postAccessID.contains("Column " + column)) {
                    System.out.printf("\n[Action] Clicking image number %s on row %s \n", column, row);
                    post.click();
                    if (column == 3) {
                        column = 1;
                        row++;
                    } else
                        column++;

                    Thread.sleep(500);
                    getElement(postUsername).click();

                    Thread.sleep(500);
                    calltag = getElement(userCalltag).getText();

                    Thread.sleep(500);
                    followers = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowers).getText()).intValue();
                    Thread.sleep(500);
                    following = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowing).getText()).intValue();

                    Thread.sleep(2000);
                    MobileElement bio = null;
                    try {
                        if (getElement(contactButton).getText().equals("Contact")) {
                            getElement(contactButton).click();
                            contactFound = true;

                            // Fixes TODO
                            // It always click the down array (recommended accounts)
                            // Doesn't fetch the email from contact nest

                            System.out.println("In contact nest");

                            Thread.sleep(1000);
                            try {
                                MobileElement nestedEmail = getElement(contactNestedEmail);
                                System.out.println(nestedEmail);
                                emailFound = true;
                                businessAcc = true;
                                userEmail = nestedEmail.getText();
                                System.out.printf("[Info] Email found in contact (%s): %s\n", calltag, userEmail);
                            } catch (Exception e) {
                                System.out.println("Failed to go into nest");
                                emailFound = false;
                            }

                            Thread.sleep(2000);
                            driver.navigate().back();
                        } else
                            continue;
                    } catch (Exception e) {
                        System.out.println("Failed to go into contact nest");
                        contactFound = false;
                    }

                    if (!contactFound) {
                        try {
                            getElement(emailButton).click();
                            Thread.sleep(1000);
                            userEmail = getElement(androidEmail).getText();
                            userEmail = userEmail.replace("<", "");
                            userEmail = userEmail.replace(">", "");
                            emailFound = true;
                            businessAcc = true;

                            System.out.printf("[Info] Email found in contact(%s): %s\n", calltag, userEmail);

                            Thread.sleep(1000);
                            driver.hideKeyboard();
                            driver.navigate().back();
                        } catch (Exception e) {
                            emailFound = false;
                        }
                    }

                    Thread.sleep(1000);
                    try {
                        bio = getElement(userBio);

                        System.out.println("[Info] Bio found");
                        if (bio.getText().contains("… more") || bio.getText().contains("... more")) {
                            TouchAction bioAction = new TouchAction(driver);
                            System.out.printf("[Action] Pressing coordinates %s, %s\n", bio.getRect().x, bio.getRect().y);
                            bioAction.press(PointOption.point(bio.getLocation().x, bio.getLocation().y));
                            bioAction.release().perform();
                        }

                        Thread.sleep(500);
                        Pattern EMAIL_PATTERN = Pattern.compile("[_A-Za-z0-9-]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})");
                        Matcher m = EMAIL_PATTERN.matcher(bio.getText());
                        if (m.find()) {
                            String match = m.group(1);
                            userBioEmail = match;
                            emailFound = true;
                            System.out.printf("[Info] Email found in bio (%s): %s\n", calltag, userBioEmail);
                        } else {
                            System.out.println("[Warning] No email in bio found");
                        }
                    } catch (Exception e) {
                        System.out.println("[Warning] No bio found");
                    }

                    if (bioEmailFound || emailFound) {
                        exportUserCredentials(prefix, postCount, calltag, followers, following, businessAcc, userEmail.isEmpty() ? userBioEmail : userEmail);
                        Thread.sleep(1000);
                    }
                    else
                        System.out.println("[Warning] No email found");

                    // Back to hashtag posts
                    driver.navigate().back();
                    driver.navigate().back();
                    postCount++;
                }
            }

            // Max Query Reached
            System.out.println("[Info] Bot stopped, max query reached");
            SCRAPE_STOP = true;
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

    public static void main(String args[]) throws MalformedURLException, InterruptedException, ParseException {

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

        scraper.emailAndNumberSearch("A", "camping", 1000, 2, 20000, 100, 3, 6, true);

    }
}