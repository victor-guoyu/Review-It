package ir.crawler;

import java.util.List;

public abstract class Crawler {

    private boolean isInitialized;

    public abstract void fetch(List<String> queries);

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isInitialized
     *            the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    /**
     * 
     * @param rating
     *            rating star
     * @param total
     *            total number of rating star
     * @return label of the comment
     */
    protected String setLabel(int rating, int total) {
        int middle = (total + 1) / 2;
        String label = "";
        if (rating > middle) {
            label = "positive";
        } else if (rating < middle) {
            label = "negative";
        } else {
            label = "netural";
        }
        return label;
    }
}