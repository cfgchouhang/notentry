package tw.meowdev.cfg.gamentry.fragments;

import android.animation.Animator;
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

    final public static int ACTION_AUTO_ADD = 0, ACTION_ADD = 1, ACTION_EDIT = 2;
    private boolean isClick = false;
    private ArrayList<FileTarget> targetList;
    private Picasso picasso;
    private GestureDetector gestureDetector;
    private Item editItem;
    private RelativeLayout webLayout;
    private int action;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Get link-URL.
            String url = (String) msg.getData().get("url");

            Log.d("FUCK", url);
            // Do something with it.
            if (url != null) {
                imgUrl.setText(url);
            }
        }
    };

    public class FileTarget implements Target {
        private Item item;
        private SQLiteDatabase db;

        public FileTarget(SQLiteDatabase db, Item item) {
            this.db = db;
            this.item = item;
            Log.d("FUCK", item.imageUri+" "+item.id);
        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
            new AsyncTask<Object, Void, Void>() {
                protected Void doInBackground(Object... objs) {
                    Bitmap tmp= (Bitmap)objs[0];
                    Item item = (Item)objs[1];
                    File file;

                    // resize the bitmap
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    Bitmap bitmap = Bitmap.createScaledBitmap(tmp, width, tmp.getHeight()*width/tmp.getWidth(), false);
                    //tmp.recycle();

                    // sync image uri of item into db
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
                        ostream.close();
                        Log.d("FILE", String.format("store %s", file.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void v) {
                    webLayout.setVisibility(View.GONE);
                    targetList.remove(FileTarget.this);
                    if(isClick) {
                        getActivity().onBackPressed();
                    }
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
                if(action == ACTION_EDIT)
                    edit(title.getText().toString(), webUrl.getText().toString(), imgUrl.getText().toString());
                else
                    add(title.getText().toString(), webUrl.getText().toString(), imgUrl.getText().toString());
            } else if(view.getId() == R.id.btnWeb) {
                slideWebLayout();
            }
        }
    };

    public void add(String title, String weburl, String imgurl) {
        editItem = new Item();
        editItem.set(title, weburl, "");
        editItem.insertOrUpdate(db);

        Log.d("FUCK", editItem.imageUri);
        FileTarget ft = new FileTarget(db, editItem);
        targetList.add(ft);
        picasso.load(Uri.parse(imgurl)).into(ft);
    }

    public void edit(String title, String weburl, String imgurl) {
        editItem.set(title, weburl, editItem.imageUri);
        editItem.insertOrUpdate(db);

        Log.d("FUCK", "EDIT "+editItem.id+" "+editItem.imageUri);
        FileTarget ft = new FileTarget(db, editItem);
        targetList.add(ft);
        picasso.load(Uri.parse(imgurl)).into(ft);
    }

    private void setItemToUI(Item editItem) {
        title.setText(editItem.title);
        webUrl.setText(editItem.webUrl);
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
        webView = (WebView)view.findViewById(R.id.webView);
        urlbar = (EditText)view.findViewById(R.id.urlbar);

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
        targetList = new ArrayList<FileTarget>();
        return view;
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
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
        editItem = new Item();
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("action")) {
            int action = bundle.getInt("action");
            if(action == ACTION_AUTO_ADD) {
                add(bundle.getString("title"), bundle.getString("webUrl"), bundle.getString("imageUrl"));
            } else if(action == ACTION_EDIT) {
                editItem = Item.query(db, bundle.getLong("id"));
                if(editItem != null) {
                    setItemToUI(editItem);
                    action = ACTION_EDIT;
                } else
                    editItem = new Item();

                Log.d("FUCK", "edit id "+editItem.id);
            }
        }

        gestureDetector = new GestureDetector(getActivity(), new SingleTapConfirm());
    }

    @Override
    public void onDetach () {
        if(targetList != null)
            targetList.clear();

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        super.onDetach();
    }

}
