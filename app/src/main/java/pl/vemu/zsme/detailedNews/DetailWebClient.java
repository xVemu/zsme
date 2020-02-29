package pl.vemu.zsme.detailedNews;

import android.content.Intent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DetailWebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, request.getUrl()));
        return true;
    }
}
