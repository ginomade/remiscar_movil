package com.nomade.movilremiscar.remiscarmovil.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nomade.movilremiscar.remiscarmovil.events.AlertEvent;
import com.nomade.movilremiscar.remiscarmovil.events.AutoEvent;
import com.nomade.movilremiscar.remiscarmovil.events.CoordenadasViajeEvent;
import com.nomade.movilremiscar.remiscarmovil.events.InicioFinEvent;
import com.nomade.movilremiscar.remiscarmovil.events.LocationEvent;
import com.nomade.movilremiscar.remiscarmovil.events.MensajeEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PanicEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PunteroEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PunteroAlternativaEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PunteroLibreEvent;
import com.nomade.movilremiscar.remiscarmovil.events.ReclamosEvent;
import com.nomade.movilremiscar.remiscarmovil.events.SimulacionEvent;
import com.nomade.movilremiscar.remiscarmovil.events.UbicacionEvent;
import com.nomade.movilremiscar.remiscarmovil.events.ValidacionEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Gino on 17/3/2018.
 */

public class ServiceUtils {

    public static String base_url = "https://carlitosbahia.com.ar/legajos/viajes/2020/";
    private static String url_validacion = base_url + "Mvalidacion.php";
    private static String url_iniciofin = base_url + "Miniciofin.php";
    private static String url_alerta = base_url + "Mpanicoalerta.php";
    private static String url_mensaje = base_url + "Mmensajes.php";
    private static String url_auto = base_url + "Mauto.php";
    private static String url_puntero = base_url + "Mmovilpuntero.php";
    private static String url_puntero_alternativa = base_url + "Mmovilpunteroalternativa.php";
    private static String url_puntero_libre = base_url + "Mmovilpunterolibre.php";
    private static String url_puntero_viaje = base_url + "Mmovilpunteroviaje.php";
    private static String url_panico = base_url + "Mpanico.php";
    public static String url_main = base_url + "Mviajeshoy.php";
    private static String url_ubicacionViaje = base_url + "Mcoordenadas.php";
    public static String url_privacy = base_url + "MPprivacidad.php";
    private static String url_reclamosMovil = base_url + "Mreclamos.php";
    private static String url_simulacion_posicion = base_url + "simulacion.php";

    public static void asMensaje(Context context) {
        Log.d("remiscar: ", "asMensaje - " + url_mensaje);
        Ion.with(context)
                .load(url_mensaje)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            MensajeEvent event = new MensajeEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en respuesta de mensajes.");
                        }
                    }
                });
    }

    public static void asCoordenadas(Context context) {
        String finalUrl = url_ubicacionViaje
                + "?Movil=" + SharedPrefsUtil.getInstance(context).getString("movil", "")
                + "&IMEI=" + SharedPrefsUtil.getInstance(context).getString("imei", "");
        Log.d("remiscar: ", "asCoordenadas - " + url_mensaje);
        Ion.with(context)
                .load(finalUrl)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            CoordenadasViajeEvent event = new CoordenadasViajeEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en respuesta de Mcoordenadas.");
                        }
                    }
                });
    }

    public static void asAuto(Context context) {

        Ion.with(context)
                .load(url_auto)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            AutoEvent event = new AutoEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en asAuto.");
                        }
                    }
                });
    }

    public static void checkPuntero(Context context) {
        Log.d("remiscar: ", "checkPuntero - " + url_puntero);
        Ion.with(context)
                .load(url_puntero)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            PunteroEvent event = new PunteroEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en checkPuntero.");
                        }
                    }
                });
    }

    public static void checkPunteroAlternativa(Context context) {

        Ion.with(context)
                .load(url_puntero_alternativa)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            PunteroAlternativaEvent event = new PunteroAlternativaEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en checkPuntero.");
                        }
                    }
                });
    }

    public static void checkPunteroLibre(Context context) {

        Ion.with(context)
                .load(url_puntero_libre)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            PunteroLibreEvent event = new PunteroLibreEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en checkPuntero.");
                        }
                    }
                });
    }

    public static void checkPunteroViaje(Context context) {

        Ion.with(context)
                .load(url_puntero_viaje)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            PunteroEvent event = new PunteroEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en checkPuntero.");
                        }
                    }
                });
    }
    /**
     * Background Async Task mensaje de Panico
     * envia actualizacion de ubicacion de alerta si el movil envio la alerta
     * new PanicTask().execute("ALERTA", url_panico);
     */
    public static void asPanic(Context context,
                               String statusIn,
                               String direccion,
                               String geopos) {
        String status = statusIn;
        String url_params = url_panico + "?status=" + status +
                "&Movil=" + SharedPrefsUtil.getInstance(context).getString("movil", "") +
                "&IMEI=" + SharedPrefsUtil.getInstance(context).getString("imei", "") +
                "&Ubicacion=" + direccion + "&geopos=" + geopos;
        Log.d("remiscar: ", "asPanic - " + url_params);
        Ion.with(context)
                .load(url_params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            PanicEvent event = new PanicEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en asPanic.");
                        }
                    }
                });
    }

    /**
     * Background Async Task Inicio y fin viaje
     */
    public static void asInicioFin(String task,
                                   Context context,
                                   String origen,
                                   String traslados,
                                   String zonaDestino,
                                   String direccion,
                                   String geopos) {
        String orig = "";
        if (task.equals("inicio")) {
            orig = origen;
        }

        try {
            String mOrigen = URLEncoder.encode(orig, "utf-8");
            String mTraslados = URLEncoder.encode(traslados, "utf-8");
            String mZonaDestino = URLEncoder.encode(zonaDestino, "utf-8");

            String url_par = url_iniciofin + "?status=" + task +
                    "&Movil=" + SharedPrefsUtil.getInstance(context).getString("movil", "") +
                    "&IMEI=" + SharedPrefsUtil.getInstance(context).getString("imei", "") +
                    "&Ubicacion=" + direccion + "&geopos=" + geopos + "&origen=" +
                    mOrigen + "&Traslados=" + mTraslados + "&ZonaFin=" + mZonaDestino;
            Ion.with(context)
                    .load(url_par)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                InicioFinEvent event = new InicioFinEvent();
                                event.setObject(result);
                                EventBus.getDefault().post(event);
                            } else {
                                Log.d("Remiscar: ", "error en asInicioFin.");
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async Task verificar status de Panico
     */
    public static void asAlert(Context context,
                               String direccion) {
        String geoposLocal = SharedPrefsUtil.getInstance(context).getString("geopos", "");
        String url_params = url_alerta + "?IMEI="
                + SharedPrefsUtil.getInstance(context).getString("imei", "")
                + "&status=&Movil=" + SharedPrefsUtil.getInstance(context).getString("movil", "")
                + "&Ubicacion=" + direccion
                + "&movil_al="
                + "&GeoPos=" + geoposLocal;

                Log.d("Remiscar: ", "asAlert. LOC - " + url_params);
        Ion.with(context)
                .load(url_params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            AlertEvent event = new AlertEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en asAlert.");
                        }
                    }
                });
    }

    public static String getVersionName(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            Log.d("Remiscar: ", "Version Name : " + version + "\n Version Code : " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("Remiscar: ", "PackageManager Catch : "+e.toString());
        }
        return version;
    }
    public static void asValidarUsuario(Context context) {
        Log.d("Remiscar: ", "asValidarUsuario - " + url_validacion);
        Ion.with(context)
                .load(url_validacion)
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .setBodyParameter("version", getVersionName(context))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            Log.d("Remiscar: ", "error en asValidarUsuario." + result.toString());
                            if (result != null) {
                                ValidacionEvent event = new ValidacionEvent();
                                event.setObject(result);
                                EventBus.getDefault().post(event);
                            } else {
                                Log.d("Remiscar: ", "error en asValidarUsuario.");
                            }
                        } catch (Exception ee) {
                            Log.d("Remiscar: ", " asValidarUsuario - error" + ee);
                        }

                    }
                });

    }

    public static void getReclamos(Context context, String imei, String celu, String mensaje, String nombreUsuario) {
        String finalUrl = url_reclamosMovil + "?IMEI=" + imei + "&Celular=" + celu
                + "&Descripcion=" + mensaje + "&Pasajero=" + nombreUsuario;
        Log.d("remiscar: ", "getReclamos - " + finalUrl);
        Ion.with(context)
                .load(url_reclamosMovil)
                .setBodyParameter("IMEI", imei)
                .setBodyParameter("Celular", celu)
                .setBodyParameter("Descripcion", mensaje)
                .setBodyParameter("Pasajero", nombreUsuario)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.d("Remiscar: ", "Reclamos  - " + result);
                        try {

                            ReclamosEvent event = new ReclamosEvent();
                            event.setDataString(result);
                            EventBus.getDefault().post(event);


                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    }
                });
    }

    public static void asLocation(Context context, String url_location) {
        Log.d("Remiscar: ", "asLocation - " + url_location);
        Ion.with(context)
                .load(url_location)
                .setBodyParameter("key", "AIzaSyCKBR3GAk_m3_Ub3VCDx8MTVucs2acq0-4")//AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (result != null) {
                            LocationEvent event = new LocationEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en asLocation.");
                        }

                    }
                });
    }

    public static void asUbicacion(Context context, String url_ubicacion) {
        Log.d("Remiscar: ", "asUbicacion - " + url_ubicacion);
        Ion.with(context)
                .load(url_ubicacion)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            UbicacionEvent event = new UbicacionEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar: ", "error en asLocation.");
                        }

                    }
                });
    }

    public static void sendFakeLocationMessageService(Context context) {
        String url_params = url_simulacion_posicion +
                "?Movil=" + SharedPrefsUtil.getInstance(context).getString("movil", "") +
                "&IMEI=" + SharedPrefsUtil.getInstance(context).getString("imei", "") +
                "&Programa=" + SharedPrefsUtil.getInstance(context).getString("app_simulacion", "");
        Log.d("remiscar: ", "posicion simulada - " + url_params);
        Ion.with(context)
                .load(url_params)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            SimulacionEvent event = new SimulacionEvent();
                            EventBus.getDefault().post(event);
                        } catch (Exception ex) {
                            Log.d("Remiscar: ", "error en sendFakeLocationMessageService.");
                        }
                    }
                });
    }
}