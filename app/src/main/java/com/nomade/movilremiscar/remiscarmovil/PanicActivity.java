package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nomade.movilremiscar.remiscarmovil.Util.GooglePlayServicesHelper;
import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


//pantalla de envio de mensaje de alerta
public class PanicActivity extends Activity implements LocationListener {

    Button prueba, alerta, inicio;
    EditText edit;

    private static String url_panico = ServiceUtils.base_url + "Mpanico.php";


    private String imei, Direccion, movil, geopos;
    String TAG_SUCCESS = "result";

    SharedPrefsUtil sharedPrefs;

    private ProgressDialog pDialog;
    //coordenadas del movil.
    Double latmovil, lonmovil;

    private GooglePlayServicesHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic);

        sharedPrefs = SharedPrefsUtil.getInstance(PanicActivity.this);

        locationHelper = new GooglePlayServicesHelper(this, true);

        imei = sharedPrefs.getString("imei", "");
        //imei = "359015062458232";//TEST/////
        //coordenadas del movil.
        latmovil = 0.0;
        lonmovil = 0.0;

        movil = sharedPrefs.getString("movil", "");
        Direccion = sharedPrefs.getString("Direccion", "");
        geopos = sharedPrefs.getString("geopos", "");
        latmovil = (double) sharedPrefs.getFloat("latmovil", 0);
        lonmovil = (double) sharedPrefs.getFloat("lonmovil", 0);

        edit = (EditText) findViewById(R.id.editText3);
        prueba = (Button) findViewById(R.id.buttonPrueba);
        alerta = (Button) findViewById(R.id.buttonAlerta);
        inicio = (Button) findViewById(R.id.buttonInicio);

        prueba.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if ("".equals(geopos)) {
                    Toast.makeText(PanicActivity.this, "Localizacion no detectada. Vuelva a intentar.", Toast.LENGTH_LONG).show();
                }else{

                    Toast.makeText(PanicActivity.this, "PRUEBA de alerta enviada.", Toast.LENGTH_LONG).show();
                    prueba.setBackgroundColor(Color.parseColor("#326166"));
                    processAlert("PRUEBA");
                }

            }
        });

        alerta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if ("".equals(geopos)) {
                    Toast.makeText(PanicActivity.this, "Localizacion no detectada. Vuelva a intentar.", Toast.LENGTH_LONG).show();
                }else{

                    Log.d("Remiscar*", "-LOCATION-" + latmovil + " - " + lonmovil);
                    processAlert("ALERTA");
                }

            }

        });

        inicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.onResume(PanicActivity.this);
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

    public void processAlert(String type) {

        try {
            // si es ALERTA hago la llamada a la api de google a obtener la direccion.
            if (type.equals("PRUEBA")) {
                asPanic(type, url_panico);

            } else {
                String url_location = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latmovil + "," + lonmovil
                        + "&key=AIzaSyCKBR3GAk_m3_Ub3VCDx8MTVucs2acq0-4";
                asUbicacion(url_location, type);
            }


        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    private void asUbicacion(String url_ubicacion, final String type) {
        Log.d("Remiscar* ", "LOC URL--" + url_ubicacion);
        Ion.with(PanicActivity.this)
                .load(url_ubicacion)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Log.d("Remiscar* ", "LOC --" + result.toString());

                            JsonObject location = result.getAsJsonArray("results").get(0).getAsJsonObject();
                            String location_string = location.get("formatted_address").getAsString();
                            Direccion = location_string;
                            Log.d("Remiscar* ", "LOC ADD--" + Direccion);
                            if (Direccion.equals("")) {
                                Direccion = "Sin determinar.";
                            }

                            asPanic(type, url_panico);

                        } catch (Exception ee) {
                            Log.d("Remiscar* ", "No se puede recuperar ubicacion.");
                        }

                    }
                });
    }


    /**
     * Background Async Task mensaje de Panico
     */
    private void asPanic(String statusIn, String url) {
        final String status = statusIn;
        try {
            String mDireccion = URLEncoder.encode(Direccion, "utf-8");
            String url_params = url + "?status=" + statusIn + "&Movil=" + movil + "&IMEI=" + imei +
                    "&Ubicacion=" + mDireccion + "&geopos=" + geopos;
            Log.d("Remiscar*", "-params-" + url_params);


            Ion.with(PanicActivity.this)
                    .load(url_params)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            if (result != null) {
                                Log.d("Remiscar*", "---------" + result.toString());
                                processPanic(result, status);
                            } else {
                                Log.d("Remiscar*", "-ALERT NULL-");
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void processPanic(JsonObject result, String status) {
        String retStatus;
        Log.d("Remiscar*", "envio Panic");
        Log.d("Remiscar*", "data:" + imei + "-" + movil + "-" + Direccion + "-" + geopos);

        //Log.d("Remiscar panic Res-", result.toString());

        // check for success tag
        try {
            Log.d("Remiscar* panic -", "P - " + result.toString());
            retStatus = result.get("status").toString();

            if (retStatus.contains("PRUEBA EXITOSA")) {

                Log.d("Remiscar* panic -", "PRUEBA");
                edit.setText("PRUEBA EXITOSA. IMEI:" + imei + " - Ubicacion:" + Direccion + " - Movil:" + movil + " - " + geopos);

            } else {
                Log.d("Remiscar* panic -", "ALERTA");

                finish();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        latmovil = (Double) location.getLatitude();
        lonmovil = (Double) location.getLongitude();
        String str = location.getLatitude() + "," + location.getLongitude();
        Log.d("Remiscar ", " -panicActivity set location -" + str);
        geopos = str;
        sharedPrefs.saveFloat("latmovil", latmovil.floatValue());
        sharedPrefs.saveFloat("lonmovil", lonmovil.floatValue());
        sharedPrefs.saveString("geopos", str);
    }

    private void getSingleLocation() {
        if (sharedPrefs != null && locationHelper != null) {
            try {
                Location singleLocation = locationHelper.getLastLocation();
                sharedPrefs.saveFloat("latmovil", ((float) singleLocation.getLatitude()));
                sharedPrefs.saveFloat("lonmovil", ((float) singleLocation.getLongitude()));
                String str = singleLocation.getLatitude() + "," + singleLocation.getLongitude();
                geopos = str;
                sharedPrefs.saveString("geopos", str);
                Log.d("Remiscar ", " -panicActivity getSingleLocation -" + str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

