package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DateUtils {

    private static final Map<String, String> dayMap = new HashMap<>();

    static {
        dayMap.put("monday", "Mon");
        dayMap.put("mon", "Mon");
        dayMap.put("tuesday", "Tue");
        dayMap.put("tue", "Tue");
        dayMap.put("wednesday", "Wed");
        dayMap.put("wed", "Wed");
        dayMap.put("thursday", "Thu");
        dayMap.put("thu", "Thu");
        dayMap.put("friday", "Fri");
        dayMap.put("fri", "Fri");
        dayMap.put("saturday", "Sat");
        dayMap.put("sat", "Sat");
        dayMap.put("sunday", "Sun");
        dayMap.put("sun", "Sun");
    }

    public static String normalizeDay(String input) {
        if (input == null) return null;
        return dayMap.get(input.trim().toLowerCase());
    }

    public static boolean isValidDay(String input) {
        return input != null && dayMap.containsKey(input.trim().toLowerCase());
    }

    public static Set<String> getValidDays() {
        return Set.copyOf(dayMap.keySet());
    }

    public static String normalizeTitleOrVenue(String input) {
        if (input == null || input.trim().isEmpty()) return "";
        String[] words = input.trim().toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

}
