package tw.meowdev.cfg.gamentry.tools;

import java.io.UnsupportedEncodingException;
import android.net.Uri;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cfg on 6/26/16.
 */
public class Misc {
    public static Map<String, String> splitQuery(Uri uri) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = uri.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
            }
        }
        return query_pairs;
    }
}
