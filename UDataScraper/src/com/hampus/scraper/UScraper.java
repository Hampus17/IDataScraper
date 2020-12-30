package com.hampus.scraper;

import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.touch.WaitOptions;
import org.apache.tools.ant.taskdefs.Echo;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
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
    private String[] navSearch = {"//android.widget.Button[@content-desc=\"Search and Explore\"]", "XPath"};
    private String[] inputSearch = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.EditText", "XPath"};
    private String[] searchTagButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout[3]", "XPath"};
    private String[] recentPostsButton = {"//android.widget.TextView[@content-desc=\"Recent\"]", "XPath"};
    private String[] postUsername = {"com.instagram.android:id/row_feed_photo_profile_name", "Id"};
    private String[] feedUsernames = {"com.instagram.android:id/row_feed_photo_profile_name", "Id"};
    private String[] emailButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.view.ViewGroup/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.Button[3]", "XPath"};
    private String[] contactButton = {"/hierarchy/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.widget.FrameLayout/android.widget.FrameLayout[2]/android.widget.FrameLayout[1]/android.widget.FrameLayout[1]/android.view.ViewGroup/android.widget.LinearLayout/android.widget.LinearLayout/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout/android.widget.Button[3]", "XPath"};
    private String[] contactNestedOption = {"com.instagram.android:id/contact_option_sub_text", "Id"};
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

    // TODO after some time create copy of the updating data file from scraper incase error would occur
    public void backupUserData() {

    }

    public void exportUserCredentials(String hashtag, String calltag, int followers, int following, boolean is_business, String email, String number) {

        System.out.println("[Info] Exporting credentials... ");
        try (FileWriter writer = new FileWriter(("exported_hashtag.csv"), true)) {

            StringBuilder sb = new StringBuilder();
            sb.append('\n');

            hashtag = hashtag.replaceAll("\\s+","");
            hashtag = hashtag.replace("#", "");

            sb.append(hashtag);
            sb.append(',');
            sb.append(calltag.replaceAll("\\s+",""));
            sb.append(',');
            sb.append(email.replaceAll("\\s+",""));
            sb.append(',');
            if (!number.equals("0"))
                sb.append(number.replaceAll("\\s+",""));
            else
                sb.append("N/A");
            sb.append(',');
            sb.append(followers);
            sb.append(',');
            sb.append(following);
            sb.append(',');
            sb.append(is_business);

            writer.write(sb.toString());

            System.out.println("[Info] Finished exporting credentials");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void scrollFeed() {
        System.out.println("\n[Action] Scrolling feed...");
        (new TouchAction(driver)).press(PointOption.point(536, 1875))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                .moveTo(PointOption.point(536, 1336))
                .release()
                .perform();
    }

    public void emailAndNumberSearch(String prefix, String h_tag, String countryCode, String numberBeginning, int max_query, int query_rate, int max_followers, int min_followers,
                                     int min_posts, int random_tick_speed, boolean recent_posts) throws InterruptedException, ParseException, IOException {
        int column = 1;
        int row = 1;
        int postCount = 0, following = 0, followers = 0;

        long startTime = System.nanoTime();
        long lastUpdateTime = System.nanoTime();
        String userEmail = "", userBioEmail = "", calltag = "", userNumber = "";
        String feedCurrentUsername = "";

        boolean SCRAPE_STOP = false;
        boolean firstRun = true;
        boolean resetRun = false;
        boolean userEmailFound = false, bioEmailFound = false, contactFound = false, emailFound = false, numberFound = false;
        boolean businessAcc = false;

        // Begin by pressing the search button
        while (!SCRAPE_STOP) {
            // set the clipboard to the hashtag, and paste it
            if (firstRun || resetRun) {
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
                getElement(recentPostsButton).click(); // Switch to recent posts instead of being on the top tab

                Thread.sleep(2000);
                var postsUnderHashtag = driver.findElementsById("com.instagram.android:id/image_button");
                MobileElement post = (MobileElement) postsUnderHashtag.get(0);
                post.click(); // Click the first post under the hashtag

            }

            // Decide which picture to click on
            while (postCount < max_query) {
                emailFound = false;
                numberFound = false;
                bioEmailFound = false;
                contactFound = false;
                businessAcc = false;

                Thread.sleep(1000);
                if (firstRun)
                    firstRun = false;
                else if (!firstRun)
                    scrollFeed();

                List<MobileElement> feedUsernames = (List<MobileElement>) driver.findElementsById("com.instagram.android:id/row_feed_photo_profile_name");
                Thread.sleep(500);
                System.out.flush();
                feedUsernames.forEach((name) -> System.out.printf("[Info] User (%s/%s) in feed: %s\n", feedUsernames.indexOf(name) + 1, feedUsernames.size(), name.getText()));

                if (feedUsernames.size() == 1) {
                    if (feedCurrentUsername.contains(feedUsernames.get(0).getText())) {
                        scrollFeed();
                        continue;
                    } else {
                        feedCurrentUsername = feedUsernames.get(0).getText();
                        feedUsernames.get(0).click();
                    }
                } else if (feedUsernames.isEmpty()) {
                    scrollFeed();
                    continue;
                } else if (feedUsernames.size() > 1) {
                    if (feedCurrentUsername.isEmpty()) {
                        feedCurrentUsername = feedUsernames.get(0).getText();
                        feedUsernames.get(0).click();
                    } else if (feedUsernames.get(0).getText().contains(feedCurrentUsername)) {
                        feedCurrentUsername = feedUsernames.get(1).getText();
                        feedUsernames.get(1).click();
                    } else {
                        feedCurrentUsername = feedUsernames.get(0).getText();
                        feedUsernames.get(0).click();
                    }
                }

                Thread.sleep(500);
                calltag = getElement(userCalltag).getText();

                System.out.printf("\n[Info] Viewing user: %s\n", calltag);

                followers = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowers).getText()).intValue();
                following = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowing).getText()).intValue();

                Thread.sleep(500);
                MobileElement bio = null;
                try {
                    bio = getElement(userBio);

                    System.out.println("[Info] Bio found");
                    if (bio.getText().contains("… more") || bio.getText().contains("... more")) {
                        TouchAction bioAction = new TouchAction(driver);
                        System.out.printf("[Action] Expanding bio\n");
                        bioAction.press(PointOption.point(bio.getLocation().x, bio.getLocation().y));
                        bioAction.release().perform();
                    }

                    Thread.sleep(500);
                    Matcher matcher = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}").matcher(bio.getText());
                    while (matcher.find()) {
                        userBioEmail = matcher.group();
                        bioEmailFound = true;
                    }

                    if (bioEmailFound)
                        System.out.printf("[Success] Email in bio found %s\n", userBioEmail);

                } catch (Exception e) {
                    System.out.println("[Warning] No bio found");
                }

                Thread.sleep(500);
                try {
                    MobileElement contactButton = null;
                    Thread.sleep(500);
                    List<MobileElement> widgetButtons = driver.findElementsByClassName("android.widget.Button");
                    for (MobileElement widget : widgetButtons ) {
                        if (widget.getText().contains("Contact"))
                            contactButton = widget;
                        else {
                            contactFound = false;
                            continue;
                        }
                    }

                    contactButton.click();
                    contactFound = true;
                    Thread.sleep(500);
                    try {
                        List<MobileElement> nestedOptions = driver.findElementsById(contactNestedOption[0]);

                        for (MobileElement option : nestedOptions) {
                            if (option.getText().contains("@")) {
                                emailFound = true;
                                businessAcc = true;
                                userEmail = option.getText();
                                System.out.printf("[Success] Email found in contact (%s): %s\n", calltag, userEmail);
                            }

                            if (option.getText().startsWith(countryCode) || option.getText().startsWith(numberBeginning)) {
                                numberFound = true;
                                businessAcc = true;
                                userNumber = option.getText();
                                System.out.printf("[Success] Number found in contact (%s): %s\n", calltag, userNumber);
                            } else if (!option.getText().startsWith(countryCode) || !option.getText().startsWith(numberBeginning))
                                System.out.println("[Warning] Phone number doesn't match desired country code, skipping");
                        }
                    } catch (Exception e) {
                        emailFound = false;
                    }

                    Thread.sleep(1000);
                    driver.navigate().back();
                } catch (Exception e) {
                    contactFound = false;
                }

                if (!contactFound) {
                    try {
                        MobileElement eButton = null;
                        Thread.sleep(500);
                        List<MobileElement> widgetButtons = driver.findElementsByClassName("android.widget.Button");
                        for (MobileElement widget : widgetButtons ) {
                            if (widget.getText().contains("Email"))
                                eButton = widget;
                            else {
                                emailFound = false;
                                continue;
                            }
                        }

                        eButton.click();
                        Thread.sleep(1000);
                        String tempPageEmail = getElement(androidEmail).getText();

                        // Move down to export thingy
                        if (tempPageEmail.toLowerCase().contains("support") || tempPageEmail.toLowerCase().contains("help") || tempPageEmail.toLowerCase().contains("hello")) {
                            System.out.println("[Warning] Email is support email, skipping");
                            emailFound = false;
                        } else {
                            userEmail = tempPageEmail.replace("<", "");
                            userEmail = userEmail.replace(">", "");
                            emailFound = true;
                            businessAcc = true;

                            System.out.printf("[Success] Email found in contact (%s): %s\n", calltag, userEmail);
                        }

                        Thread.sleep(1000);
                        driver.hideKeyboard();
                        driver.navigate().back();
                    } catch (Exception e) {
                        emailFound = false;
                    }
                }

                if (numberFound || bioEmailFound || emailFound) {
                    String tmpEmail = "N/A";
                    if (!userEmail.isEmpty())
                        tmpEmail = userEmail;
                    else if (!userBioEmail.isEmpty()) {
                        if (userEmail.isEmpty())
                            tmpEmail = userBioEmail;
                        else
                            tmpEmail = userEmail;
                    }
                    exportUserCredentials(h_tag, calltag, followers, following, businessAcc, tmpEmail, userNumber.isEmpty() ? "N/A" : userNumber);
                    Thread.sleep(1000);
                } else {
                    System.out.println("[Warning] Neither number or email found");
                }


                Thread.sleep(2000);
                driver.navigate().back();
                postCount++;
            }

            // Max Query Reached
            System.out.println("[Info] Bot stopped, max query reached");
            SCRAPE_STOP = true;
        }

        // resetScraper(); here we can check if to continue or close

        // every time loops runs, check if should reset column and row counter (like every 2 hours maybe)
        // Also logic around how often to randomize certain times depending on random tick speed (higher risk of slow interactions)
        // Maybe like do a random number 0 - random tick speed and add to the intervals between clicks

    }

    public static void main(String args[]) throws IOException, InterruptedException, ParseException {

        System.out.println("### Program Started ###\n");

        // Variables
        UScraper scraper = new UScraper(true);

        int searchMode, maxSearches, waitInterval, resetInterval, randomTickSens;
        String hashtag, username, fileLocation;
        boolean recentPosts;

        if (args.length > 0) {
            if (args[0].equals("--help") || args[0].equals("help") || args[0].equals("-help") || args[0].equals("-h")) {
                System.out.println("Help info");
                return;
            }

            // Parse first input
            try {
                searchMode = parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("[Warning] Invalid parameter " + e);
                System.out.println("Help info");
                return;
            }

            switch (searchMode) {
                case 1:
                    System.out.println("[Info] Search Mode: Hashtag");
                    // scraper.emailAndNumberSearch();
                    break;
                default:
                    System.out.println("[Warning] Invalid mode");
                    break;
            }
        } else {
            // System.out.println("Help menu");
        }

        Thread.sleep(4000);
        // scraper.testActions("scroll");
        scraper.emailAndNumberSearch("A", "kök", "+46", "07", 1000, 2, 20000, 100, 3, 6, true);
        // scraper.appExit();
    }
}