package com.harvey.nuandsu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class DBHelper(context: Context) : SQLiteOpenHelper(context, "MyDB.db", null, 3) {



    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "quantity INTEGER," +
                    "status TEXT," +
                    "imageUri TEXT," +
                    "typ TEXT," +
                    "pc INTEGER," +
                    "totalCost INTEGER," +
                    "des TEXT," +
                    "date TEXT)"
        )

        db.execSQL(
            "CREATE TABLE history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "time TEXT," +
                    "new TEXT," +
                    "imageUri TEXT)"
        )

        db.execSQL(
            "CREATE TABLE transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "amount INTEGER," +
                    "date TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL(
                "CREATE TABLE transactions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "amount INTEGER," +
                        "date TEXT)"
            )
        }
    }

    // บันทึกรายจ่ายจริง (ยอมให้ค่าติดลบได้ เพื่อใช้หักลบรายการที่คีย์ผิด)
    fun insertTransaction(amount: Int) {
        if (amount == 0) return
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("amount", amount)
        cv.put("date", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
        db.insert("transactions", null, cv)
    }


    fun insertHistory(history: ProductHis): Long {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("name", history.name)
        cv.put("time", history.time)
        cv.put("new", history.new)
        cv.put("imageUri", history.imageUri)
        return db.insert("history", null, cv)
    }



    // เพิ่มสินค้า
    fun insertProduct(product: Product): Long {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("name", product.name)
        cv.put("quantity", product.quantity)
        cv.put("status", product.status)
        cv.put("imageUri", product.imageUri)
        cv.put("typ", product.typ)
        cv.put("pc", product.pc)
        cv.put("des", product.des)
        cv.put("totalCost", product.totalCost)
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        cv.put("date", dateStr)
        
        val id = db.insert("products", null, cv)
        
        insertTransaction(product.totalCost)
        
        return id
    }

    // ลบสินค้า
    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        return db.delete("products", "id = ?", arrayOf(id.toString()))
    }


    fun updateProduct(product: Product): Int {
        val db = writableDatabase
        val cv = ContentValues()

        cv.put("name", product.name)
        cv.put("quantity", product.quantity)
        cv.put("status", product.status)
        cv.put("imageUri", product.imageUri)
        cv.put("typ", product.typ)
        cv.put("pc", product.pc)
        cv.put("des", product.des)
        cv.put("totalCost", product.totalCost)
        cv.put("date", product.date)

        return db.update(
            "products",
            cv,
            "id = ?",
            arrayOf(product.id.toString())
        )
    }




    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM products ORDER BY id DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date")) ?: ""
                
                val status = try {
                    if (dateStr.isNotEmpty()) {
                        val datePart = dateStr.substring(0, 10)
                        val createdDate = LocalDate.parse(datePart)
                        val today = LocalDate.now()
                        val diffDays = ChronoUnit.DAYS.between(createdDate, today)
                        if (diffDays >= 7L) "หมดอายุ" else "ปกติ"
                    } else {
                        "ปกติ"
                    }
                } catch (e: Exception) {
                    "ปกติ"
                }

                list.add(
                    Product(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        pc = cursor.getInt(cursor.getColumnIndexOrThrow("pc")),
                        quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        status = status,
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        typ = cursor.getString(cursor.getColumnIndexOrThrow("typ")),
                        des = cursor.getString(cursor.getColumnIndexOrThrow("des")),
                        totalCost = cursor.getInt(cursor.getColumnIndexOrThrow("totalCost")),
                        date = dateStr
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }



    fun getLowStatusCount(): Int {
        val allProducts = getAllProducts()
        return allProducts.count { it.status == "ใกล้หมดอายุ" }
    }

    //วัตถุดิบทั้งหมด
    fun getTotalProductCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM products",
            null
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        return count
    }

    // ยอดรวมรายจ่ายทั้งหมด
    fun getTotalSpent(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(amount) FROM transactions",
            null
        )

        var total = 0
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    // ยอดรวมเดือนนี้
    fun getTotalSpentCurrentMonth(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT SUM(amount)
            FROM transactions
            WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime')
            """,
            null
        )

        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        return total
    }

    // ยอดรวมเดือนที่แล้ว
    fun getTotalSpentLastMonth(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT SUM(amount)
            FROM transactions
            WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', '-1 month', 'localtime')
            """,
            null
        )

        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        return total
    }

    // ดึงยอดตามวันที่เจาะจง
    fun getTotalSpentByDate(date: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(amount) FROM transactions WHERE date(date) = ?",
            arrayOf(date)
        )
        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        return total
    }

    // วันนี้
    fun getTotalSpentToday(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT SUM(amount)
        FROM transactions
        WHERE date(date) = date('now', 'localtime')
        """,
            null
        )

        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        return total
    }


    // เมื่อวาน
    fun getTotalSpentYesterday(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT SUM(amount)
        FROM transactions
        WHERE date(date) = date('now', '-1 day', 'localtime')
        """,
            null
        )

        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        return total
    }


    //กูใช้ทำกราฟ
    fun getMonthlyProductsForChart(): List<Pair<Int, Float>> {
        val list = mutableListOf<Pair<Int, Float>>()
        val db = readableDatabase

        val sql = """
        SELECT CAST(strftime('%d', date) AS INTEGER) AS day, amount
        FROM transactions
        WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now', 'localtime')
        ORDER BY date
    """

        val cursor = db.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val day = cursor.getInt(0)
            val total = cursor.getFloat(1)
            list.add(day to total)
        }
        cursor.close()
        return list
    }

    fun getAllHistory(): List<ProductHis> {
        val list = mutableListOf<ProductHis>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM history ORDER BY id DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    ProductHis(
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        time = cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        new = cursor.getString(cursor.getColumnIndexOrThrow("new")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

}
