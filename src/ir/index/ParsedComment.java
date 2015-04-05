package ir.index;

import com.google.common.base.Joiner;

import ir.crawler.Source;

public final class ParsedComment {
    public static enum Fields {
        SEARCHABLE_TEXT, ID, COMMENT, PRODUCT_NAME, URL, LABEL, SOURCE
    }

    public final String id;
    public final String comment;
    public final String productName;
    public final String commentUrl;
    public final String commentLabel;
    public final Source source;

    public String fullSearchableText() {
        return Joiner.on(" ").join(productName, comment);
    }

    private ParsedComment(Builder builder) {
        id = builder.id;
        comment = builder.comment;
        productName = builder.productName;
        commentUrl = builder.commentUrl;
        commentLabel = builder.commentLabel;
        source = builder.source;
    }

    @Override
    public String toString() {
        return String
                .format("[id = %s, comment = %s, productName = %s, url = %s, label = %s, source = %s]",
                        id, comment, productName, commentUrl, commentLabel, source);
    }

    public static class Builder {
        private final String id;
        private final Source source;
        private String       comment      = "";
        private String       productName  = "";
        private String       commentUrl   = "";
        private String       commentLabel = "";

        public Builder(String id, Source source) {
            this.id = id;
            this.source = source;
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

        public Builder commentLabel(String val) {
            commentLabel = val;
            return this;
        }

        public ParsedComment build() {
            return new ParsedComment(this);
        }
    }
}
