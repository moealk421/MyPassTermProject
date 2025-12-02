package com.mypass;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility for managing security questions and their stored format.
 */
public final class SecurityQuestionUtil {
    private static final String DELIMITER = "::"; // Separates question key from answer
    private static final Map<String, String> QUESTION_MAP = new LinkedHashMap<>();

    // Predefined security questions
    static {
        QUESTION_MAP.put("first_pet", "What was the name of your first pet?");
        QUESTION_MAP.put("birth_city", "In what city were you born?");
        QUESTION_MAP.put("mother_maiden", "What is your mother's maiden name?");
        QUESTION_MAP.put("favorite_teacher", "What is the name of your favorite teacher?");
        QUESTION_MAP.put("first_school", "What was the name of your first school?");
        QUESTION_MAP.put("favorite_food", "What is your all-time favorite food?");
    }

    private SecurityQuestionUtil() {} // Utility class

    public static Map<String, String> getSecurityQuestions() {
        return Collections.unmodifiableMap(QUESTION_MAP);
    }

    /**
     * Encodes question key and answer into storage format: "key::answer"
     */
    public static String encodeQuestionAndAnswer(String questionKey, String answer) {
        if (questionKey == null) {
            questionKey = "custom";
        }
        if (answer == null) {
            answer = "";
        }
        return questionKey.trim() + DELIMITER + answer.trim();
    }

    /**
     * Extracts question key from stored value.
     */
    public static String extractQuestionKey(String storedValue) {
        if (storedValue == null) {
            return "custom";
        }
        String[] parts = storedValue.split(DELIMITER, 2);
        return parts.length > 1 ? parts[0] : "custom";
    }

    /**
     * Extracts answer from stored value.
     */
    public static String extractAnswer(String storedValue) {
        if (storedValue == null) {
            return "";
        }
        String[] parts = storedValue.split(DELIMITER, 2);
        if (parts.length > 1) {
            return parts[1];
        }
        // Legacy support: assume entire value is the answer
        return storedValue;
    }

    /**
     * Gets human-readable question text for a stored value.
     */
    public static String extractQuestionText(String storedValue) {
        String key = extractQuestionKey(storedValue);
        if ("custom".equals(key)) {
            return storedValue != null && storedValue.contains(DELIMITER)
                    ? storedValue.split(DELIMITER, 2)[0]
                    : "Security Question";
        }
        return QUESTION_MAP.getOrDefault(key, "Security Question");
    }
}
