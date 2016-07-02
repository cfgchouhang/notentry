package tw.meowdev.cfg.gamentry.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import tw.meowdev.cfg.gamentry.tools.Time;

/**
 * Created by cfg on 5/20/16.
 */
public class Item {

    final public static String tableName = "item";
    final public static String[][] columns = new String[][] {
            {"_id", "INTEGER PRIMARY KEY AUTOINCREMENT"},
            {"title", "CHAR"},
            {"webUrl", "CHAR"},
            {"imageUri", "CHAR"},
            {"order", "INTEGER"}
    };

    public long id = -1;
    public String title, webUrl, imageUri;
    public int order;

    public Item() {
    }

    public void set(String title, String weburl, String imguri) {
        this.title = title;
        this.webUrl = weburl;
        this.imageUri = imguri;
        this.order = 0;
    }

    public static Item fromCursor(Cursor cursor) {
        Item item = new Item();

        item.id = cursor.getLong(cursor.getColumnIndex("_id"));
        item.title = cursor.getString(cursor.getColumnIndex("title"));
        item.webUrl = cursor.getString(cursor.getColumnIndex("webUrl"));
        item.imageUri = cursor.getString(cursor.getColumnIndex("imageUri"));

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
                {"webUrl", this.webUrl},
                {"imageUri", this.imageUri},
                {"`order`", Integer.toString(this.order)}
        };
    }

    public long insertOrUpdate(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        for(String[] kv: getColVal()) {
            cv.put(kv[0], kv[1]);
        }

        if(this.id == -1) {
            this.id = db.insert(tableName, null, cv);
            Log.d("DB", String.format("insert %d %s\n%s\n%s", id, title, webUrl, imageUri));
        } else {
            cv.remove("imageUri");
            db.update(tableName, cv, "`_id`=?", new String[]{Long.toString(this.id)});
            Log.d("DB", String.format("update %d %s\n%s\n%s", id, title, webUrl, imageUri));
        }

        return id;
    }

    public void updateImage(SQLiteDatabase db, String imageUri) {
        Log.d("FUCK", "Update "+id+" "+imageUri);
        ContentValues cv = new ContentValues();
        cv.put("imageUri", imageUri);
        db.update(tableName, cv, "`_id`=?", new String[]{Long.toString(this.id)});
    }

    public static void loadList(SQLiteDatabase db, ArrayList<Item> list) {
        list.clear();
        Cursor cursor = db.query(Item.tableName, null, null, null, null, null, "`order`");
        if(cursor.moveToFirst()) {
           do {
               list.add(Item.fromCursor(cursor));
           } while(cursor.moveToNext());
        }
    }
}
