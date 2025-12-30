package suprim.sepay.logging;

/**
 * Factory for creating logger instances.
 * Uses SLF4J if available on classpath, otherwise no-op logger.
 */
public final class SePayLoggerFactory {

    private static final boolean SLF4J_AVAILABLE = checkSlf4jAvailable();

    private SePayLoggerFactory() {
        // Utility class
    }

    private static boolean checkSlf4jAvailable() {
        try {
            Class.forName("org.slf4j.LoggerFactory");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Creates a logger for the specified class.
     *
     * @param clazz the class requesting the logger
     * @return logger instance (SLF4J-backed or no-op)
     */
    public static SePayLogger getLogger(Class<?> clazz) {
        if (SLF4J_AVAILABLE) {
            return new Slf4jSePayLogger(clazz);
        }
        return NoOpSePayLogger.INSTANCE;
    }

    /**
     * Returns whether SLF4J is available on the classpath.
     */
    public static boolean isSlf4jAvailable() {
        return SLF4J_AVAILABLE;
    }
}
