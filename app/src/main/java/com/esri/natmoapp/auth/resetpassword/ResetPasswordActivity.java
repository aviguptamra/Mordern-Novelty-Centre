package com.esri.natmoapp.auth.resetpassword;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActivityResetpasswordBinding;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class ResetPasswordActivity extends BindingActivity<ActivityResetpasswordBinding, ResetPasswordActivityVM>
        implements ResetPasswordInteractor {

    ProgressDialog progressDialog;
    String email,otp;


    @Override
    public ResetPasswordActivityVM onCreate() {
        email=getIntent().getExtras().getString("email");
        otp=getIntent().getExtras().getString("otp");
        progressDialog=new ProgressDialog(this);
        return new ResetPasswordActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_resetpassword;
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
        Toast.makeText(ResetPasswordActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


