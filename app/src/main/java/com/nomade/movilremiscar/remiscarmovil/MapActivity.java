package com.nomade.movilremiscar.remiscarmovil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements LocationListener {

    Double latmovil, lonmovil, latOrigen, lonOrigen;
    private LocationManager locationManager;
    GoogleMap googleMap;
    ArrayList<Location> listLocs;
    Marker Mtrack;
    String latlonOrigen, imei, movil, status, direccion;
    String strMovil;
    Handler mHandler;
    int flg_run = 0; // flag repeating task running
    private final static int INTERVAL = 20 * 1000; // segundos
    private static String url_actual = "http://carlitosbahia.dynns.com/legajos/viajes/Mactual3.php";
    String TAG_SUCCESS = "result";
    String tag_remis = "remiscar map ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //coordenadas del movil.
        latmovil = 0.0;
        lonmovil = 0.0;

        //coordenadas de direccion Origen.
        latOrigen = 0.0;
        lonOrigen = 0.0;

        mHandler = new Handler();

        listLocs = new ArrayList<Location>();

        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        latmovil = (double) settings.getFloat("latmovil", 0);
        lonmovil = (double) settings.getFloat("lonmovil", 0);
        latlonOrigen = settings.getString("latlonOrigen", "");
        imei = settings.getString("imei", "");
        movil = settings.getString("movil", "");
        status = settings.getString("status", "");
        direccion = settings.getString("Direccion", "");
        Log.d(tag_remis, "origen-" + latlonOrigen);
        strMovil = latmovil + "," + lonmovil;
        //Location inicio
        locationInicio();
        //Location fin

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latmovil, lonmovil), 10.0f));
//-34.935506,-57.9556878
        final LatLng Inicio = new LatLng(latmovil, lonmovil);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latmovil, lonmovil), 17.0f));
        Marker TP = googleMap.addMarker(new MarkerOptions().position(Inicio).title("Movil"));

        if (!latlonOrigen.equals("")) {
            Log.d(tag_remis, "marcador****LATLON  NOvacio");
            String[] separated = latlonOrigen.split(",");
            latOrigen = Double.parseDouble(separated[0]);
            lonOrigen = Double.parseDouble(separated[1]);
            final LatLng Origen = new LatLng(latOrigen, lonOrigen);
            Marker Ori = googleMap.addMarker(new MarkerOptions()
                    .position(Origen)
                    .title("Origen")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            midPoint(latmovil, lonmovil, latOrigen, lonOrigen);
        }

        startRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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

    private void locationInicio() {
        PackageManager pm = MapActivity.this.getPackageManager();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
                Log.d(tag_remis, " GPS conectado");
            } else {
                //showGPSDisabledAlertToUser();

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
                //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                Log.d(tag_remis, " NETWORK over GPS");
            }
        } else {
            //showGPSDisabledAlertToUser();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
            Log.d(tag_remis, " NETWORK conectado");
        }
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
        getMyLocationAddress();
    }

    //Location inicio
    @Override
    public void onProviderDisabled(String provider) {

        /******** Called when User off Gps *********/
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d(tag_remis, " - set location");
        if (Mtrack != null) {
            Mtrack.remove();
        }

        strMovil = location.getLatitude() + "," + location.getLongitude();
        latmovil = (Double) location.getLatitude();
        lonmovil = (Double) location.getLongitude();
        if (!latlonOrigen.equals("")) {
            midPoint(latmovil, lonmovil, latOrigen, lonOrigen);
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latmovil, lonmovil), 17.0f));
        }


        Mtrack = googleMap.addMarker(new MarkerOptions().position(new LatLng(latmovil, lonmovil)).title("Movil"));

        //path en mapa
        listLocs.add(location);
        drawPrimaryLinePath(listLocs);
    }

    //Location fin

    private void drawPrimaryLinePath(ArrayList<Location> listLocsToDraw) {
        if (googleMap == null) {
            return;
        }

        if (listLocsToDraw.size() < 2) {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        options.color(Color.parseColor("#CC0000FF"));
        options.width(5);
        options.visible(true);

        for (Location locRecorded : listLocsToDraw) {
            options.add(new LatLng(locRecorded.getLatitude(),
                    locRecorded.getLongitude()));
        }

        googleMap.addPolyline(options);

    }

    public void midPoint(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);
        Log.d(tag_remis, "DIST-" + dLon);
        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //print out in degrees
        //System.out.println(Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3)), 12.5f));
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();
        stopRepeatingTask();
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        stopRepeatingTask();
        finish();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        stopRepeatingTask();

    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            asMActual();

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask() {
        if (flg_run == 0) mHandlerTask.run();
        flg_run = 1;
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
        flg_run = 0;
    }

    /**
     * Background Async Task obtener datos de Mactual
     */
    private void asMActual() {

        String url_params = url_actual + "?status=" + status + "&Movil=" + movil + "&IMEI=" + imei + "&Ubicacion=" + direccion + "&geopos=" + strMovil;
        Ion.with(MapActivity.this)
                .load(url_params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        processMActual(result);

                    }

                });
    }


    private void processMActual(JsonObject data) {
        // check log cat from response
        Log.d("Remiscar Res- ", data.toString());


    }


    public void getMyLocationAddress() {

        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latmovil + "," + lonmovil + "&sensor=false&location_type=RANGE_INTERPOLATED&key=AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw";
            asUbicacion(url);
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void asUbicacion(String url_ubicacion) {
        Ion.with(MapActivity.this)
                .load(url_ubicacion)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Log.d("Remiscar ", "LOC --" + result.toString());

                            JsonObject location = result.getAsJsonArray("results").get(0).getAsJsonObject();
                            String location_string = location.get("formatted_address").getAsString();
                            direccion = location_string;


                        } catch (Exception ee) {
                            Log.d("Remiscar ", "No se puede recuperar ubicacion.");
                        }

                    }
                });
    }


}
