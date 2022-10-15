package com.esri.natmoapp.auth.otpverification;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActivityOtpverificationBinding;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class OtpVerification extends BindingActivity<ActivityOtpverificationBinding, OtpVerificationVM>
        implements OtpVerificationInteractor {

    ProgressDialog progressDialog;
    String email, otp;
    int isReset;


    @Override
    public OtpVerificationVM onCreate() {
        email=getIntent().getExtras().getString("email");
        otp=getIntent().getExtras().getString("otp");
        isReset=getIntent().getExtras().getInt("isReset");
        progressDialog=new ProgressDialog(this);
        return new OtpVerificationVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_otpverification;
    }

    @Override
    public void setShowProgress() {
        progressDialog.show();
    }

    @Override
    public void setHideProgress() {
        progressDialog.hide();
    }

    @Override
    public void setServerError() {
        Toast.makeText(OtpVerification.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

