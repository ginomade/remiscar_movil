package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//pantalla de carga de datos para propietarios
public class PropActivity extends Activity {

    WebView mWebView;
    private static final String URL_prop = "http://carlitosbahia.dynns.com/legajos/viajes/Mactual1.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prop);

        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        String movil = settings.getString("movil", "");

        String imei = getPhoneImei();
        String url = URL_prop + "?IMEI=" + imei +"&Movil="+movil;
        mWebView = (WebView) findViewById(R.id.webView2);

        mWebView.setWebViewClient(yourWebClient);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.loadUrl(url);
    }

    //Obtener numero de imei
    private String getPhoneImei(){
        TelephonyManager mTelephonyManager;
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager.getDeviceId();
    }

    WebViewClient yourWebClient = new WebViewClient(){
        // you tell the webclient you want to catch when a url is about to load
        @Override
        public boolean shouldOverrideUrlLoading(WebView  view, String  url){
            //mWebView.loadUrl(url);
            return true;
        }
        // here you execute an action when the URL you want is about to load
        @Override
        public void onLoadResource(WebView  view, String  url){

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
