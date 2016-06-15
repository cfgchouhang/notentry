package tw.meowdev.cfg.gamentry.fragments;

import android.animation.Animator;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import tw.meowdev.cfg.gamentry.FloatingBubbleService;
import tw.meowdev.cfg.gamentry.managers.Database;
import tw.meowdev.cfg.gamentry.R;
import tw.meowdev.cfg.gamentry.models.Item;
import tw.meowdev.cfg.gamentry.models.Media;

/**
 * Created by cfg on 5/23/16.
 */
public class EditFragment extends Fragment {
    private Button btnSave, btnWeb;
    private EditText title, url, content, urlbar;
    private WebView webView;
    private SQLiteDatabase db;
    private static String storagePath = Environment.getExternalStorageDirectory().getPath();

    final public static int ACTION_ADD = 0, ACTION_EDIT = 1;
    private boolean isClick = false;
    private ArrayList<FileTarget> targetList;
    private Picasso picasso;
    private GestureDetector gestureDetector;
    private int action = ACTION_ADD;
    private Item editItem;
    private RelativeLayout webLayout;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btnSave && !isClick) {
                isClick = true;
                edit(title.getText().toString(), url.getText().toString(), content.getText().toString());

            } else if(view.getId() == R.id.btnWeb) {
                slideWebLayout();
            }
        }
    };

    public void edit(String title, String url, String content) {
        editItem.set(title, content);
        long id = editItem.insertOrUpdate(db);

        FileTarget ft = new FileTarget(db, id);
        targetList.add(ft);
        picasso.load(Uri.parse(url)).into(ft);
    }

    private void setItemToUI(long id) {
        editItem = Item.query(db, id);
        if(editItem != null) {
            title.setText(editItem.title);
            content.setText(editItem.content);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(onClickListener);

        btnWeb = (Button)view.findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(onClickListener);

        title = (EditText)view.findViewById(R.id.editTitle);
        url = (EditText)view.findViewById(R.id.editUrl);
        content = (EditText)view.findViewById(R.id.editContent);


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
        /*webView.setOnTouchListener(new View.OnTouchListener() {
            //private int initialX;
            //private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    // single tap
                    // popWindow
                    Log.d("Service", "click");
                    return false;
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //initialX = params.x;
                            //initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            if (event.getRawY() - initialTouchY > 0) {
                                slideWebView();
                            }
                            return true;
                    }
                }
                return false;
            }
        });*/

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

        editItem = new Item();
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey("action")) {
            action = bundle.getInt("action");
            if(action == ACTION_ADD) {
                edit(bundle.getString("title"), bundle.getString("url"), bundle.getString("content"));
            } else if(action == ACTION_EDIT) {
                setItemToUI(bundle.getLong("id"));
            }
        }

        gestureDetector = new GestureDetector(getActivity(), new SingleTapConfirm());

        /*Intent intent = new Intent(getActivity(), FloatingBubbleService.class);
        getActivity().startService(intent);
        Log.d("Service", "start service");*/
    }

    @Override
    public void onDetach () {
        /*Intent intent = new Intent(getActivity(), FloatingBubbleService.class);
        getActivity().stopService(intent);
        Log.d("Service", "start service");*/

        if(targetList != null)
            targetList.clear();
        super.onDetach();
    }

    public class FileTarget implements Target {
        private long id;
        private SQLiteDatabase db;

        public FileTarget(SQLiteDatabase db, long id) {
            this.db = db;
            this.id = id;
        }

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
            new AsyncTask<Object, Void, Void>() {
                protected Void doInBackground(Object... objs) {
                    Bitmap bitmap = (Bitmap)objs[0];
                    long id = (long)objs[1];
                    File file;
                    Media media = Media.query(db, id);

                    if(media == null) {
                        file = new File(String.format("%s/%s.jpg", storagePath, System.currentTimeMillis()));
                        media = new Media(id, file.toString(), "infoImg");
                        media.insert(FileTarget.this.db);
                    } else {
                        file = new File(media.uri);
                    }

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
                    Log.d("EDIT", "arraylist size "+targetList.size());
                    if(isClick) {
                        getActivity().onBackPressed();
                    }
                    targetList.remove(FileTarget.this);
                    Log.d("EDIT", "arraylist size "+targetList.size());
                }
            }.execute(bitmap, this.id);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    }

    public boolean webViewGoBack() {
        if(webLayout.isShown() && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
