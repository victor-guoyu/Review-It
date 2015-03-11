package ir.index;

public final class ParsedDocument {
    public final String documentId;
    public final String comment;
    public final String productName;
    public final String commentUrl;

    private ParsedDocument(Builder builder) {
        documentId = builder.documentId;
        comment = builder.comment;
        productName = builder.productName;
        commentUrl = builder.commentUrl;
    }

    public static class Builder {
        private final String documentId;
        private String       comment     = "";
        private String       productName = "";
        private String       commentUrl  = "";

        public Builder(String documentId) {
            this.documentId = documentId;
        }

        public Builder comment(String val) {
            comment = val;
            return this;
        }

        public Builder productName(String val) {
            productName = val;
            return this;
        }

        public Builder commentUrl(String val) {
            commentUrl = val;
            return this;
        }

        public ParsedDocument build() {
            return new ParsedDocument(this);
        }
    }
}
