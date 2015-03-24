package ir.index;

import ir.crawler.Source;

public final class ParsedDocument {
    public static enum Fields {
        DOC_ID,
        DOC_BODY,
        PRODUCT_NAME,
        DOC_URL,
        SOURCE
    }
    public final String documentId;
    public final String body;
    public final String productName;
    public final String commentUrl;
    public final Source source;

    private ParsedDocument(Builder builder) {
        documentId = builder.documentId;
        body = builder.body;
        productName = builder.productName;
        commentUrl = builder.commentUrl;
        source = builder.source;
    }

    public static class Builder {
        private final String documentId;
        private final Source source;
        private String       body        = "";
        private String       productName = "";
        private String       commentUrl  = "";

        public Builder(String documentId, Source source) {
            this.documentId = documentId;
            this.source = source;
        }

        public Builder comment(String val) {
            body = val;
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
