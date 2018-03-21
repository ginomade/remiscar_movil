package com.nomade.movilremiscar.remiscarmovil.events;

import com.google.gson.JsonObject;

/**
 * Created by Gino on 20/3/2018.
 */

public class MActualEvent {
    private JsonObject object;

    public JsonObject getObject() {
        return object;
    }

    public void setObject(JsonObject object) {
        this.object = object;
    }
}
