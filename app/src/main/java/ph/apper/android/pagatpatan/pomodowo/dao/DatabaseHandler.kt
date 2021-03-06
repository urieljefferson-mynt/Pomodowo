package ph.apper.android.pagatpatan.pomodowo.dao


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todolistjeff.model.TodoModelClass

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION
) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "todo_database"
        private val TABLE_CONTACTS = "task_table"
        private val KEY_ID = "id"
        private val KEY_TITLE = "title"
        private val KEY_STATUS = "isChecked"
        private val KEY_PRIORITY = "priority"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT,"
                + KEY_STATUS + " TEXT," + KEY_PRIORITY + " TEXT" + ")") //Should be int??
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addTodo(emp: TodoModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, emp.title) // EmpModelClass Name
        contentValues.put(KEY_STATUS,emp.isChecked ) // EmpModelClass Phone
        contentValues.put(KEY_PRIORITY, emp.priority)
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
        var priority: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                title = cursor.getString(cursor.getColumnIndex("title"))
                //Retrieve checkbox state from database
                isChecked = cursor.getInt(cursor.getColumnIndex("isChecked")).toBoolean() //Prinoblema ko to ng ilang araw hahaha
                priority = cursor.getString(cursor.getColumnIndex("priority"))
                val todo= TodoModelClass(title = title, isChecked = isChecked, id = id, priority = priority)
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
        contentValues.put(KEY_PRIORITY, emp.priority)

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id = ${emp.id}",null)
        Log.d("UPDATING", emp.title + " " + emp.isChecked.toString())

        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteTodo(todo: TodoModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, todo.id) // Todo UserId
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"id="+todo.id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}

private fun Int.toBoolean() = if (this == 1) true else false
