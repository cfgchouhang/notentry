package tw.meowdev.cfg.gamentry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import tw.meowdev.cfg.gamentry.models.Item;
import tw.meowdev.cfg.gamentry.models.Media;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment f = new MainFragment();
        FragmentTransaction tc = getSupportFragmentManager().beginTransaction();
        tc.replace(R.id.container, f, "main").commit();

        /*ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> item1 = new HashMap<String,String>(), item2 = new HashMap<String,String>();
        item1.put("title", "薩爾達傳說：眾神三角神力2");
        item1.put("url", "http://hiddentriforce.com/wp-content/uploads/2015/02/a-link-between-worlds-sound-track.jpg");
        list.add(item1);
        item2 = new HashMap<String,String>();
        item2.put("title", "Mario & Luigi RPG: Dream Team");
        item2.put("url", "https://metrouk2.files.wordpress.com/2013/07/marioluigi-dreamteam-wallpaper-02-1920x1200.jpg");
        list.add(item2);
        for(int i=0; i<10; i++) {
            list.add(i % 2 == 1 ? item1:item2);
        }*/
    }
}
