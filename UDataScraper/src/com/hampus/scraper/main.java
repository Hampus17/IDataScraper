package com.hampus.scraper;

import java.io.IOException;
import java.text.ParseException;

import static java.lang.Integer.parseInt;

public class main {
    public static void main(String args[]) throws IOException, InterruptedException, ParseException {

        System.out.println("### Program Started ###");
        System.out.println("### Bot starting... ###\n");

        // Variables
        UScraper scraper = new UScraper();

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

        Thread.sleep(2000);

        boolean SCRAPING_INPROGRESS = true;
        while (SCRAPING_INPROGRESS) {
            try {
                scraper.emailAndNumberSearch("pysseltips", "+46", "07", 1000, 100000, 10, 10000, 3);
            } catch (Exception e) {
                System.out.println("[Error] An error has been identified, the bot will restart");
                e.printStackTrace();
                SCRAPING_INPROGRESS = true;
                scraper = new UScraper();
                continue;
            }

            SCRAPING_INPROGRESS = false;
        }

        // scraper.appExit();
    }
}
