package tw.meowdev.cfg.gamentry.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import tw.meowdev.cfg.gamentry.managers.Database;
import tw.meowdev.cfg.gamentry.R;
import tw.meowdev.cfg.gamentry.models.Item;

/**
 * Created by cfg on 5/23/16.
 */
public class EditFragment extends Fragment {
    private Button btnSave, btnWeb;
    private EditText title, webUrl, imgUrl, urlbar;
    private WebView webView;
    private SQLiteDatabase db;
    private static String storagePath = Environment.getExternalStorageDirectory().getPath();

    final public static int ACTION_ADD = 1, ACTION_EDIT = 2;
    private int action;
    private long id;
    private Item item;

    private boolean isClick = false;
    private Picasso picasso;
    private GestureDetector gestureDetector;
    private RelativeLayout webLayout;

    private static ArrayList<FileTarget> list = new ArrayList<FileTarget>();

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    public static class FileTarget implements Target {
        private Activity activity;
        private Item item;
        private SQLiteDatabase db;
        private boolean isAuto;

        public FileTarget(Activity activity, SQLiteDatabase db, Item item, boolean isAuto) {
            this.activity = activity;
            this.db = db;
            this.item = item;
            this.isAuto = isAuto;
        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
            new AsyncTask<Object, Void, Void>() {
                protected Void doInBackground(Object... objs) {
                    Bitmap tmp= (Bitmap)objs[0];
                    Item item = (Item)objs[1];
                    File file;

                    // resize the bitmap
                    Log.d("FUCK", " ft "+item.imageUri);
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    Bitmap bitmap = Bitmap.createScaledBitmap(tmp, width, tmp.getHeight()*width/tmp.getWidth(), false);

                    // sync image uri of item into db
                    Log.d("FUCK", " ft "+item.imageUri);
                    if(item.imageUri.equals("")) {
                        file = new File(String.format("%s/%s.jpg", storagePath, System.currentTimeMillis()));
                        item.updateImage(db, file.toString());
                    } else {
                        file = new File(item.imageUri);
                    }

                    // store the image resource
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.flush();
                        ostream.close();
                        Log.d("FILE", String.format("store %s", file.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    list.remove(this);
                    if(!isAuto) activity.onBackPressed();
                }
            }.execute(bitmap, this.item);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnSave && !isClick) {
                isClick = true;
                addOrEdit(title.getText().toString(), webUrl.getText().toString(), imgUrl.getText().toString());
                webLayout.setVisibility(View.GONE);
            } else if(view.getId() == R.id.btnWeb) {
                slideWebLayout();
            }
        }
    };

    public void addOrEdit(String title, String webUrl, String imgUrl) {
        item.set(title, webUrl);
        item.insertOrUpdate(db);

        if(!imgUrl.equals("")) {
            Log.d("FUCK", imgUrl);
            FileTarget ft = new FileTarget(getActivity(), db, item, false);
            list.add(ft);
            picasso.load(Uri.parse(imgUrl)).into(ft);
        } else {
            getActivity().onBackPressed();
        }
    }

    public static void autoAdd(Activity activity, String title, String webUrl, String imgUrl) {
        SQLiteDatabase db = Database.getWritableDatabase(activity);
        Item item = new Item();
        item.set(title, webUrl);
        item.insertOrUpdate(db);

        FileTarget ft = new FileTarget(activity, db, item, true);
        list.add(ft);
        Picasso.with(activity).load(Uri.parse(imgUrl)).into(ft);
    }

    private void setContent() {
        title.setText(item.title);
        webUrl.setText(item.webUrl);
    }

    private void slideWebLayout() {
        if(webLayout.getVisibility() == View.VISIBLE) {
            Animation bottomDown = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
            webLayout.setAnimation(bottomDown);
            webLayout.setVisibility(View.GONE);
        } else {
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
            webLayout.setAnimation(bottomUp);
            webLayout.setVisibility(View.VISIBLE);
        }
    }

    public boolean webViewGoBack() {
        if(webLayout.isShown() && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(onClickListener);

        btnWeb = (Button)view.findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(onClickListener);
        btnWeb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                webUrl.setText(webView.getUrl());
                return true;
            }
        });

        title = (EditText)view.findViewById(R.id.editTitle);
        webUrl = (EditText)view.findViewById(R.id.editWebUrl);
        imgUrl = (EditText)view.findViewById(R.id.editImgUrl);

        webLayout = (RelativeLayout)view.findViewById(R.id.webLayout);
        urlbar = (EditText)view.findViewById(R.id.urlbar);
        webView = (WebView)view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                urlbar.setText(url);

                return false;
            }
            @Override
            public void onLoadResource(WebView view, String url) {

            }
        });

        webView.loadUrl("http://www.google.com");

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = Database.getWritableDatabase(getActivity());
        picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
                getActivity().onBackPressed();
            }
        }).build();

        action = ACTION_ADD;
        Bundle bundle = getArguments();
        if(bundle != null) {
            action = bundle.getInt("action");
            id = bundle.getLong("id");
        }

        if(action == ACTION_ADD) {
            item = new Item();
        } else if(action == ACTION_EDIT) {
            item = Item.query(db, id);
            setContent();
        }

        gestureDetector = new GestureDetector(getActivity(), new SingleTapConfirm());
    }

    @Override
    public void onDetach () {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        super.onDetach();
    }

}
