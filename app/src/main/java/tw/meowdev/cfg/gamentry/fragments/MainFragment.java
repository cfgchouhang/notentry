package tw.meowdev.cfg.gamentry.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

import tw.meowdev.cfg.gamentry.MainActivity;
import tw.meowdev.cfg.gamentry.views.Adapter;
import tw.meowdev.cfg.gamentry.managers.Database;
import tw.meowdev.cfg.gamentry.R;
import tw.meowdev.cfg.gamentry.models.Item;

/**
 * Created by cfg on 5/22/16.
 */
public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private Adapter adapter = null;
    private EditText title, url;
    private Button button;
    private FloatingActionButton fab;
    private SQLiteDatabase db = null;
    private Cursor cursor;
    private ArrayList<Item> itemList;
    private int screenHeight;

    // Extend the Callback class
    ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {
        //and in your imlpementaion of
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // get the viewHolder's and target's positions in your adapter data, swap them
            Collections.swap(itemList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            // and notify the adapter that its dataset has changed
            adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //TODO
        }

        //defines the enabled move directions in each state (idle, swiping, dragging).
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
        }
    };

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.fab) {
                Bundle bundle = new Bundle();
                bundle.putInt("action", EditFragment.ACTION_ADD);
                bundle.putLong("id", -1);
                ((MainActivity)getActivity()).gotoEditFragment(bundle);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = Database.getWritableDatabase(getContext());
        itemList = new ArrayList<Item>();
        adapter = new Adapter(getActivity(), db, itemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);



        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(listener);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            int margin = ((RelativeLayout.LayoutParams)fab.getLayoutParams()).bottomMargin;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)fab.getLayoutParams();
                int[] loc = new int[2];
                fab.getLocationOnScreen(loc);
                if (dy > 0 && loc[1] < screenHeight) {
                    layoutParams.bottomMargin -= (int)(dy*0.37);
                    fab.setLayoutParams(layoutParams);
                } else if(dy < 0 && layoutParams.bottomMargin < margin) {
                    layoutParams.bottomMargin -= (int)(dy*0.37);
                    fab.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    //fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        Item.loadList(db, itemList);
        adapter.notifyDataSetChanged();

        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //ItemTouchHelper ith = new ItemTouchHelper(_ithCallback);
        //ith.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
    }
}
