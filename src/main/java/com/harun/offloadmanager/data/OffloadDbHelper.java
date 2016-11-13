package com.harun.offloadmanager.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import static com.harun.offloadmanager.data.OffloadContract.TransactionEntry;
import static com.harun.offloadmanager.data.OffloadContract.VehicleEntry;


public class OffloadDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = OffloadDbHelper.class.getSimpleName();
    private Context mContext;

    private static final String DATABASE_NAME = "Offloader002_DataBase.db";
    private static final int DATABASE_VERSION = 1;

    public OffloadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
//        Toast.makeText(mContext, context.getString(R.string.constructor_call), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            String SQL_CREATE_VEHICLE_TABLE = "CREATE TABLE "
                    + VehicleEntry.TABLE_NAME + " ("
                    + VehicleEntry.COLUMN_VEHICLE_REGISTRATION + " TEXT PRIMARY KEY, "
                    + VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION + " REAL NOT NULL, "
                    + VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE + " REAL NOT NULL, "
                    + VehicleEntry.COLUMN_VEHICLE_REGISTRATION_DATE + " INTEGER NOT NULL, "
                    + VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME + " INTEGER NOT NULL, "

                    + "UNIQUE (" + VehicleEntry.COLUMN_VEHICLE_REGISTRATION + ") ON CONFLICT REPLACE );";

            String SQL_CREATE_TRANSACTION_TABLE = "CREATE TABLE "
                    + TransactionEntry.TABLE_NAME + " ("
                    + TransactionEntry.COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TransactionEntry.COLUMN_VEHICLE_KEY + " INTEGER NOT NULL, "
                    + TransactionEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                    + TransactionEntry.COLUMN_TYPE + " INTEGER NOT NULL, "
                    + TransactionEntry.COLUMN_DESCRIPTION + " VARCHAR(255), "
                    + TransactionEntry.COLUMN_DATE_TIME + " INTEGER NOT NULL, "
                    + TransactionEntry.COLUMN_SYNC + " INTEGER NOT NULL, "
                    + "FOREIGN KEY (" + TransactionEntry.COLUMN_VEHICLE_KEY + ") REFERENCES "
                    + VehicleEntry.TABLE_NAME + "(" + VehicleEntry.COLUMN_VEHICLE_REGISTRATION +
                    ") " + ");";

            sqLiteDatabase.execSQL(SQL_CREATE_VEHICLE_TABLE);
            sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTION_TABLE);

            Log.w(LOG_TAG, "onCreate:  SQL_CREATE_VEHICLE_TABLE, SQL_CREATE_TRANSACTION_TABLE" );

        } catch (SQLException e) {
            Toast.makeText(mContext, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        try {
            String SQL_DROP_VEHICLE_TABLE = "DROP TABLE IF EXISTS " + VehicleEntry.TABLE_NAME;
            sqLiteDatabase.execSQL(SQL_DROP_VEHICLE_TABLE);
            String SQL_DROP_TRANSACTION_TABLE = "DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME;
            sqLiteDatabase.execSQL(SQL_DROP_TRANSACTION_TABLE);
            onCreate(sqLiteDatabase);

        } catch (SQLException e) {
            Toast.makeText(mContext, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteDatabase(){
        mContext.deleteDatabase(DATABASE_NAME);
    }

}

