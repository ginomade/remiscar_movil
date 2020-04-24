package com.nomade.movilremiscar.remiscarmovil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nomade.movilremiscar.remiscarmovil.Util.ServiceUtils;
import com.nomade.movilremiscar.remiscarmovil.events.SimulacionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ErrorMessageActivityActivity extends AppCompatActivity {

    Button vOkBtn;

    private boolean mPosicionSimuladaEnviada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_message_activity);

        ServiceUtils.sendFakeLocationMessageService(ErrorMessageActivityActivity.this);

        vOkBtn = findViewById(R.id.ok_btn);
        vOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosicionSimuladaEnviada) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Subscribe()
    public void processSimulacionPosicion(SimulacionEvent event) {
        mPosicionSimuladaEnviada = true;
    }
}