package com.nomade.movilremiscar.remiscarmovil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationListener;
import com.google.gson.JsonObject;
import com.nomade.movilremiscar.remiscarmovil.Util.GooglePlayServicesHelper;
import com.nomade.movilremiscar.remiscarmovil.Util.MinutePollingEvent;
import com.nomade.movilremiscar.remiscarmovil.Util.PollingManager;
import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.Util.SharedPrefsUtil;
import com.nomade.movilremiscar.remiscarmovil.events.AlertEvent;
import com.nomade.movilremiscar.remiscarmovil.events.AutoEvent;
import com.nomade.movilremiscar.remiscarmovil.events.CoordenadasViajeEvent;
import com.nomade.movilremiscar.remiscarmovil.events.InicioFinEvent;
import com.nomade.movilremiscar.remiscarmovil.events.LocationEvent;
import com.nomade.movilremiscar.remiscarmovil.events.MensajeEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PanicEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PollingEvent;
import com.nomade.movilremiscar.remiscarmovil.events.UbicacionEvent;
import com.nomade.movilremiscar.remiscarmovil.events.ValidacionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private boolean fastReload = true;
    String t = "0"; //mensajes test
    private String imei, Direccion;
    String TAG_SUCCESS = "result";
    int st_flag = 0; // status general - 0=libre - 1=ocupado

    int flg_origen = 0; // flag repeating task running
    public int pa = 0;// flag para boton de panico
    int flg_mens = 0; // flag para mensajes
    int flg_mens_auto = 0;
    Double lat = 0.0;
    Double lon = 0.0;

    Button buttonMap, Crono, buttonNov, buttonPanico;
    ImageButton reloadButton;

    String carlitos, carlibres, bahia, bahialibres, status, movil, geopos;
    String Origen, ZonaDestino, ObtCoordenadas;
    String al_status, al_geopos, al_movil, al_fecha, al_ubicacion;

    TextView textNroMovil;


    File outfile = null;

    WebView mWebView;

    /////////////TEST/////////////
    // setear a true para generar el log en memoria sd del equipo.
    boolean flg_logsd = false;
    boolean flg_logsdLOC = false;
    /////////////TEST/////////////

    FrameLayout frmAlerta, frmStatusLoc, frmStatusOrigen;

    private GooglePlayServicesHelper locationHelper;

    String[] mPermission = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int MY_PERMISSIONS_REQUEST = 1;

    Context mContext;

    SharedPrefsUtil sharedPrefs;

    PollingManager pollingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContext = MainActivity.this;
        sharedPrefs = SharedPrefsUtil.getInstance(mContext);

        pollingManager = new PollingManager(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }

        locationHelper = new GooglePlayServicesHelper(this, true);


        ////////////TEST////////////////////////////
        ////log en sdcard
        // setear el IF a true para generar el log

        if (flg_logsd || flg_logsdLOC) {
            File storageDir = new File(Environment
                    .getExternalStorageDirectory(), "/remiscar/");


            storageDir.mkdir();
            try {

                if (outfile == null) {
                    outfile = File.createTempFile("remiscarLog", ".txt", storageDir);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ////log en sdcard
        ////////////TEST////////////////////////////

        initializeUI();
        inicializarDatos();
        checkConnection();
        checkLocationService();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        ServiceUtils.asValidarUsuario(mContext);

        iniciarServicios();

    }


    private void iniciarServicios() {
        setMainView();
        ServiceUtils.asAuto(mContext);
        ServiceUtils.asMensaje(mContext);
        ServiceUtils.asCoordenadas(mContext);
    }

    private void setMainView() {
        String geoposLocal = sharedPrefs.getString("geopos", "");
       // logLocationToSdcard("setMainView - " + geoposLocal);
        String finalUrl = ServiceUtils.url_main + "?imei=" + imei
                + "&Movil=" + movil
                + "&geopos=" + geoposLocal;
        mWebView = (WebView) findViewById(R.id.webViewMain);

        mWebView.setWebViewClient(mainWebClient);
        mWebView.loadUrl(finalUrl);

    }

    WebViewClient mainWebClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.setScrollY(0);
            mWebView.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            final WebView newView = view;

            fastReload = !url.contains("transfer")&&!url.contains("McobrarMercadoPago")&&!url.contains("Mviajeshoyver")&&!url.contains("MTarifador")&&!url.contains("buscar")&&!url.contains("McobroTDF")&&!url.contains("McobrarTDF")&&!url.contains("ppago.php")&&!url.contains("ppago")&&!url.contains("rcar")&&!url.contains("http://arauvoip.dnsalias.net/rcar");
            newView.postDelayed(new Runnable() {
                public void run() {
                    if (newView.getProgress() == 100) {
                        newView.postDelayed(new Runnable() {
                            public void run() {
                                newView.scrollTo(0, 0);
                                //pageloaded = true;
                            }
                        }, 10);
                    } else {
                        newView.post(this);
                    }
                }
            }, 100);


        }

    };

    private void initializeUI() {
        frmStatusLoc = (FrameLayout) findViewById(R.id.frmStatusLoc);

        reloadButton = (ImageButton) findViewById(R.id.buttonReload);
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMainView();
            }
        });

        buttonMap = (Button) findViewById(R.id.buttonMapa);
        buttonMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);

            }

        });
        Crono = (Button) findViewById(R.id.Crono);
        Crono.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, CronoActivity.class);
                startActivity(intent);

            }

        });
        buttonNov = (Button) findViewById(R.id.buttonNov);
        buttonNov.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, NovedadesActivity.class);
                startActivity(intent);

            }

        });

        //boton de panico
        buttonPanico = (Button) findViewById(R.id.buttonPanico);
        buttonPanico.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, PanicActivity.class);
                startActivity(intent);


            }

        });

        //datos en pantalla desde Mactual
        textNroMovil = (TextView) findViewById(R.id.textNroMovil);

        frmAlerta = (FrameLayout) findViewById(R.id.frmAlerta);
        frmAlerta.setVisibility(View.GONE);

        frmAlerta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getSingleLocation();
                Intent intent = new Intent(MainActivity.this, AlertaActivity.class);
                startActivity(intent);

            }

        });
    }

    private void checkPermissions() {
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[1])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[2])
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        mPermission, MY_PERMISSIONS_REQUEST);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted.


        } else {

            // permission denied.
            Toast.makeText(mContext, "Faltan permisos necesarios para funcionar.", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void logToSdcard(String tag, String statement) {
        // generacion de log en memoria sd del equipo

        ///////////////////TEST///////////////////

        if (flg_logsdLOC) {

            String state = android.os.Environment.getExternalStorageState();
            if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
                try {
                    throw new IOException("SD Card is not mounted.  It is " + state + ".");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());


            try {


                try (FileOutputStream fOut = new FileOutputStream(outfile, true)) {
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(currentDateandTime + " " + tag + "     ");
                    myOutWriter.append(statement);
                    myOutWriter.append("\n");
                    myOutWriter.flush();
                    myOutWriter.close();
                    fOut.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void inicializarDatos() {
        carlitos = "";
        carlibres = "";
        bahia = "";
        bahialibres = "";
        status = "";
        ObtCoordenadas = "";
        Origen = "";
        ZonaDestino = "";
        movil = "";
        imei = getPhoneImei();
        //imei = "359015062458232";//TEST/////
        SharedPrefsUtil settings = SharedPrefsUtil.getInstance(mContext);
        settings.saveFloat("latmovil", 0);
        settings.saveFloat("lonmovil", 0);
        settings.saveString("geopos", "");
        settings.saveString("Traslados", "");
        settings.saveString("latlonOrigen", "");
        settings.saveString("movil", "");
        settings.saveString("imei", imei);
        settings.saveString("al_status", "");
        settings.saveString("al_fecha", "");
        settings.saveString("al_geopos", "");
        settings.saveString("al_movil", "");
        settings.saveString("al_ubicacion", "");

    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ((netInfo != null) && netInfo.isConnected());
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((netInfo != null) && netInfo.isConnected());
    }

    public void checkConnection() {
        if (isWifiConnected(MainActivity.this)) {
            Log.d("Remiscar ", " WIFI conectado");
            logToSdcard("Remiscar ", " WIFI conectado");
            frmStatusLoc.setBackgroundColor(Color.parseColor("#ff9d9d9d"));
        } else if (isMobileConnected(MainActivity.this)) {
            Log.d("Remiscar ", " 3G conectado");
            logToSdcard("Remiscar ", " 3G conectado");
            frmStatusLoc.setBackgroundColor(Color.parseColor("#ff9d9d9d"));
        } else {
            Toast.makeText(getBaseContext(), "Esperando conexion.. ", Toast.LENGTH_LONG).show();
            frmStatusLoc.setBackgroundColor(Color.parseColor("#FF0000"));
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        pollingManager.stopRepeatingTask();
        locationHelper.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        locationHelper.onResume(MainActivity.this);
        pollingManager.startRepeatingTask();

    }

    private void getSingleLocation() {
        if (sharedPrefs != null) {
            Location singleLocation = locationHelper.getLastLocation();
            saveLocationData(singleLocation);
        }
    }

    private void saveLocationData(Location singleLocation) {
        sharedPrefs.saveFloat("latmovil", ((float) singleLocation.getLatitude()));
        sharedPrefs.saveFloat("lonmovil", ((float) singleLocation.getLongitude()));
        String str = singleLocation.getLatitude() + "," + singleLocation.getLongitude();
        sharedPrefs.saveString("geopos", str);
        Log.d("Remiscar ", "saveLocationData -" + str);
       // logLocationToSdcard("saveLocationData - " + str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    //Obtener numero de imei
    private String getPhoneImei() {
        String imei = "";
        TelephonyManager mTelephonyManager;
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

        } else {
        imei = mTelephonyManager.getDeviceId();
    }

        Log.d("Remiscar ", " - set imei -" + imei);
        return imei;
    }


    @Override
    public void onLocationChanged(Location location) {


        // metodo vacio.

        // la localizacion se obtiene en getSingleLocation().

    }

    public void getMyLocationAddress() {

        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon;
            //+"&sensor=false&location_type=RANGE_INTERPOLATED&key=AIzaSyBZsub9dRqt2nJXZrSBzLRpFh4e0iK7XAE";
            ServiceUtils.asUbicacion(mContext, url);
        } catch (Exception e) {

            e.printStackTrace();
            if (t == "1")
                Toast.makeText(mContext, "Could not get address..!", Toast.LENGTH_LONG).show();
        }
    }

    // getting location.

    @Subscribe()
    public void processUbicacion(UbicacionEvent data) {

        try {
            JsonObject result = data.getObject();
            Log.d("Remiscar ", "LOC --" + result.getAsString());
            logToSdcard("Remiscar ", "LOC --" + result.getAsString());

            JsonObject location = result.getAsJsonArray("results").get(0).getAsJsonObject();
            String location_string = location.get("formatted_address").getAsString();
            Direccion = location_string;
            sharedPrefs.saveString("Direccion", Direccion);

            frmStatusLoc.setBackgroundColor(Color.parseColor("#00FF00"));

        } catch (Exception ee) {
            if (t == "1")
                Toast.makeText(getBaseContext(), "No se puede recuperar ubicacion.", Toast.LENGTH_LONG).show();
            Log.d("Remiscar ", "No se puede recuperar ubicacion.");
            logToSdcard("Remiscar ", "No se puede recuperar ubicacion.");
            frmStatusLoc.setBackgroundColor(Color.parseColor("#ff9d9d9d"));
        }

    }


    public void getAddressLocation() {

        try {
            String lOrigen = Calles(Origen);

            String formatedUrl = URLEncoder.encode(lOrigen + ",Ushuaia,Tierra del Fuego,Argentina", "utf-8");
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + formatedUrl;//&key=AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw";
            /////TEST//////////////////
            //String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+ Origen +",La Plata,La Plata,Argentina&key=AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw";
            /////TEST//////////////////
            ServiceUtils.asLocation(mContext, url);
            Log.d("REMISCAR - ", "Buscando Direccion ****** " + lOrigen);
            logToSdcard("REMISCAR - ", "Buscando Direccion ****** " + lOrigen);
        } catch (Exception e) {

            e.printStackTrace();
            if (t == "1")
                Toast.makeText(getApplicationContext(), "Could not get address..!", Toast.LENGTH_LONG).show();

        }
    }

    private String Calles(String origen) {
        /// Formateo particular de calles ///
        // para agregar nuevas calles agregar nuevs if o else if con los datos correspondientes.
        // locOrigen es la variable de salida con el formato correcto
        // dejar siempre al final el else por defecto
        // ejemplo:
        // else if (origen.toLowerCase().contains("calle xx")){
        //    locOrigen = Origen.toLowerCase().replaceFirst("calle xx", "calle correcta");}
        //
        // para verificar la calle:
        // https://maps.googleapis.com/maps/api/geocode/json?address=calle,Ushuaia,Tierra del Fuego,Argentina&key=AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw
        // debe aparecer al final de los datos devueltos:
        // "status" : "OK"

        String locOrigen = "";

        if (origen.toLowerCase().contains("alem") && !origen.equals("av. alem") && !origen.equals("av. leandro n. alem")) {
            locOrigen = Origen.toLowerCase().replaceFirst("alem", "av. alem");
        } else {
            // dato por defecto retorna origen sin cambios.
            return origen;
        }

        if (origen.toLowerCase().contains("ELCANO") && !origen.equals("xxx") && !origen.equals("xxx")) {
            locOrigen = Origen.toLowerCase().replaceFirst("ELCANO", "xxx");
        }

        return locOrigen;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void processUbicacionViaje(CoordenadasViajeEvent result) {
        if (result != null) {
            JsonObject data = result.getObject();
            String coordenadas = data.get("Coordenadas").getAsString();
            Log.d("REMISCAR - ", "****CoordenadasViajeEvent Coordenadas - " + coordenadas);
            String cubierto = data.get("Cubierto").getAsString();
            Log.d("REMISCAR - ", "****CoordenadasViajeEvent Cubierto - " + cubierto);
            sharedPrefs.saveString("latlonOrigen", coordenadas);
        }
    }

    @Subscribe()
    public void processLocation(LocationEvent result) {
        String data = result.getObject();
        Log.d("REMISCAR - ", " ADDRESS- POST LOCATION");
        logToSdcard("REMISCAR - ", " ADDRESS- POST LOCATION");
        JSONObject results = null;
        JSONObject geometry = null;
        JSONObject location = null;
        String locationType = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            Log.d("REMISCAR - ", " ADDRESS- ret- " + jsonObject.toString());
            logToSdcard("REMISCAR - ", " ADDRESS- ret- " + jsonObject.toString());
            if (jsonObject.has("results")) {
                results = jsonObject.getJSONArray("results").getJSONObject(0);
                Log.d("REMISCAR - ", " ADDRESS- results- " + results.toString());
                logToSdcard("REMISCAR - ", " ADDRESS- results- " + results.toString());


                if (results.has("geometry")) {
                    geometry = results.getJSONObject("geometry");
                }
                if (geometry.has("location")) {
                    location = geometry.getJSONObject("location");
                }
                if (geometry.has("location_type")) {
                    locationType = geometry.getString("location_type");
                }

                if (locationType.equals("APPROXIMATE")) {
                    // si geopos no puede encontrar la direccion devuelve APPROXIMATE
                    // enviamos vacio en latlonOrigen
                    sharedPrefs.saveString("latlonOrigen", "");

                    Log.d("REMISCAR - ", " ADDRESS- fallo en geopos");
                    logToSdcard("REMISCAR - ", " ADDRESS- fallo en geopos");
                    frmStatusOrigen.setBackgroundColor(Color.parseColor("#fff4b2"));
                } else {
                    // si geopos detecta la direccion enviamos las coordenadas encontradas
                    Double location_lat = location.getDouble("lat");
                    Double location_lon = location.getDouble("lng");
                    ObtCoordenadas = String.valueOf(location_lat) + "," + String.valueOf(location_lon);
                    sharedPrefs.saveString("latlonOrigen", ObtCoordenadas);
                    sharedPrefs.saveString("geopos", ObtCoordenadas);

                    Log.d("REMISCAR - ", " ADDRESS OK-" + ObtCoordenadas);
                    logToSdcard("REMISCAR - ", " ADDRESS OK-" + ObtCoordenadas);
                    frmStatusOrigen.setBackgroundColor(Color.parseColor("#00ff00"));
                    flg_origen = 1;// seteo flag para no usar la api de geopos si ya obtuvo la localizacion
                }
            } else {
                Log.d("REMISCAR - ", " ADDRESS- NO results- ");
                logToSdcard("REMISCAR - ", " ADDRESS- NO results- ");
                frmStatusOrigen.setBackgroundColor(Color.parseColor("#fff4b2"));
            }

        } catch (JSONException e) {
            //Toast.makeText(getBaseContext(), "No se puede recuperar ubicaci�n.", Toast.LENGTH_LONG).show();
            Log.d("Remiscar - ", "ADDRESS-Error con direccion");
            logToSdcard("Remiscar - ", "ADDRESS-Error con direccion");
        }
    }

    //Location fin

    @Subscribe()
    public void processValidacion(ValidacionEvent result) {
        if (result != null) {
            JsonObject data = result.getObject();
            // check log cat from response
            Log.d("Remiscar* - ", "Login Response " + data.toString());
            logToSdcard("Remiscar - ", "Login Response " + data.toString());
            // check for success tag

            int success = 1;//data.get(TAG_SUCCESS).getAsInt();
            if (success == 1 || success == 2) {
                if (data.has("movil") && !data.get("movil").isJsonNull())
                    movil = data.get("movil").getAsString();
            }

            if (success == 2) {
                sharedPrefs.saveString("movil", movil);
                sharedPrefs.saveString("imei", imei);

                Intent intent = new Intent(mContext, PropActivity.class);
                startActivity(intent);
                finish();

            } else if (success == 1) {
                //inicio valido para movil autorizado
                Log.w("Remiscar", "VALIDACION - " + movil + " - " + imei);
                textNroMovil.setText(movil);
                sharedPrefs.saveString("movil", movil);
                sharedPrefs.saveString("imei", imei);

                pollingManager.startRepeatingTask();


            } else if (success == 0) {
                Toast.makeText(mContext, "App solo para propietarios autorizados.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            } else {
                Toast.makeText(mContext, "No se pudo conectar. Intente de nuevo.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void processInicioFin(InicioFinEvent result) {
        try {
            if (result != null) {
                Log.d("Remiscar-IF-Response ", result.toString());
                logToSdcard("Remiscar-IF-Response ", result.toString());
            }
        } catch (Exception e) {
            Log.e("Remiscar ", e.toString());
        }

    }

    @Subscribe
    public void processAlert(AlertEvent data) {
        // check for success tag
        try {
            JsonObject result = data.getObject();
            Log.d("Remiscar A- alerta r-", result.toString());
            logToSdcard("Remiscar - alerta r-", result.toString());
            //success = json.getInt(TAG_SUCCESS);
            //al_status, al_geopos, al_movil, al_fecha, al_ubicacion

            al_status = result.get("status").getAsString();
            //al_status = "ALERTA";/////////////////////////////////////////TEST

            Log.d("Remiscar A-", "s- " + al_status);
            logToSdcard("Remiscar -", "s- " + al_status);
            if (al_status.equals("ALERTA") || al_status.equals("SEGUIMIENTO")) {
                    /*al_fecha = "11/08";//json.getString("Fecha");
                    al_geopos = "-34.935506,-57.9556878";//json.getString("GeoPos");
                    al_movil = "1";//json.getString("Movil");
                    al_ubicacion = "57 y 23";//json.getString("Ubicacion");*/

                al_fecha = result.get("Fecha").getAsString();
                al_geopos = result.get("GeoPos").getAsString();
                al_movil = result.get("Movil").getAsString();
                al_ubicacion = result.get("Ubicacion").getAsString();
            }

            if (al_status.equals("ALERTA") || al_status.equals("SEGUIMIENTO")) {
                Log.d("Remiscar AL-", "moviles - " + al_movil + " - " + movil);
                if (al_movil.equals(movil)) {
                    // la alerta es de este movil, envio actualizacion de datos.
                    Log.d("Remiscar -", "SEGUIMIENTO");
                    logToSdcard("Remiscar -", "SEGUIMIENTO");

                    //obtengo direccion en seguimiento
                    getMyLocationAddress();
                    ServiceUtils.asPanic(MainActivity.this, "SEGUIMIENTO", Direccion, geopos);
                } else {
                    sharedPrefs.saveString("al_status", al_status);
                    sharedPrefs.saveString("al_fecha", al_fecha);
                    sharedPrefs.saveString("al_geopos", al_geopos);
                    sharedPrefs.saveString("al_movil", al_movil);
                    sharedPrefs.saveString("al_ubicacion", al_ubicacion);

                    frmAlerta.setVisibility(View.VISIBLE);
                    //AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) frmAlerta.getLayoutParams();
                    //params.height = 100;
                    Log.d("Remiscar -", "alerta recibida");
                    logToSdcard("Remiscar -", "alerta recibida");
                }

            } else {
                frmAlerta.setVisibility(View.GONE);
                Log.d("Remiscar -", "alerta finalizada");
                logToSdcard("Remiscar -", "alerta finalizada");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe()
    public void processMensaje(MensajeEvent data) {
        //reenvio a url de Mviajeshoy.php para actualizar datos de geopos.

        int success;
        JsonObject result = new JsonObject();
        result = data.getObject();
        Log.d("Remiscar", "data:" + imei + "-" + movil);
        logToSdcard("Remiscar", "data:" + imei + "-" + movil);

        // check for success tag
        try {
            Log.d("Remiscar - mensaje r-", result.toString());
            logToSdcard("Remiscar - mensaje r-", result.toString());
            success = result.get(TAG_SUCCESS).getAsInt();

            Log.d("Remiscar -", "s- " + success);
            logToSdcard("Remiscar -", "s- " + success);

            if (success == 0) {
                Log.d("Remiscar -", "sin mensajes.");
                logToSdcard("Remiscar -", "sin mensajes.");
                flg_mens = 0;
                buttonNov.setText("NOVEDADES");
                buttonNov.setBackgroundColor(Color.parseColor("#01579B"));
                buttonNov.setTextColor(Color.parseColor("#d5d9ea"));
            } else if (success == 1) {
                Log.d("Remiscar -", "HAY mensajes.");
                logToSdcard("Remiscar -", "HAY mensajes.");
                if (flg_mens == 0) {
                    Toast.makeText(mContext, "Hay nuevos mensajes para usted.", Toast.LENGTH_SHORT).show();
                    final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.c2answer);
                    mp.start();
                    buttonNov.setText("HAY MENSAJES");
                    buttonNov.setBackgroundColor(Color.parseColor("#d5d9ea"));
                    buttonNov.setTextColor(Color.parseColor("#01579B"));
                    flg_mens = 1;
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe()
    public void processAuto(AutoEvent data) {
        int success;
        JsonObject result = new JsonObject();
        result = data.getObject();
        Log.d("Remiscar", "data:" + imei + "-" + movil);
        logToSdcard("Remiscar", "data:" + imei + "-" + movil);

        // check for success tag
        try {
            Log.d("Remiscar - mensaje r-", result.toString());
            logToSdcard("Remiscar - mensaje r-", result.toString());
            success = result.get(TAG_SUCCESS).getAsInt();

            Log.d("Remiscar -", "s- " + success);
            logToSdcard("Remiscar -", "s- " + success);

            if (success == 0) {
                Log.d("Remiscar -", "sin mensajes.");
                logToSdcard("Remiscar -", "sin mensajes.");
                flg_mens_auto = 0;
            } else if (success == 1) {
                Log.d("Remiscar -", "AUTODESPACHO.");
                logToSdcard("Remiscar -", "AUTODESPACHO.");
                if (flg_mens_auto == 0) {
                    Toast.makeText(mContext, "AUTODESPACHO.", Toast.LENGTH_SHORT).show();
                    final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.c2answer);
                    mp.start();
                    flg_mens_auto = 1;
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void processPanic(PanicEvent data) {
        JsonObject result = new JsonObject();
        result = data.getObject();
        Log.d("Remiscar", "envio Panic");
        Log.d("Remiscar", "data:" + imei + "-" + movil + "-" + Direccion + "-" + geopos);

        // check for success tag
        try {
            Log.d("Remiscar panic Res-", result.toString());
            if (status == "PRUEBA") {
                Log.d("Remiscar panic -", "PRUEBA");

            } else {
                Log.d("Remiscar panic -", " seguimiento ALERTA");
                //Intent intent = new Intent(PanicActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void onPollingStep(PollingEvent event) {
        if(fastReload){
            processPollingStep();
        }

    }
    @Subscribe
    public void onMinutePollingStep(MinutePollingEvent event) {
        if(!fastReload){
            processPollingStep();
        }
    }

    private void processPollingStep() {
        pa = 0;//reset boton de panico
        flg_origen = 0;
        if (textNroMovil.getText().toString().equals("00")
                || textNroMovil.getText().toString().equals("")) {
            textNroMovil.setText(movil.toString());
        }
        if (movil.equals("")) {
            ServiceUtils.asValidarUsuario(mContext);
        }
        getSingleLocation();
        setMainView();
    }


    public void checkLocationService() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(R.string.gps_disabled);
            dialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(mContext, "La aplicación no funcionará correctamente si no activa la localización del equipo.", Toast.LENGTH_LONG).show();

                }
            });
            dialog.show();
        }
    }}