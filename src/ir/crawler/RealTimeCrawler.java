package ir.crawler;

public abstract class RealTimeCrawler<T> {
    private boolean isInitialized;

    public abstract T fetch(String query);

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }
}
