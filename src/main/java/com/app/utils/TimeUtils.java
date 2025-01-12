package com.app.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author InuHa
 */
public class TimeUtils {
    
    public static String now(String format) { 
	LocalDateTime now = LocalDateTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return now.format(formatter);
    }
    
    public static String date(String format, Timestamp timestamp) { 
	LocalDateTime dateTime = timestamp.toLocalDateTime();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }
	
    public static String date(String format, LocalDate localDate) { 
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
	return localDate.format(formatter);
    }
    
    public static String date(String format, String timestamp) { 
	boolean hasTime = timestamp.length() > 10;
        
	if (hasTime) {
            String formatTime = "yyyy-MM-dd HH:mm:ss";
            int lenghtSecond = getMiliSecondLength(timestamp);
            if (lenghtSecond > 0) {
                formatTime += ".";
                for(int i = 0; i < lenghtSecond; i++) { 
                    formatTime += "S";
                }
            }
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(formatTime);
	    LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
	    return dateTime.format(formatter);
	} else {	
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate dateTime = LocalDate.parse(timestamp, inputFormatter);
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
	    return dateTime.format(formatter);
	}
    }
	
    public static String currentDate() { 
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }
    
    public static String currentDateTime() { 
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
	
    public static int getMiliSecondLength(String timestamp) {
        if (timestamp.contains(".")) {
            String[] parts = timestamp.split("\\.");
            if (parts.length > 1) {
                String milliseconds = parts[1];
                return milliseconds.length();
            }
        }
        return 0;
    }
}
