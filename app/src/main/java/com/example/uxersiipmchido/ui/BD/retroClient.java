package com.example.uxersiipmchido.ui.BD;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class retroClient {
    private static Retrofit retrofit;
    private static final String bdUrl = "https:/k91n550s-8000.usw3.devtunnels.ms/uxersiiPruebas/";

    public static Retrofit getRetrofitInstance (){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(bdUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
