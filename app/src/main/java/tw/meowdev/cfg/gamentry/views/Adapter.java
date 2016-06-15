package tw.meowdev.cfg.gamentry.views;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import tw.meowdev.cfg.gamentry.MainActivity;
import tw.meowdev.cfg.gamentry.R;
import tw.meowdev.cfg.gamentry.models.Item;
import tw.meowdev.cfg.gamentry.models.Media;

/**
 * Created by cfg on 5/19/16.
 */
public class Adapter extends CursorRecyclerViewAdapter<ItemViewHolder> {

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
        holder.id = item.id;
        holder.context = this.context;

        if(infoUri != null) {
            Picasso.with(context).load(new File(infoUri.toString())).into(holder.infoImage);
        } else {
            Picasso.with(context).load(R.drawable.progress_animation).into(holder.infoImage);
        }

        setAnimation(holder, cursor.getPosition());
    }

    private void setAnimation(ItemViewHolder holder, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > holder.lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            holder.cardView.startAnimation(animation);
            holder.lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ItemViewHolder holder) {
        holder.clearAnimation();
    }
}
