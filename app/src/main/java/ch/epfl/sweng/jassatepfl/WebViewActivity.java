package ch.epfl.sweng.jassatepfl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebView for Tequila login
 * Allows user to authenticate with Tequila
 * and retrieve authentication code
 *
 * @author Alexis Montavon
 */
public class WebViewActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);

        // Load the page into WebView
        Intent intent = getIntent();
        if (intent.getData() != null) {
            webview.loadUrl(intent.getDataString());
        }

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Show url in activity's title bar when a page is loaded
                setTitle(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // Terminates WebView activity after retrieving
                // redirect URI if all went well
                if (url.contains("?code=")) {
                    Intent result = new Intent();
                    result.putExtra("url", url);
                    setResult(RESULT_OK, result);
                    finish();
                } else if (url.contains("error=")) {
                    Intent result = new Intent();
                    result.putExtra("url", url);
                    setResult(RESULT_CANCELED, result);
                    finish();
                }
            }
        });
    }
}
