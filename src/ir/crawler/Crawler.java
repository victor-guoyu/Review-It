package ir.crawler;

import java.util.List;

public abstract class Crawler{

    private boolean isInitialized;

    public abstract void fetch(List<String> queries);

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