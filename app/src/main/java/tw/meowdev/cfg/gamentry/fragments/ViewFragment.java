package tw.meowdev.cfg.gamentry.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.squareup.picasso.Picasso;

import tw.meowdev.cfg.gamentry.R;
import tw.meowdev.cfg.gamentry.managers.Database;
import tw.meowdev.cfg.gamentry.models.Item;

/**
 * Created by cfg on 7/5/16.
 */
public class ViewFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);

        webView = (WebView)view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);

        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long id = -1;
        Bundle bundle = getArguments();
        if(bundle != null) {
            id = bundle.getLong("id");
        }

        Item item = Item.query(Database.getReadableDatabase(getActivity()), id);
        webView.loadUrl(item.webUrl);
    }
}
