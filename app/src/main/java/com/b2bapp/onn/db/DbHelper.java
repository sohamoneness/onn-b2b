package com.b2bapp.onn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.b2bapp.onn.base.GlobalVariable;
import com.b2bapp.onn.model.AreaModel;
import com.b2bapp.onn.model.CartModel;
import com.b2bapp.onn.model.CategoryModel;
import com.b2bapp.onn.model.CollectionModel;
import com.b2bapp.onn.model.ColorModel;
import com.b2bapp.onn.model.DistributorModel;
import com.b2bapp.onn.model.MomDbModel;
import com.b2bapp.onn.model.OrderDbModel;
import com.b2bapp.onn.model.OrderProductModel;
import com.b2bapp.onn.model.ProductModel;
import com.b2bapp.onn.model.SizeModel;
import com.b2bapp.onn.model.StoreModel;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    //Table for area
    private static final String ONN_AREAS = "onn_areas";
    //Table for stores
    private static final String ONN_STORES = "onn_stores";
    //Table for collections
    private static final String ONN_COLLECTIONS = "onn_collections";
    //Table for categories
    private static final String ONN_CATEGORIES = "onn_categories";
    //Table for distributors
    private static final String ONN_DISTRIBUTORS = "onn_distributors";
    //Table for distributor's MOM
    private static final String ONN_DISTRIBUTORS_MOM = "onn_distributors_mom";
    //Table for products
    private static final String ONN_PRODUCTS = "onn_products";
    //Table for colors
    private static final String ONN_COLORS = "onn_colors";
    //Table for sizes
    private static final String ONN_SIZES = "onn_sizes";
    //Table for carts
    private static final String ONN_CARTS = "onn_carts";
    //Table for orders
    private static final String ONN_ORDERS = "onn_orders";
    //Table for order products
    private static final String ONN_ORDER_PRODUCTS = "onn_order_products";

    //Columns for area
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STATE = "state";

    //Columns for stores
    private static final String COLUMN_STORE_ID = "id";
    private static final String COLUMN_STORE_NAME = "store_name";
    private static final String COLUMN_BUSINESS_NAME = "bussiness_name";
    private static final String COLUMN_STORE_OCC_NUMBER = "store_OCC_number";
    private static final String COLUMN_CONTACT = "contact";
    private static final String COLUMN_EMAIL ="email";
    private static final String COLUMN_WHATSAPP = "whatsapp";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_AREA = "area";
    private static final String COLUMN_STORE_STATE = "state";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_PIN = "pin";
    private static final String COLUMN_IMAGE = "image";

    //Columns for collections
    private static final String COLUMN_COLLECTION_ID = "id";
    private static final String COLUMN_COLLECTION_NAME = "name";
    private static final String COLUMN_COLLECTION_POSITION = "position";

    //Columns for categories
    private static final String COLUMN_CATEGORY_ID = "id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_PARENT = "parent";

    //Columns for distributors
    private static final String COLUMN_DISTRIBUTOR_ID = "id";
    private static final String COLUMN_DISTRIBUTOR_NAME = "store_name";
    private static final String COLUMN_DISTRIBUTOR_BUSINESS_NAME = "bussiness_name";
    private static final String COLUMN_DISTRIBUTOR_OCC_NUMBER = "DISTRIBUTOR_OCC_number";
    private static final String COLUMN_DISTRIBUTOR_CONTACT = "contact";
    private static final String COLUMN_DISTRIBUTOR_EMAIL ="email";
    private static final String COLUMN_DISTRIBUTOR_WHATSAPP = "whatsapp";
    private static final String COLUMN_DISTRIBUTOR_ADDRESS = "address";
    private static final String COLUMN_DISTRIBUTOR_AREA = "area";
    private static final String COLUMN_DISTRIBUTOR_STATE = "state";
    private static final String COLUMN_DISTRIBUTOR_CITY = "city";
    private static final String COLUMN_DISTRIBUTOR_PIN = "pin";
    private static final String COLUMN_DISTRIBUTOR_IMAGE = "image";
    private static final String COLUMN_DISTRIBUTOR_DISTRIBUTOR_ID = "distributor_id";

    //Columns for distributor's mom
    private static final String COLUMN_MOM_ID = "id";
    private static final String COLUMN_MOM_USER_ID = "user_id";
    private static final String COLUMN_MOM_DISTRIBUTOR_NAME = "distributor_name";
    private static final String COLUMN_MOM_COMMENT = "comment";

    //Columns for products
    private static final String COLUMN_PRODUCT_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "name";
    private static final String COLUMN_PRODUCT_IMAGE = "image";
    private static final String COLUMN_PRODUCT_STYLE_NO = "style_no";
    private static final String COLUMN_PRODUCT_MASTER_PACK_COUNT = "master_pack_count";
    private static final String COLUMN_PRODUCT_COLLECTION_ID = "collection_id";
    private static final String COLUMN_PRODUCT_CATEGORY_ID = "category_id";

    //Columns for colors
    private static final String COLUMN_COLOR_NAME = "name";
    private static final String COLUMN_COLOR_CODE = "code";
    private static final String COLUMN_COLOR_PRODUCT_ID = "product_id";

    //Columns for sizes
    private static final String COLUMN_SIZE_ID = "id";
    private static final String COLUMN_SIZE_SIZE = "size";
    private static final String COLUMN_SIZE_PRICE = "price";
    private static final String COLUMN_SIZE_OFFER_PRICE = "offer_price";
    private static final String COLUMN_SIZE_QTY = "qty";
    private static final String COLUMN_SIZE_DESCRIPTION = "description";
    private static final String COLUMN_SIZE_PRODUCT_ID = "product_id";
    private static final String COLUMN_SIZE_COLOR_CODE = "color_code";

    //Columns for carts
    private static final String COLUMN_CART_ID = "id";
    private static final String COLUMN_CART_PRODUCT_NAME = "product_name";
    private static final String COLUMN_CART_PRODUCT_STYLE = "product_style";
    private static final String COLUMN_CART_COLOR = "color";
    private static final String COLUMN_CART_SIZE = "size";
    private static final String COLUMN_CART_QTY = "qty";
    private static final String COLUMN_CART_OFFER_PRICE = "offer_price";

    //Columns for orders
    private static final String COLUMN_ORDER_ID = "id";
    private static final String COLUMN_ORDER_USER_ID = "user_id";
    private static final String COLUMN_ORDER_STORE_ID = "store_id";
    private static final String COLUMN_ORDER_ORDER_TYPE = "order_type";
    private static final String COLUMN_ORDER_COMMENT = "comment";
    private static final String COLUMN_ORDER_ORDER_DATE = "order_date";

    //Columns for order products
    private static final String COLUMN_ORDER_PRODUCTS_ID = "id";
    private static final String COLUMN_ORDER_PRODUCTS_PRODUCT_NAME = "product_name";
    private static final String COLUMN_ORDER_PRODUCTS_PRODUCT_STYLE = "product_style";
    private static final String COLUMN_ORDER_PRODUCTS_COLOR = "color";
    private static final String COLUMN_ORDER_PRODUCTS_SIZE = "size";
    private static final String COLUMN_ORDER_PRODUCTS_QTY = "qty";
    private static final String COLUMN_ORDER_PRODUCTS_OFFER_PRICE = "offer_price";
    private static final String COLUMN_ORDER_PRODUCTS_ORDER_ID = "order_id";

    public DbHelper(Context context) {
        super(context, GlobalVariable.DATABASE_NAME_14, null, GlobalVariable.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER = "CREATE TABLE " + ONN_AREAS + " ("
                + COLUMN_ID + " TEXT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_STATE + " TEXT)";
        db.execSQL(CREATE_USER);

        String CREATE_STORE = "CREATE TABLE " + ONN_STORES + " ("
                + COLUMN_STORE_ID + " TEXT, "
                + COLUMN_STORE_NAME + " TEXT, "
                + COLUMN_BUSINESS_NAME + " TEXT, "
                + COLUMN_STORE_OCC_NUMBER + " TEXT, "
                + COLUMN_CONTACT + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_WHATSAPP + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_AREA + " TEXT, "
                + COLUMN_STORE_STATE + " TEXT, "
                + COLUMN_CITY + " TEXT, "
                + COLUMN_PIN + " TEXT, "
                + COLUMN_IMAGE + " TEXT)";
        db.execSQL(CREATE_STORE);

        String CREATE_COLLECTION = "CREATE TABLE " + ONN_COLLECTIONS + " ("
                + COLUMN_COLLECTION_ID + " TEXT, "
                + COLUMN_COLLECTION_NAME + " TEXT, "
                + COLUMN_COLLECTION_POSITION + " TEXT)";

        db.execSQL(CREATE_COLLECTION);

        String CREATE_CATEGORY = "CREATE TABLE " + ONN_CATEGORIES + " ("
                + COLUMN_CATEGORY_ID + " TEXT, "
                + COLUMN_CATEGORY_NAME + " TEXT, "
                + COLUMN_CATEGORY_PARENT + " TEXT)";

        db.execSQL(CREATE_CATEGORY);

        String CREATE_DISTRIBUTOR = "CREATE TABLE " + ONN_DISTRIBUTORS + " ("
                + COLUMN_DISTRIBUTOR_ID + " TEXT, "
                + COLUMN_DISTRIBUTOR_NAME + " TEXT, "
                + COLUMN_DISTRIBUTOR_BUSINESS_NAME + " TEXT, "
                + COLUMN_DISTRIBUTOR_OCC_NUMBER + " TEXT, "
                + COLUMN_DISTRIBUTOR_CONTACT + " TEXT, "
                + COLUMN_DISTRIBUTOR_EMAIL + " TEXT, "
                + COLUMN_DISTRIBUTOR_WHATSAPP + " TEXT, "
                + COLUMN_DISTRIBUTOR_ADDRESS + " TEXT, "
                + COLUMN_DISTRIBUTOR_AREA + " TEXT, "
                + COLUMN_DISTRIBUTOR_STATE + " TEXT, "
                + COLUMN_DISTRIBUTOR_CITY + " TEXT, "
                + COLUMN_DISTRIBUTOR_PIN + " TEXT, "
                + COLUMN_DISTRIBUTOR_IMAGE + " TEXT, "
                + COLUMN_DISTRIBUTOR_DISTRIBUTOR_ID + " TEXT)";

        db.execSQL(CREATE_DISTRIBUTOR);

        String CREATE_DISTRIBUTOR_MOM = "CREATE TABLE " + ONN_DISTRIBUTORS_MOM + " ("
                + COLUMN_MOM_ID + " TEXT, "
                + COLUMN_MOM_USER_ID + " TEXT, "
                + COLUMN_MOM_DISTRIBUTOR_NAME + " TEXT, "
                + COLUMN_MOM_COMMENT + " TEXT)";

        db.execSQL(CREATE_DISTRIBUTOR_MOM);

        String CREATE_PRODUCTS = "CREATE TABLE " + ONN_PRODUCTS + " ("
                + COLUMN_PRODUCT_ID + " TEXT, "
                + COLUMN_PRODUCT_NAME + " TEXT, "
                + COLUMN_PRODUCT_IMAGE + " TEXT, "
                + COLUMN_PRODUCT_STYLE_NO + " TEXT, "
                + COLUMN_PRODUCT_MASTER_PACK_COUNT + " TEXT, "
                + COLUMN_PRODUCT_COLLECTION_ID + " TEXT, "
                + COLUMN_PRODUCT_CATEGORY_ID + " TEXT)";

        db.execSQL(CREATE_PRODUCTS);

        String CREATE_COLORS = "CREATE TABLE " + ONN_COLORS + " ("
                + COLUMN_COLOR_NAME + " TEXT, "
                + COLUMN_COLOR_CODE + " TEXT, "
                + COLUMN_COLOR_PRODUCT_ID + " TEXT)";

        db.execSQL(CREATE_COLORS);

        String CREATE_SIZES = "CREATE TABLE " + ONN_SIZES + " ("
                + COLUMN_SIZE_ID + " TEXT, "
                + COLUMN_SIZE_SIZE + " TEXT, "
                + COLUMN_SIZE_PRICE + " TEXT, "
                + COLUMN_SIZE_OFFER_PRICE + " TEXT, "
                + COLUMN_SIZE_QTY + " TEXT, "
                + COLUMN_SIZE_DESCRIPTION + " TEXT, "
                + COLUMN_SIZE_PRODUCT_ID + " TEXT, "
                + COLUMN_SIZE_COLOR_CODE + " TEXT)";

        db.execSQL(CREATE_SIZES);

        String CREATE_CARTS = "CREATE TABLE " + ONN_CARTS + " ("
                + COLUMN_CART_ID + " TEXT, "
                + COLUMN_CART_PRODUCT_NAME + " TEXT, "
                + COLUMN_CART_PRODUCT_STYLE + " TEXT, "
                + COLUMN_CART_COLOR + " TEXT, "
                + COLUMN_CART_SIZE + " TEXT, "
                + COLUMN_CART_QTY + " TEXT, "
                + COLUMN_CART_OFFER_PRICE + " TEXT)";

        db.execSQL(CREATE_CARTS);

        String CREATE_ORDERS = "CREATE TABLE " + ONN_ORDERS + " ("
                + COLUMN_ORDER_ID + " TEXT, "
                + COLUMN_ORDER_USER_ID + " TEXT, "
                + COLUMN_ORDER_STORE_ID + " TEXT, "
                + COLUMN_ORDER_ORDER_TYPE + " TEXT, "
                + COLUMN_ORDER_COMMENT + " TEXT, "
                + COLUMN_ORDER_ORDER_DATE + " TEXT)";

        db.execSQL(CREATE_ORDERS);

        String CREATE_ORDER_PRODUCTS = "CREATE TABLE " + ONN_ORDER_PRODUCTS + " ("
                + COLUMN_ORDER_PRODUCTS_ID + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_PRODUCT_NAME + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_PRODUCT_STYLE + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_COLOR + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_SIZE + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_QTY + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_OFFER_PRICE + " TEXT, "
                + COLUMN_ORDER_PRODUCTS_ORDER_ID + " TEXT)";

        db.execSQL(CREATE_ORDER_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_AREAS);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_STORES);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_COLLECTIONS);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_DISTRIBUTORS);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_DISTRIBUTORS_MOM);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_COLORS);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_SIZES);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS "+ ONN_ORDER_PRODUCTS);
        onCreate(db);
    }

    /**
     * This method is for adding an area
     * @param areaModel
     */
    public void addAreaData(AreaModel areaModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_ID, areaModel.getId());
        values.put(COLUMN_NAME, areaModel.getName());
        values.put(COLUMN_STATE, areaModel.getState());
        // Inserting Row
        db.insert(ONN_AREAS, null, values);
        db.close();
    }

    /**
     * This method is for fetching area list
     * @return area list
     */
    public ArrayList<AreaModel> areaList(){
        ArrayList<AreaModel> areaModelArrayList = new ArrayList<AreaModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_AREAS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                AreaModel areaModel = new AreaModel();

                areaModel.setId(cursor.getString(0));
                areaModel.setName(cursor.getString(1));
                areaModel.setState(cursor.getString(2));

                areaModelArrayList.add(areaModel);
            } while (cursor.moveToNext());
        }
        return areaModelArrayList;
    }

    /**
     * This method is for deleting areas
     */
    public void deleteAllAreas() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_AREAS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a store
     * @param storeModel
     */
    public void addStoreData(StoreModel storeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_STORE_ID, storeModel.getId());
        values.put(COLUMN_STORE_NAME, storeModel.getStore_name());
        values.put(COLUMN_BUSINESS_NAME, storeModel.getBussiness_name());
        values.put(COLUMN_STORE_OCC_NUMBER, storeModel.getStore_OCC_number());
        values.put(COLUMN_CONTACT, storeModel.getContact());
        values.put(COLUMN_EMAIL, storeModel.getEmail());
        values.put(COLUMN_WHATSAPP, storeModel.getWhatsapp());
        values.put(COLUMN_ADDRESS, storeModel.getAddress());
        values.put(COLUMN_AREA, storeModel.getArea());
        values.put(COLUMN_STORE_STATE, storeModel.getState());
        values.put(COLUMN_CITY, storeModel.getCity());
        values.put(COLUMN_PIN, storeModel.getPin());
        values.put(COLUMN_IMAGE, storeModel.getImage());
        // Inserting Row
        db.insert(ONN_STORES, null, values);
        db.close();
    }

    /**
     * This method is for fetching store list
     * @return store list
     */
    public ArrayList<StoreModel> fetchStoreList(){
        ArrayList<StoreModel> storeModelArrayList = new ArrayList<StoreModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_STORES ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                StoreModel storeModel = new StoreModel();

                storeModel.setId(cursor.getString(0));
                storeModel.setStore_name(cursor.getString(1));
                storeModel.setBussiness_name(cursor.getString(2));
                storeModel.setStore_OCC_number(cursor.getString(3));
                storeModel.setContact(cursor.getString(4));
                storeModel.setEmail(cursor.getString(5));
                storeModel.setWhatsapp(cursor.getString(6));
                storeModel.setAddress(cursor.getString(7));
                storeModel.setArea(cursor.getString(8));
                storeModel.setState(cursor.getString(9));
                storeModel.setCity(cursor.getString(10));
                storeModel.setPin(cursor.getString(11));
                storeModel.setImage(cursor.getString(12));

                storeModelArrayList.add(storeModel);
            } while (cursor.moveToNext());
        }
        return storeModelArrayList;
    }

    /**
     * This method is for deleting stores
     */
    public void deleteAllStoreData() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_STORES ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a collection
     * @param collectionModel
     */
    public void addCollectionData(CollectionModel collectionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_COLLECTION_ID, collectionModel.getId());
        values.put(COLUMN_COLLECTION_NAME, collectionModel.getName());
        values.put(COLUMN_COLLECTION_POSITION, collectionModel.getPosition());
        // Inserting Row
        db.insert(ONN_COLLECTIONS, null, values);
        db.close();
    }

    /**
     * This method is for fetching collection list
     * @return collection list
     */
    public ArrayList<CollectionModel> collectionList(){
        ArrayList<CollectionModel> collectionModelArrayList = new ArrayList<CollectionModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_COLLECTIONS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CollectionModel collectionModel = new CollectionModel();

                collectionModel.setId(cursor.getString(0));
                collectionModel.setName(cursor.getString(1));
                collectionModel.setPosition(cursor.getString(2));

                collectionModelArrayList.add(collectionModel);
            } while (cursor.moveToNext());
        }
        return collectionModelArrayList;
    }

    /**
     * This method is for deleting collections
     */
    public void deleteAllCollections() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_COLLECTIONS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a distributor
     * @param distributorModel
     */
    public void addDistributorData(DistributorModel distributorModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_DISTRIBUTOR_ID, distributorModel.getId());
        values.put(COLUMN_DISTRIBUTOR_NAME, distributorModel.getStore_name());
        values.put(COLUMN_DISTRIBUTOR_BUSINESS_NAME, distributorModel.getBussiness_name());
        values.put(COLUMN_DISTRIBUTOR_OCC_NUMBER, distributorModel.getStore_OCC_number());
        values.put(COLUMN_DISTRIBUTOR_CONTACT, distributorModel.getContact());
        values.put(COLUMN_DISTRIBUTOR_EMAIL, distributorModel.getEmail());
        values.put(COLUMN_DISTRIBUTOR_WHATSAPP, distributorModel.getWhatsapp());
        values.put(COLUMN_DISTRIBUTOR_ADDRESS, distributorModel.getAddress());
        values.put(COLUMN_DISTRIBUTOR_AREA, distributorModel.getArea());
        values.put(COLUMN_DISTRIBUTOR_STATE, distributorModel.getState());
        values.put(COLUMN_DISTRIBUTOR_CITY, distributorModel.getCity());
        values.put(COLUMN_DISTRIBUTOR_PIN, distributorModel.getPin());
        values.put(COLUMN_DISTRIBUTOR_IMAGE, distributorModel.getImage());
        values.put(COLUMN_DISTRIBUTOR_DISTRIBUTOR_ID, distributorModel.getDistributor_id());
        // Inserting Row
        db.insert(ONN_DISTRIBUTORS, null, values);
        db.close();
    }

    /**
     * This method is for fetching distributor list
     * @return distributor list
     */
    public ArrayList<DistributorModel> fetchDistributorList(){
        ArrayList<DistributorModel> distributorModelArrayList = new ArrayList<DistributorModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_DISTRIBUTORS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                DistributorModel distributorModel = new DistributorModel();

                distributorModel.setId(cursor.getString(0));
                distributorModel.setStore_name(cursor.getString(1));
                distributorModel.setBussiness_name(cursor.getString(2));
                distributorModel.setStore_OCC_number(cursor.getString(3));
                distributorModel.setContact(cursor.getString(4));
                distributorModel.setEmail(cursor.getString(5));
                distributorModel.setWhatsapp(cursor.getString(6));
                distributorModel.setAddress(cursor.getString(7));
                distributorModel.setArea(cursor.getString(8));
                distributorModel.setState(cursor.getString(9));
                distributorModel.setCity(cursor.getString(10));
                distributorModel.setPin(cursor.getString(11));
                distributorModel.setImage(cursor.getString(12));
                distributorModel.setDistributor_id(cursor.getString(13));

                distributorModelArrayList.add(distributorModel);
            } while (cursor.moveToNext());
        }
        return distributorModelArrayList;
    }

    /**
     * This method is for deleting distributors
     */
    public void deleteAllDistributors() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_DISTRIBUTORS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a category
     * @param categoryModel
     */
    public void addCategoryData(CategoryModel categoryModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_CATEGORY_ID, categoryModel.getId());
        values.put(COLUMN_CATEGORY_NAME, categoryModel.getName());
        values.put(COLUMN_CATEGORY_PARENT, categoryModel.getParent());

        // Inserting Row
        db.insert(ONN_CATEGORIES, null, values);
        db.close();
    }

    /**
     * This method is for fetching category list
     * @return category list
     */
    public ArrayList<CategoryModel> categoryList(){
        ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_CATEGORIES ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CategoryModel categoryModel = new CategoryModel();

                categoryModel.setId(cursor.getString(0));
                categoryModel.setName(cursor.getString(1));
                categoryModel.setParent(cursor.getString(2));

                categoryModelArrayList.add(categoryModel);
            } while (cursor.moveToNext());
        }
        return categoryModelArrayList;
    }

    /**
     * This method is for collection wise category list
     * @param collectionId
     * @return
     */
    public ArrayList<CategoryModel> collectionWiseCategoryList(String collectionId){
        ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<CategoryModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_CATEGORIES+" WHERE "+COLUMN_CATEGORY_PARENT+" = "+collectionId ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CategoryModel categoryModel = new CategoryModel();

                categoryModel.setId(cursor.getString(0));
                categoryModel.setName(cursor.getString(1));
                categoryModel.setParent(cursor.getString(2));

                categoryModelArrayList.add(categoryModel);
            } while (cursor.moveToNext());
        }
        return categoryModelArrayList;
    }

    /**
     * This method is for deleting categories
     */
    public void deleteAllCategories() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_CATEGORIES ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a distributor MOM
     * @param momDbModel
     */
    public void addDistributorMomData(MomDbModel momDbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_MOM_ID, momDbModel.getId());
        values.put(COLUMN_MOM_USER_ID, momDbModel.getUser_id());
        values.put(COLUMN_MOM_DISTRIBUTOR_NAME, momDbModel.getDistributor_name());
        values.put(COLUMN_MOM_COMMENT, momDbModel.getComment());
        // Inserting Row
        db.insert(ONN_DISTRIBUTORS_MOM, null, values);
        db.close();
    }

    /**
     * This method is for fetching distributor mom list
     * @return mom list
     */
    public ArrayList<MomDbModel> momLists(){
        ArrayList<MomDbModel> momDbModelArrayList = new ArrayList<MomDbModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_DISTRIBUTORS_MOM ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                MomDbModel momDbModel = new MomDbModel();

                momDbModel.setId(cursor.getString(0));
                momDbModel.setUser_id(cursor.getString(1));
                momDbModel.setDistributor_name(cursor.getString(2));
                momDbModel.setComment(cursor.getString(3));

                momDbModelArrayList.add(momDbModel);
            } while (cursor.moveToNext());
        }
        return momDbModelArrayList;
    }

    /**
     * This method is for deleting distributor MOM
     */
    public void deleteAllMOM() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_DISTRIBUTORS_MOM ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a product
     * @param productModel
     */
    public void addProductData(ProductModel productModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_PRODUCT_ID, productModel.getId());
        values.put(COLUMN_PRODUCT_NAME, productModel.getName());
        values.put(COLUMN_PRODUCT_IMAGE, productModel.getImage());
        values.put(COLUMN_PRODUCT_STYLE_NO, productModel.getStyle_no());
        values.put(COLUMN_PRODUCT_MASTER_PACK_COUNT, productModel.getMaster_pack_count());
        values.put(COLUMN_PRODUCT_COLLECTION_ID, productModel.getCollection_id());
        values.put(COLUMN_PRODUCT_CATEGORY_ID, productModel.getCategory_id());

        // Inserting Row
        db.insert(ONN_PRODUCTS, null, values);
        db.close();
    }

    /**
     * This method is for fetching all products
     * @return product list
     */
    public ArrayList<ProductModel> allProductList(){
        ArrayList<ProductModel> productModelArrayList = new ArrayList<ProductModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_PRODUCTS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ProductModel productModel = new ProductModel();

                productModel.setId(cursor.getString(0));
                productModel.setName(cursor.getString(1));
                productModel.setImage(cursor.getString(2));
                productModel.setStyle_no(cursor.getString(3));
                productModel.setMaster_pack_count(cursor.getString(3));
                productModel.setCollection_id(cursor.getString(3));
                productModel.setCategory_id(cursor.getString(3));

                productModelArrayList.add(productModel);
            } while (cursor.moveToNext());
        }
        return productModelArrayList;
    }

    /**
     * This method is getting product list, collection and category wise
     * @param collectioId
     * @param categoryId
     * @return product list
     */
    public ArrayList<ProductModel> filterProductList(String collectioId,String categoryId){
        ArrayList<ProductModel> productModelArrayList = new ArrayList<ProductModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_PRODUCTS +" WHERE "+COLUMN_PRODUCT_COLLECTION_ID+" = "+collectioId+" AND "
                +COLUMN_PRODUCT_CATEGORY_ID+" = "+categoryId;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ProductModel productModel = new ProductModel();

                productModel.setId(cursor.getString(0));
                productModel.setName(cursor.getString(1));
                productModel.setImage(cursor.getString(2));
                productModel.setStyle_no(cursor.getString(3));
                productModel.setMaster_pack_count(cursor.getString(3));
                productModel.setCollection_id(cursor.getString(3));
                productModel.setCategory_id(cursor.getString(3));

                productModelArrayList.add(productModel);
            } while (cursor.moveToNext());
        }
        return productModelArrayList;
    }

    /**
     * This method is for deleting product
     */
    public void deleteAllProducts() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_PRODUCTS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a color
     * @param colorModel
     */
    public void addColorData(ColorModel colorModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_COLOR_NAME, colorModel.getName());
        values.put(COLUMN_COLOR_CODE, colorModel.getCode());
        values.put(COLUMN_COLOR_PRODUCT_ID, colorModel.getProduct_id());
        // Inserting Row
        db.insert(ONN_COLORS, null, values);
        db.close();
    }

    /**
     * This method is for fetching all colors
     * @return color list
     */
    public ArrayList<ColorModel> allColorList(){
        ArrayList<ColorModel> colorModelArrayList = new ArrayList<ColorModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_COLORS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ColorModel colorModel = new ColorModel();

                colorModel.setName(cursor.getString(0));
                colorModel.setCode(cursor.getString(1));
                colorModel.setProduct_id(cursor.getString(2));

                colorModelArrayList.add(colorModel);
            } while (cursor.moveToNext());
        }
        return colorModelArrayList;
    }

    /**
     * This method is for fetching colors product wise
     * @param productId
     * @return color list
     */
    public ArrayList<ColorModel> filterColorList(String productId){
        ArrayList<ColorModel> colorModelArrayList = new ArrayList<ColorModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_COLORS+" WHERE "+COLUMN_COLOR_PRODUCT_ID+" = "+productId ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ColorModel colorModel = new ColorModel();

                colorModel.setName(cursor.getString(0));
                colorModel.setCode(cursor.getString(1));
                colorModel.setProduct_id(cursor.getString(2));

                colorModelArrayList.add(colorModel);
            } while (cursor.moveToNext());
        }
        return colorModelArrayList;
    }

    /**
     * This method is to delete all color data
     */
    public void deleteAllColors() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_COLORS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a size
     * @param sizeModel
     */
    public void addSizeData(SizeModel sizeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_SIZE_ID, sizeModel.getId());
        values.put(COLUMN_SIZE_SIZE, sizeModel.getSize());
        values.put(COLUMN_SIZE_PRICE, sizeModel.getPrice());
        values.put(COLUMN_SIZE_OFFER_PRICE, sizeModel.getOffer_price());
        values.put(COLUMN_SIZE_QTY, sizeModel.getQty());
        values.put(COLUMN_SIZE_DESCRIPTION, sizeModel.getDescription());
        values.put(COLUMN_SIZE_PRODUCT_ID, sizeModel.getProduct_id());
        values.put(COLUMN_SIZE_COLOR_CODE, sizeModel.getColor_code());
        // Inserting Row
        db.insert(ONN_SIZES, null, values);
        db.close();
    }

    /**
     * This method is for fetching size list
     * @return size list
     */
    public ArrayList<SizeModel> allSizeList(){
        ArrayList<SizeModel> sizeModelArrayList = new ArrayList<SizeModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_SIZES ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                SizeModel sizeModel = new SizeModel();

                sizeModel.setId(cursor.getString(0));
                sizeModel.setSize(cursor.getString(1));
                sizeModel.setPrice(cursor.getString(2));
                sizeModel.setOffer_price(cursor.getString(3));
                sizeModel.setQty(cursor.getString(4));
                sizeModel.setDescription(cursor.getString(5));
                sizeModel.setProduct_id(cursor.getString(6));
                sizeModel.setColor_code(cursor.getString(7));

                sizeModelArrayList.add(sizeModel);
            } while (cursor.moveToNext());
        }
        return sizeModelArrayList;
    }

    /**
     * This method is for fetching size list product and color wise
     * @param productId
     * @param colorCode
     * @return size list
     */
    public ArrayList<SizeModel> filterSizeList(String productId, String colorCode){
        ArrayList<SizeModel> sizeModelArrayList = new ArrayList<SizeModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_SIZES + " WHERE "+COLUMN_SIZE_PRODUCT_ID+" = "+productId
                +" AND "+COLUMN_SIZE_COLOR_CODE+" = "+colorCode;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                SizeModel sizeModel = new SizeModel();

                sizeModel.setId(cursor.getString(0));
                sizeModel.setSize(cursor.getString(1));
                sizeModel.setPrice(cursor.getString(2));
                sizeModel.setOffer_price(cursor.getString(3));
                sizeModel.setQty(cursor.getString(4));
                sizeModel.setDescription(cursor.getString(5));
                sizeModel.setProduct_id(cursor.getString(6));
                sizeModel.setColor_code(cursor.getString(7));

                sizeModelArrayList.add(sizeModel);
            } while (cursor.moveToNext());
        }
        return sizeModelArrayList;
    }

    /**
     * This method is to delete a size
     */
    public void deleteAllSizes() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_SIZES ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a cart data
     * @param cartModel
     */
    public void addCartData(CartModel cartModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_CART_ID, cartModel.getId());
        values.put(COLUMN_CART_PRODUCT_NAME, cartModel.getProduct_name());
        values.put(COLUMN_CART_PRODUCT_STYLE, cartModel.getProduct_style());
        values.put(COLUMN_CART_COLOR, cartModel.getColor());
        values.put(COLUMN_CART_SIZE, cartModel.getSize());
        values.put(COLUMN_CART_QTY, cartModel.getQty());
        values.put(COLUMN_CART_OFFER_PRICE, cartModel.getOffer_price());

        // Inserting Row
        db.insert(ONN_CARTS, null, values);
        db.close();
    }

    /**
     * This method is for fetching cart list
     * @return
     */
    public ArrayList<CartModel> allCartList(){
        ArrayList<CartModel> cartModelArrayList = new ArrayList<CartModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_CARTS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CartModel cartModel = new CartModel();

                cartModel.setId(cursor.getString(0));
                cartModel.setProduct_name(cursor.getString(1));
                cartModel.setProduct_style(cursor.getString(2));
                cartModel.setColor(cursor.getString(3));
                cartModel.setSize(cursor.getString(4));
                cartModel.setQty(cursor.getString(5));
                cartModel.setOffer_price(cursor.getString(6));

                cartModelArrayList.add(cartModel);
            } while (cursor.moveToNext());
        }
        return cartModelArrayList;
    }

    /**
     * This method is to delete a size
     */
    public void deleteCartData() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_CARTS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method to delete particular cart data
     * @param id
     */
    public void deleteParticularCartData(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_CARTS +" WHERE "+COLUMN_CART_ID+" = "+id;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding a order
     * @param orderDbModel
     */
    public void addOrderData(OrderDbModel orderDbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_ORDER_ID, orderDbModel.getId());
        values.put(COLUMN_ORDER_USER_ID, orderDbModel.getUser_id());
        values.put(COLUMN_ORDER_STORE_ID, orderDbModel.getStore_id());
        values.put(COLUMN_ORDER_ORDER_TYPE, orderDbModel.getOrder_type());
        values.put(COLUMN_ORDER_COMMENT, orderDbModel.getComment());
        values.put(COLUMN_ORDER_ORDER_DATE, orderDbModel.getOrder_date());

        // Inserting Row
        db.insert(ONN_ORDERS, null, values);
        db.close();
    }

    /**
     * This method is for getting all order list
     * @return order list
     */
    public ArrayList<OrderDbModel> allOrderList(){
        ArrayList<OrderDbModel> orderDbModelArrayList = new ArrayList<OrderDbModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_ORDERS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                OrderDbModel orderDbModel = new OrderDbModel();

                orderDbModel.setId(cursor.getString(0));
                orderDbModel.setUser_id(cursor.getString(1));
                orderDbModel.setStore_id(cursor.getString(2));
                orderDbModel.setOrder_type(cursor.getString(3));
                orderDbModel.setComment(cursor.getString(4));
                orderDbModel.setOrder_date(cursor.getString(5));

                orderDbModelArrayList.add(orderDbModel);
            } while (cursor.moveToNext());
        }
        return orderDbModelArrayList;
    }

    /**
     * This method is to delete order data
     */
    public void deleteOrderData() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_ORDERS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }

    /**
     * This method is for adding data to order products
     * @param orderProductModel
     */
    public void addOrderProductData(OrderProductModel orderProductModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //Set values
        values.put(COLUMN_ORDER_PRODUCTS_ID, orderProductModel.getId());
        values.put(COLUMN_ORDER_PRODUCTS_PRODUCT_NAME, orderProductModel.getProduct_name());
        values.put(COLUMN_ORDER_PRODUCTS_PRODUCT_STYLE, orderProductModel.getProduct_style());
        values.put(COLUMN_ORDER_PRODUCTS_COLOR, orderProductModel.getColor());
        values.put(COLUMN_ORDER_PRODUCTS_SIZE, orderProductModel.getSize());
        values.put(COLUMN_ORDER_PRODUCTS_QTY, orderProductModel.getQty());
        values.put(COLUMN_ORDER_PRODUCTS_OFFER_PRICE, orderProductModel.getOffer_price());
        values.put(COLUMN_ORDER_PRODUCTS_ORDER_ID, orderProductModel.getOrder_id());

        // Inserting Row
        db.insert(ONN_ORDER_PRODUCTS, null, values);
        db.close();
    }

    /**
     * This method is for fetching cart list
     * @return order product list
     */
    public ArrayList<OrderProductModel> allOrderProductList(){
        ArrayList<OrderProductModel> orderProductModelArrayList = new ArrayList<OrderProductModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_ORDER_PRODUCTS ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                OrderProductModel orderProductModel = new OrderProductModel();

                orderProductModel.setId(cursor.getString(0));
                orderProductModel.setProduct_name(cursor.getString(1));
                orderProductModel.setProduct_style(cursor.getString(2));
                orderProductModel.setColor(cursor.getString(3));
                orderProductModel.setSize(cursor.getString(4));
                orderProductModel.setQty(cursor.getString(5));
                orderProductModel.setOffer_price(cursor.getString(6));
                orderProductModel.setOrder_id(cursor.getString(7));

                orderProductModelArrayList.add(orderProductModel);
            } while (cursor.moveToNext());
        }
        return orderProductModelArrayList;
    }

    /**
     * This method is for fetching order wise products
     * @param orderId
     * @return order product list
     */
    public ArrayList<OrderProductModel> orderWiseProductList(String orderId){
        ArrayList<OrderProductModel> orderProductModelArrayList = new ArrayList<OrderProductModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query = "SELECT * FROM " + ONN_ORDER_PRODUCTS + " WHERE "+ COLUMN_ORDER_PRODUCTS_ORDER_ID + " = "+orderId ;
        //Query execution
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                OrderProductModel orderProductModel = new OrderProductModel();

                orderProductModel.setId(cursor.getString(0));
                orderProductModel.setProduct_name(cursor.getString(1));
                orderProductModel.setProduct_style(cursor.getString(2));
                orderProductModel.setColor(cursor.getString(3));
                orderProductModel.setSize(cursor.getString(4));
                orderProductModel.setQty(cursor.getString(5));
                orderProductModel.setOffer_price(cursor.getString(6));
                orderProductModel.setOrder_id(cursor.getString(7));

                orderProductModelArrayList.add(orderProductModel);
            } while (cursor.moveToNext());
        }
        return orderProductModelArrayList;
    }

    /**
     * This method is for deleting order products
     */
    public void deleteOrderProductData() {

        SQLiteDatabase db = this.getWritableDatabase();
        //Writing a query
        String query  = "DELETE FROM " + ONN_ORDER_PRODUCTS ;
        //Query execution
        db.execSQL(query);
        db.close();
    }
}
