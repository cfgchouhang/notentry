package tw.meowdev.cfg.gamentry.tools;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import tw.meowdev.cfg.gamentry.models.Media;

/**
 * Created by cfg on 5/23/16.
 */
public class FileTarget implements Target {
    private long id;
    private SQLiteDatabase db;
    private static String storagePath = Environment.getExternalStorageDirectory().getPath();

    public FileTarget(SQLiteDatabase db, long id) {
        this.db = db;
        this.id = id;
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        new AsyncTask<Object, Void, Void>() {
            protected Void doInBackground(Object... objs) {
                Bitmap bitmap = (Bitmap)objs[0];
                long id = (long)objs[1];
                File file = new File(String.format("%s/%s.jpg", storagePath, System.currentTimeMillis()));

                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                    Log.d("FILE", String.format("store %s", file.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Media img = new Media(id, file.toString(), "infoImg");
                img.insert(FileTarget.this.db);

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
            }
        }.execute(bitmap, this.id);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {}

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {}
}
