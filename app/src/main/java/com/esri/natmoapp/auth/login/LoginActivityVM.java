package com.esri.natmoapp.auth.login;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.util.Log;
import android.widget.Toast;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.forgotpassword.ForgotPasswordActivity;
import com.esri.natmoapp.auth.register.RegisterActivity;
import com.esri.natmoapp.db.DatabaseController;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.LoginResponse;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.model.UserDetail;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.esri.natmoapp.util.AESCrypt;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.Constants;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivityVM extends ActivityViewModel<LoginActivity> {

    LoginInteractor interactor;
    CommonFunctions commonFunctions=new CommonFunctions();
    private static final String TAG = LoginActivity.class.getSimpleName();
    ServiceGenerator serviceGenerator;


    public LoginActivityVM(LoginActivity activity, LoginInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator=new ServiceGenerator();
        commonFunctions.createLogfolder(activity);
        new DatabaseController(activity).getWritableDatabase();
        IntializeFilters();
    }

    public void Login() {
        String username = activity.getBinding().emailEdt.getText().toString();
        String password = activity.getBinding().passEdt.getText().toString();
        if (!commonFunctions.isNetworkConnected(activity)) {
            commonFunctions.showInternetAlert(activity, 0);
        } else if (username.trim().equals("") || password.trim().equals("")) {
            showInValidAlert(activity);
        } else {
            checkNumberOrEmail(); }
    }

    public void showInValidAlert(Activity activity)
    {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.alertdialog);
        alertDialogBuilder.setMessage("Username and password cannot be blank !!");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> dialog.cancel());
        alertDialogBuilder.show();
        alertDialogBuilder.setCancelable(false);
    }

    public void checkNumberOrEmail() {
        try {
            String username = activity.getBinding().emailEdt.getText().toString().trim().toLowerCase();
            String password = activity.getBinding().passEdt.getText().toString();
            UserDetail userDetail = new UserDetail();
            userDetail.setPassword(AESCrypt.encrypt(password.trim()));
            int isnumber = 1;
            for (int i = 0; i <= username.length() - 1; i++) {
                if (!(username.charAt(i) >= 48 && username.charAt(i) <= 57)) {
                    isnumber = 0;
                }
            }
            if (isnumber == 0) {
                userDetail.setEmail(username.trim());
            } else {
                userDetail.setMobile(username.trim());
            }
            if (commonFunctions.isNetworkConnected(activity)) {
                LoginUser(userDetail);
            } else {
                commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void LoginUser(UserDetail userDetail) {
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        //String input = new Gson().toJson(userDetail);
        Call<LoginResponse> call = serviceGenerator.getService().Login(userDetail);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    interactor.setHideProgress();
                    LoginResponse loginResponse = response.body();
                    if (response.code() == 200) {
                        Login_Success(loginResponse.getUserProfile(), loginResponse.getToken());
                    } else if (response.code() == 409) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG).show();
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    } else if (response.code() == 401) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG).show();
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    } else {
                        commonFunctions.showInternalError_Msg(activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Login_Success(Registration registration,String token)
    {
        SharedPref sharedPref=new SharedPref(activity,"");
        sharedPref.set("UserId",registration.getUserId());
        sharedPref.set("Username",registration.getFname());
        sharedPref.set("LastName", registration.getLname());
        sharedPref.set("OrganizationName", registration.getOrganizationName());
        sharedPref.set("OrganizationType", registration.getOrganizationType());
        sharedPref.set("DOB", registration.getDob());
        sharedPref.set("Address", registration.getAddress());
        sharedPref.set("State", registration.getState() );
        sharedPref.set("District", registration.getDistrict());
        sharedPref.set("PIN", registration.getPin());
        sharedPref.set("EmailID", registration.getEmail());
        sharedPref.set("Mobile", registration.getMobile());
        sharedPref.set("Gender", registration.getGender());
        sharedPref.set("UserType", registration.getUserType());
        sharedPref.set("UserPoints",registration.getUserPoints());
        sharedPref.set("Token",token);

        activity.runOnUiThread(() -> logToUser(Constants.IS_ERROR, Constants.USER_AUTHENTICATION_MSG));
        Intent intent = new Intent(activity, ProductScannedActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private void logToUser(boolean isError, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

    public void forgotpassword() {
        Intent intent = new Intent(activity, ForgotPasswordActivity.class);
        activity.startActivity(intent);
    }

    public void Register()
    {
        Intent intent=new Intent(activity, RegisterActivity.class);
        activity.startActivity(intent);
    }

    public void IntializeFilters()
    {
        activity.getBinding().emailEdt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("name")});
        activity.getBinding().passEdt.setFilters(new InputFilter[]{ignoreFirstWhiteSpace("pwd")});
    }

    public InputFilter ignoreFirstWhiteSpace(String text) {
        return (source, start, end, dest, dstart, dend) -> {

            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                   /* if (dstart == 0)*/
                        return "";
                }
            }
            if (text.equals("name")) {
                if (dend > 99) {
                    return "";
                }
            }
            if (text.equals("pwd")) {
                if (dend > 14) {
                    return "";
                }
            }
            return null;
        };
    }

}

