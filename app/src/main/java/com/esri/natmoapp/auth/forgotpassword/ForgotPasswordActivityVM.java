package com.esri.natmoapp.auth.forgotpassword;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.esri.natmoapp.util.CommonFunctions;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

public class ForgotPasswordActivityVM extends ActivityViewModel<ForgotPasswordActivity> {

    ForgotPasswordInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();

    public ForgotPasswordActivityVM(ForgotPasswordActivity activity, ForgotPasswordInteractor interactor) {
        super(activity);
        this.interactor = interactor;
    }

    public void showResetPasswordAlert() {
        if (commonFunctions.isNetworkConnected(activity)) {
            if (validateForgotpasswordform()) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage("Are you sure want to reset password for this user !!");
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> showsoftKeyBoard(activity));
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                alertDialogBuilder.show();
            }
        } else {
            commonFunctions.showInternetAlert(activity,1);
        }
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


}
