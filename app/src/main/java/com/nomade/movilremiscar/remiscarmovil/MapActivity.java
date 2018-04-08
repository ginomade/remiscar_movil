package com.nomade.movilremiscar.remiscarmovil;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nomade.movilremiscar.remiscarmovil.Util.GooglePlayServicesHelper;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    Double latmovil, lonmovil, latOrigen, lonOrigen;
    GoogleMap googleMap;
    ArrayList<Location> listLocs;
    Marker Mtrack;
    String latlonOrigen, imei, movil, status, direccion;
    String strMovil;
    Handler mHandler;
    String tag_remis = "remiscar map ";
    GooglePlayServicesHelper locationHelper;

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

        SharedPrefsUtil prefs = SharedPrefsUtil.getInstance(this);
        latmovil = (double) prefs.getFloat("latmovil", 0);
        lonmovil = (double) prefs.getFloat("lonmovil", 0);
        if (latmovil == 0) {
            Location newLocation = locationHelper.getLastLocation();
            latmovil = newLocation.getLatitude();
            lonmovil = newLocation.getLongitude();
        }
        latlonOrigen = prefs.getString("latlonOrigen", "");
        imei = prefs.getString("imei", "");
        movil = prefs.getString("movil", "");
        status = prefs.getString("status", "");
        direccion = prefs.getString("Direccion", "");
        Log.d(tag_remis, "origen-" + latlonOrigen);
        strMovil = latmovil + "," + lonmovil;

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationHelper = new GooglePlayServicesHelper(MapActivity.this, true);


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
    protected void onResume() {
        super.onResume();
        locationHelper.onResume(MapActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.onPause();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //-34.935506,-57.9556878
        final LatLng Inicio = new LatLng(latmovil, lonmovil);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latmovil, lonmovil), 17.0f));
        Marker TP = googleMap.addMarker(new MarkerOptions().position(Inicio).title("Movil"));

        //TEST///
        //latlonOrigen="-34.935506,-57.9556878";
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
    }
}
