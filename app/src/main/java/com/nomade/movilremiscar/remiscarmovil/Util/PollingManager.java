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
    private final static int INTERVAL = 20 * 1000; // segundos
    Handler mHandler;
    SharedPrefsUtil prefs;

    public PollingManager(Context context) {
        mContext = context;
        mHandler = new Handler();
        prefs = SharedPrefsUtil.getInstance(mContext);
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            ServiceUtils.asMActual(mContext, prefs.getString("status", ""),
                    prefs.getString("Direccion", ""), prefs.getString("geopos", ""));
            ServiceUtils.asAuto(mContext);
            if(Utils.esMultiplo(clearCacheCounter, 5)){
                ServiceUtils.asMensaje(mContext);
                ServiceUtils.asAlert(mContext, prefs.getString("Direccion", ""), prefs.getString("geopos", ""));
            }

            clearCacheCounter++; // 180 == 1 hora
            if (clearCacheCounter >= (180 * 1)) {
                deleteCache(mContext);
                clearCacheCounter = 0;
                Log.w("Remiscar*", "***** CLEAR CACHE *****");
            }

            EventBus.getDefault().post(new PollingEvent());

            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    public void startRepeatingTask() {
        if (flg_run == 0){
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