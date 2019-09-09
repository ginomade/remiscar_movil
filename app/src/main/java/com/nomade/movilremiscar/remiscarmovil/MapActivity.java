package com.nomade.movilremiscar.remiscarmovil;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.nomade.movilremiscar.remiscarmovil.Util.GooglePlayServicesHelper;
import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;
import com.nomade.movilremiscar.remiscarmovil.events.CoordenadasViajeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    Double latmovil, lonmovil, latOrigen, lonOrigen;
    GoogleMap googleMap;
    ArrayList<Location> listLocs;
    Marker movilMarker, originMarker;
    String latlonOrigen, imei, movil, status, direccion;
    String strMovil;
    Handler mHandler;
    String tag_remis = "remiscar map ";
    GooglePlayServicesHelper locationHelper;
    SharedPrefsUtil prefs;

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

        prefs = SharedPrefsUtil.getInstance(this);
        latmovil = (double) prefs.getFloat("latmovil", 0);
        lonmovil = (double) prefs.getFloat("lonmovil", 0);
        /*if (latmovil == 0) {
            Location newLocation = locationHelper.getLastLocation();
            latmovil = newLocation.getLatitude();
            lonmovil = newLocation.getLongitude();
        }*/
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

    @Subscribe
    public void processUbicacionViaje(CoordenadasViajeEvent result) {
        if (result != null) {
            JsonObject data = result.getObject();
            String coordenadas = data.get("Coordenadas").getAsString();
            Log.d("REMISCAR - ", "****CoordenadasViajeEvent Coordenadas - " + coordenadas);
            String cubierto = data.get("Cubierto").getAsString();
            Log.d("REMISCAR - ", "****CoordenadasViajeEvent Cubierto - " + cubierto);
            prefs.saveString("latlonOrigen", coordenadas);
            addOrigenMarker(coordenadas);
        }
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
        strMovil = location.getLatitude() + "," + location.getLongitude();
        latmovil = (Double) location.getLatitude();
        lonmovil = (Double) location.getLongitude();

        if (movilMarker != null) {
            movilMarker.remove();
        }
        final LatLng Inicio = new LatLng(latmovil, lonmovil);
        movilMarker = googleMap.addMarker(new MarkerOptions().alpha(0.5f).position(Inicio).title("Movil"));
        addOrigenMarker(latlonOrigen);

        //path en mapa
        listLocs.add(location);
        drawPrimaryLinePath(listLocs);

        if (latlonOrigen.equals("")) {
            ServiceUtils.asCoordenadas(MapActivity.this);
        }
    }

    private void addOrigenMarker(String origen) {
        if (originMarker != null) {
            originMarker.remove();
        }

        if (!origen.equals("")) {
            //midPoint(latmovil, lonmovil, latOrigen, lonOrigen);
            String[] separated = origen.split(",");
            latOrigen = Double.parseDouble(separated[0]);
            lonOrigen = Double.parseDouble(separated[1]);

            originMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latOrigen, lonOrigen)).title("Origen"));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(originMarker.getPosition());
            builder.include(movilMarker.getPosition());

            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 80);
            googleMap.animateCamera(cu);
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latmovil, lonmovil), 17.0f));
        }

        //movilMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latmovil, lonmovil)).title("Movil"));
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
        EventBus.getDefault().register(this);

        if (latlonOrigen.equals("")) {
            ServiceUtils.asCoordenadas(MapActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.onPause();
        EventBus.getDefault().unregister(this);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //-34.935506,-57.9556878
        final LatLng Inicio = new LatLng(latmovil, lonmovil);
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latmovil, lonmovil), 17.0f));
        movilMarker = googleMap.addMarker(new MarkerOptions().alpha(0.5f).position(Inicio).title("Movil"));

        //TEST///
        //latlonOrigen="-34.935506,-57.9556878";
        if (!latlonOrigen.equals("")) {
            Log.d(tag_remis, "marcador****LATLON  NOvacio");
            String[] separated = latlonOrigen.split(",");
            addOrigenMarker(latlonOrigen);
        }
    }
}
