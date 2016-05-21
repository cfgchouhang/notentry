package tw.meowdev.cfg.gamentry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import tw.meowdev.cfg.gamentry.models.Item;
import tw.meowdev.cfg.gamentry.models.Media;

/**
 * Created by cfg on 5/19/16.
 */
public class Adapter extends CursorRecyclerViewAdapter<ItemViewHolder> {

    private List<HashMap<String,String>> dataset;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;

    public Adapter(Context context, SQLiteDatabase db, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
        this.db = db;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, Cursor cursor) {
        Item item = Item.fromCursor(cursor);
        Uri infoUri = Media.getInfoImageUri(db, item.id);
        holder.infoText.setText(item.title);

        Picasso.with(context).load(new File(infoUri.toString())).into(holder.infoImage);
    }
}
