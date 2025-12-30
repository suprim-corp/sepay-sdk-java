package suprim.sepay.logging;

/**
 * No-op logger implementation used when SLF4J is not available.
 * All methods do nothing.
 */
class NoOpSePayLogger implements SePayLogger {

    static final NoOpSePayLogger INSTANCE = new NoOpSePayLogger();

    private NoOpSePayLogger() {
        // Singleton
    }

    @Override
    public void debug(String message) {
        // No-op
    }

    @Override
    public void debug(String format, Object... args) {
        // No-op
    }

    @Override
    public void info(String message) {
        // No-op
    }

    @Override
    public void info(String format, Object... args) {
        // No-op
    }

    @Override
    public void warn(String message) {
        // No-op
    }

    @Override
    public void warn(String format, Object... args) {
        // No-op
    }

    @Override
    public void error(String message) {
        // No-op
    }

    @Override
    public void error(String message, Throwable t) {
        // No-op
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }
}
