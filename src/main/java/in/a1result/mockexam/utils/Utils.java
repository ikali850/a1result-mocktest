package in.a1result.mockexam.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.a1result.mockexam.entity.Answer;

public class Utils {

    // Method to format both date and time
    public static String getFormattedDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    // Method to format only time (HH:mm:ss)
    public static String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    // Method to calculate difference in minutes between two timestamps
    public static long getDifferenceInMinutes(long startTime, long endTime) {
        // Calculate the difference in milliseconds
        long differenceInMillis = endTime - startTime;

        // Convert milliseconds to minutes
        long differenceInMinutes = differenceInMillis / (1000 * 60);

        return differenceInMinutes;
    }

     public static Map<String, Integer> countCorrectAndWrong(List<Answer> answers) {
        int correct = 0;
        int wrong = 0;

        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                correct++;
            } else {
                wrong++;
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("correct", correct);
        result.put("wrong", wrong);
        return result;
    }

}
