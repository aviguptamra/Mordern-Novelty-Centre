package com.esri.natmoapp.server;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {

    //Server URL
    private static final String BASE_URL="https://6bdf-2406-7400-63-8e0-2db5-72c3-401-bfce.in.ngrok.io/";

    private static Retrofit retrofit = null;
    TaskService taskService;

    public ServiceGenerator() {
        retrofit = new Retrofit.Builder()
                /*.client(client)*/
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        taskService = retrofit.create(TaskService.class);
    }
    public TaskService getService(){
        return taskService;
    }
}



