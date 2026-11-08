package com.rini.keuanganharian;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TransactionDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "keuangan_harian.db";
    private static final int DB_VERSION = 1;

    public TransactionDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT NOT NULL, amount REAL NOT NULL, " +
                "note TEXT, created_at INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Belum ada migrasi karena ini versi pertama aplikasi.
    }

    public void insert(String type, double amount, String note) {
        ContentValues values = new ContentValues();
        values.put("type", type);
        values.put("amount", amount);
        values.put("note", note);
        values.put("created_at", System.currentTimeMillis());
        getWritableDatabase().insert("transactions", null, values);
    }

    public List<TransactionItem> getAll() {
        List<TransactionItem> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, type, amount, note, created_at FROM transactions ORDER BY created_at DESC", null);
        try {
            while (cursor.moveToNext()) {
                result.add(new TransactionItem(
                        cursor.getLong(0), cursor.getString(1), cursor.getDouble(2),
                        cursor.getString(3), cursor.getLong(4)));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public double getTotal(String type) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = ?",
                new String[]{type});
        try {
            return cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        } finally {
            cursor.close();
        }
    }

    public static class TransactionItem {
        public final long id;
        public final String type;
        public final double amount;
        public final String note;
        public final long createdAt;

        public TransactionItem(long id, String type, double amount, String note, long createdAt) {
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.note = note;
            this.createdAt = createdAt;
        }
    }
}
