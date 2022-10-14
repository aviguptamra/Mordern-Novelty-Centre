package com.esri.natmoapp.auth.forgotpassword;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.otpverification.OtpVerification;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.ForgotPassword;
import com.esri.natmoapp.model.ProductDetails;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivityVM extends ActivityViewModel<ForgotPasswordActivity> {

    ForgotPasswordInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();
    ServiceGenerator serviceGenerator;
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    public ForgotPasswordActivityVM(ForgotPasswordActivity activity, ForgotPasswordInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        this.serviceGenerator=new ServiceGenerator();
    }

    public void showForgotPasswordAlert() {
        if (commonFunctions.isNetworkConnected(activity)) {
            if (validateForgotpasswordform()) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.alertdialog);
                alertDialogBuilder.setMessage("Are you sure want to forgot password for this user !!");
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> ForgotPassword());
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                alertDialogBuilder.show();
            }
        } else {
            commonFunctions.showInternetAlert(activity,1);
        }
    }

    public void ForgotPassword()
    {
        SharedPref sharedPref=new SharedPref(activity,"");
        String otp=getRandomNumberString();
        ForgotPassword forgotPassword=new ForgotPassword();
        forgotPassword.setEmail(activity.getBinding().emailidEdt.getText().toString().trim().toLowerCase());
        forgotPassword.setOtp(otp.trim());
        sharedPref.set("otp",otp);
        if (commonFunctions.isNetworkConnected(activity)) {
            ForgotPasswordServer(forgotPassword);
        } else {
            commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public void NavigateToOtpScreen()
    {
        Intent intent=new Intent(activity, OtpVerification.class);
        intent.putExtra("email",activity.getBinding().emailidEdt.getText().toString());
        intent.putExtra("otp",new SharedPref(activity,"").get("otp"));
        activity.startActivity(intent);
    }


    private boolean validateForgotpasswordform() {
        boolean valid = true;
        if (activity.getBinding().emailidEdt.getText().toString().trim().equals("")) {
            valid = false;
            activity.getBinding().emailidEdt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter email !!", false, activity);
        }
        else if (!isValidEmail(activity.getBinding().emailidEdt.getText().toString().trim())) {
            valid = false;
            activity.getBinding().emailidEdt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter valid email !!", false, activity);
        }
        return valid;
    }
    public void showsoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 9999
        Random rnd = new Random();
        int number = rnd.nextInt(9999);
        // this will convert any number sequence into 6 character.
        return String.format("%04d", number);
    }

    public void ForgotPasswordServer(ForgotPassword forgotPassword) {
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        String input = new Gson().toJson(forgotPassword);
        Call<Void> call = serviceGenerator.getService().ForgotPassword(forgotPassword);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 204) {
                        activity.runOnUiThread(() -> logToUser(false, "Mail has been sent successfully"));
                        NavigateToOtpScreen();
                    } else if (response.code() == 409 || response.code()==404) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG);
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    } else if (response.code() == 401) {
                        commonFunctions.showSessionExpired_Msg(activity);
                    } else {
                        commonFunctions.showInternalError_Msg(activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logToUser(boolean isError, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }


}
