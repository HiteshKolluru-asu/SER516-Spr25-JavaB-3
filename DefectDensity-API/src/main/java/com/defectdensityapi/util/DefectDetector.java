package com.defectdensityapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefectDetector {

    // Precompiled regex patterns for defect detection.
    public static final Pattern PATTERN_NULL_METHOD_CALL = Pattern.compile("\\b\\w+\\s*\\.\\s*\\w+\\s*\\(\\s*null\\s*\\)");
    public static final Pattern PATTERN_EMPTY_CATCH = Pattern.compile("catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}");
    public static final Pattern PATTERN_NUMERIC_LITERALS = Pattern.compile("\\b(?!-?[0-1]\\b)\\b-?\\d+\\b");
    public static final Pattern PATTERN_PRINTLN = Pattern.compile("System\\.out\\.println");
    public static final Pattern PATTERN_TYPE_CAST = Pattern.compile("\\([A-Za-z]+\\)\\s*\\w+");
    public static final Pattern PATTERN_FILE_PATH = Pattern.compile("\"[/\\\\][^\"]+\"");
    public static final Pattern PATTERN_RETURN = Pattern.compile("\\breturn\\b");
    public static final Pattern PATTERN_CODE_BLOCK = Pattern.compile("\\{([^{}]|\\{[^{}]*\\})*\\}");

    public static final Pattern[] DEFECT_PATTERNS = new Pattern[]{
        PATTERN_NULL_METHOD_CALL,
        PATTERN_EMPTY_CATCH,
        PATTERN_NUMERIC_LITERALS,
        PATTERN_PRINTLN,
        PATTERN_TYPE_CAST,
        PATTERN_FILE_PATH,
        PATTERN_RETURN,
        PATTERN_CODE_BLOCK
    };

    /**
     * Applies all defect regex patterns to a single line of code.
     *
     * @param line the code line.
     * @return the number of defects found.
     */
    public static int countDefectsInLine(String line) {
        int defects = 0;
        for (Pattern pattern : DEFECT_PATTERNS) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                defects++;
            }
        }
        return defects;
    }
}
