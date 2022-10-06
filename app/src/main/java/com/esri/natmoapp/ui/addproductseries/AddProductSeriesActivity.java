package com.esri.natmoapp.ui.addproductseries;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.adapter.SearchUserListAdapter;
import com.esri.natmoapp.databinding.ActvityAddproductseriesBinding;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.GPSTracker;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.util.ArrayList;
import java.util.List;

public class AddProductSeriesActivity extends BindingActivity<ActvityAddproductseriesBinding, AddProductSeriesActivityVM>
        implements AddProductSeriesInteractor {

    ProgressDialog progressDialog;
    public GPSTracker LocationUpdates;
    public Location location;
    public double latitude, longitude;
    List SearchListResponseData;
    List SearchListResponseDataActual;
    SearchUserListAdapter searchUserListAdapter;
    RecyclerView recyclerView;
    ImageView search;
    AppCompatAutoCompleteTextView editsearch;
    public static String[] feild_Names = {"productSeriesUniqueId", "pointsScored", "createdDate"};
    public static String[] feild_Labels = {"Points", "Created On"};
    TextView backbtn;


    @Override
    public AddProductSeriesActivityVM onCreate() {
        SearchListResponseData = (ArrayList) getIntent().getSerializableExtra("list");
        SearchListResponseDataActual = (ArrayList) getIntent().getSerializableExtra("listcopy");
        LocationUpdates = new GPSTracker(this);
        GetLocation();
        progressDialog=new ProgressDialog(this);

        search=(ImageView)findViewById(R.id.seachimg_ap);
        backbtn=(TextView)findViewById(R.id.backbtn_addseries);
        editsearch = (AppCompatAutoCompleteTextView) findViewById(R.id.search_ap);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView_ap) ;
        return new AddProductSeriesActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.actvity_addproductseries;
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
        Toast.makeText(AddProductSeriesActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProductScannedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        GetLocation();
    }

    public void GetLocation() {
        Class<? extends Activity> ResultantActivity = AddProductSeriesActivity.class;
        if (new CommonFunctions().CheckLocationStatus(this, ResultantActivity)) {
            location = LocationUpdates.getLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }
}
