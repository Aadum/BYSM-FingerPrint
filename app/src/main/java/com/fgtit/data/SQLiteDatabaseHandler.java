
package com.fgtit.data;

        import java.util.LinkedList;
        import java.util.List;
        import java.util.ArrayList;


        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.CursorIndexOutOfBoundsException;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BYDB";
    private static final String TABLE_NAME = "Transactions";
    private static final String KEY_ID = "id";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_PREGNANCY_STATUS = "pregnancy_status";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_USERID = "userid";
    public static final String PAYMENT_DATE = "payment_date";
    private static final String[] COLUMNS = { KEY_ID, KEY_USERID, KEY_AMOUNT, KEY_COMMENT, KEY_PREGNANCY_STATUS, PAYMENT_DATE };

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Transactions ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "userid INTEGER," + "amount INTEGER," +
                "comment TEXT, " + "pregnancy_status TEXT," + "payment_date DATETIME DEFAULT CURRENT_TIMESTAMP )";



        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // If you need to add a column
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE TABLE_NAME ADD COLUMN" +  PAYMENT_DATE +  "DATETIME DEFAULT CURRENT_TIMESTAMP");
        }
        this.onCreate(db);
    }

    public void deleteOne(Transaction transaction) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(transaction.getId()) });
        db.close();
    }

    public Transaction getTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();


        Transaction transaction = new Transaction();
        transaction.setId(Integer.parseInt(cursor.getString(0)));
        transaction.setUserid(Integer.parseInt(cursor.getString(1)));
        transaction.setAmount(Integer.parseInt(cursor.getString(2)));
        transaction.setComment(cursor.getString(3));
        transaction.setPregnancy_status(cursor.getString(4));
        transaction.setPaymentDate(cursor.getString(5));

        return transaction;
    }





    public List<Transaction> allTransactions() {

        List<Transaction> transactions = new LinkedList<Transaction>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Transaction transaction = null;

        if (cursor.moveToFirst()) {
            do {
                transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setUserid(Integer.parseInt(cursor.getString(1)));
                transaction.setAmount(Integer.parseInt(cursor.getString(2)));
                transaction.setComment(cursor.getString(3));
                transaction.setPregnancy_status(cursor.getString(4));
                transaction.setPaymentDate(cursor.getString(5));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        return transactions;
    }


    public List<Transaction> allUserTransaction(int user_id) {
        List<Transaction> transactions = new LinkedList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Transaction transaction = null;
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " userid = ?", // c. selections
                new String[]{String.valueOf(user_id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            do {
                transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setUserid(Integer.parseInt(cursor.getString(1)));
                transaction.setAmount(Integer.parseInt(cursor.getString(2)));
                transaction.setComment(cursor.getString(3));
                transaction.setPregnancy_status(cursor.getString(4));
                transaction.setPaymentDate(cursor.getString(5));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        return transactions;
    }


    public Cursor allUsersTransaction(int user_id) {
        List<Transaction> transactions = new LinkedList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        Transaction transaction = null;
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " userid = ?", // c. selections
                new String[]{String.valueOf(user_id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            do {
                transaction = new Transaction();
                transaction.setId(Integer.parseInt(cursor.getString(0)));
                transaction.setUserid(Integer.parseInt(cursor.getString(1)));
                transaction.setAmount(Integer.parseInt(cursor.getString(2)));
                transaction.setComment(cursor.getString(3));
                transaction.setPregnancy_status(cursor.getString(4));
                transaction.setPaymentDate(cursor.getString(5));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        return cursor;
    }




    public void addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, transaction.getUserid());
        values.put(KEY_AMOUNT, transaction.getAmount());
        values.put(KEY_COMMENT, transaction.getComment());
        values.put(KEY_PREGNANCY_STATUS, transaction.getPregnancy_status());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public void deleteTransactionData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, transaction.getUserid());
        values.put(KEY_AMOUNT, transaction.getAmount());
        values.put(KEY_COMMENT, transaction.getComment());
        values.put(KEY_PREGNANCY_STATUS, transaction.getPregnancy_status());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(transaction.getId()) });

        db.close();

        return i;
    }

}