package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// pantalla con detalles de alerta recibida desde otro movil
public class AlertaActivity extends Activity {

    String imei, al_status, al_geopos, al_movil, al_fecha, al_ubicacion,mimovil;
    TextView movil, ubicacion, fecha;
    Double lat, lon;


    private static String url_alerta = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanicoalerta.php";

    GoogleMap googleMap;

    Button buttonInicio;

    Handler mHandler;
    //seteo de intervalo de actualizacion de datos
    private final static int INTERVAL = 20 * 1000; // segundos
    int flg_run=0; // flag para el handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try{
            mHandler = new Handler();

            getData();
            buttonInicio = (Button) findViewById(R.id.buttonInicio);
            buttonInicio.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    finish();
                }

            });

            startRepeatingTask();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void getData() {
        try {
            SharedPreferences settings = getSharedPreferences("RemisData", 0);
            al_status = settings.getString("al_status", "");
            imei = settings.getString("imei", "");
            mimovil = settings.getString("movil", "");
            if(al_status.equals("ALERTA")) {
                Log.d("Remiscar AL", "dataAlerta:" + al_fecha + "-" + al_movil + "-" + al_ubicacion + "-" + al_geopos);
                al_geopos = settings.getString("al_geopos", "");
                al_movil = settings.getString("al_movil", "");
                al_fecha = settings.getString("al_fecha", "");
                al_ubicacion = settings.getString("al_ubicacion", "");


                showData();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void showData() {
        try{
            String[] separated = al_geopos.split(",");
            lat = Double.parseDouble(separated[0]);
            lon = Double.parseDouble(separated[1]);

            movil = (TextView) findViewById(R.id.textMovil);
            ubicacion = (TextView) findViewById(R.id.textUbicacion);
            fecha = (TextView) findViewById(R.id.textFecha);

            movil.setText(al_movil);
            ubicacion.setText(al_ubicacion);
            fecha.setText(al_fecha);

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17.0f));
    //-34.935506,-57.9556878
            final LatLng Inicio = new LatLng(lat, lon);
            Marker TP = googleMap.addMarker(new MarkerOptions().position(Inicio).title("Movil"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {

            asAlert();
            //showData();

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask()
    {
        if(flg_run==0)mHandlerTask.run();
        flg_run=1;

    }

    void stopRepeatingTask()
    {
        if(flg_run==1)mHandler.removeCallbacks(mHandlerTask);
        flg_run=0;
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
        finish();

    }

    /**
     * Background Async Task verificar status de Panico
     * */


    private void asAlert() {
        Ion.with(AlertaActivity.this)
                .load(url_alerta)
                .setBodyParameter("status", "")
                .setBodyParameter("Movil", mimovil)
                .setBodyParameter("IMEI", imei)
                .setBodyParameter("movil_al", al_movil)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        processAlert(result);

                    }
                });
    }

    private void processAlert(JsonObject result){
        Log.d("Remiscar A- alerta r-", result.toString());
        // check for success tag
        try {

            al_status = result.get("status").getAsString();

            Log.d("Remiscar AL-", "s- " + al_status);

            if (al_status.equals("ALERTA")||al_status.equals("SEGUIMIENTO")){
                al_fecha = result.get("Fecha").getAsString();
                al_geopos = result.get("GeoPos").getAsString();
                al_movil = result.get("Movil").getAsString();
                al_ubicacion = result.get("Ubicacion").getAsString();
            }

            if (al_status.equals("ALERTA")){

                if(al_movil.equals(movil)){}else{

                    showData();
                    Log.d("Remiscar AL-", "alerta recibida");

                }

            } else if(al_status.equals("SEGUIMIENTO")) {
                showData();
                Log.d("Remiscar AL-", "SEGUIMIENTO");
            } else {

                Log.d("Remiscar AL-", "alerta finalizada");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
