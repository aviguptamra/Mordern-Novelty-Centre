package com.esri.natmoapp.auth.otpverification;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.resetpassword.ResetPasswordActivity;
import com.esri.natmoapp.model.ForgotPassword;
import com.esri.natmoapp.model.ResetPassword;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.SharedPref;
import com.goodiebag.pinview.Pinview;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.util.Random;

public class OtpVerificationVM extends ActivityViewModel<OtpVerification> {

    OtpVerificationInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();

    public OtpVerificationVM(OtpVerification activity, OtpVerificationInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        ConfigureOtpView();
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
                NavigateToResetpassword();
            } else {
                activity.progressDialog.cancel();
            }
        } else {
            commonFunctions.showInternetAlert(activity, 1);
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

    public void NavigateToResetpassword() {
        Intent intent = new Intent(activity, ResetPasswordActivity.class);
        intent.putExtra("email",activity.email);
        intent.putExtra("otp",activity.otp);
        activity.startActivity(intent);
    }


}
