package com.esri.natmoapp.server;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {

    //Server URL
    private static final String BASE_URL="https://7feb-2406-7400-63-c2d7-6db5-d5cd-9a2a-4c43.in.ngrok.io/";

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



