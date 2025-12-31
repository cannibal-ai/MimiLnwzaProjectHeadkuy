import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.harvey.nuandsu.Product
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, "MyDB.db", null, 2) {



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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
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
        cv.put("date", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
        return db.insert("products", null, cv)
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
        cv.put("date", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))

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
            "SELECT * FROM products ORDER BY date DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Product(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        pc = cursor.getInt(cursor.getColumnIndexOrThrow("pc")),
                        quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        typ = cursor.getString(cursor.getColumnIndexOrThrow("typ")),
                        des = cursor.getString(cursor.getColumnIndexOrThrow("des")),
                        totalCost = cursor.getInt(cursor.getColumnIndexOrThrow("totalCost")),
                        date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }



    fun getLowStatusCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM products WHERE status = ?",
            arrayOf("น้อย")
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
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
        db.close()
        return count
    }

    //เดือนนี้
    fun getTotalSpent(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(totalCost) FROM products",
            null
        )

        var total = 0
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return total
    }

    // วันนี้
    fun getTotalSpentToday(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT SUM(totalCost)
        FROM products
        WHERE date(date) = date('now', 'localtime')
        """,
            null
        )

        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        db.close()
        return total
    }


    // เมื่อวาน
    fun getTotalSpentYesterday(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT SUM(totalCost)
        FROM products
        WHERE date(date) = date('now', '-1 day', 'localtime')
        """,
            null
        )

        val total = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else 0
        cursor.close()
        db.close()
        return total
    }


}
