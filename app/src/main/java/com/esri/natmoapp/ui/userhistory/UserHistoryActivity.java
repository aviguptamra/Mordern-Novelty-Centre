package com.esri.natmoapp.ui.userhistory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.adapter.SearchUserListAdapter;
import com.esri.natmoapp.adapter.ViewPagerAdapter;
import com.esri.natmoapp.databinding.ActivityUserhistoryBinding;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.GPSTracker;
import com.google.android.material.tabs.TabLayout;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.util.ArrayList;
import java.util.List;

public class UserHistoryActivity extends BindingActivity<ActivityUserhistoryBinding, UserHistoryActivityVM>
        implements UserHistoryInteractor {

    ViewPager viewPager;
    TabLayout tabLayout;
    Class<? extends Activity> ResultantActivity = null;
    RecyclerView historyRecylerview;
    LinearLayout historyListLytRecylerview;
    TextView textViewNorecords;
    ProgressDialog progressDialog;
    String[] tabs = {"Score History", "Redeem History"};
    ImageView backUserHistory, redeem;
    SearchUserListAdapter searchUserListAdapter;
    Registration registration;

    public static String[] feild_Names_UserHist = {"productId", "createdDate","points_Scoredstr"};
    public static String[] feild_Labels_UserHist = {"Scan Time","Score"};
    public static String[] feild_Names_RedeemHist = {"pointsRedeemed", "createdBy", "createdDate"};
    public static String[] feild_Labels_RedeemHist = {"Redeemed By", "Redeemed On"};
    List SearchListUserHistoryData;
    List SearchListRedeemHistoryData;
    public GPSTracker LocationUpdates;
    public Location location;
    public double latitude, longitude;
    int scoredpoints;

    @Override
    public UserHistoryActivityVM onCreate() {
        SearchListUserHistoryData = (ArrayList) getIntent().getSerializableExtra("userhistory_list");
        SearchListRedeemHistoryData = (ArrayList) getIntent().getSerializableExtra("redeemhistory_list");
        registration=(Registration)getIntent().getSerializableExtra("registrationobj");
        scoredpoints=Integer.parseInt(getIntent().getExtras().getString("scoredpoints").trim());

        LocationUpdates = new GPSTracker(this);
        GetLocation();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.title = tabs;
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.theme_red));
        tabLayout.setSelectedTabIndicatorHeight(8);
        //tabLayout.setTabTextColors(R.color.black, R.color.black);

        textViewNorecords = (TextView) findViewById(R.id.norecordheading);
        progressDialog = new ProgressDialog(this);
        backUserHistory = (ImageView) findViewById(R.id.backuserhistory);
        backUserHistory.setOnClickListener(view -> onBackPressed());
        redeem = (ImageView) findViewById(R.id.redeem);
        return new UserHistoryActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_userhistory;
    }

    @Override
    public void onBackPressed() {
        getViewModel().GetUserDetails();
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
        Toast.makeText(UserHistoryActivity.this, getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        GetLocation();
    }

    public void GetLocation() {
        Class<? extends Activity> ResultantActivity = UserHistoryActivity.class;
        if (new CommonFunctions().CheckLocationStatus(this, ResultantActivity)) {
            location = LocationUpdates.getLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }


}

