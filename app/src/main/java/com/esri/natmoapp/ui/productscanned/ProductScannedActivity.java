package com.esri.natmoapp.ui.productscanned;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.widget.ImageView;
import android.widget.Toast;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.databinding.ActvityCreditscoreBinding;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.GPSTracker;
import com.esri.natmoapp.util.SharedPref;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

public class ProductScannedActivity extends BindingActivity<ActvityCreditscoreBinding, ProductScannedActivityVM>
        implements ProductScannedInteractor {

    ProgressDialog progressDialog;
    public GPSTracker LocationUpdates;
    public Location location;
    public double latitude, longitude;
    ImageView image_dsh,logout,addproductseries,userdetail;

    @Override
    public ProductScannedActivityVM onCreate() {
        LocationUpdates = new GPSTracker(this);
        GetLocation();
        image_dsh=findViewById(R.id.admin_dsh);
        addproductseries=findViewById(R.id.addseries);
        userdetail=findViewById(R.id.userdetail);
        logout=findViewById(R.id.logout);
        progressDialog = new ProgressDialog(this);

        SharedPref sharedPref=new SharedPref(this,"");
        String usertype=sharedPref.get("UserType");

        if(usertype.trim().toLowerCase().equals("public user")) {
            progressDialog.setMessage("Fetching data from server...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        return new ProductScannedActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.actvity_creditscore;
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
        Toast.makeText(ProductScannedActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onResume() {
        super.onResume();
        GetLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void GetLocation() {
        Class<? extends Activity> ResultantActivity = ProductScannedActivity.class;
        if (new CommonFunctions().CheckLocationStatus(this, ResultantActivity)) {
            location = LocationUpdates.getLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }
}
