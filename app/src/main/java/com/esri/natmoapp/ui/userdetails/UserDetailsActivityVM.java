package com.esri.natmoapp.ui.userdetails;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.ActivateUser;
import com.esri.natmoapp.model.ProductDetails;
import com.esri.natmoapp.model.RedeemHistory;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.model.UserHistory;
import com.esri.natmoapp.model.UserHistoryModel;
import com.esri.natmoapp.model.UserPoints;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.esri.natmoapp.ui.searchuser.SearchUserActivity;
import com.esri.natmoapp.ui.userhistory.UserHistoryActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsActivityVM extends ActivityViewModel<UserDetailsActivity> {

    UserDetailsInteractor interactor;
    private static final String TAG = UserDetailsActivity.class.getSimpleName();
    ServiceGenerator serviceGenerator;
    CommonFunctions commonFunctions=new CommonFunctions();

    public UserDetailsActivityVM(UserDetailsActivity activity, UserDetailsInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator=new ServiceGenerator();
        IntilaizeUserDetails();
    }

    public void IntilaizeUserDetails()
    {
        activity.getBinding().usernames.setText(activity.registration.getFname().toUpperCase()+" "+ activity.registration.getLname().toUpperCase());
        activity.getBinding().email.setText(activity.registration.getEmail());
        activity.getBinding().phone.setText(activity.registration.getMobile());
        activity.getBinding().orgtypes.setText(activity.registration.getOrganizationType());
        activity.getBinding().orgname.setText(activity.registration.getOrganizationName());
        activity.getBinding().gender.setText(activity.registration.getGender());
        activity.getBinding().dob.setText(activity.registration.getDob());
        activity.getBinding().address.setText(activity.registration.getAddress());
        activity.getBinding().states.setText(activity.registration.getState());
        activity.getBinding().district.setText(activity.registration.getDistrict());
        activity.getBinding().pincodes.setText(activity.registration.getPin());
        activity.getBinding().credits.setText(activity.scoredpoints);
        if (activity.registration.isActive()) {
            activity.getBinding().status.setText("Active");
            activity.getBinding().actdeactSwitch.setChecked(true);
        } else {
            activity.getBinding().status.setText("Inactive");
            activity.getBinding().actdeactSwitch.setChecked(false);
        }
        activity.getBinding().cardView.setVisibility(View.VISIBLE);
        activity.getBinding().backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (activity.sharedPref.get("UserType").toLowerCase().trim().equals("admin")) {
                        GetAllUsers();
                    } else {
                        Intent intent = new Intent(activity, ProductScannedActivity.class);
                        activity.startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.userhistoryicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (commonFunctions.isNetworkConnected(activity)) {
                        GetUserHistory();
                    } else {
                        commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SharedPref sharedPref = new SharedPref(activity, "");
        String usertype = sharedPref.get("UserType");
        if (usertype.trim().toLowerCase().equals("admin")) {
            activity.getBinding().actdeactSwitch.setVisibility(View.VISIBLE);
        } else {
            activity.getBinding().actdeactSwitch.setVisibility(View.GONE);
        }

        activity.getBinding().actdeactSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (commonFunctions.isNetworkConnected(activity)) {
                        if (activity.getBinding().actdeactSwitch.isChecked()) {
                            activity.getBinding().status.setText("Active");
                            ActivateUser_DeActivateUser(true);
                        } else {
                            activity.getBinding().status.setText("Inactive");
                            ActivateUser_DeActivateUser(false);
                        }
                    } else {
                        commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void ActivateUser_DeActivateUser(boolean status) {
        ActivateUser activateUser = new ActivateUser();
        activateUser.setUserId(activity.registration.getUserId());
        activateUser.setActive(status);
        if (commonFunctions.isNetworkConnected(activity)) {
            Activate_DeactivateUser(activateUser);
        } else {
            commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public void Activate_DeactivateUser(ActivateUser activateUser) {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        Call<Void> call = serviceGenerator.getService().Activate_DeActivateUser(activateUser, "Bearer " + token.trim());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 204) {
                        if (activateUser.isActive()) {
                            Toast.makeText(activity, "User is activated successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "User is  de activated successfully.", Toast.LENGTH_LONG).show();
                        }
                    } else if (response.code() == 409 || response.code() == 404) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG);
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    }
                    else if (response.code() == 401) {
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
                       NavigateToHistory(userHistory);
                    } else if (response.code() == 409 || response.code() == 404) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(activity, apiErrorResponse.getMessage(), Toast.LENGTH_LONG);
                        commonFunctions.showMessage(activity, "Alert", apiErrorResponse.getMessage());
                    }
                    else if (response.code() == 401) {
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

    public void NavigateToHistory(UserHistory userHistory) {
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

        Intent intent = new Intent(activity, UserHistoryActivity.class);
        intent.putExtra("userhistory_list", (Serializable) userHistory.getProductScannedList());
        intent.putExtra("redeemhistory_list", (Serializable) redeemHistoryList);
        intent.putExtra("registrationobj", (Serializable) activity.registration);
        intent.putExtra("scoredpoints", activity.scoredpoints.split(" ")[0]);
        activity.startActivity(intent);
        activity.finish();
    }

    public void GetAllUsers() {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        Call<List<Registration>> call = serviceGenerator.getService().GetAllUsers("Bearer " + token);
        call.enqueue(new Callback<List<Registration>>() {
            @Override
            public void onResponse(Call<List<Registration>> call, Response<List<Registration>> response) {
                interactor.setHideProgress();
                List<Registration> registrationlist = response.body();
                if (response.code() == 200) {
                    NavigateToSearchUI(registrationlist);
                } else if (response.code() == 401) {
                    commonFunctions.showSessionExpired_Msg(activity);
                } else {
                    commonFunctions.showInternalError_Msg(activity);
                }
            }

            @Override
            public void onFailure(Call<List<Registration>> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void NavigateToSearchUI(List<Registration> registrationList) {
        List<UserPoints> userPointsList = new ArrayList<>();
        List<UserPoints> userPointsListcopy = new ArrayList<>();
        for (Registration registration : registrationList) {
            UserPoints userPoints = new UserPoints();
            userPoints.setUsername(registration.getFname() + " " + registration.getLname());
            userPoints.setPoints_Scored(String.valueOf(registration.getUserPoints()+" Points"));
            userPoints.setUser_Organization(registration.getOrganizationName());
            userPoints.setCreatedDate(registration.getLastActive());
            userPoints.setUserId(registration.getUserId());
            userPoints.setActive(registration.isActive());
            userPointsList.add(userPoints);
        }
        userPointsListcopy.addAll(userPointsList);
        Intent intent = new Intent(activity, SearchUserActivity.class);
        intent.putExtra("list", (Serializable) userPointsList);
        intent.putExtra("listcopy", (Serializable) userPointsListcopy);
        activity.startActivity(intent);
        finish();
    }

}
