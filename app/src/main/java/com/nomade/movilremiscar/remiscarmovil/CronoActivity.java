package com.nomade.movilremiscar.remiscarmovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Pablo on 04/09/2015.
 */
public class CronoActivity extends Activity {

    Button buttonInicio;
    Button Iniciar, Detener;
    Chronometer Crono;
    long Time=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crono);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        buttonInicio = (Button) findViewById(R.id.buttonInicio);
        buttonInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        Crono = (Chronometer) findViewById(R.id.chronometer);
        Iniciar= (Button) findViewById(R.id.buttonContar);
        Detener=(Button) findViewById(R.id.buttonPausa);
        Iniciar.setEnabled(true);
        Detener.setEnabled(false);

        Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Iniciar.setEnabled(false);
                buttonInicio.setEnabled(false);
                Detener.setEnabled(true);
                Crono.setBase(SystemClock.elapsedRealtime());
                Detener.setTextColor(Color.parseColor("#FA67FF01"));
                buttonInicio.setTextColor(Color.parseColor("#ffff1b00"));
                Crono.start();
            }
        });
        Detener.setOnClickListener(new View.OnClickListener() {
        @Override

        public void onClick(View v) {
            Iniciar.setEnabled(true);
            buttonInicio.setEnabled(true);
            Detener.setEnabled(false);
            Detener.setTextColor(Color.parseColor("#d5d9ea"));
            buttonInicio.setTextColor(Color.parseColor("#FA67FF01"));
            Crono.stop();
        }
    });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();

        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();

        finish();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

}
