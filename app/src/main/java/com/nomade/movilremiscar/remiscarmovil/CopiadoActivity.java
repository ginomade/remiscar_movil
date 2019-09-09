package com.nomade.movilremiscar.remiscarmovil;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;

//pantalla de datos de viajes
public class CopiadoActivity extends AppCompatActivity {

    WebView mWebView;
    private static final String URL = ServiceUtils.base_url + "Mcopiado.php";

    Button buttonInicio;

    String movil;
    String Traslados;

    SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copiado);

        sharedPrefs = SharedPrefsUtil.getInstance(CopiadoActivity.this);
        movil = sharedPrefs.getString("movil", "");
        Traslados = sharedPrefs.getString("Traslados", "");
        mWebView = (WebView) findViewById(R.id.webView5);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(yourWebClient);

        String fullUrl = URL + "?Traslados=" + Traslados;
        mWebView.loadUrl(fullUrl);

        //new ViajesTask().execute();
        //
        //cronometro
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
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_copiado, menu);
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

