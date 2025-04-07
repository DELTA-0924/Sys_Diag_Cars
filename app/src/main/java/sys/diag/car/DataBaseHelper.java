package sys.diag.car;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cars.db";

    public static final String TABLE_NAME = "cars";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_MARK = "mark";
    public static final String COLUMN_NAME_MODEL = "model";
    public static final String COLUMN_NAME_YEAR = "year";
    public static final String COLUMN_NAME_ISSUE = "issue";
    public static final String COLUMN_PATH_IMAGE = "image_path";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME_MARK + " TEXT," +
                    COLUMN_NAME_MODEL + " TEXT," +
                    COLUMN_NAME_YEAR + " TEXT," +
                    COLUMN_PATH_IMAGE + " TEXT," +
                    COLUMN_NAME_ISSUE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}