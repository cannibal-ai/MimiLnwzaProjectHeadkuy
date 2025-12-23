import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.harvey.nuandsu.Product
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, "MyDB.db", null, 1) {



    override fun onCreate(db: SQLiteDatabase) {
        // สร้างตาราง products
        db.execSQL(
            "CREATE TABLE products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "quantity INTEGER," +
                    "status TEXT," +
                    "imageUri TEXT," +
                    "typ TEXT," +
                    "pc INTEGER," +
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
        cv.put("date", product.date)
        return db.insert("products", null, cv)
    }

    // ลบสินค้า
    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        return db.delete("products", "id = ?", arrayOf(id.toString()))
    }


    fun updateProduct(product: Product): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", product.name)
            put("quantity", product.quantity)
            put("status", product.status)
            put("imageUri", product.imageUri)
            put("typ", product.typ)
            put("des", product.des)
            put("date", product.date)
            put("pc", product.pc)

        }

        return db.update(
            "products",

            values,
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
                        quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        typ = cursor.getString(cursor.getColumnIndexOrThrow("typ")),
                        pc = cursor.getInt(cursor.getColumnIndexOrThrow("pc")),
                        des = cursor.getString(cursor.getColumnIndexOrThrow("des")),
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

    fun getOutStatusCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM products WHERE status = ?",
            arrayOf("หมด")
        )

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }

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

    fun getCurrentMonthTotal(): Int {
        val db = readableDatabase

        val monthFormat = java.text.SimpleDateFormat("MM", Locale.getDefault())
        val yearFormat = java.text.SimpleDateFormat("yyyy", Locale.getDefault())

        val month = monthFormat.format(Date())
        val year = yearFormat.format(Date())

        val cursor = db.rawQuery(
            """
        SELECT SUM(pc * quantity)
        FROM products
        WHERE strftime('%m', date) = ?
          AND strftime('%Y', date) = ?
        """,
            arrayOf(month, year)
        )

        var total = 0
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getInt(0)
        }

        cursor.close()
        return total
    }


}
