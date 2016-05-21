package tw.meowdev.cfg.gamentry.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by cfg on 5/20/16.
 */
public class Media {
    final static public String tableName = "media";
    final public static String[][] columns = new String[][] {
            {"_id", "INTEGER PRIMARY KEY AUTOINCREMENT"},
            {"itemId", "INTEGER"},
            {"uri", "CHAR"},
            {"type", "CHAR"}
    };

    long itemId;
    String uri, type;

    public Media(long itemId, String uri, String type) {
        this.itemId = itemId;
        this.uri = uri;
        this.type = type;
    }

    public String[][] getColVal() {
        return new String[][]{
                {"itemId", String.valueOf(this.itemId)},
                {"uri", this.uri},
                {"type", this.type}
        };
    }

    public void insert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        for(String[] kv: getColVal()) {
            cv.put(kv[0], kv[1]);
        }
        db.insert(tableName, null, cv);
    }

    public static Uri getInfoImageUri(SQLiteDatabase db, int id) {
        Uri uri = null;
        Cursor c = db.query(tableName, null, "itemId=? and type=?", new String[]{String.valueOf(id), "info"}, null, null, null);
        if(c.moveToFirst())
            uri = Uri.parse(c.getString(c.getColumnIndex("uri")));

        c.close();

        return uri;
    }
}
