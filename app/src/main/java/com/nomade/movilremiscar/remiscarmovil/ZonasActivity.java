package com.nomade.movilremiscar.remiscarmovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;

//pantalla de datos de viajes
public class ZonasActivity extends AppCompatActivity {

    WebView mWebView;
    private static final String URL = ServiceUtils.base_url + "Mzonas.php";
    private static final String URL_CA = ServiceUtils.base_url + "MzonasCA.php";
    Button buttonInicio;
    Button Crono;

    String movil, imei, geopos;
    SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);

        sharedPrefs = SharedPrefsUtil.getInstance(ZonasActivity.this);
        movil = sharedPrefs.getString("movil", "");
        imei = sharedPrefs.getString("imei", "");
        geopos = sharedPrefs.getString("geopos", "");

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(yourWebClient);

        String fullUrl = URL + "?Movil=" + movil + "&imei=" + imei + "&geopos=" + geopos;
        mWebView.loadUrl(fullUrl);

        //new ViajesTask().execute();
        //
        //cronometro

        Crono = (Button) findViewById(R.id.Crono);
        Crono.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(ZonasActivity.this, CronoActivity.class);
                startActivity(intent);

            }

        });
        //
        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        buttonInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }

        });
    }

    // somewhere on your code...
    WebViewClient yourWebClient = new WebViewClient() {
        // you tell the webclient you want to catch when a url is about to load
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }

        // here you execute an action when the URL you want is about to load
        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.equals(URL_CA)) {
                mWebView.loadUrl(URL_CA);
                Toast.makeText(ZonasActivity.this, "Cobro.", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viajes, menu);
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
