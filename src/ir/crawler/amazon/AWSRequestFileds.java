package ir.crawler.amazon;

public class AWSRequestFileds {
    // XML tags
    public static final String ITEM_TAG                   = "Item";
    public static final String ITEMLINK_TAG               = "ItemLink";
    public static final String DESCRIPTION_TAG            = "Description";
    public static final String ERROR_TAG                  = "Error";
    public static final String URL_TAG                    = "URL";
    public static final String TITLE_TAG                  = "Title";
    public static final String CODE_TAG                   = "Code";
    public static final String MESSAGE_TAG                = "Message";
    public static final int    FIRST_ELEMENT              = 0;

    // request params
    public static final String PARAM_KEY_ASSOCIATETAG     = "AssociateTag";
    public static final String PARAM_KEY_SERVICE          = "Service";
    public static final String PARAM_VALUE_SERVICE        = "AWSECommerceService";
    public static final String PARAM_KEY_OPERATION        = "Operation";
    public static final String PARAM_VALUE_OPERATION      = "ItemSearch";
    public static final String PARAM_KEY_SEARCHINDEX      = "SearchIndex";
    public static final String PARAM_KEY_KEYWORDS         = "Keywords";
    public static final String PARAM_KEY_INCLUDEREVIEWS   = "IncludeReviewsSummary";
    public static final String PARAM_VALUE_INCLUDEREVIEWS = "true";
    public static final String PARAM_KEY_ITEMPAGE         = "ItemPage";
}