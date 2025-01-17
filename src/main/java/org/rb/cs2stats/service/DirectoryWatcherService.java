package org.rb.cs2stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

@Service
public class DirectoryWatcherService {

    private static final String LOG_DIRECTORY = "src/main/resources";
    private static final String PROCESSED_DIRECTORY = "src/main/resources/processed";
    private static final Logger logger = Logger.getLogger(DirectoryWatcherService.class.getName());

    @Autowired
    private LogParserService logParserService;

    @Scheduled(fixedRate = 10000) // Kör var 10:e sekund
    public void watchDirectory() {
        logger.info("Watching directory: " + LOG_DIRECTORY);
        File dir = new File(LOG_DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".log"));

        if (files != null) {
            for (File file : files) {
                try {
                    logger.info("Processing file: " + file.getName());
                    logParserService.parseLogFile(String.valueOf(file));
                    // Efter att filen parsats, flytta den till processed-katalogen
                    Files.move(file.toPath(), Paths.get(PROCESSED_DIRECTORY, file.getName()), StandardCopyOption.REPLACE_EXISTING);
                    logger.info("File moved to processed: " + file.getName());
                } catch (Exception e) {
                    logger.severe("Error processing file: " + file.getName() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            logger.info("No log files found in directory: " + LOG_DIRECTORY);
        }
    }
}