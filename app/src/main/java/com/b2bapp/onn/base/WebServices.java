package com.b2bapp.onn.base;

public class WebServices {
    //private static final String BASE_URL = "https://onnb2b.torzo.in/api/";
    private static final String BASE_URL = "https://clever-mendeleev.43-225-53-183.plesk.page/api/";

    //public static final String IMAGE_BASE_URL = "https://onnb2b.torzo.in/";
    public static final String IMAGE_BASE_URL = "https://clever-mendeleev.43-225-53-183.plesk.page/";

    public static String URL_GET_OTP = BASE_URL + "get-otp";
    public static String URL_LOGIN_WITH_OTP = BASE_URL + "login-with-otp";
    public static String URL_USER_PROFILE = BASE_URL + "user-profile";
    public static String URL_STORE_LIST = BASE_URL + "store";
    public static String URL_CATEGORY_LIST = BASE_URL + "category";
    public static String URL_CATEGORY_WISE_PRODUCTS_LIST = BASE_URL + "category";
    public static String URL_COLLECTIOB_CATEGORY_WISE_PRODUCTS_LIST = BASE_URL + "collection";

    public static String URL_GET_STATE_FROM_PIN = "https://api.postalpincode.in/pincode/";

    public static String URL_PRODUCT_COLOR = BASE_URL + "products-color/view";
    public static String URL_PRODUCT_SIZE_VIEW = BASE_URL + "products-size/view";

    public static String URL_PRODUCT_ADD_CART = BASE_URL + "bulkAddcart";
    public static String URL_PRODUCT_ADD_CART_SINGLE = BASE_URL + "simpleBulkAddcart";
    public static String URL_CART_LIST = BASE_URL + "cart/user";
    public static String URL_CART_DELETE = BASE_URL + "cart/delete";
    public static String URL_PLACE_ORDER = BASE_URL + "place-order-update";
    public static String URL_CART_CLEAR = BASE_URL + "cart/clear";

    public static String URL_USER_ACTIVITY_LOG = BASE_URL + "useractivity";

    public static String URL_STORE_ADD = BASE_URL + "store/create";

    public static String URL_USER_ACTIVITY_CREATE = BASE_URL + "useractivity/create";

    public static String URL_NO_ORDER_REASON_UPDATE = BASE_URL + "no-order-reason/update";

    public static String URL_STORE_WISE_ORDER_LIST = BASE_URL + "filter/store";
    public static String URL_STORE_WISE_ORDER_DETAILS = BASE_URL + "filter/order";

    public static String URL_ALL_STORE_LIST = BASE_URL + "all-store-list";

    public static String URL_DISTRIBUTOR_LIST = BASE_URL + "distributor";
    public static String URL_DISTRIBUTOR_ADD = BASE_URL + "distributor/create";


    public static String URL_DISTRIBUTOR_MOM_LIST = BASE_URL + "user-wise-mom-list";



    public static String URL_STORE_VISIT_LIST = BASE_URL + "latest/store-visit";
    public static String URL_STORE_VISIT_CREATE = BASE_URL + "store-visit/create";

    public static String URL_OFFER_LIST = BASE_URL + "offers";

    public static String URL_SEARCH = BASE_URL + "search";

    public static String URL_AREA_LIST = BASE_URL + "area/list";

    public static String URL_CATALOGUE_LIST = BASE_URL + "catalogue/list";

    public static String URL_VP_REPORT = BASE_URL + "vp-report";
    public static String URL_VP_REPORT_ALL = BASE_URL + "vp-report-all";
    public static String URL_VP_REPORT_RSM_WISE = BASE_URL + "vp-report-rsm-wise";
    public static String URL_VP_REPORT_ASM_WISE = BASE_URL + "vp-report-asm-wise";

    public static String URL_VP_REPORT_DISTRIBUTOR = BASE_URL + "vp-report-distributor";
    public static String URL_VP_REPORT_DISTRIBUTOR_WISE = BASE_URL + "vp-report-distributor-wise";
    public static String URL_VP_REPORT_ASE_WISE = BASE_URL + "vp-report-ase-wise";

    public static String URL_DISTRIBUTOR_PRODUCT_ADD_CART = BASE_URL + "distributor/bulkAddcart";
    public static String URL_DISTRIBUTOR_CART_LIST = BASE_URL + "distributor/cart/user";
    public static String URL_DISTRIBUTOR_DELETE_CART = BASE_URL + "distributor/cart/delete";
    public static String URL_DISTRIBUTOR_PLACE_ORDER = BASE_URL + "distributor/place-order-update";
    public static String URL_DISTRIBUTOR_ORDER_LIST = BASE_URL + "distributor/order/view";

    public static String URL_RSM_WISE_DISTRIBUTOR = BASE_URL + "rsm-wise-distributor";
    public static String URL_ASM_WISE_DISTRIBUTOR = BASE_URL + "asm-wise-distributor";

    public static String URL_USER_LOGIN = BASE_URL + "user-login";

    public static String URL_COLLECTION_LIST = BASE_URL + "collection";

    public static String URL_STORE_DISTRIBUTOR_MOM = BASE_URL + "distributor/mom/store";
    public static String URL_MY_ORDERS = BASE_URL + "order/view";
    public static String URL_MY_ORDERS_FILTER = BASE_URL + "my-orders-filter";

    public static String URL_DISTRIBUTOR_ORDER_DETAILS = BASE_URL + "distributor/order/view";

    public static String URL_ASE_DASHBOARD_REPORT = BASE_URL + "ase/report";

    public static String URL_ASE_WISE_STORE_REPORT = BASE_URL + "ase/store/orders";
    public static String URL_ASE_WISE_PRODUCT_REPORT = BASE_URL + "ase/product/orders";

    public static String URL_ASM_WISE_TEAM_REPORT = BASE_URL + "asm/store/orders";
    public static String URL_ASM_WISE_PRODUCT_REPORT = BASE_URL + "asm/product/orders";

    public static String URL_RSM_WISE_AREAS = BASE_URL + "rsm-wise-areas";
    public static String URL_RSM_WISE_TEAM_REPORT = BASE_URL + "rsm/store/orders";
    public static String URL_RSM_WISE_PRODUCT_REPORT = BASE_URL + "rsm/product/orders";

    public static String URL_VP_WISE_STATES = BASE_URL + "vp-wise-states";
    public static String URL_VP_WISE_AREAS = BASE_URL + "vp-wise-areas";
    public static String URL_VP_WISE_TEAM_REPORT = BASE_URL + "vp/store/orders";
    public static String URL_VP_WISE_PRODUCT_REPORT = BASE_URL + "vp/product/orders";

    public static String URL_ORDER_FILTER = BASE_URL + "store-orders-filter";

    public static String URL_VISIT_START = BASE_URL + "visit/start";
    public static String URL_VISIT_END = BASE_URL + "visit/end";


    public static String URL_NOTIFICATION_LIST = BASE_URL + "notification-list";
    public static String URL_NOTIFICATION_READ = BASE_URL + "read-notification";

    public static String URL_COLLECTION_WISE_PRODUCT_STYLES = BASE_URL + "category/product/collection";

    public static String URL_GET_ALL_DATA = BASE_URL + "get-all-data";

    public static String URL_CREATE_ORDER_FROM_LOCAL = BASE_URL + "create-order-from-local";
    public static String URL_SAVE_ORDER_PRODUCT_FROM_LOCAL = BASE_URL + "save-order-product-from-local";
}
