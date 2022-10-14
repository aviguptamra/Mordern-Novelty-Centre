package com.esri.natmoapp.auth.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;
import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActivityLoginBinding;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class LoginActivity extends BindingActivity<ActivityLoginBinding, LoginActivityVM>
        implements LoginInteractor {

    ProgressDialog progressDialog;
    String usertype;

    @Override
    public LoginActivityVM onCreate() {
        //usertype = getIntent().getExtras().getString("usertype");
        progressDialog = new ProgressDialog(this);
        return new LoginActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
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
        Toast.makeText(LoginActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
