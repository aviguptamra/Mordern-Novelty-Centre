package com.esri.natmoapp.auth.otpverification;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.login.LoginActivity;
import com.esri.natmoapp.auth.resetpassword.ResetPasswordActivity;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.ForgotPassword;
import com.esri.natmoapp.model.ResetPassword;
import com.esri.natmoapp.model.VerifyMail;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.SharedPref;
import com.goodiebag.pinview.Pinview;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerificationVM extends ActivityViewModel<OtpVerification> {

    OtpVerificationInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();
    ServiceGenerator serviceGenerator;
    public android.app.AlertDialog builder_DialogRegSuccess;
    Button submit;


    public OtpVerificationVM(OtpVerification activity, OtpVerificationInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        ConfigureOtpView();
        serviceGenerator=new ServiceGenerator();
    }

    public void ConfigureOtpView() {
        activity.getBinding().otpView.setTextColor(activity.getResources().getColor(R.color.black));
        activity.getBinding().emailmsg.setText("Please type the verification code sent to your email "+
                activity.email.trim());
    }


    public void VerifyOtp() {

        if (commonFunctions.isNetworkConnected(activity)) {
            activity.progressDialog.setMessage("Verifying otp from server");
            activity.progressDialog.show();
            activity.progressDialog.setCancelable(false);
            if (validateOtpView()) {
                Toast.makeText(activity, "Otp has been verified successfully", Toast.LENGTH_LONG).show();
                activity.progressDialog.cancel();
                if (activity.isReset == 1) {
                    NavigateToResetpassword();
                } else {
                    VerifyEmail();
                }
            } else {
                activity.progressDialog.cancel();
            }
        } else {
            commonFunctions.showInternetAlert(activity, 1);
        }
    }

    public void VerifyEmail()
    {
        VerifyMail verifyMail=new VerifyMail();
        verifyMail.setEmail(activity.email.trim());
        verifyMail.setOtp(activity.otp.trim());
        if (commonFunctions.isNetworkConnected(activity)) {
            VerifyMailFromServer(verifyMail);
        } else {
            commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public boolean validateOtpView()
    {
        boolean valid = true;
        activity.getBinding().otpmsg.setVisibility(View.GONE);
        if (activity.getBinding().otpView.getValue().trim().equals("")) {
            valid = false;
            activity.getBinding().otpView.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().otpmsg.setText("* Please enter otp");
            activity.getBinding().otpmsg.setVisibility(View.VISIBLE);
        }
        if (activity.getBinding().otpView.getValue().trim().length()<4) {
            valid = false;
            activity.getBinding().otpView.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().otpmsg.setText("* Please enter otp");
            activity.getBinding().otpmsg.setVisibility(View.VISIBLE);
        }
        else if (!(activity.getBinding().otpView.getValue().trim().equals(activity.otp.trim()))) {
            valid = false;
            activity.getBinding().otpView.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().otpmsg.setText("* Please enter valid otp");
            activity.getBinding().otpmsg.setVisibility(View.VISIBLE);
        }
        return valid;
    }
    public void showsoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void VerifyMailFromServer(VerifyMail verifyMail) {
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        String input = new Gson().toJson(verifyMail);
        Call<Void> call = serviceGenerator.getService().VerifyMail(verifyMail);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 204) {
                        show_VerifymailSuccessDialog();
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

    public void NavigateToResetpassword() {
        Intent intent = new Intent(activity, ResetPasswordActivity.class);
        intent.putExtra("email",activity.email);
        intent.putExtra("otp",activity.otp);
        activity.startActivity(intent);
    }

    public void show_VerifymailSuccessDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View regsuccess_popup = inflater.inflate(R.layout.dialog_verifymailsuccess, null);
        IntializeResetSuccess_ViewPopup(regsuccess_popup);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(regsuccess_popup);
        builder_DialogRegSuccess = builder.create();
        builder_DialogRegSuccess.show();
        builder_DialogRegSuccess.setCancelable(false);
        builder_DialogRegSuccess.setCanceledOnTouchOutside(false);
    }

    private void IntializeResetSuccess_ViewPopup(View regsuccess_popup) {

        submit = (Button) regsuccess_popup.findViewById(R.id.veremail_btn);
        submit.setOnClickListener(v -> {
            builder_DialogRegSuccess.cancel();
            Intent intent=new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        });
    }


}
