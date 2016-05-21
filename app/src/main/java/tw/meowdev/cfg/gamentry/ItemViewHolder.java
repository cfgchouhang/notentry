package tw.meowdev.cfg.gamentry;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView infoText;
    public ImageView infoImage;

    public ItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        infoText = (TextView)itemView.findViewById(R.id.infoText);
        infoImage = (ImageView)itemView.findViewById(R.id.infoImage);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), infoText.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
