package com.example.invytrack

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "DatabaseInventaris"
        private const val TABLE_INVENTORY = "TabelInventaris"
        private const val KEY_ID = "_id"
        private const val KEY_NAMA_ALAT = "nama_alat"
        private const val KEY_KONDISI = "kondisi"
        private const val KEY_LOKASI = "lokasi"
        private const val KEY_STOCK = "stock"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_INVENTORY_TABLE = ("CREATE TABLE " +
                TABLE_INVENTORY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAMA_ALAT + " TEXT," +
                KEY_KONDISI + " TEXT," +
                KEY_LOKASI + " TEXT," +
                KEY_STOCK + " TEXT" + ")")
        db?.execSQL(CREATE_INVENTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_INVENTORY")
        onCreate(db)
    }

    fun tambahInventaris(item: com.example.invytrack.InventoryModelClass): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAMA_ALAT, item.namaAlat)
        contentValues.put(KEY_KONDISI, item.kondisi)
        contentValues.put(KEY_LOKASI, item.lokasi)
        contentValues.put(KEY_STOCK, item.stock)
        val success = db.insert(TABLE_INVENTORY, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun tampilInventaris(): ArrayList<InventoryModelClass> {
        val inventoryList: ArrayList<InventoryModelClass> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_INVENTORY"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val namaAlat = cursor.getString(cursor.getColumnIndex(KEY_NAMA_ALAT))
                val kondisi = cursor.getString(cursor.getColumnIndex(KEY_KONDISI))
                val lokasi = cursor.getString(cursor.getColumnIndex(KEY_LOKASI))
                val stock = cursor.getString(cursor.getColumnIndex(KEY_STOCK))

                val item = InventoryModelClass(id, namaAlat, kondisi, lokasi, stock)
                inventoryList.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return inventoryList
    }

    fun updateInventaris(item: com.example.invytrack.InventoryModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAMA_ALAT, item.namaAlat)
        contentValues.put(KEY_KONDISI, item.kondisi)
        contentValues.put(KEY_LOKASI, item.lokasi)
        contentValues.put(KEY_STOCK, item.stock)
        val success = db.update(TABLE_INVENTORY, contentValues, "$KEY_ID=?", arrayOf(item.id.toString()))
        db.close()
        return success
    }

    fun hapusInventaris(itemId: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_INVENTORY, "$KEY_ID=?", arrayOf(itemId.toString()))
        db.close()
        return success
    }
}
