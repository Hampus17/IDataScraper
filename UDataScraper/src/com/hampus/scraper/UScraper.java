package com.hampus.scraper;

import io.appium.java_client.touch.WaitOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;


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
    private String[] androidEmail = {"com.google.android.gm:id/to", "Id"};
    private String[] androidNumber = {"com.android.dialer:id/digits", "Id"};

    private AndroidDriver driver;

    private int RESET_SCRAPER_TIME = 7200000; // 2 hours (in milliseconds)

    private boolean SCRAPE_STOP = false;
    private boolean SCRAPE_RESET = false;


    public UScraper() throws MalformedURLException {
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
    private void backupUserData() {
        // Check for duplicates and remove one of those as well
    }

    private void exportUserCredentials(String hashtag, String calltag, int followers, int following, String email, String number, int postCount) throws IOException {

        hashtag = hashtag.replaceAll("\\s+","");
        hashtag = hashtag.replace("#", "");

        String filePath = "H:" + File.separator + "ExportedData" + File.separator;
        String fileName = hashtag + "_export.csv";
        File file = null;

        StringBuilder sb = new StringBuilder();

        File dir = new File(filePath);
        dir.mkdirs();
        file = new File(dir, fileName);
        file.createNewFile();

        String firstLine = "";

        try {
            firstLine = Files.readAllLines(Path.of(file.getPath())).get(0);
        } catch (Exception e) {
            firstLine = "FILE_EMPTY";
        }

        // Check if first line includes datas
        if (postCount == 1 && firstLine.contains("FILE_EMPTY"))
            sb.append("Username,Email,Number,Followers,Following\n");
        else if (firstLine.contains("Username,Email,Number,Followers,Following"))
            sb.append('\n');

        sb.append(calltag.replaceAll("\\s+",""));
        sb.append(',');
        sb.append(email.replaceAll("\\s+",""));
        sb.append(',');
        sb.append(number.replaceAll("\\s+",""));
        sb.append(',');
        sb.append(followers);
        sb.append(',');
        sb.append(following);

        Files.writeString(Path.of(file.getPath()), sb.toString(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    private void scrollFeed() {
        System.out.println("\n[Action] Scrolling feed...");
        (new TouchAction(driver)).press(PointOption.point(536, 1875))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                .moveTo(PointOption.point(536, 1300))
                .release()
                .perform();
    }

    public void emailAndNumberSearch(String h_tag, String countryCode, String numberBeginning, int max_query, int max_followers, int min_followers,
                                     int max_following, int random_tick_speed) throws InterruptedException, ParseException, IOException {

        System.out.printf("Bot running search mode: Hashtag (#%s)" +
                "\nMax Query: %s  |  Max Followers: %s  |  Min Followers: %s  | Max Following: %s\n\n", h_tag, max_query, max_followers, min_followers, max_following);

        while (!SCRAPE_STOP) {
            int postCount = 1, following = 0, followers = 0, scrollTimes = 0;

            long startTime = System.currentTimeMillis();
            long lastUpdateTime = System.currentTimeMillis();
            String userEmail = "", userBioEmail = "", calltag = "", userNumber = "";
            String feedCurrentUsername = "";

            boolean firstRun = true;
            boolean userEmailFound = false, bioEmailFound = false, contactFound = false, emailFound = false, numberFound = false;

            SCRAPE_RESET = false;

            while (!SCRAPE_RESET && !SCRAPE_STOP && !(RESET_SCRAPER_TIME < startTime - lastUpdateTime)) {
                lastUpdateTime = System.currentTimeMillis();
                if (firstRun) {

                    System.out.println("[Notice] Preparing scraping, don't touch anything while the bot is running");

                    Thread.sleep(500);
                    getElement(navSearch).click();

                    Thread.sleep(500);
                    getElement(inputSearch).click();
                    driver.hideKeyboard();

                    Thread.sleep(500);
                    getElement(searchTagButton).click();

                    Thread.sleep(500);
                    if (h_tag.contains("#"))
                        getElement(inputSearch).sendKeys(h_tag);
                    else
                        getElement(inputSearch).sendKeys("#" + h_tag);

                    Thread.sleep(1000);
                    var searchResults = driver.findElementsByClassName("android.widget.TextView");
                    for (var el : searchResults) {
                        if (((MobileElement) el).getText().equals("#" + h_tag) || ((MobileElement) el).getText().equals(h_tag)) {
                            ((MobileElement) el).click();
                            break;
                        }
                    }

                    Thread.sleep(7000);
                    getElement(recentPostsButton).click(); // Switch to recent posts instead of being on the top tab

                    Thread.sleep(1000);
                    var postsUnderHashtag = driver.findElementsById("com.instagram.android:id/image_button");
                    MobileElement post = (MobileElement) postsUnderHashtag.get(0);
                    post.click(); // Click the first post under the hashtag
                }

                // Decide which picture to click on
                while (postCount < max_query && !SCRAPE_RESET) {
                    emailFound = false;
                    numberFound = false;
                    bioEmailFound = false;
                    contactFound = false;

                    userBioEmail = "";
                    userEmail = "";
                    userNumber = "";

                    if (firstRun)
                        firstRun = false;
                    else if (!firstRun) {
                        Thread.sleep(2000);
                        scrollFeed();
                        scrollTimes++;
                    }

                    if (scrollTimes == 4)
                        System.out.println("[Warning] An error have possibly occurred, will retry 3 times");
                    else if (scrollTimes == 7)
                        SCRAPE_RESET = true;

                    Thread.sleep(2000);
                    List<MobileElement> feedUsernames = (List<MobileElement>) driver.findElementsById("com.instagram.android:id/row_feed_photo_profile_name");

                    if (feedUsernames.size() == 1) {
                        if (feedCurrentUsername.contains(feedUsernames.get(0).getText())) {
                            scrollFeed();
                            scrollTimes++;
                            continue;
                        } else {
                            feedCurrentUsername = feedUsernames.get(0).getText();
                            feedUsernames.get(0).click();
                            scrollTimes = 0;
                        }
                    } else if (feedUsernames.isEmpty()) {
                        scrollFeed();
                        scrollTimes++;
                        continue;
                    } else if (feedUsernames.size() > 1) {
                        if (feedCurrentUsername.isEmpty()) {
                            feedCurrentUsername = feedUsernames.get(0).getText();
                            feedUsernames.get(0).click();
                            scrollTimes = 0;

                        } else if (feedUsernames.get(0).getText().contains(feedCurrentUsername)) {
                            feedCurrentUsername = feedUsernames.get(1).getText();
                            feedUsernames.get(1).click();
                            scrollTimes = 0;

                        } else {
                            feedCurrentUsername = feedUsernames.get(0).getText();
                            feedUsernames.get(0).click();
                            scrollTimes = 0;
                        }
                    }

                    Thread.sleep(1000);
                    calltag = getElement(userCalltag).getText();

                    // Check if user has a link in description, and probably assume that it is a business or support profile
                    Thread.sleep(1000);
                    if (getElement(userFollowers).getText().toLowerCase().contains("k"))
                        followers = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowers).getText()).intValue() * 1000;
                    else if (getElement(userFollowers).getText().toLowerCase().contains("m"))
                        followers = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowers).getText()).intValue() * 1000000;
                    else
                        followers = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowers).getText()).intValue();

                    if (getElement(userFollowing).getText().toLowerCase().contains("k"))
                        following = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowing).getText()).intValue() * 1000;
                    else if (getElement(userFollowers).getText().toLowerCase().contains("m"))
                        following = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowing).getText()).intValue() * 1000000;
                    else
                        following = NumberFormat.getNumberInstance(Locale.US).parse(getElement(userFollowing).getText()).intValue();

                    System.out.printf("\n[Info] User: %s (Followers: %s | Following: %s)\n", calltag, followers, following);


                    Thread.sleep(1000);
                    MobileElement bio = null;
                    try {
                        bio = getElement(userBio);
                        String tempPageEmail = "";

                        if (bio.getText().contains("… more") || bio.getText().contains("... more")) {
                            TouchAction bioAction = new TouchAction(driver);
                            bioAction.press(PointOption.point((driver.manage().window().getSize().width - 60), bio.getLocation().y));
                            bioAction.release().perform();
                        }

                        Matcher matcher = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}").matcher(bio.getText());
                        while (matcher.find()) {
                            tempPageEmail = matcher.group();
                            bioEmailFound = true;
                        }

                        if (bioEmailFound) {
                            if (tempPageEmail.toLowerCase().contains("support") || tempPageEmail.toLowerCase().contains("help") || tempPageEmail.toLowerCase().contains("info") || tempPageEmail.toLowerCase().contains("hello")) {
                                System.out.println("[Warning] Email is support email, skipping");
                                bioEmailFound = false;
                            } else {
                                userBioEmail = tempPageEmail.replace("<", "");
                                userBioEmail = userBioEmail.replace(">", "");

                                System.out.printf("[Success] Email found in bio (%s): %s\n", calltag, userBioEmail);
                            }
                        }

                    } catch (Exception e) {
                    }

                    Thread.sleep(1000);
                    try {
                        MobileElement contactButton = null;
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

                        Thread.sleep(1000);
                        try {
                            List<MobileElement> nestedOptions = driver.findElementsById(contactNestedOption[0]);
                            boolean dismatchingCC = false;
                            boolean supportEmail = false;

                            for (MobileElement option : nestedOptions) {

                                // Move down to export thingy
                                if (option.getText().contains("support") || option.getText().contains("help") || option.getText().contains("info") || option.getText().contains("hello")) {
                                    supportEmail = true;
                                    emailFound = false;
                                } else if (option.getText().contains("@")) {
                                    userEmail = option.getText();
                                    emailFound = true;
                                    System.out.printf("[Success] Email found in contact (%s): %s\n", calltag, userEmail);
                                }

                                if (option.getText().startsWith(countryCode) || option.getText().startsWith(numberBeginning)) {
                                    numberFound = true;
                                    userNumber = option.getText();
                                    System.out.printf("[Success] Number found in contact (%s): %s\n", calltag, userNumber);
                                } else if (!option.getText().startsWith(countryCode) && !option.getText().contains("@") || !option.getText().startsWith(numberBeginning) && !option.getText().contains("@"))
                                    dismatchingCC = true;
                            }

                            if (dismatchingCC)
                                System.out.printf("[Warning] Phone number doesn't match desired country code (%s), skipping\n", countryCode);

                            if (supportEmail)
                                System.out.printf("[Warning] Email is support email, skipping\n");

                        } catch (Exception e) {
                            emailFound = false;
                        }

                        Thread.sleep(1000);
                        driver.navigate().back();
                    } catch (Exception e) {
                        contactFound = false;
                    }

                    Thread.sleep(1000);
                    if (!contactFound) {
                        try {
                            MobileElement eButton = null;
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
                            Thread.sleep(2000);
                            String tempPageEmail = getElement(androidEmail).getText();

                            // Move down to export thingy
                            if (tempPageEmail.toLowerCase().contains("support") || tempPageEmail.toLowerCase().contains("help") || tempPageEmail.toLowerCase().contains("info") || tempPageEmail.toLowerCase().contains("hello")) {
                                System.out.println("[Warning] Email is support email, skipping");
                                emailFound = false;
                            } else {
                                userEmail = tempPageEmail.replace("<", "");
                                userEmail = userEmail.replace(">", "");
                                userEmail = userEmail.replace(",", " ").trim();

                                emailFound = true;

                                System.out.printf("[Success] Email found in contact (%s): %s\n", calltag, userEmail);
                            }

                            Thread.sleep(2000);
                            driver.hideKeyboard();
                            driver.navigate().back();
                        } catch (Exception e) {
                            emailFound = false;
                        }
                    }

                    Thread.sleep(1000);
                    if (numberFound || bioEmailFound || emailFound) {
                        String tmpEmail = "N/A";
                        userNumber = userNumber.isBlank() ? "N/A" : userNumber;

                        if (!userEmail.isEmpty())
                            tmpEmail = userEmail;
                        else if (!userBioEmail.isEmpty()) {
                            if (userEmail.isEmpty())
                                tmpEmail = userBioEmail;
                            else
                                tmpEmail = userEmail;
                        }


                        if (followers > min_followers && followers < max_followers && following < max_following) {
                            exportUserCredentials(h_tag, calltag, followers, following, tmpEmail, userNumber, postCount);
                            postCount++;
                        } else
                            System.out.println("[Warning] User doesn't match desired criteria(s), skipping");
                    } else {
                        System.out.println("[Info] No number or email found");
                    }

                    Thread.sleep(2000);
                    driver.navigate().back();
                }

                System.out.print("\n[Error] An error has been identified, the bot will restart");
                System.out.print(".");
                Thread.sleep(2000);
                System.out.print(".");
                Thread.sleep(2000);
                System.out.print(".\n\n");
                Thread.sleep(1000);

                if (SCRAPE_RESET) {
                    // At some point transfer the current data to restart
                }
            }
        }
    }
}