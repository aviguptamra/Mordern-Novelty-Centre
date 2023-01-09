package com.esri.natmoapp.server;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {

    //Server URL
    private static final String BASE_URL="http://3.111.2.67:8080/";

    //Local URL
    //private static final String BASE_URL="https://be37-49-207-202-172.in.ngrok.io";

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



