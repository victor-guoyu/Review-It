package ir.server;

import org.apache.logging.log4j.Level;

public enum LoggerLevel {
    RESULT("RESULT", 450);
    private final String levelName;
    private int intValue;
    private LoggerLevel(String leveName, int intValue) {
        this.levelName = leveName;
        this.intValue = intValue;
    }
    
    public Level getLevel() {
        return Level.forName(levelName, intValue);
    }
}
