package com.esri.natmoapp.ui.userhistory;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.natmoapp.R;
import com.esri.natmoapp.adapter.SearchUserListAdapter;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.ProductDetails;
import com.esri.natmoapp.model.RedeemHistory;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.model.UserHistory;
import com.esri.natmoapp.model.UserHistoryModel;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.userdetails.UserDetailsActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.Constants;
import com.esri.natmoapp.util.SharedPref;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHistoryActivityVM extends ActivityViewModel<UserHistoryActivity> {

    UserHistoryInteractor interactor;
    public android.app.AlertDialog builder_DialogRedeemPoint;
    Button submit_redeem, cancel_redeem;
    TextInputEditText points_ToRedeem, remarks;
    TextView pointsredeem_msg, remarksmsg;
    CommonFunctions commonFunctions = new CommonFunctions();
    ServiceGenerator serviceGenerator;

    public android.app.AlertDialog builder_DialogSuccessRedeemPoint;
    Button success_redeem;
    TextView sucessredeemmsg;

    public UserHistoryActivityVM(UserHistoryActivity activity, UserHistoryInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator = new ServiceGenerator();
        setDataInRecyclerView_UserHistory();
        setDataInRecyclerView_RedeemHistory();
        IntializeEventHandlers();
    }

    public void IntializeEventHandlers() {
        activity.tabLayout.getTabAt(0).select();
        EvaluateNoRecords();
        activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.VISIBLE);
        activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
        activity.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int postion = tab.getPosition();
                if (postion == 0) {
                    if (activity.SearchListUserHistoryData != null) {
                        if (activity.SearchListUserHistoryData.size() != 0) {
                            activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.VISIBLE);
                            activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                            activity.getBinding().norecordheading.setVisibility(View.GONE);
                        } else {
                            activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
                            activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                            activity.getBinding().norecordheading.setVisibility(View.VISIBLE);
                        }
                    } else {
                        activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
                        activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                        activity.getBinding().norecordheading.setVisibility(View.VISIBLE);
                    }

                } else if (postion == 1) {
                    if (activity.SearchListRedeemHistoryData != null) {
                        if (activity.SearchListRedeemHistoryData.size() != 0) {
                            activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
                            activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.VISIBLE);
                            activity.getBinding().norecordheading.setVisibility(View.GONE);
                        } else {
                            activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
                            activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                            activity.getBinding().norecordheading.setVisibility(View.VISIBLE);
                        }
                    } else {
                        activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
                        activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                        activity.getBinding().norecordheading.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        activity.redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.scoredpoints > 0) {
                    show_RedeemPointDialog();
                } else {
                    new CommonFunctions().showMessage(activity, "Alert", "You cannot redeem points due to insufficient point balance !!");
                }
            }
        });

        SharedPref sharedPref = new SharedPref(activity, "");
        String usertype = sharedPref.get("UserType");
        if (usertype.trim().toLowerCase().equals("admin")) {
            activity.redeem.setVisibility(View.VISIBLE);
        } else {
            activity.redeem.setVisibility(View.GONE);
        }
    }

    private void EvaluateNoRecords() {
        if (activity.SearchListUserHistoryData != null) {
            if (activity.SearchListUserHistoryData.size() != 0) {
                activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.VISIBLE);
                activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                activity.getBinding().norecordheading.setVisibility(View.GONE);
            } else {
                activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
                activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
                activity.getBinding().norecordheading.setVisibility(View.VISIBLE);
            }
        } else {
            activity.getBinding().recyclerViewLytuserhistory.setVisibility(View.GONE);
            activity.getBinding().recyclerViewLytredeemhistory.setVisibility(View.GONE);
            activity.getBinding().norecordheading.setVisibility(View.VISIBLE);
        }
    }


    private void setDataInRecyclerView_UserHistory() {
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(activity);
        activity.getBinding().recyclerViewuserhistorylist.setLayoutManager(gridLayoutManager);
        activity.getBinding().recyclerViewuserhistorylist.addItemDecoration(new SpacesItemDecoration(20));
        activity.searchUserListAdapter = new SearchUserListAdapter(activity, activity.SearchListUserHistoryData, activity.feild_Names_UserHist, activity.feild_Labels_UserHist, 3);
        activity.getBinding().recyclerViewuserhistorylist.setAdapter(activity.searchUserListAdapter);
    }

    private void setDataInRecyclerView_RedeemHistory() {
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(activity);
        activity.getBinding().recyclerViewredeemhistorylist.setLayoutManager(gridLayoutManager);
        activity.getBinding().recyclerViewredeemhistorylist.addItemDecoration(new SpacesItemDecoration(20));
        activity.searchUserListAdapter = new SearchUserListAdapter(activity, activity.SearchListRedeemHistoryData, activity.feild_Names_RedeemHist, activity.feild_Labels_RedeemHist, 4);
        activity.getBinding().recyclerViewredeemhistorylist.setAdapter(activity.searchUserListAdapter);
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    public void show_RedeemPointDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View redeem_popup = inflater.inflate(R.layout.dialog_redeempoints, null);
        IntializeRegSuccess_ViewPopup(redeem_popup);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), R.style.alertdialog);
        builder.setView(redeem_popup);
        builder_DialogRedeemPoint = builder.create();
        builder_DialogRedeemPoint.show();
        builder_DialogRedeemPoint.setCancelable(false);
        builder_DialogRedeemPoint.setCanceledOnTouchOutside(false);
    }

    private void IntializeRegSuccess_ViewPopup(View regsuccess_popup) {

        submit_redeem = (Button) regsuccess_popup.findViewById(R.id.btn_redeem);
        cancel_redeem = (Button) regsuccess_popup.findViewById(R.id.btn_cancelredeem);
        points_ToRedeem = (TextInputEditText) regsuccess_popup.findViewById(R.id.pointsredeemedt);
        remarks = (TextInputEditText) regsuccess_popup.findViewById(R.id.remarksredeemedt);
        pointsredeem_msg = (TextView) regsuccess_popup.findViewById(R.id.redeempointmsg);
        remarksmsg = (TextView) regsuccess_popup.findViewById(R.id.remarksmsg);
        submit_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRedeeemAlertpopup();
            }
        });
        cancel_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_DialogRedeemPoint.cancel();
            }
        });
    }

    public void showRedeeemAlertpopup() {
        if (validateRedeemform()) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.alertdialog);
            alertDialogBuilder.setMessage("Are you sure want to redeem points from  this user !!");
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    RedeemPoint();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }

    public void RedeemPoint() {
        RedeemHistory redeemHistory = new RedeemHistory();
        redeemHistory.setPointsRedeemed(Integer.parseInt(Objects.requireNonNull(points_ToRedeem.getText()).toString()));
        redeemHistory.setUserId(activity.registration.getUserId());
        redeemHistory.setLatitude((String.valueOf(activity.latitude)));
        redeemHistory.setLongitude((String.valueOf(activity.longitude)));
        redeemHistory.setRemarks(remarks.getText().toString());
        redeemHistory.setDeviceId(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        redeemHistory.setAppVersion(Constants.APP_VERSION);
        if (commonFunctions.isNetworkConnected(activity)) {
            RedeemPointsServer(redeemHistory);
        } else {
            commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public void RedeemPointsServer(RedeemHistory redeemHistory) {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        String input = new Gson().toJson(redeemHistory);
        Call<Void> call = serviceGenerator.getService().RedeemPoints(redeemHistory, "Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 201 || response.code() == 200) {
                        Toast.makeText(activity, "points redeemed successfully", Toast.LENGTH_LONG);
                        builder_DialogRedeemPoint.cancel();
                        show_RedeemSuccessDialog();
                        GetUserHistory();
                    } else if (response.code() == 409 || response.code() == 404) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG);
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    } else if (response.code() == 401) {
                        commonFunctions.showSessionExpired_Msg(activity);
                    } else {
                        commonFunctions.showInternalError_Msg(activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void GetUserHistory() {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        Call<UserHistory> call = serviceGenerator.getService().GetUserHistory(activity.registration.getUserId(), "Bearer " + token);
        call.enqueue(new Callback<UserHistory>() {
            @Override
            public void onResponse(Call<UserHistory> call, Response<UserHistory> response) {
                try {
                    interactor.setHideProgress();
                    UserHistory userHistory = response.body();
                    if (response.code() == 200) {
                        refreshHistory(userHistory);
                    } else if (response.code() == 409 || response.code() == 409) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG);
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    } else if (response.code() == 401) {
                        commonFunctions.showSessionExpired_Msg(activity);
                    } else {
                        commonFunctions.showInternalError_Msg(activity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserHistory> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refreshHistory(UserHistory userHistory)
    {
        List<RedeemHistory> redeemHistoryList = new ArrayList<>();
        for (UserHistoryModel userHistoryModelobj : userHistory.getRedeemResponses()) {
            RedeemHistory redeemHistory=new RedeemHistory();
            redeemHistory.setPointsRedeemed(userHistoryModelobj.getRedeemHistory().getPointsRedeemed());
            redeemHistory.setCreatedBy(userHistoryModelobj.getCreatedBy());
            redeemHistory.setCreatedDate(userHistoryModelobj.getRedeemHistory().getCreatedDate());
            redeemHistoryList.add(redeemHistory);
        }
        for (ProductDetails productDetails : userHistory.getProductScannedList()) {
            productDetails.setPoints_Scoredstr(productDetails.getPointsScored() + " Points");
        }
        activity.SearchListRedeemHistoryData.clear();
        activity.SearchListRedeemHistoryData.addAll(redeemHistoryList);
        activity.searchUserListAdapter.notifyDataSetChanged();

        activity.SearchListUserHistoryData.clear();
        activity.SearchListUserHistoryData.addAll(userHistory.getProductScannedList());
        activity.searchUserListAdapter.notifyDataSetChanged();
    }


    private boolean validateRedeemform() {
        boolean valid = true;

        pointsredeem_msg.setVisibility(View.GONE);
        remarksmsg.setVisibility(View.GONE);

        if (points_ToRedeem.getText().toString().equals("")) {
            valid = false;
            points_ToRedeem.requestFocus();
            pointsredeem_msg.setVisibility(View.VISIBLE);
        }
        else if (points_ToRedeem.getText().toString().equals("0")) {
            valid = false;
            points_ToRedeem.requestFocus();
            pointsredeem_msg.setText("You cannot redeem 0 points");
            pointsredeem_msg.setVisibility(View.VISIBLE);
        } else if (remarks.getText().toString().equals("")) {
            valid = false;
            remarks.requestFocus();
            remarksmsg.setVisibility(View.VISIBLE);
        }
        return valid;
    }

    public void show_RedeemSuccessDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View redeemsuccess_popup = inflater.inflate(R.layout.dialog_redeem_success, null);
        IntializeRedeemSuccess_ViewPopup(redeemsuccess_popup);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(redeemsuccess_popup);
        builder_DialogSuccessRedeemPoint = builder.create();
        builder_DialogSuccessRedeemPoint.show();
        builder_DialogSuccessRedeemPoint.setCancelable(false);
        builder_DialogSuccessRedeemPoint.setCanceledOnTouchOutside(false);
    }

    private void IntializeRedeemSuccess_ViewPopup(View redeemsuccess_popup) {

        success_redeem = (Button) redeemsuccess_popup.findViewById(R.id.rede_btn);
        sucessredeemmsg = (TextView) redeemsuccess_popup.findViewById(R.id.redeemmsgdetail);
        sucessredeemmsg.setText(String.valueOf(points_ToRedeem.getText().toString()) + " Points have been redeemed from the user");
        success_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_DialogSuccessRedeemPoint.cancel();
            }
        });
    }

    public void GetUserDetails() {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        Call<Registration> call = serviceGenerator.getService().GetUserDetails_ById(activity.registration.getUserId(), "Bearer " + token);
        call.enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {
                interactor.setHideProgress();
                Registration registration = response.body();
                if (response.code() == 200) {
                        Intent intent = new Intent(activity, UserDetailsActivity.class);
                        intent.putExtra("userregistrationobj", (Serializable) registration);
                        intent.putExtra("Credits", String.valueOf(registration.getUserPoints()+" Points"));
                        activity.startActivity(intent);
                        finish();

                } else if (response.code() == 401) {
                    commonFunctions.showSessionExpired_Msg(activity);
                } else {
                    commonFunctions.showInternalError_Msg(activity);
                }
            }

            @Override
            public void onFailure(Call<Registration> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}