package com.nomade.movilremiscar.remiscarmovil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;

//pantalla de datos de viajes
public class ViajesActivity extends AppCompatActivity implements LocationListener {

    WebView mWebView;
    private static final String URL = ServiceUtils.base_url + "Mviajeshoy.php";
    private static final String URL_cobro = ServiceUtils.base_url + "Mcobro.php";
    Button buttonInicio;
    Button Crono;

    String movil, imei, geopos;

    Double lat, lon;

    private LocationManager locationManager;
    SharedPrefsUtil sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharedPrefs = SharedPrefsUtil.getInstance(ViajesActivity.this);
        movil = sharedPrefs.getString("movil", "");
        imei = sharedPrefs.getString("imei", "");
        geopos = sharedPrefs.getString("geopos", "");

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(yourWebClient);

        reloadWebviewData();


        //
        //cronometro

        Crono = (Button) findViewById(R.id.Crono);
        Crono.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(ViajesActivity.this, CronoActivity.class);
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

        locationInicio();
    }

    private void reloadWebviewData() {
        String fullUrl = URL + "?Movil=" + movil + "&imei=" + imei + "&geopos=" + geopos;
        mWebView.loadUrl(fullUrl);
    }

    @Override
    public void onLocationChanged(Location location) {

        String str = location.getLatitude() + "," + location.getLongitude();

        geopos = str;
        Log.d("Remiscar ", " - set location -" + str);

        lat = (Double) location.getLatitude();
        lon = (Double) location.getLongitude();

        reloadWebviewData();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void locationInicio() {
        PackageManager pm = ViajesActivity.this.getPackageManager();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                Log.d("Remiscar -", " GPS conectado");
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
                Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                Log.d("Remiscar -", " NETWORK over GPS");
            }

        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
            Log.d("Remiscar -", " NETWORK conectado");

        }
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
            if (url.equals(URL_cobro)) {
                String fullCobroUrl = URL_cobro + "?imei=" + imei + "&geopos=" + geopos;
                mWebView.loadUrl(fullCobroUrl);
                Toast.makeText(ViajesActivity.this, "Cobro.", Toast.LENGTH_LONG).show();
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