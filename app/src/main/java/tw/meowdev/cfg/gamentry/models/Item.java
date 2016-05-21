package tw.meowdev.cfg.gamentry.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tw.meowdev.cfg.gamentry.Time;

/**
 * Created by cfg on 5/20/16.
 */
public class Item {

    final public static String tableName = "item";
    final public static String[][] columns = new String[][] {
            {"_id", "INTEGER PRIMARY KEY AUTOINCREMENT"},
            {"title", "CHAR"},
            {"content", "TEXT"},
            {"create_time", "CHAR"}
    };

    public int id;
    public String title, content, time;

    public Item() {
    }

    public Item(String title, String content) {
        this.title = title;
        this.content = content;
        this.time = Time.now();
    }

    public static Item fromCursor(Cursor cursor) {
        Item item = new Item();

        item.id = cursor.getInt(cursor.getColumnIndex("_id"));
        item.title = cursor.getString(cursor.getColumnIndex("title"));
        item.content = cursor.getString(cursor.getColumnIndex("content"));
        item.time = cursor.getString(cursor.getColumnIndex("create_time"));

        return item;
    }

    public String[][] getColVal() {
        return new String[][]{
                {"title", this.title},
                {"content", this.content},
                {"create_time", this.time}
        };
    }

    public long insert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        for(String[] kv: getColVal()) {
            cv.put(kv[0], kv[1]);
        }
        return db.insert(tableName, null, cv);
    }
}
