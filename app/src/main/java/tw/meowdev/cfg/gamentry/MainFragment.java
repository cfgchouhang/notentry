package tw.meowdev.cfg.gamentry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import tw.meowdev.cfg.gamentry.models.Item;
import tw.meowdev.cfg.gamentry.models.Media;

/**
 * Created by cfg on 5/22/16.
 */
public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private Adapter adapter;
    private EditText title, url;
    private SQLiteDatabase db;
    private Cursor cursor;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Item item = new Item(title.getText().toString(), "fuck");
            long id = item.insert(db);
            File file = new File(
                    Environment.getExternalStorageDirectory().getPath()
                            + "/"+title.getText().toString()+".jpg");
            Media img = new Media(id, file.toString(), "info");
            img.insert(db);

            Picasso.with(getActivity()).load(Uri.parse(url.getText().toString())).into(target);
        }
    };

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncTask<Object, Void, Void>() {
                protected Void doInBackground(Object... objs) {
                    Bitmap bitmap = (Bitmap)objs[0];
                    String name = (String)objs[1];
                    File file = new File(Environment.getExternalStorageDirectory().getPath()
                                    + String.format("/%s.jpg", name));
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                        ostream.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    cursor = db.query(Item.tableName, null, null, null, null, null, null);
                    Cursor c = adapter.swapCursor(cursor);
                    c.close();
                }
            }.execute(bitmap, title.getText().toString());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        title = (EditText)view.findViewById(R.id.editTitle);
        url = (EditText)view.findViewById(R.id.editUrl);
        view.findViewById(R.id.button).setOnClickListener(listener);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = Database.getWritableDatabase(getActivity());
        cursor = db.query(Item.tableName, null, null, null, null, null, null);
        adapter = new Adapter(getActivity(), db, cursor);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
