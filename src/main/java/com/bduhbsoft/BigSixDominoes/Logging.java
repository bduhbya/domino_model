package com.bduhbsoft.BigSixDominoes;

/*
* Logging
*
* Logs to configured location
*/

//TODO: Implment log levels and configs for android or any other logging system/setup
public class Logging {

    public enum LogLevel {
        TRACE,
        DEBUG,
        WARN,
        INFO,
        ERROR,
    }

    public static synchronized void LogMsg(LogLevel level, String TAG, String msg) {
        //TODO: Check log level and call based on the config (i.e. anroid, consoler, etc...)
        logConsole(level, TAG, msg);
    }

    private static void logConsole(LogLevel level, String TAG, String msg) {
        System.out.println(level + " " + TAG + ": " + msg);
    }

}
