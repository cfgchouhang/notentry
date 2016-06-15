package tw.meowdev.cfg.gamentry.views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tw.meowdev.cfg.gamentry.MainActivity;
import tw.meowdev.cfg.gamentry.R;
import tw.meowdev.cfg.gamentry.fragments.EditFragment;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public CardView cardView;
    public TextView infoText;
    public ImageView infoImage;
    public long id;
    public Context context;
    public int lastPosition = -1;

    public ItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        cardView = (CardView)itemView.findViewById(R.id.card_view);
        infoText = (TextView)itemView.findViewById(R.id.infoText);
        infoImage = (ImageView)itemView.findViewById(R.id.infoImage);
        itemView.findViewById(R.id.imgEdit).setOnClickListener(this);
        id = -1;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imgEdit && id != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt("action", EditFragment.ACTION_EDIT);
            bundle.putLong("id", id);
            ((MainActivity)context).gotoEditFragment(bundle);
        } else {

        }
    }

    public void clearAnimation() {
        cardView.clearAnimation();
    }
}
