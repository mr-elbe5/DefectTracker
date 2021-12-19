package de.elbe5.base.log;

import java.util.logging.*;

public class Log {

    public static final int LOG = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;
    private static Logger julog = null;
    private static LogFormatter logFormatter = new LogFormatter();

    public static void initLog(String name) {
        if (julog != null) {
            for (Handler handler : julog.getHandlers()) {
                julog.removeHandler(handler);
            }
        }
        julog = Logger.getLogger(name);
        if (julog.getUseParentHandlers() && julog.getParent() != null) {
            for (Handler handler : julog.getParent().getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    handler = new ConsoleHandler();
                    handler.setFormatter(logFormatter);
                    handler.setLevel(Level.FINE);
                    julog.addHandler(handler);
                } else if (handler instanceof FileHandler) {
                    julog.addHandler(handler);
                }
            }
            julog.setUseParentHandlers(false);
        } else {
            for (Handler handler : julog.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    julog.removeHandler(handler);
                    handler = new ConsoleHandler();
                    handler.setFormatter(logFormatter);
                    handler.setLevel(Level.FINE);
                    julog.addHandler(handler);
                }
            }
        }
    }

    private static void log(int level, String message, Throwable t) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        //0: getStackTrace,  1: this method, 2: log, 3: caller
        StackTraceElement caller = level > LOG && stack.length > 2 ? stack[3] : null;
        Level lev;
        switch (level) {
            case LOG:
                lev = Level.INFO;
                break;
            case INFO:
                lev = Level.INFO;
                break;
            case WARN:
                lev = Level.WARNING;
                break;
            case ERROR:
                lev = Level.SEVERE;
                break;
            default:
                lev = Level.OFF;
                break;
        }
        LogRecord rec = new LogRecord(lev, message);
        rec.setLoggerName(julog.getName());
        rec.setSourceClassName(caller != null ? caller.getClassName() : "");
        rec.setSourceMethodName(caller != null ? caller.getMethodName() : "");
        rec.setThrown(t);
        julog.log(rec);
    }

    public static void log(String message) {
        log(LOG, message, null);
    }

    public static void trace(String message) {
        log(INFO, message, null);
    }

    public static void info(String message) {
        log(INFO, message, null);
    }

    public static void warn(String message) {
        log(WARN, message, null);
    }

    public static void error(String message) {
        log(ERROR, message, null);
    }

    public static void error(String message, Throwable t) {
        log(ERROR, message, t);
    }
}
