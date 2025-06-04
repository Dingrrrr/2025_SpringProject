package com.dita.domain;

public enum TimePeriod {
    아침("아침"),
    점심("점심"), 
    저녁("저녁"),
    야간("야간");
    
    private final String displayName;
    
    TimePeriod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static TimePeriod fromString(String text) {
        for (TimePeriod period : TimePeriod.values()) {
            if (period.displayName.equals(text)) {
                return period;
            }
        }
        throw new IllegalArgumentException("Unknown time period: " + text);
    }
}