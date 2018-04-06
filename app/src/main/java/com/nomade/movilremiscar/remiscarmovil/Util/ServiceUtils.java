package com.nomade.movilremiscar.remiscarmovil.Util;

import android.content.Context;
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
import com.nomade.movilremiscar.remiscarmovil.events.UbicacionEvent;
import com.nomade.movilremiscar.remiscarmovil.events.ValidacionEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Gino on 17/3/2018.
 */

public class ServiceUtils {

    private static String url_validacion = "http://carlitosbahia.dynns.com/legajos/viajes/Mvalidacion.php";
    private static String url_iniciofin = "http://carlitosbahia.dynns.com/legajos/viajes/Miniciofin.php";
    private static String url_alerta = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanicoalerta.php";
    private static String url_mensaje = "http://carlitosbahia.dynns.com/legajos/viajes/Mmensajes.php";
    private static String url_auto = "http://carlitosbahia.dynns.com/legajos/viajes/Mauto.php";
    private static String url_panico = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanico.php";
    public static String url_main = "http://carlitosbahia.dynns.com/legajos/viajes/Mviajeshoy.php";
    private static String url_ubicacionViaje = "http://carlitosbahia.dynns.com/legajos/viajes/Mcoordenadas.php";

    public static void asMensaje(Context context) {

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
                            Log.d("Remiscar* ", "error en respuesta de mensajes.");
                        }
                    }
                });
    }

    public static void asCoordenadas(Context context) {

        Ion.with(context)
                .load(url_ubicacionViaje)
                .setBodyParameter("Movil", SharedPrefsUtil.getInstance(context).getString("movil", ""))
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            CoordenadasViajeEvent event = new CoordenadasViajeEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar* ", "error en respuesta de Mcoordenadas.");
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
                            Log.d("Remiscar* ", "error en asAuto.");
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
                            Log.d("Remiscar* ", "error en asPanic.");
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
                                Log.d("Remiscar* ", "error en asInicioFin.");
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
                         String direccion,
                         String geopos) {
        String url_params = url_alerta + "?IMEI=" + SharedPrefsUtil.getInstance(context).getString("imei", "") +
                "&status=&Movil=" + SharedPrefsUtil.getInstance(context).getString("movil", "") + "&Ubicacion=" + direccion + "&GeoPos=" + geopos + "&movil_al=";
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
                            Log.d("Remiscar* ", "error en asAlert.");
                        }
                    }
                });
    }

    public static void asValidarUsuario(Context context) {
        Ion.with(context)
                .load(url_validacion)
                .setBodyParameter("IMEI", SharedPrefsUtil.getInstance(context).getString("imei", ""))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            if (result != null) {
                                ValidacionEvent event = new ValidacionEvent();
                                event.setObject(result);
                                EventBus.getDefault().post(event);
                            } else {
                                Log.d("Remiscar* ", "error en asValidarUsuario.");
                            }
                        } catch (Exception ee) {
                            Log.d("Remiscar* ", " asValidarUsuario - error" + ee);
                        }

                    }
                });
    }

    public static void asLocation(Context context, String url_location) {
        Ion.with(context)
                .load(url_location)
                .setBodyParameter("key", "AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw")//AIzaSyD4m6agvDZRVJahBFnBe5wWGi3cM7Hlmxw
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (result != null) {
                            LocationEvent event = new LocationEvent();
                            event.setObject(result);
                            EventBus.getDefault().post(event);
                        } else {
                            Log.d("Remiscar* ", "error en asLocation.");
                        }

                    }
                });
    }

    public static void asUbicacion(Context context, String url_ubicacion) {
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
                            Log.d("Remiscar* ", "error en asLocation.");
                        }

                    }
                });
    }
}
