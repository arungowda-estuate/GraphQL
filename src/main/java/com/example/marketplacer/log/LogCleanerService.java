package com.example.marketplacer.log;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;

@Service
public class LogCleanerService {
    @Value("${log.retention.minutes:60}")
    private long retentionMinutes;

    private static final String LOG_FILE = "application.log.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedRate = 60000) // Every 1 minute
    public void cleanOldLogs() {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) return;

        try {
            Path tempFile = Files.createTempFile("log_clean_", ".txt");
            BufferedWriter writer = Files.newBufferedWriter(tempFile);
            Instant cutoff = Instant.now().minus(Duration.ofMinutes(retentionMinutes));

            Files.lines(logFile.toPath())
                    .filter(
                            line -> {
                                try {
                                    String timestampStr = line.substring(0, 19);
                                    LocalDateTime timestamp = LocalDateTime.parse(timestampStr, FORMATTER);
                                    return timestamp.atZone(ZoneId.systemDefault()).toInstant().isAfter(cutoff);
                                } catch (Exception e) {
                                    // Keep malformed lines just in case
                                    return true;
                                }
                            })
                    .forEach(
                            line -> {
                                try {
                                    writer.write(line);
                                    writer.newLine();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

            writer.close();
            Files.move(tempFile, logFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
