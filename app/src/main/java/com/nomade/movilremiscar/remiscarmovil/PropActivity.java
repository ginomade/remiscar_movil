package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;


//pantalla de carga de datos para propietarios
public class PropActivity extends Activity {

    WebView mWebView;
    private static final String URL_prop = ServiceUtils.base_url + "Mactual1.php";

    SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prop);

        sharedPrefs = SharedPrefsUtil.getInstance(PropActivity.this);
        String movil = sharedPrefs.getString("movil", "");
        String imei = sharedPrefs.getString("imei", "");

        String url = URL_prop + "?IMEI=" + imei + "&Movil=" + movil;
        mWebView = (WebView) findViewById(R.id.webView2);

        mWebView.setWebViewClient(yourWebClient);

        mWebView.loadUrl(url);
    }


    WebViewClient yourWebClient = new WebViewClient() {
        // you tell the webclient you want to catch when a url is about to load
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //mWebView.loadUrl(url);
            return true;
        }

        // here you execute an action when the URL you want is about to load
        @Override
        public void onLoadResource(WebView view, String url) {

        }
    };

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();

        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();

        finish();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}
