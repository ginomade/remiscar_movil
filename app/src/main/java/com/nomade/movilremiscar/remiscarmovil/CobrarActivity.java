package com.nomade.movilremiscar.remiscarmovil;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

// pantalla de datos de viajes para el movil
public class CobrarActivity extends ActionBarActivity {

    WebView mWebView;
    private static final String URL = "http://carlitosbahia.dynns.com/legajos/viajes/Mcobro.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobrar);

        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        String movil = settings.getString("movil", "");

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.loadUrl(URL);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cobrar, menu);
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

        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}
