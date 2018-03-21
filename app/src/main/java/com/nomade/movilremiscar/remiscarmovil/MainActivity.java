package com.nomade.movilremiscar.remiscarmovil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.events.MActualEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements LocationListener {

    String t = "0"; //mensajes test
    private LocationManager locationManager;
    private String imei, Direccion;
    String TAG_SUCCESS = "result";
    int st_flag = 0; // status general - 0=libre - 1=ocupado
    int flg_run = 0; // flag repeating task running
    int flg_origen = 0; // flag repeating task running
    public int pa = 0;// flag para boton de panico
    int flg_mens = 0; // flag para mensajes
    Double lat, lon;
    private int clearCacheCounter = 0;

    private ProgressDialog pDialog;
    Button buttonMap, buttonNov, buttonViajes, buttonCobrar, buttonPanico, buttonInicio;
    LinearLayout layZonas;

    String carlitos, carlibres, bahia, bahialibres, status, movil, geopos;
    String HCubierto, Origen, Pasajero, ObtDestino, Traslados, CantViajes,
            ZonaDestino, ObtAgencia, Observaciones, ObtCoordenadas;
    String al_status, al_geopos, al_movil, al_fecha, al_ubicacion;

    TextView textCarlitos, textLibres, textBahia, textLibresB, textOrigen, textDestino, textPasajero,
            textHoraViaje, textEmpresa, textObs, textStatus, textNroMovil;

    private static String url_validacion = "http://carlitosbahia.dynns.com/legajos/viajes/Mvalidacion.php";
    private static String url_actual = "http://carlitosbahia.dynns.com/legajos/viajes/Mactual3.php";
    private static String url_iniciofin = "http://carlitosbahia.dynns.com/legajos/viajes/Miniciofin.php";
    private static String url_alerta = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanicoalerta.php";
    private static String url_mensaje = "http://carlitosbahia.dynns.com/legajos/viajes/Mmensajes.php";
    private static String url_auto = "http://carlitosbahia.dynns.com/legajos/viajes/Mauto.php";
    private static String url_panico = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanico.php";


    //seteo de intervalo de actualizacion de datos
    private final static int INTERVAL = 20 * 1000; // segundos
    Handler mHandler;
    File outfile = null;

    /////////////TEST/////////////
    // setear a true para generar el log en memoria sd del equipo.
    boolean flg_logsd = false;
    /////////////TEST/////////////

    FrameLayout frmAlerta, frmStatusLoc, frmStatusOrigen;

    String[] mPermission = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int MY_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        ////////////TEST////////////////////////////
        ////log en sdcard
        // setear el IF a true para generar el log

        if (flg_logsd) {
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

        inicializarDatos();
        checkConnection();
        //Location inicio
        locationInicio();
        //Location fin

        mHandler = new Handler();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        asValidarUsuario();

        initializeUI();

    }

    private void initializeUI() {
        frmStatusLoc = (FrameLayout) findViewById(R.id.frmStatusLoc);
        frmStatusOrigen = (FrameLayout) findViewById(R.id.frmStatusOrigen);
        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        buttonInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (st_flag == 0) {
                    callDialogDestinoInicio();

                }

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

        buttonNov = (Button) findViewById(R.id.buttonNov);
        buttonNov.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, NovedadesActivity.class);
                startActivity(intent);

            }

        });

        buttonViajes = (Button) findViewById(R.id.buttonViajes);
        buttonViajes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, ViajesActivity.class);
                startActivity(intent);

            }

        });


        buttonCobrar = (Button) findViewById(R.id.buttonCobrar);
        buttonCobrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (st_flag == 1) {
                    callDialogDestinoFin();

                }

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

        layZonas = (LinearLayout) findViewById(R.id.layZonas);
        layZonas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, ZonasActivity.class);
                startActivity(intent);

            }

        });

        //datos en pantalla desde Mactual
        textCarlitos = (TextView) findViewById(R.id.textCarlitos);
        textLibres = (TextView) findViewById(R.id.textLibres);
        textBahia = (TextView) findViewById(R.id.textBahia);
        textLibresB = (TextView) findViewById(R.id.textLibresB);
        textOrigen = (TextView) findViewById(R.id.textOrigen);
        textDestino = (TextView) findViewById(R.id.textDestino);
        textPasajero = (TextView) findViewById(R.id.textPasajero);
        textEmpresa = (TextView) findViewById(R.id.textEmpresa);
        textObs = (TextView) findViewById(R.id.textObs);
        textHoraViaje = (TextView) findViewById(R.id.textHoraViaje);
        textStatus = (TextView) findViewById(R.id.textStatus);
        textNroMovil = (TextView) findViewById(R.id.textNroMovil);
        textStatus.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
                if (textStatus.getText() == "LIBRE") {
                    textStatus.setTextColor(Color.parseColor("#00ff00"));
                } else {
                    textStatus.setTextColor(Color.parseColor("#ff0000"));
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        frmAlerta = (FrameLayout) findViewById(R.id.frmAlerta);
        frmAlerta.setVisibility(View.GONE);

        frmAlerta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

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
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[3])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[4])
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
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted.


        } else {

            // permission denied.
            //Toast.makeText(getBaseContext(), "Permissions need to be granted.", Toast.LENGTH_LONG).show();
        }

    }


    public void callDialogDestinoFin() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Fin Viaje");
        alertDialog.setMessage("Ingrese zona donde finalizo:");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog.setView(input);


        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ZonaDestino = input.getText().toString();

                        buttonInicio.setBackgroundColor(Color.parseColor("#FF08FF02"));
                        buttonCobrar.setBackgroundColor(Color.parseColor("#848FBF"));
                        asInicioFin("fin");
                        Toast.makeText(getBaseContext(), "Viaje Finalizado.", Toast.LENGTH_LONG).show();
                        final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.c2answer);
                        mp.start();
                        st_flag = 0;
                        flg_origen = 0;
                    }
                });


        alertDialog.show();
    }

    public void callDialogDestinoInicio() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Destino");
        alertDialog.setMessage("Ingrese zona final:");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog.setView(input);


        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ZonaDestino = input.getText().toString();

                        buttonInicio.setBackgroundColor(Color.parseColor("#848FBF"));
                        buttonCobrar.setBackgroundColor(Color.parseColor("#FF08FF02"));
                        asInicioFin("inicio");
                        Toast.makeText(getBaseContext(), "Viaje Iniciado.", Toast.LENGTH_LONG).show();
                        final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.c2answer);
                        mp.start();
                        st_flag = 1;

                    }
                });


        alertDialog.show();
    }


    public void logToSdcard(String tag, String statement) {
        // generacion de log en memoria sd del equipo

        ///////////////////TEST///////////////////

        if (flg_logsd) {

            Log.d("Remiscar -", "inside logtosdcard$$");

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


                FileOutputStream fOut = new FileOutputStream(outfile, true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(currentDateandTime + " " + tag + "     ");
                myOutWriter.append(statement);
                myOutWriter.append("\n");
                myOutWriter.flush();
                myOutWriter.close();
                fOut.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void locationInicio() {
        PackageManager pm = MainActivity.this.getPackageManager();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
                Log.d("Remiscar -", " GPS conectado");
                logToSdcard("Remiscar -", " GPS conectado");
            } else {
                showGPSDisabledAlertToUser();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
                Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                Log.d("Remiscar -", " NETWORK over GPS");
                logToSdcard("Remiscar -", " NETWORK over GPS");
            }

        } else {
            showGPSDisabledAlertToUser();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
            Log.d("Remiscar -", " NETWORK conectado");
            logToSdcard("Remiscar -", " NETWORK conectado");
        }
        /////TEST//////////////////
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
        /////TEST//////////////////
        //processAlert();
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
        imei = getPhoneImei();
        //imei = "359015062458232";//TEST/////
        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("latmovil", 0);
        editor.putFloat("lonmovil", 0);
        editor.putString("geopos", "");
        editor.putString("Traslados", "");
        editor.putString("latlonOrigen", "");
        editor.putString("movil", "");
        editor.putString("imei", imei);
        editor.putString("al_status", "");
        editor.putString("al_fecha", "");
        editor.putString("al_geopos", "");
        editor.putString("al_movil", "");
        editor.putString("al_ubicacion", "");
        editor.putString("movil", "");


        // Commit the edits!
        editor.commit();

    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS deshabilitado. Quiere activarlo?")
                .setCancelable(false)
                .setPositiveButton("Ir a GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
        stopRepeatingTask();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopRepeatingTask();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //inicializarDatos();
        if (flg_run == 0) {
            startRepeatingTask();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        //if(flg_run==0){startRepeatingTask();};
        // Activity being restarted from stopped state
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

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            checkConnection();
            //processAlert();
            ServiceUtils.asMActual(MainActivity.this, status, movil, imei,
                    Direccion, geopos);
            asMensaje();
            asAuto();
            asAlert();
            clearCacheCounter++; // 180 == 1 hora
            if (clearCacheCounter >= (180 * 1)) {
                deleteCache(MainActivity.this);
                clearCacheCounter = 0;
                Log.w("Remiscar*", "***** CLEAR CACHE *****");
            }
            pa = 0;//reset boton de panico
            flg_origen = 0;
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


    //Obtener numero de imei
    private String getPhoneImei() {
        TelephonyManager mTelephonyManager;
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

        }
        return mTelephonyManager.getDeviceId();
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


        String str = location.getLatitude() + "," + location.getLongitude();

        Log.d("Remiscar ", " - set location -" + str);
        logToSdcard("Remiscar ", " - set location -" + str);
        lat = (Double) location.getLatitude();
        lon = (Double) location.getLongitude();
        ////////////////////TEST///////////
        /*lat = -54.805006;
        lon = -68.330199;
        str = lat.toString()+","+lon.toString();*/
        ////////////////////TEST///////////
        geopos = str;
        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("latmovil", lat.floatValue());
        editor.putFloat("lonmovil", lon.floatValue());
        editor.putString("geopos", str);

        // Commit the edits!
        editor.commit();


        //processAlert();
    }

    public void getMyLocationAddress() {

        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon;
            //+"&sensor=false&location_type=RANGE_INTERPOLATED&key=AIzaSyBZsub9dRqt2nJXZrSBzLRpFh4e0iK7XAE";
            asUbicacion(url);
        } catch (Exception e) {

            e.printStackTrace();
            if (t == "1")
                Toast.makeText(getApplicationContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
        }
    }

    // getting location.

    private void asUbicacion(String url_ubicacion) {
        Ion.with(MainActivity.this)
                .load(url_ubicacion)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Log.d("Remiscar ", "LOC --" + result.getAsString());
                            logToSdcard("Remiscar ", "LOC --" + result.getAsString());

                            JsonObject location = result.getAsJsonArray("results").get(0).getAsJsonObject();
                            String location_string = location.get("formatted_address").getAsString();
                            Direccion = location_string;
                            SharedPreferences settings = getSharedPreferences("RemisData", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Direccion", Direccion);
                            // Commit the edits!
                            editor.commit();


                            frmStatusLoc.setBackgroundColor(Color.parseColor("#00FF00"));

                        } catch (Exception ee) {
                            if (t == "1")
                                Toast.makeText(getBaseContext(), "No se puede recuperar ubicacion.", Toast.LENGTH_LONG).show();
                            Log.d("Remiscar ", "No se puede recuperar ubicacion.");
                            logToSdcard("Remiscar ", "No se puede recuperar ubicacion.");
                            frmStatusLoc.setBackgroundColor(Color.parseColor("#ff9d9d9d"));
                        }

                    }
                });
    }


    public void getAddressLocation() {

        try {
            String lOrigen = Calles(Origen);

            String formatedUrl = URLEncoder.encode(lOrigen + ",Ushuaia,Tierra del Fuego,Argentina", "utf-8");
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + formatedUrl;//&key=AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw";
            /////TEST//////////////////
            //String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+ Origen +",La Plata,La Plata,Argentina&key=AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw";
            /////TEST//////////////////
            //new HttpAsyncLocation().execute(url);
            asLocation(url);
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


    private void asLocation(String url_location) {
        Ion.with(MainActivity.this)
                .load(url_location)
                .setBodyParameter("key", "AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw")//AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            processLocation(result);

                        } catch (Exception ee) {
                            Log.d("Remiscar ", " GET - error" + ee.toString());
                            logToSdcard("Remiscar ", " GET - error" + ee.toString());
                        }

                    }
                });
    }

    private void processLocation(String data) {
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
                    SharedPreferences settings = getSharedPreferences("RemisData", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("latlonOrigen", "");
                    editor.commit();
                    Log.d("REMISCAR - ", " ADDRESS- fallo en geopos");
                    logToSdcard("REMISCAR - ", " ADDRESS- fallo en geopos");
                    frmStatusOrigen.setBackgroundColor(Color.parseColor("#fff4b2"));
                } else {
                    // si geopos detecta la direccion enviamos las coordenadas encontradas
                    Double location_lat = location.getDouble("lat");
                    Double location_lon = location.getDouble("lng");
                    ObtCoordenadas = String.valueOf(location_lat) + "," + String.valueOf(location_lon);
                    SharedPreferences settings = getSharedPreferences("RemisData", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("latlonOrigen", ObtCoordenadas);
                    editor.putString("geopos", ObtCoordenadas);
                    // Commit the edits!
                    editor.commit();
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

    /**
     * Background Async Task validar usuario
     */

    private void asValidarUsuario() {
        Ion.with(MainActivity.this)
                .load(url_validacion)
                .setBodyParameter("IMEI", imei)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Log.d("Remiscar* ", "imei " + imei);
                            logToSdcard("Remiscar ", "imei " + imei);
                            Log.d("Remiscar* ", "antes de envio validacion");
                            logToSdcard("Remiscar ", "antes de envio validacion");

                            processValidacion(result);


                        } catch (Exception ee) {
                            Log.d("Remiscar* ", " validar - error" + ee);
                            logToSdcard("Remiscar ", " validar - error");
                        }

                    }
                });
    }

    private void processValidacion(JsonObject data) {
        SharedPreferences settings = getSharedPreferences("RemisData", 0);
        SharedPreferences.Editor editor = settings.edit();
        if (data != null) {
            // check log cat from response
            Log.d("Remiscar* - ", "Login Response " + data.toString());
            logToSdcard("Remiscar - ", "Login Response " + data.toString());
            // check for success tag

            int success = data.get(TAG_SUCCESS).getAsInt();
            if (success == 1 || success == 2) {
                if (data.has("movil") && !data.get("movil").isJsonNull())
                    movil = data.get("movil").getAsString();
            }

            if (success == 2) {
                //Mactual1 - propietario
                editor.putString("movil", movil);
                editor.putString("imei", imei);

                // Commit the edits!
                editor.commit();

                Intent intent = new Intent(MainActivity.this, PropActivity.class);
                startActivity(intent);
                finish();

            } else if (success == 1) {
                //inicio valido para movil autorizado
                textNroMovil.setText(movil);


                editor.putString("movil", movil);
                editor.putString("imei", imei);

                // Commit the edits!
                editor.commit();
                ServiceUtils.asMActual(MainActivity.this, status, movil, imei,
                        Direccion, geopos);
                if (flg_run == 0) {
                    startRepeatingTask();
                }

            } else if (success == 0) {
                Toast.makeText(getApplicationContext(), "App solo para propietarios autorizados.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo conectar. Intente de nuevo.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

    @Subscribe
    private void processMActual(MActualEvent event) {

        try {
            JsonObject data = event.getObject();
            Log.d("Remiscar MAc Res- ", data.toString());
            logToSdcard("Remiscar MAc Res- ", data.toString());
            int success = data.get(TAG_SUCCESS).getAsInt();
            carlitos = data.get("carlitos").getAsString();
            carlibres = data.get("carlibres").getAsString();
            bahia = data.get("bahia").getAsString();
            bahialibres = data.get("bahialibres").getAsString();
            status = data.get("status").getAsString();
            if (data.has("Origen")) {
                Origen = data.get("Origen").getAsString();
            }
            if (data.has("ObtDestino")) {
                ObtDestino = data.get("ObtDestino").getAsString();
            }
            if (data.has("Pasajero")) {
                Pasajero = data.get("Pasajero").getAsString();
            }
            if (data.has("ObtAgencia")) {
                ObtAgencia = data.get("ObtAgencia").getAsString();
            }
            if (data.has("Observaciones")) {
                Observaciones = data.get("Observaciones").getAsString();
            }
            if (data.has("HCubierto")) {
                HCubierto = data.get("HCubierto").getAsString();
            }
            if (data.has("Traslados")) {
                Traslados = data.get("Traslados").getAsString();
            }
            if (data.has("ObtCoordenadas")) {
                ObtCoordenadas = data.get("ObtCoordenadas").getAsString();
            }

            Log.d("REMISCAR - ", String.valueOf(success));
            logToSdcard("REMISCAR - ", String.valueOf(success));

            if (success == 0) {

                String resultLoc = "";
                textCarlitos.setText(carlitos);
                textLibres.setText(carlibres);
                textBahia.setText(bahia);
                textLibresB.setText(bahialibres);
                textStatus.setText(status);
                frmStatusOrigen.setBackgroundColor(Color.parseColor("#fff4b2"));

                SharedPreferences settings = getSharedPreferences("RemisData", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("latlonOrigen", resultLoc);
                editor.putString("movil", movil);
                editor.putString("status", status);
                editor.putString("imei", imei);
                editor.commit();

                if (status == "LIBRE") {
                    buttonInicio.setBackgroundColor(Color.parseColor("#1FA9FF"));
                    buttonCobrar.setBackgroundColor(Color.parseColor("#1FA9FF"));
                    textStatus.setTextColor(Color.GREEN);//color de texto status

                    textOrigen.setText("");
                    textDestino.setText("");
                    textPasajero.setText("");
                    textEmpresa.setText("");
                    textObs.setText("");
                    textHoraViaje.setText("");
                } else {

                    textStatus.setTextColor(Color.RED);//color de texto status

                    textOrigen.setText(Origen);
                    textDestino.setText(ObtDestino);
                    textPasajero.setText(Pasajero);
                    textEmpresa.setText(ObtAgencia);
                    textObs.setText(Observaciones);
                    textHoraViaje.setText(HCubierto);
                }

                //si tengo coordenadas las envio, sino busco por el dato de origen.
                if (ObtCoordenadas.equals("")) {
                    ///////////////TEST///////////////////
                    //Origen="1200 alem";
                    ///////////////TEST///////////////////
                    Log.d("REMISCAR - ", "origen -- " + Origen);
                    logToSdcard("REMISCAR - ", "origen -- " + Origen);

                    if (Origen.equals("")) {

                        resultLoc = "";
                        editor.putString("latlonOrigen", resultLoc);
                        editor.commit();

                    } else {
                        if (flg_origen == 0) getAddressLocation();
                        // busco si el numero de la direccion viene primero
                        /*if(Origen.substring(0, 1).matches("[0-9]")){
                            Log.d("REMISCAR - ","origen --NUMERO ");
                            getAddressLocation();
                        }else{
                            Origen="";
                        }*/


                    }
                } else {
                    resultLoc = ObtCoordenadas;
                    editor.putString("latlonOrigen", resultLoc);
                    editor.commit();
                    frmStatusOrigen.setBackgroundColor(Color.parseColor("#00ff00"));
                    //-34.935506,-57.9556878
                    //editor.putString("latlonOrigen","-34.935506,-57.9");

                }

            } else if (success == 2) {
                Toast.makeText(getApplicationContext(), "App solo para propietarios autorizados.", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Background Async Task Inicio y fin viaje
     */
    private void asInicioFin(String task) {
        String orig = "";
        if (task.equals("inicio")) {
            orig = Origen;
        }

        try {
            String mOrigen = URLEncoder.encode(orig, "utf-8");
            String mTraslados = URLEncoder.encode(Traslados, "utf-8");
            String mZonaDestino = URLEncoder.encode(ZonaDestino, "utf-8");

            String url_par = url_iniciofin + "?status=" + task + "&Movil=" + movil + "&IMEI=" + imei +
                    "&Ubicacion=" + Direccion + "&geopos=" + geopos + "&origen=" + mOrigen + "&Traslados=" + mTraslados + "&ZonaFin=" + mZonaDestino;
            Ion.with(MainActivity.this)
                    .load(url_par)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.d("Remiscar", "antes de envio InicioFin");
                            logToSdcard("Remiscar", "antes de envio InicioFin");
                            Log.d("Remiscar", "IF-data:" + imei + "-" + movil + "-" + Direccion + "-" + status + geopos);
                            logToSdcard("Remiscar", "IF-data:" + imei + "-" + movil + "-" + Direccion + "-" + status + geopos);


                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /*private void processInicioFin(JsonObject result){
        try {
            if (result != null) {
                Log.d("Remiscar-IF-Response ", result.toString());
                logToSdcard("Remiscar-IF-Response ", result.toString());
            }
        }catch(Exception e){
            Log.e("Remiscar ",e.toString());
        }



    };*/


    /**
     * Background Async Task verificar status de Panico
     */
    private void asAlert() {
        String url_params = url_alerta + "?IMEI=" + imei +
                "&status=&Movil=" + movil + "&Ubicacion=" + Direccion + "&GeoPos=" + geopos + "&movil_al=";
        Ion.with(MainActivity.this)
                .load(url_params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        processAlert(result);

                    }
                });
    }

    private void processAlert(JsonObject result) {
        // check for success tag
        try {
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
                    asPanic("SEGUIMIENTO", url_panico);
                } else {
                    SharedPreferences settings = getSharedPreferences("RemisData", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("al_status", al_status);
                    editor.putString("al_fecha", al_fecha);
                    editor.putString("al_geopos", al_geopos);
                    editor.putString("al_movil", al_movil);
                    editor.putString("al_ubicacion", al_ubicacion);
                    editor.commit();

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

    ;


    /**
     * Background Async Task verificar mensajes al user
     */
    private void asMensaje() {

        Ion.with(MainActivity.this)
                .load(url_mensaje)
                .setBodyParameter("Movil", movil)
                .setBodyParameter("IMEI", imei)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        processMensaje(result);

                    }
                });
    }

    private void asAuto() {

        Ion.with(MainActivity.this)
                .load(url_auto)
                .setBodyParameter("Movil", movil)
                .setBodyParameter("IMEI", imei)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        processAuto(result);

                    }
                });
    }

    private void processMensaje(JsonObject result) {
        int success;
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
                buttonNov.setBackgroundColor(Color.parseColor("#000000"));
                buttonNov.setTextColor(Color.parseColor("#FDFC80"));
            } else if (success == 1) {
                Log.d("Remiscar -", "HAY mensajes.");
                logToSdcard("Remiscar -", "HAY mensajes.");
                if (flg_mens == 0) {
                    Toast.makeText(getApplicationContext(), "Hay nuevos mensajes para usted.", Toast.LENGTH_SHORT).show();
                    final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.c2answer);
                    mp.start();
                    buttonNov.setText("HAY MENSAJES");
                    buttonNov.setBackgroundColor(Color.parseColor("#FDFC80"));
                    buttonNov.setTextColor(Color.parseColor("#000000"));
                    flg_mens = 1;
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ;

    private void processAuto(JsonObject result) {
        int success;
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
                buttonViajes.setText("VIAJES");
                buttonViajes.setBackgroundColor(Color.parseColor("#000000"));
                buttonViajes.setTextColor(Color.parseColor("#FDFC80"));
            } else if (success == 1) {
                Log.d("Remiscar -", "AUTODESPACHO.");
                logToSdcard("Remiscar -", "AUTODESPACHO.");
                if (flg_mens == 0) {
                    Toast.makeText(getApplicationContext(), "AUTODESPACHO.", Toast.LENGTH_SHORT).show();
                    final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.c2answer);
                    mp.start();
                    buttonViajes.setText("AUTODESPACHO");
                    buttonViajes.setBackgroundColor(Color.parseColor("#FDFC80"));
                    buttonViajes.setTextColor(Color.parseColor("#000000"));
                    flg_mens = 1;
                } else {

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ;

    /**
     * Background Async Task mensaje de Panico
     * envia actualizacion de ubicacion de alerta si el movil envio la alerta
     * new PanicTask().execute("ALERTA", url_panico);
     */

    private void asPanic(String statusIn, String url) {
        String status = statusIn;
        String url_params = url + "?status=" + status + "&Movil=" + movil + "&IMEI=" + imei +
                "&Ubicacion=" + Direccion + "&geopos=" + geopos;
        Ion.with(MainActivity.this)
                .load(url_params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        processPanic(result);

                    }
                });
    }

    private void processPanic(JsonObject result) {
        int success;
        Log.d("Remiscar", "envio Panic");
        Log.d("Remiscar", "data:" + imei + "-" + movil + "-" + Direccion + "-" + geopos);


        // check for success tag
        try {
            Log.d("Remiscar panic Res-", result.toString());
            //success = result.get(TAG_SUCCESS).getAsInt();

            //if (success == 0) {
            if (status == "PRUEBA") {
                Log.d("Remiscar panic -", "PRUEBA");


            } else {
                Log.d("Remiscar panic -", " seguimiento ALERTA");
                //Intent intent = new Intent(PanicActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
            }

            // }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ;

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}