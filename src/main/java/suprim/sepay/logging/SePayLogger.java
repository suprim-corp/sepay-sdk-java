package suprim.sepay.logging;

/**
 * Logging abstraction for SePay SDK.
 * Works with SLF4J if available, otherwise falls back to no-op.
 */
public interface SePayLogger {

    void debug(String message);

    void debug(String format, Object... args);

    void info(String message);

    void info(String format, Object... args);

    void warn(String message);

    void warn(String format, Object... args);

    void error(String message);

    void error(String message, Throwable t);

    boolean isDebugEnabled();

    /**
     * Gets a logger for the specified class.
     *
     * @param clazz the class requesting the logger
     * @return logger instance
     */
    static SePayLogger getLogger(Class<?> clazz) {
        return SePayLoggerFactory.getLogger(clazz);
    }
}
