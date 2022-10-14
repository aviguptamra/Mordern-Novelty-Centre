package com.esri.natmoapp.auth.resetpassword;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.login.LoginActivity;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.ForgotPassword;
import com.esri.natmoapp.model.ResetPassword;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.util.AESCrypt;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivityVM extends ActivityViewModel<ResetPasswordActivity> {

    ResetPasswordInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();
    ServiceGenerator serviceGenerator;
    public android.app.AlertDialog builder_DialogRegSuccess;
    Button submit;

    public ResetPasswordActivityVM(ResetPasswordActivity activity, ResetPasswordInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator=new ServiceGenerator();
    }

    public void showResetPasswordAlert() {
        if (commonFunctions.isNetworkConnected(activity)) {
            if (validateResetPasswordform()) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.alertdialog);
                alertDialogBuilder.setMessage("Are you sure want to reset password for this user !!");
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> ResetPassword());
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                alertDialogBuilder.show();
            }
        } else {
            commonFunctions.showInternetAlert(activity,1);
        }
    }

    public void ResetPassword() {
        try {
            ResetPassword resetPassword = new ResetPassword();
            resetPassword.setEmail(activity.email.trim().toLowerCase());
            resetPassword.setOtp(activity.otp.trim());
            resetPassword.setNewPassword(AESCrypt.encrypt(activity.getBinding().newpassedt.getText().toString().trim()));
            if (commonFunctions.isNetworkConnected(activity)) {
                ResetPasswordServer(resetPassword);
            } else {
                commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ResetPasswordServer(ResetPassword resetPassword) {
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        String input = new Gson().toJson(resetPassword);
        Call<Void> call = serviceGenerator.getService().ResetPassword(resetPassword);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 204) {
                        show_ResetSuccessDialog();
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

    public void show_ResetSuccessDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View regsuccess_popup = inflater.inflate(R.layout.dialog_resetsuccess, null);
        IntializeResetSuccess_ViewPopup(regsuccess_popup);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(regsuccess_popup);
        builder_DialogRegSuccess = builder.create();
        builder_DialogRegSuccess.show();
        builder_DialogRegSuccess.setCancelable(false);
        builder_DialogRegSuccess.setCanceledOnTouchOutside(false);
    }

    private void IntializeResetSuccess_ViewPopup(View regsuccess_popup) {

        submit = (Button) regsuccess_popup.findViewById(R.id.reset_btn);
        submit.setOnClickListener(v -> {
            builder_DialogRegSuccess.cancel();
            Intent intent=new Intent(activity,LoginActivity.class);
            activity.startActivity(intent);
        });
    }

    private boolean validateResetPasswordform() {
        boolean valid = true;
        activity.getBinding().newpassmsg.setVisibility(View.GONE);
        activity.getBinding().conpassmsg.setVisibility(View.GONE);

        if (activity.getBinding().newpassedt.getText().toString().trim().equals("")) {
            valid = false;
            activity.getBinding().newpassedt.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().newpassmsg.setText(" * Please enter new password.");
            activity.getBinding().newpassmsg.setVisibility(View.VISIBLE);
        }
        else if (activity.getBinding().newpassedt.getText().toString().trim().length()<8) {
            valid = false;
            activity.getBinding().newpassedt.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().newpassmsg.setText(" * Password should be at least 8 characters.");
            activity.getBinding().newpassmsg.setVisibility(View.VISIBLE);
        }
        else if (!(isValidPassword(activity.getBinding().newpassedt.getText().toString().trim()))) {
            valid = false;
            activity.getBinding().newpassedt.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().newpassmsg.setText(" * Password must contain at least one upper case , one lower case , one digit and one special character .");
            activity.getBinding().newpassmsg.setVisibility(View.VISIBLE);
        }
        else if (activity.getBinding().connewpassedt.getText().toString().trim().equals("")) {
            valid = false;
            activity.getBinding().connewpassedt.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().conpassmsg.setText(" * Please enter confirm new password.");
            activity.getBinding().conpassmsg.setVisibility(View.VISIBLE);
        }
        else if (!(activity.getBinding().newpassedt.getText().toString().trim().equals(activity.getBinding().connewpassedt.getText().toString().trim()))) {
            valid = false;
            activity.getBinding().newpassmsg.requestFocus();
            showsoftKeyBoard(activity);
            activity.getBinding().conpassmsg.setText(" * Password and confirm password does not match.");
            activity.getBinding().conpassmsg.setVisibility(View.VISIBLE);
        }
        return valid;
    }

    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void showsoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


}
