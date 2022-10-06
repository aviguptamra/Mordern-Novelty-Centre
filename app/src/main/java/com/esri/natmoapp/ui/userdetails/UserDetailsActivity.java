package com.esri.natmoapp.ui.userdetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActvityCreditscoreuserBinding;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.esri.natmoapp.util.SharedPref;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class UserDetailsActivity extends BindingActivity<ActvityCreditscoreuserBinding,UserDetailsActivityVM>
        implements UserDetailsInteractor {

    ProgressDialog progressDialog;
    String scoredpoints;
    Registration registration;
    ImageView userhistoryicon;
    SharedPref sharedPref;

    @Override
    public UserDetailsActivityVM onCreate() {
        scoredpoints=getIntent().getExtras().getString("Credits");
        registration=(Registration) (getIntent().getSerializableExtra("userregistrationobj"));

        sharedPref=new SharedPref(this,"");
        userhistoryicon=(ImageView) findViewById(R.id.userhistoryicon);
        progressDialog=new ProgressDialog(this);
        return new UserDetailsActivityVM(this, this);
    }

    @Override
    public int getVariable()
    {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.actvity_creditscoreuser;
    }

    @Override
    public void setShowProgress() {
        progressDialog.show();
    }

    @Override
    public void setHideProgress() {
        progressDialog.cancel();
    }

    @Override
    public void setProgessDialogMessgae(String messgae)
    {
        progressDialog.setMessage(messgae);
    }

    @Override
    public void setServerError() {
        Toast.makeText(UserDetailsActivity.this,getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (sharedPref.get("UserType").toLowerCase().trim().equals("admin")) {
            getViewModel().GetAllUsers();
        } else {
            Intent intent = new Intent(this, ProductScannedActivity.class);
            startActivity(intent);
            finish();
        }

    }

}
