package com.example.expensetracker.db;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.expensetracker.ListModel;
import com.example.expensetracker.R;

import java.util.ArrayList;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "my_expenses_app.db";

    private static final String CATEGORIES_TABLE_NAME = "categories";
    private static final String EXPENSES_TABLE_NAME = "expenses";

    private static final String COL_1 = "catID";
    private static final String COL_2 = "CATEGORY";

    private static final String COL_expId = "expenses_ID";
    private static final String COL_expCat = "expenseCategory";
    public static final String COL_expAmount = "expenseAmount";
    public static final String COL_total = "expenseTotal";
    public static final String COL_date = "date";

    private Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + CATEGORIES_TABLE_NAME +
                //" ( ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " ( " + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT NOT NULL)";
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORIES);


        String CREATE_TABLE_EXPENSES = "CREATE TABLE " + EXPENSES_TABLE_NAME +
                " ( " + COL_expId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_expCat + " TEXT NOT NULL, " +
                COL_total + " INTEGER NOT NULL, " +
                COL_date + " TEXT NOT NULL, " +
                COL_expAmount + " INTEGER NOT NULL)";
        sqLiteDatabase.execSQL(CREATE_TABLE_EXPENSES);


        ContentValues contentValues = new ContentValues();
        Resources resources = context.getResources();
        String[] mArray = resources.getStringArray(R.array.initial_categories);
        for (String cats : mArray) {
            contentValues.put(COL_2, cats);
            sqLiteDatabase.insert(CATEGORIES_TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public boolean addExp(String category, int amount, int total, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_expCat, category);
        contentValues.put(COL_expAmount, amount);
        contentValues.put(COL_total, total);
        contentValues.put(COL_date, date);
        long result = db.insert(EXPENSES_TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1)
            return false;
        else return true;
    }

    public ArrayList<ListModel> getExpensesList() {
        ArrayList<ListModel> listOfExpenses = new ArrayList();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        try {
            int catIndex = cursor.getColumnIndex(DataBaseHelper.COL_expCat);
            int amountIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);
            int dateIndex = cursor.getColumnIndex(DataBaseHelper.COL_date);

            // Check if the columns exist in the result set
            if (catIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                while (cursor.moveToNext()) {
                    String categoryName = cursor.getString(catIndex);
                    String amount = cursor.getString(amountIndex);
                    String date = cursor.getString(dateIndex);
                    ListModel listModel = new ListModel(categoryName, amount, date);
                    listOfExpenses.add(listModel);
                }
            } else {
                // Handle the case where one or more columns are not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return listOfExpenses;
    }


    public int total() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT " + COL_expAmount + " FROM " + EXPENSES_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int colIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);

            // Check if the column exists in the result set
            if (colIndex != -1) {
                while (cursor.moveToNext()) {
                    total = total + cursor.getInt(colIndex);
                }
            } else {
                // Handle the case where the column is not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return total;
    }


    public int totalForToday() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = date('now', 'localtime')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int colIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);

            // Check if the column exists in the result set
            if (colIndex != -1) {
                while (cursor.moveToNext()) {
                    total = total + cursor.getInt(colIndex);
                }
            } else {
                // Handle the case where the column is not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return total;
    }


    public int totalForWeek() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date
                + " BETWEEN date('now','-6 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int colIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);

            // Check if the column exists in the result set
            if (colIndex != -1) {
                while (cursor.moveToNext()) {
                    total = total + cursor.getInt(colIndex);
                }
            } else {
                // Handle the case where the column is not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return total;
    }


    public int totalForMonth() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " BETWEEN date('now','-29 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int colIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);

            // Check if the column exists in the result set
            if (colIndex != -1) {
                while (cursor.moveToNext()) {
                    total = total + cursor.getInt(colIndex);
                }
            } else {
                // Handle the case where the column is not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return total;
    }


    public int totalForCertainDate(String dateC) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = '" + dateC + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int colIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);

            // Check if the column exists in the result set
            if (colIndex != -1) {
                while (cursor.moveToNext()) {
                    total = total + cursor.getInt(colIndex);
                }
            } else {
                // Handle the case where the column is not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return total;
    }



    public boolean addCat(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, item);
        long result = db.insert(CATEGORIES_TABLE_NAME, null, contentValues);
        db.close();
        if (result == -1)
            return false;
        else return true;
    }

    public ArrayList<String> getCategoriesList() {
        ArrayList<String> listOfCategories = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + CATEGORIES_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int colIndex = cursor.getColumnIndex(DataBaseHelper.COL_2);

            // Check if the column exists in the result set
            if (colIndex != -1) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        listOfCategories.add(cursor.getString(colIndex));
                    }
                } else {
                    // Handle the case where the result set is empty
                    // You might want to log a message or take appropriate action
                    Log.e("CNF","Column not found");

                }
            } else {
                // Handle the case where the column is not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return listOfCategories;
    }


    public ArrayList<ListModel> getTodayData() {
        ArrayList<ListModel> listOfExpenses = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = date('now', 'localtime')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            int catIndex = cursor.getColumnIndex(DataBaseHelper.COL_expCat);
            int amountIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);
            int dateIndex = cursor.getColumnIndex(DataBaseHelper.COL_date);

            // Check if the columns exist in the result set
            if (catIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                while (cursor.moveToNext()) {
                    String categoryName = cursor.getString(catIndex);
                    String amount = cursor.getString(amountIndex);
                    String date = cursor.getString(dateIndex);
                    ListModel listModel = new ListModel(categoryName, amount, date);
                    listOfExpenses.add(listModel);
                }
            } else {
                // Handle the case where columns are not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return listOfExpenses;
    }


    public ArrayList<ListModel> getLasTWeekData() {
        ArrayList<ListModel> listOfExpenses = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date
                + " BETWEEN date('now','-6 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                int catIndex = cursor.getColumnIndex(DataBaseHelper.COL_expCat);
                int amountIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);
                int dateIndex = cursor.getColumnIndex(DataBaseHelper.COL_date);

                // Check if the columns exist in the result set
                if (catIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                    String categoryName = cursor.getString(catIndex);
                    String amount = cursor.getString(amountIndex);
                    String date = cursor.getString(dateIndex);
                    ListModel listModel = new ListModel(categoryName, amount, date);
                    listOfExpenses.add(listModel);
                } else {
                    // Handle the case where columns are not found
                    // You might want to log a message or take appropriate action
                    Log.e("CNF","Column not found");

                }
            }
        } finally {
            cursor.close(); // Close the cursor in a finally block to ensure it gets closed even if an exception occurs
        }

        return listOfExpenses;
    }


    public ArrayList<ListModel> getLastMonthData() {
        ArrayList<ListModel> listOfExpenses = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE "
                + COL_date + " BETWEEN date('now','-29 days') AND date('now')";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int catIndex = cursor.getColumnIndex(DataBaseHelper.COL_expCat);
            int amountIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);
            int dateIndex = cursor.getColumnIndex(DataBaseHelper.COL_date);

            // Check if the columns exist in the result set
            if (catIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                String categoryName = cursor.getString(catIndex);
                String amount = cursor.getString(amountIndex);
                String date = cursor.getString(dateIndex);
                ListModel listModel = new ListModel(categoryName, amount, date);
                listOfExpenses.add(listModel);
            } else {
                // Handle the case where columns are not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");

            }
        }

        cursor.close(); // Close the cursor when done
        return listOfExpenses;
    }


    public ArrayList<ListModel> getCertainDateData(String dateC) {
        ArrayList<ListModel> listOfExpenses = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        String query = "SELECT * FROM " + EXPENSES_TABLE_NAME + " WHERE " + COL_date + " = '" + dateC + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int catIndex = cursor.getColumnIndex(DataBaseHelper.COL_expCat);
            int amountIndex = cursor.getColumnIndex(DataBaseHelper.COL_expAmount);
            int dateIndex = cursor.getColumnIndex(DataBaseHelper.COL_date);

            // Check if the columns exist in the result set
            if (catIndex != -1 && amountIndex != -1 && dateIndex != -1) {
                String categoryName = cursor.getString(catIndex);
                String amount = cursor.getString(amountIndex);
                String date = cursor.getString(dateIndex);
                ListModel listModel = new ListModel(categoryName, amount, date);
                listOfExpenses.add(listModel);
            } else {
                // Handle the case where columns are not found
                // You might want to log a message or take appropriate action
                Log.e("CNF","Column not found");
            }
        }

        cursor.close(); // Close the cursor when done
        return listOfExpenses;
    }


    public void deleteCatRow(String name) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query3 = "DELETE FROM " + CATEGORIES_TABLE_NAME + " WHERE " + COL_2 + " = '" + name + "'";
        sqLiteDatabase.execSQL(query3);

    }


}



