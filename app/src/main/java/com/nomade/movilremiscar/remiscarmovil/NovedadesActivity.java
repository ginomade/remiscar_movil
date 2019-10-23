package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;

// pantalla de datos de novedades para el movil
public class NovedadesActivity extends Activity {

    WebView mWebView;
    private static final String URL = ServiceUtils.base_url + "Mnovedades.php";
    private static final String URL_venc = ServiceUtils.base_url + "Mvencimientos.php";
    private static final String URL_calles = ServiceUtils.base_url + "buscar.php";
    //private static final String URL_tarifador = ServiceUtils.base_url + "MTarifador.php";
    private static final String URL_empresas = ServiceUtils.base_url + "Mempresas.php";

    Button buttonInicio, buttonVenc, buttonCalles, buttonEmpresas, buttonCamUsu;

    String params;

    SharedPrefsUtil sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novedades);

        sharedPrefs = SharedPrefsUtil.getInstance(NovedadesActivity.this);
        String imei = sharedPrefs.getString("imei", "");
        String movil = sharedPrefs.getString("movil", "");

        params = "?IMEI=" + imei + "&Movil=" + movil;

        mWebView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable Javascript.
        mWebView.setWebViewClient(yourWebClient);
        //webSettings.setAllowFileAccessFromFileURLs(true);

        mWebView.loadUrl(URL + params);

        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        buttonInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }

        });

        buttonVenc = (Button) findViewById(R.id.buttonVenc);
        buttonVenc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                hideBtns();
                mWebView.loadUrl(URL_venc + params);


            }

        });

        buttonEmpresas = (Button) findViewById(R.id.buttonEmpresas);
        buttonEmpresas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                hideBtns();
                mWebView.loadUrl(URL_empresas + params);


            }

        });

        buttonCamUsu = (Button) findViewById(R.id.buttonCamUsu);
        buttonCamUsu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(NovedadesActivity.this, CamUsuActivity.class);
                startActivity(intent);

            }

        });

        buttonCalles = (Button) findViewById(R.id.buttonCalles);
        buttonCalles.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                hideBtns();
                mWebView.loadUrl(URL_calles + params);

            }

        });
    }

    WebViewClient yourWebClient = new WebViewClient() {
        // you tell the webclient you want to catch when a url is about to load
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

    };


    public void hideBtns() {
        buttonCalles.setVisibility(View.GONE);
        buttonEmpresas.setVisibility(View.GONE);
        buttonCamUsu.setVisibility(View.GONE);
        buttonVenc.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_novedades, menu);
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
