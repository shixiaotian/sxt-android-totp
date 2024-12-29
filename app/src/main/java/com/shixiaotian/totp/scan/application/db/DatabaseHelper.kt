package com.shixiaotian.totp.scan.application.db


import android.content.Context
import android.database.Cursor
import androidx.core.content.contentValuesOf
import com.shixiaotian.totp.scan.application.common.MyConstants
import com.shixiaotian.totp.scan.application.vo.User
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, MyConstants.dbName, null, 1) {

    private val dbContext:Context = context

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 更新数据库的时候调用
    }

    private fun openEncryptedDatabase(): SQLiteDatabase {
        val dbHelper = DatabaseHelper(dbContext)
        val db = dbHelper.getWritableDatabase(MyConstants.dbPassword)
        return db
    }

    // 写入数据
    fun insertUser(username: String, secretKey: String, issuer: String): Long {
        val db = this.openEncryptedDatabase()
        var id =db.insert("sxt_totp_users", null, contentValuesOf("username" to username, "secretKey" to secretKey, "issuer" to issuer))
        db.close()
        return id
    }

    // 删除数据
    fun deleteUser(id: Int) {
        val db = this.openEncryptedDatabase()
        db.delete("sxt_totp_users", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // 查询数据
    fun getUser(id: Int): User? {
        val db = this.openEncryptedDatabase()
        val cursor: Cursor = db.query("sxt_totp_users", null, "id = ?", arrayOf(id.toString()), null, null, null)

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val secretKey = cursor.getString(2)
            val issuer = cursor.getString(3)
            // 假设User有id和name两个字段
            user = User(id, name,secretKey, issuer)
        }
        cursor.close()
        db.close()
        return user
    }

    fun getAllUser(): List<User> {
        val db = this.openEncryptedDatabase()
        val items = ArrayList<User>()
        val cursor: Cursor  = db.query("sxt_totp_users", arrayOf("id","username","secretKey", "issuer"), null, null, null, null, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val secretKey = cursor.getString(2)
            val issuer = cursor.getString(3)
            items.add(User(id, name,secretKey, issuer))
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return items
    }

    fun initDB() {
        // 创建表
        val db = this.openEncryptedDatabase()
        db.execSQL("CREATE TABLE sxt_totp_users (id INTEGER PRIMARY KEY, username TEXT, secretKey TEXT, issuer TEXT)")
        db.close()
    }

    fun init() {
        this.insertUser("apple","apple", "github")
        this.insertUser("pear","pear", "steam")
        this.insertUser("apricot","apricot","wiki")
        this.insertUser("peach","peach","TK")
    }
}