package com.nomade.movilremiscar.remiscarmovil.Util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nomade.movilremiscar.remiscarmovil.events.MinutePollingEvent;
import com.nomade.movilremiscar.remiscarmovil.events.PollingEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by Gino on 26/3/2018.
 */

public class PollingManager {

    Context mContext;

    int flg_run = 0; // flag repeating task running
    //seteo de intervalo de actualizacion de datos
    private final static int INTERVAL = 10000; // 10segundos
    Handler mHandler;
    SharedPrefsUtil prefs;
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

                EventBus.getDefault().post(new MinutePollingEvent());
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

            // PERIODO 20 SEGUNDOS
            if (Utils.esMultiplo(counter, 2)) {
                EventBus.getDefault().post(new PollingEvent());
            }

            //RESET COUNTER
            if(counter > 360) {
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
