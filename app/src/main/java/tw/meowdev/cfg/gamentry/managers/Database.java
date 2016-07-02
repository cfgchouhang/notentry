package tw.meowdev.cfg.gamentry.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tw.meowdev.cfg.gamentry.models.Item;

/**
 * Created by cfg on 5/20/16.
 */
public class Database {
    private final static String DB_NAME = "gamentry";
    private final static int DB_VERSION = 1;

    private static SQLiteOpenHelper openHelper = null;

    public static synchronized SQLiteOpenHelper getHelper(Context context) {
        if(openHelper == null) {
            openHelper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                    db.execSQL(genCreateSQL(Item.tableName, Item.columns));
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                    db.execSQL("DROP TABLE IF EXISTS `"+Item.tableName+"`;");
                    onCreate(db);
                }
            };
        }

        return openHelper;
    }

    public static SQLiteDatabase getWritableDatabase(Context context) {
        return getHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context) {
        return getHelper(context).getReadableDatabase();
    }

    public static String genCreateSQL(String tableName, String[][] columns) {
        String sql = "CREATE TABLE `"+tableName+"` (";
        for(int i=0;i<columns.length;++i) {
            if(i!=0) sql += ", ";
            sql += "`"+columns[i][0]+"` "+columns[i][1];
        }
        sql += ");";

        return sql;
    }

    public static String[] columnName(String[][] columns) {
        String[] names = new String[columns.length];

        for(int i=0;i<columns.length;++i) {
            names[i] = columns[i][0];
        }

        return names;
    }

    public static void reset(Context context) {
        SQLiteDatabase db = getWritableDatabase(context);
        db.execSQL("DROP TABLE IF EXISTS `"+Item.tableName+"`;");
        db.execSQL(genCreateSQL(Item.tableName, Item.columns));
    }
}
