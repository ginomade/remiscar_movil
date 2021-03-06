package com.nomade.movilremiscar.remiscarmovil.Util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nomade.movilremiscar.remiscarmovil.events.PollingEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by Gino on 26/3/2018.
 */

public class PollingManager {

    Context mContext;

    private int clearCacheCounter = 0;
    int flg_run = 0; // flag repeating task running
    //seteo de intervalo de actualizacion de datos
    private final static int INTERVAL = 10 * 1000; // segundos /** 20 **/
    Handler mHandler;
    SharedPrefsUtil prefs;
    int mProcessDelay = 0;
    int counter = 0;

    public PollingManager(Context context) {
        mContext = context;
        mHandler = new Handler();
        prefs = SharedPrefsUtil.getInstance(mContext);
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            counter++;

            // PERIODO 1 MINUTO
            if (Utils.esMultiplo(counter, 6)) {
                ServiceUtils.asAuto(mContext);
                ServiceUtils.asMensaje(mContext);
                ServiceUtils.asAlert(mContext, prefs.getString("Direccion", ""));
                ServiceUtils.asCoordenadas(mContext);
            }

            // PERIODO 1 HORA
            if (Utils.esMultiplo(counter, 360)) {
                deleteCache(mContext);
                Log.w("Remiscar*", "***** CLEAR CACHE *****");
            }

            // PERIODO 5 MINUTOS
            if (Utils.esMultiplo(counter, 30)) {
                ServiceUtils.asMensaje(mContext);
            }

            // PERIODO 3 MINUTOS
            if (Utils.esMultiplo(counter, 18)) {

//este es el metodo que carga el webview cada 3 minutos, si se desea hacerlo cada 5 por ejemplo,
// se puede copiarel contenido del if adentro del if anterior PERIODO 5 MINUTOS

                EventBus.getDefault().post(new MinutePollingEvent());


            }

            // PERIODO 20 SEGUNDOS
            if (Utils.esMultiplo(counter, 2)) {
                EventBus.getDefault().post(new PollingEvent());
                ServiceUtils.asAuto(mContext);
                ServiceUtils.checkPuntero(mContext);
            }

            //RESET COUNTER
            if (counter > 360) {
                counter = 0;
            }

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };


    public void startRepeatingTask() {
        if (flg_run == 0) {
            mHandlerTask.run();
            flg_run = 1;
        }

    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
        flg_run = 0;
    }

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
