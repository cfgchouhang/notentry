package tw.meowdev.cfg.gamentry.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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

    public long itemId;
    public String type, uri;

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
        Log.d("DB", String.format("insert %d %s %s", itemId, uri, type));
    }

    public static Media fromCursor(Cursor c) {
        return new Media(
            c.getLong(c.getColumnIndex("itemId")),
            c.getString(c.getColumnIndex("uri")),
            c.getString(c.getColumnIndex("type"))
        );
    }
    public static Media query(SQLiteDatabase db, long itemId) {
        Cursor c = db.query(tableName, null, "itemId=? and type=?", new String[]{String.valueOf(itemId), "infoImg"}, null, null, null, "1");
        Media m = null;
        if(c.moveToFirst())
            m = fromCursor(c);
        c.close();

        return m;

    }

    public static Uri getInfoImageUri(SQLiteDatabase db, long itemId) {
        Uri uri = null;
        Media m = query(db, itemId);
        if(m != null)
            uri = Uri.parse(m.uri);

        return uri;
    }
}
