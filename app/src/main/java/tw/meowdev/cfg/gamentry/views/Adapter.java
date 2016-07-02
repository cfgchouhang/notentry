package tw.meowdev.cfg.gamentry.views;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

/**
 * Created by cfg on 5/19/16.
 */
public class Adapter extends RecyclerView.Adapter<ItemViewHolder> {

    private SQLiteDatabase db;
    private List<Item> list;
    private Context context;

    public Adapter(Context context, SQLiteDatabase db, List<Item> list) {
        this.context = context;
        this.list = list;
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
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = list.get(position);
        Log.i("FUCK", item.title+" "+item.imageUri);
        Uri uri = Uri.parse(item.imageUri);
        holder.infoText.setText(item.title);
        holder.id = item.id;
        holder.context = this.context;

        if(uri != null) {
            Picasso.with(context).load(new File(uri.toString())).into(holder.infoImage);
        } else {
            Picasso.with(context).load(R.drawable.progress_animation).into(holder.infoImage);
        }

        //setAnimation(holder, position);
    }

    /*private void setAnimation(ItemViewHolder holder, int position) {
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
    }*/

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}
