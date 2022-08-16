package com.b2bapp.onn.base;

import com.b2bapp.onn.model.AseModel;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.RsmModel;
import com.b2bapp.onn.model.StoreModel;
import com.b2bapp.onn.model.UserModel;
import com.b2bapp.onn.model.VpDistributorModel;
import com.b2bapp.onn.model.VpStoreModel;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariable {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME_14 = "onn_db_14";

    public static UserModel userModel;
    public static ArrayList<StoreModel> storeModelArrayList = new ArrayList<StoreModel>();
    public static ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();
    public static ArrayList<DistributorModel> distributorModelArrayList = new ArrayList<DistributorModel>();
    public static ArrayList<VpDistributorModel> vpDistributorModelArrayList = new ArrayList<VpDistributorModel>();
    public static ArrayList<RsmModel> rsmModelArrayList = new ArrayList<RsmModel>();
    public static ArrayList<AseModel> aseModelArrayList = new ArrayList<AseModel>();
    public static ArrayList<VpStoreModel> vpStoreModelArrayList = new ArrayList<VpStoreModel>();
    public static List<CartModel> cartList = new ArrayList<CartModel>();

    public static StoreModel storeModel;
    public static ProductModel productModel;

    public static String order_type = "Store Visit";

    public static String product_id = "";
    public static String category_id = "";

    public static String choosed_collection_id = "";

    public static String store_id = "";

    public static ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();

    public static String pack_count = "";

    public static String selected_area = "";

    //For Store/Distributor wise report in ASE dashboard
    public static String store_report_ase_id = "";
    public static String store_report_date_from = "";
    public static String store_report_date_to = "";
    public static String store_report_collection = "";
    public static String store_report_style_no = "";
    public static String store_report_orderBy = "";

    //For Product wise report in ASE dashboard
    public static String product_report_ase_id = "";
    public static String product_date_from = "";
    public static String product_date_to = "";
    public static String product_collection = "";
    public static String product_category = "";
    public static String product_style_no = "";

    //For Store/Distributor wise report in ASE dashboard
    public static String asm_report_date_from = "";
    public static String asm_report_date_to = "";
    public static String asm_report_collection = "";
    public static String asm_report_style_no = "";
    public static String asm_report_orderBy = "";

    public static String asm_ase_name = "";

    //For Store/Distributor wise report in RSM dashboard
    public static String rsm_report_date_from = "";
    public static String rsm_report_date_to = "";
    public static String rsm_report_collection = "";
    public static String rsm_report_style_no = "";
    public static String rsm_report_orderBy = "";
    public static String rsm_report_area = "";

    public static String rsm_asm_name = "";

    //For Store/Distributor wise report in VP dashboard
    public static String vp_report_date_from = "";
    public static String vp_report_date_to = "";
    public static String vp_report_collection = "";
    public static String vp_report_style_no = "";
    public static String vp_report_orderBy = "";
    public static String vp_report_area = "";
    public static String vp_report_state = "";

    public static String vp_rsm_name = "";
    public static String vp_asm_name = "";
    public static int notiCount = 0;

    public static String ase_id_from_asm_dashboard = "";
    public static String ase_name = "";
}
