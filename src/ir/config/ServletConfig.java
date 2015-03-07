package ir.config;

public final class ServletConfig {
    private String className;
    private String contextPath;

    public String getClassName() {
        return className;
    }

    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String toString() {
        return String.format("ClassName: %s, ContextPath: %s",
                className, contextPath);
    }
}
