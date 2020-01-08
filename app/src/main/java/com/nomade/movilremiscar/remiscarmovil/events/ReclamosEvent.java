package com.nomade.movilremiscar.remiscarmovil.events;

public class ReclamosEvent extends MensajeEvent {
    private String dataString;

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }
}
