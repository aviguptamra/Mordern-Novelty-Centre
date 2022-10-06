package com.esri.natmoapp.auth.register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.widget.Toast;
import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActvityRegisterBinding;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.GPSTracker;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class RegisterActivity extends BindingActivity<ActvityRegisterBinding, RegisterActivityVM>
        implements RegisterInteractor {

    ProgressDialog progressDialog;
    public GPSTracker LocationUpdates;
    public Location location;
    public  double latitude,longitude;

    @Override
    public RegisterActivityVM onCreate() {
        LocationUpdates = new GPSTracker(this);
        GetLocation();
        progressDialog = new ProgressDialog(this);
        return new RegisterActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.actvity_register;
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
        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();
        GetLocation();
    }

    public void GetLocation() {
        Class<? extends Activity> ResultantActivity= RegisterActivity.class;
        if (new CommonFunctions().CheckLocationStatus(this,ResultantActivity)) {
            location = LocationUpdates.getLocation();
            if(location!=null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }
}
