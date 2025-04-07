package sys.diag.car;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CarRepository {
    private static CarRepository instanse;
    private SQLiteDatabase _db;
    private DataBaseHelper _dbHelper;

    // Constructor now takes a Context
    public CarRepository(Context context) {
        _dbHelper = DataBaseHelper.getInstance(context);
        _db = _dbHelper.getWritableDatabase();
    }
    public synchronized static CarRepository getInstanse(Context context){
        if(instanse==null){
            instanse=new CarRepository(context);
        }
        return  instanse;
    }

    // Close the database when you're done with it
    public void close() {
        _dbHelper.close();
    }

    // --- CREATE ---
    public long addCar(Car car) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_NAME_MARK, car.getMarkCar());
        values.put(DataBaseHelper.COLUMN_NAME_MODEL, car.getModelCar());
        values.put(DataBaseHelper.COLUMN_NAME_YEAR, car.getYearRelease());
        values.put(DataBaseHelper.COLUMN_NAME_ISSUE, car.getIssueBroken());
        values.put(DataBaseHelper.COLUMN_PATH_IMAGE, car.getImageUri());

        long newRowId = _db.insert(DataBaseHelper.TABLE_NAME, null, values);
        Log.d("CarRepository", "addCar: newRowId = " + newRowId);
        return newRowId; // Returns the ID of the newly inserted row, or -1 if there was an error
    }

    // --- READ (ALL) ---
    public List<Car> getCars() {
        List<Car> carList = new ArrayList<>();
        Cursor cursor = _db.query(
                DataBaseHelper.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (null for all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                    // don't filter by row groups
                null               // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Car car = cursorToCar(cursor);
                carList.add(car);
                Log.e("LOAD_DATA_2",String.valueOf(car.getId()));
                Log.e("LOAD_DATA_2-1",String.valueOf(carList.get(0).getId()));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return carList;
    }

    // --- READ (ONE BY ID) ---
    public Car getCarById(long id) {
        Cursor cursor = _db.query(
                DataBaseHelper.TABLE_NAME,
                null,
                DataBaseHelper.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Car car = null;
        if (cursor != null && cursor.moveToFirst()) {
            car = cursorToCar(cursor);
            cursor.close();
        }

        return car;
    }

    // --- UPDATE ---
    public int updateCar(Car car) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_NAME_MARK, car.getMarkCar());
        values.put(DataBaseHelper.COLUMN_NAME_MODEL, car.getModelCar());
        values.put(DataBaseHelper.COLUMN_NAME_YEAR, car.getYearRelease());
        values.put(DataBaseHelper.COLUMN_NAME_ISSUE, car.getIssueBroken());

        return _db.update(
                DataBaseHelper.TABLE_NAME,
                values,
                DataBaseHelper.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(car.getId())}
        );
    }

    // --- DELETE ---
    public int deleteCar(long id) {
        return _db.delete(
                DataBaseHelper.TABLE_NAME,
                DataBaseHelper.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // --- Helper method to convert a Cursor row to a Car object ---
    private Car cursorToCar(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME_ID));
        String mark = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME_MARK));
        String model = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME_MODEL));
        String year = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME_YEAR));
        String issue = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_NAME_ISSUE));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_PATH_IMAGE));
        Log.e("LOAD_DATA",String.valueOf(id));
        return new Car(id, mark, model, year,issue,image);
    }
}