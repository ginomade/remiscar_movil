package com.nomade.movilremiscar.remiscarmovil.Util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nomade.movilremiscar.remiscarmovil.MainActivity;
import com.nomade.movilremiscar.remiscarmovil.events.MActualEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Gino on 17/3/2018.
 */

public class ServiceUtils {

    private static String url_validacion = "http://carlitosbahia.dynns.com/legajos/viajes/Mvalidacion.php";
    private static String url_actual = "http://carlitosbahia.dynns.com/legajos/viajes/Mactual3.php";
    private static String url_iniciofin = "http://carlitosbahia.dynns.com/legajos/viajes/Miniciofin.php";
    private static String url_alerta = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanicoalerta.php";
    private static String url_mensaje = "http://carlitosbahia.dynns.com/legajos/viajes/Mmensajes.php";
    private static String url_auto = "http://carlitosbahia.dynns.com/legajos/viajes/Mauto.php";
    private static String url_panico = "http://carlitosbahia.dynns.com/legajos/viajes/Mpanico.php";

    public static void asMActual(Context context,
                                 String status,
                                 String movil,
                                 String imei,
                                 String direccion,
                                 String geopos) {

        String url_params = url_actual + "?status=" + status + "&Movil=" + movil + "&IMEI=" + imei + "&Ubicacion="
                + direccion + "&geopos=" + geopos;
        Log.d("Remiscar MACT DATA- ", url_params);
        try {
            Ion.with(context)
                    .load(url_params)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                            //processMActual(result);
                            if (result != null) {
                                Log.d("Remiscar MAc Res- ", result.toString());
                                MActualEvent event = new MActualEvent();
                                event.setObject(result);
                                EventBus.getDefault().post(result);
                            } else {
                                Log.d("Remiscar* ", "Remiscar MAc Res- NULL**********");
                            }
                        }

                    });
        } catch (Exception ex) {
            Log.e("Remiscar* ", "** error en MActual **");
        }
    }
}
