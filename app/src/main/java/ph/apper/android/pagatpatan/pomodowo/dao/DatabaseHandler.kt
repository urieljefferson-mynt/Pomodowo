package com.example.todolistjeff.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todolistjeff.model.TodoModelClass

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "todo_database"
        private val TABLE_CONTACTS = "task_table"
        private val KEY_ID = "id"
        private val KEY_TITLE = "title"
        private val KEY_STATUS = "isChecked"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addTodo(emp: TodoModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, emp.title) // EmpModelClass Name
        contentValues.put(KEY_STATUS,emp.isChecked ) // EmpModelClass Phone
        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewTasks():List<TodoModelClass>{
        val taskList:ArrayList<TodoModelClass> = ArrayList<TodoModelClass>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var title: String
        var isChecked: Boolean
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                isChecked = cursor.getString(cursor.getColumnIndex("isChecked")).toBoolean()
                val todo= TodoModelClass(title = title, isChecked = isChecked, id = id)
                taskList.add(todo)
            } while (cursor.moveToNext())
        }
        return taskList
    }
    //method to update data
    fun updateTodo(emp: TodoModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, emp.title) // EmpModelClass Name
        contentValues.put(KEY_STATUS,emp.isChecked ) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id = ${emp.id}",null)
        Log.d("UPDATING", emp.title + " " + emp.isChecked.toString())
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
//    fun deleteEmployee(emp: TodoModelClass):Int{
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.id) // EmpModelClass UserId
//        // Deleting Row
//        val success = db.delete(TABLE_CONTACTS,"id="+emp.id,null)
//        //2nd argument is String containing nullColumnHack
//        db.close() // Closing database connection
//        return success
//    }
}