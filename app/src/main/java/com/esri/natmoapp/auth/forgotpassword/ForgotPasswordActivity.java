package com.esri.natmoapp.auth.forgotpassword;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActivityForgotpasswordBinding;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class ForgotPasswordActivity extends BindingActivity<ActivityForgotpasswordBinding, ForgotPasswordActivityVM>
        implements ForgotPasswordInteractor {

    ProgressDialog progressDialog;


    @Override
    public ForgotPasswordActivityVM onCreate() {
        progressDialog=new ProgressDialog(this);
        return new ForgotPasswordActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_forgotpassword;
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
        Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }
}
