package tw.meowdev.cfg.gamentry.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import tw.meowdev.cfg.gamentry.tools.Time;

/**
 * Created by cfg on 5/20/16.
 */
public class Item {

    final public static String tableName = "item";
    final public static String[][] columns = new String[][] {
            {"_id", "INTEGER PRIMARY KEY AUTOINCREMENT"},
            {"title", "CHAR"},
            {"content", "TEXT"}
    };

    public long id = -1;
    public String title, content;

    public Item() {
    }

    public void set(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Item fromCursor(Cursor cursor) {
        Item item = new Item();

        item.id = cursor.getLong(cursor.getColumnIndex("_id"));
        item.title = cursor.getString(cursor.getColumnIndex("title"));
        item.content = cursor.getString(cursor.getColumnIndex("content"));

        return item;
    }

    public static Item query(SQLiteDatabase db, long id) {
        Cursor c = db.query(tableName, null, "`_id` = ?", new String[]{Long.toString(id)}, null, null, null, "1");
        Item item = null;
        if(c.moveToFirst())
            item = fromCursor(c);
        c.close();

        return item;
    }

    public String[][] getColVal() {
        return new String[][]{
                {"title", this.title},
                {"content", this.content}
        };
    }

    public long insertOrUpdate(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        for(String[] kv: getColVal()) {
            cv.put(kv[0], kv[1]);
        }

        long id;
        if(this.id == -1) {
            id = db.insert(tableName, null, cv);
            Log.d("DB", String.format("insert %d %s %s", id, title, content));
        } else {
            id = db.update(tableName, cv, "`_id`=?", new String[]{Long.toString(this.id)});
            Log.d("DB", String.format("update %d %s %s", id, title, content));
        }

        return id;
    }
}
