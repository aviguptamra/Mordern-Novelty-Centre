package com.esri.natmoapp.server;

import com.esri.natmoapp.model.ActivateUser;
import com.esri.natmoapp.model.AddProductSeries;
import com.esri.natmoapp.model.ForgotPassword;
import com.esri.natmoapp.model.LoginResponse;
import com.esri.natmoapp.model.ProductDetails;
import com.esri.natmoapp.model.RedeemHistory;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.model.ResetPassword;
import com.esri.natmoapp.model.UserDetail;
import com.esri.natmoapp.model.UserHistory;
import com.esri.natmoapp.model.UserHistoryModel;
import com.esri.natmoapp.model.VerifyMail;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskService {

    @POST("user/register")
    Call<Registration> RegisterUser(@Body Registration registration);

    @POST("user/login")
    Call<LoginResponse> Login(@Body UserDetail userDetail);

    @GET("user/{userId}")
    Call<Registration> GetUserDetails_ById(@Path("userId") String userId,
                                           @Header("Authorization") String auth);

    @GET("user/users")
    Call<List<Registration>> GetAllUsers(@Header("Authorization") String auth);

    @POST("product/productSeries")
    Call<AddProductSeries> AddProductSeries(@Body AddProductSeries addProductSeries,@Header("Authorization") String auth);

    @GET("product/productSeries")
    Call<List<AddProductSeries>> GetAllProductSeries(@Header("Authorization") String auth);

    @DELETE("product/productSeries/{productSeriesId}")
    Call<Void> DeleteProductSeries(@Path("productSeriesId") String productSeriesId,
                                           @Header("Authorization") String auth);

    @POST("product/scan")
    Call<Void> ScanProduct(@Body ProductDetails productDetails, @Header("Authorization") String auth);

    @GET("user/history/{userId}")
    Call<UserHistory> GetUserHistory(@Path("userId") String userId,
                                                @Header("Authorization") String auth);

    @POST("/redeem/points")
    Call<Void> RedeemPoints(@Body RedeemHistory redeemHistory, @Header("Authorization") String auth);

    @POST("/user/forgotPassword")
    Call<Void> ForgotPassword(@Body ForgotPassword forgotPassword);

    @POST("/user/resetPassword")
    Call<Void> ResetPassword(@Body ResetPassword resetPassword);

    @POST("/user/verifyMail")
    Call<Void> VerifyMail(@Body VerifyMail verifyMail);

    @POST("/user/activateUser")
    Call<Void> Activate_DeActivateUser(@Body ActivateUser activateUser, @Header("Authorization") String auth);

}

