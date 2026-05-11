package avill.ladv.chordo.data.local.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        const val DATABASE_NAME: String = "MyFaces.db"
        const val FACE_TABLE_NAME: String = "faces"
        const val FACE_COLUMN_ID: String = "id"
        const val FACE_COLUMN_NAME: String = "name"
        const val FACE_COLUMN_EMBEDDING: String = "embedding"
        const val DATABASE_VERSION: Int = 2
    }
    override fun onCreate(db: SQLiteDatabase) {
        // TODO Auto-generated method stub
        db.execSQL(
            "create table faces " +
                    "(id integer primary key, name text,embedding text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS faces")
        onCreate(db)
    }
    fun insertFace(name: String?, embedding: Any) {
        val floatList = embedding as Array<FloatArray>
        val embeddingString = StringBuilder()
        for (f in floatList[0]) {
            embeddingString.append(f.toString()).append(",")
        }
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FACE_COLUMN_NAME, name)
        contentValues.put(FACE_COLUMN_EMBEDDING, embeddingString.toString())
        db.insert("faces", null, contentValues)
    }
    @SuppressLint("Range")

    fun getData(id: Int): Cursor {
        val db = this.readableDatabase
        val res = db.rawQuery("select * from faces where id=$id", null)
        return res
    }

    fun numberOfRows(): Int {
        val db = this.readableDatabase
        val numRows = DatabaseUtils.queryNumEntries(db, FACE_TABLE_NAME).toInt()
        return numRows
    }

    fun updateFace(id: Int, name: String?, embedding: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FACE_COLUMN_NAME, name)
        contentValues.put(FACE_COLUMN_EMBEDDING, embedding)
        db.update(FACE_TABLE_NAME, contentValues, "id = ? ", arrayOf(id.toString()))
        return true
    }

    fun deleteFace(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            FACE_TABLE_NAME,
            "id = ? ",
            arrayOf(id.toString())
        )
    }
}