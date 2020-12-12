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

    public UScraper(bool debug) {


    }

    private AndroidDriver driver;

    public void setUp() throws MalformedURLException {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("deviceName", "emulator-5554");
        desiredCapabilities.setCapability("platformName", "android");
        desiredCapabilities.setCapability("appPackage", "com.instagram.android");
        desiredCapabilities.setCapability("appActivity", ".activity.MainTabActivity");
        desiredCapabilities.setCapability("noReset", true);

        URL remoteUrl = new URL("http://127.0.0.1:4723/wd/hub");

        driver = new AndroidDriver(remoteUrl, desiredCapabilities);
    }

    public void appExit() {
        driver.quit();
    }

    public void userSearch(string username) {

    }

    public void emailAndNumberSearch(string h_tag, int max_query, int query_rate, bool recent_posts) {

        // If email or number found
        // IUser user = new IUser();

        // In the end export the credentials to a csv file
        // user.exportCredentials();
    }

    public static void main(String args[]) throws MalformedURLException, InterruptedException {

        System.out.println("### BOT RUNNING... ###");

        if (args[0].equals("--help") || args[0].equals("help") || args[0].equals("-help")) {
            System.out.println("Help info");
            return;
        }

        // Variables
        UScraper scraper = new UScraper();

        int searchMode, maxSearches, waitInterval;
        string hashtag, username, fileLocation;
        bool recentPosts;


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
}