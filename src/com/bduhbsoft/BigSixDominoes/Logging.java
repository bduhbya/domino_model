package com.bduhbsoft.BigSixDominoes;

/*
* Logging
*
* Logs to configured location
*/

//TODO: Implment log levels and configs for android or any other logging system/setup
public class Logging {

    private static LogLevel sLevel = LogLevel.TRACE;

    public enum LogLevel {
        TRACE(0),
        DEBUG(1),
        WARN(2),
        INFO(3),
        ERROR(4);

        private int mVal;

        LogLevel(int val) {
            mVal = val;
        }

        public int value() {
            return mVal;
        }
    }

    /**
    * Logs message with tag to configured logging sytem at configured level
    *
    * @param level Log level for the message
    * @param TAG Tag for the message in log output
    * @param msg Message to be logged
    */
    public static synchronized void LogMsg(LogLevel level, String TAG, String msg) {
        //TODO: Check log level and call based on the config (i.e. anroid, consoler, etc...)
        if(level.value() >= sLevel.value())
            logConsole(level, TAG, msg);
    }

    /**
    * Sets the desired log level
    *
    * @param level Log level for the system
    */
    public static void setLogLevel(LogLevel level) {
        sLevel = level;
    }

    private static void logConsole(LogLevel level, String TAG, String msg) {
        System.out.println(level + " " + TAG + ": " + msg);
    }

}
