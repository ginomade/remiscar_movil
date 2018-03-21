package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

// pantalla de datos de novedades para el movil
public class CamUsuActivity extends Activity {

    WebView mWebView;
    private static final String URL = "http://carlitosbahia.dynns.com/legajos/viajes/Mnovedades.php";
    private static final String URL_venc = "http://carlitosbahia.dynns.com/legajos/viajes/Mvencimientos.php";
    private static final String URL_calles = "http://carlitosbahia.dynns.com/legajos/viajes/buscar.php";
    private static final String URL_CamUsu = "http://carlitosbahia.dynns.com/legajos/viajes/Mcamusu.php";
    private static final String URL_Cambio = "http://carlitosbahia.dynns.com/legajos/viajes/Mcambio.php";

    Button buttonInicio, buttonCamUsu ;

    String params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camusu);

        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        String imei = settings.getString("imei", "");
        String movil = settings.getString("movil", "");

        params = "?IMEI="+imei+"&Movil="+movil;

        mWebView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable Javascript.
        mWebView.setWebViewClient(yourWebClient);

        webSettings.setAllowFileAccessFromFileURLs(true);  // Enable HTML Imports to access file://.
        //webSettings.setAllowUniversalAccessFromFileURLs(true);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.loadUrl(URL_Cambio + params);

        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        buttonInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }

        });


        buttonCamUsu = (Button) findViewById(R.id.buttonCamUsu);
        buttonCamUsu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                hideBtns();
                mWebView.loadUrl(URL_CamUsu + params);

            }

        });

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

    public void hideBtns(){
        buttonCamUsu.setVisibility(View.GONE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camusu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
