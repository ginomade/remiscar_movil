package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

// pantalla de datos por zonas
public class CallesActivity extends Activity {

    WebView mWebView;
    private static final String URL = "http://carlitosbahia.dynns.com/legajos/viajes/buscar.php";

    Button buttonInicio;
    String params;
    String TAG_SUCCESS = "result";

    JSONParser jsonParser = new JSONParser();

    String movil, resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calles);


        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        movil = settings.getString("movil", "");

        mWebView = (WebView) findViewById(R.id.webView4);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(yourWebClient);

        String fullUrl = URL + "?Movil=" + movil;
        mWebView.loadUrl(fullUrl);


        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        buttonInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calles, menu);
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

    }

}
