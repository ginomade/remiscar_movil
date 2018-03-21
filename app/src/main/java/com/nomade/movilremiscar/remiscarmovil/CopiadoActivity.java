package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

//pantalla de datos de viajes
public class CopiadoActivity extends Activity {

    WebView mWebView;
    private static final String URL = "http://carlitosbahia.dynns.com/legajos/viajes/Mcopiado.php";

    Button buttonInicio;
    String TAG_SUCCESS = "result";

    JSONParser jsonParser = new JSONParser();

    String movil, resp;
    String Traslados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copiado);

        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        movil = settings.getString("movil", "");
        Traslados = settings.getString("Traslados", "");
        mWebView = (WebView) findViewById(R.id.webView5);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(yourWebClient);

        String fullUrl = URL+"?Traslados="+Traslados;
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
    WebViewClient yourWebClient = new WebViewClient(){
        // you tell the webclient you want to catch when a url is about to load
        @Override
        public boolean shouldOverrideUrlLoading(WebView  view, String  url){
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

    /**
     * Background Async Task Obtener viajes
     * */
    class CopiadoTask extends AsyncTask<String, String, String> {

        int success;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            //String imei = "";

            String fullUrl = URL+"?Traslados="+Traslados;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Movil", movil));
            params.add(new BasicNameValuePair("Traslados", Traslados));

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(fullUrl);

            Log.d("Remiscar", "antes de envio Viajes");

            try {
                post.setEntity(new UrlEncodedFormEntity(params));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String responseStr = null;
            try {
                responseStr = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (responseStr!=""){
                //editTextStatus.setText((responseStr.length()));
            }


            // check log cat from response
            Log.d("Remiscar-Viajes-Resp ", responseStr);

            return responseStr;


        }

        /**
         * After completing background task
         * **/
        protected void onPostExecute(String response) {


            mWebView.loadUrl(response);
            //mWebView.reload();


        }

    }
}

