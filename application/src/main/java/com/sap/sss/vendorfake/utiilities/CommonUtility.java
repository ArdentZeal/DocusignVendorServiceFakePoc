package com.sap.sss.vendorfake.utiilities;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonUtility {
    public static Gson getConfiguredGsonInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();

        return Converters.registerInstant(gsonBuilder).create();
    }
}