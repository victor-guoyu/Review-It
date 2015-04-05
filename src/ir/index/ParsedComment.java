package ir.index;

import com.google.common.base.Joiner;

import ir.crawler.Source;

public final class ParsedComment {
    public enum Fields {
        SEARCHABLE_TEXT,
        ID,
        COMMENT,
        PRODUCT_NAME,
        URL,
        SOURCE
    }
    private String id;
    private String comment;
    private String productName;
    private String commentUrl;
    private Source source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCommentUrl() {
        return commentUrl;
    }

    public void setCommentUrl(String commentUrl) {
        this.commentUrl = commentUrl;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String fullSearchableText() {
        return Joiner.on(" ").join(productName, comment);
    }

    private ParsedComment(Builder builder) {
        id = builder.id;
        comment = builder.comment;
        productName = builder.productName;
        commentUrl = builder.commentUrl;
        source = builder.source;
    }

    @Override
    public String toString() {
        return String
                .format("[id = %s, comment = %s, productName = %s, url = %s, source = %s]",
                        id, comment, productName, commentUrl, source);
    }

    public static class Builder {
        private final String id;
        private final Source source;
        private String       comment        = "";
        private String       productName = "";
        private String       commentUrl  = "";

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

        public ParsedComment build() {
            return new ParsedComment(this);
        }
    }
}
