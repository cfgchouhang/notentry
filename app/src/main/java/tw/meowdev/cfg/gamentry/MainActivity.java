package tw.meowdev.cfg.gamentry;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import tw.meowdev.cfg.gamentry.fragments.EditFragment;
import tw.meowdev.cfg.gamentry.fragments.MainFragment;
import tw.meowdev.cfg.gamentry.fragments.ViewFragment;
import tw.meowdev.cfg.gamentry.managers.Database;
import tw.meowdev.cfg.gamentry.tools.Misc;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private static int MAIN_FRAGMENT = 0, EDIT_FRAGMENT = 1;
    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null) {
            MainFragment f = new MainFragment();
            FragmentTransaction tc = getSupportFragmentManager().beginTransaction();
            tc.replace(R.id.container, f, "main").commit();
            currentFragment = MAIN_FRAGMENT;
        }

        onNewIntent(getIntent());

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
        /*Intent intent = new Intent(this, FloatingBubbleService.class);
        startService(intent);
        Log.d("FUCK", "start service");*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Uri uri = intent.getData();
        if(uri != null) {
            Map<String, String> query = Misc.splitQuery(uri);
            EditFragment.autoAdd(this, uri.getPath().substring(1), query.get("web"), query.get("image"));
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            EditFragment f = (EditFragment)fm.findFragmentByTag("edit");
            if(f == null || !f.webViewGoBack()) {
                fm.popBackStack();
            }
        }
    }

    @Override
    public void onDestroy() {
        /*Intent intent = new Intent(this, FloatingBubbleService.class);
        stopService(intent);
        Log.d("FUCK", "stop service");*/
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_resetdb:
                Database.reset(this);
                break;
        }
        return true;
    }

    public void gotoEditFragment(Bundle bundle) {
        EditFragment editFragment = new EditFragment();
        editFragment.setArguments(bundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.container, editFragment, "edit");
        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = EDIT_FRAGMENT;
    }

    public void gotoViewFragment(Bundle bundle) {
        ViewFragment viewFragment = new ViewFragment();
        viewFragment.setArguments(bundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.container, viewFragment, "view");
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
