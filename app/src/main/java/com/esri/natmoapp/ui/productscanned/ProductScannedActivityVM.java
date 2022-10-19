package com.esri.natmoapp.ui.productscanned;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.login.LoginActivity;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.AddProductSeries;
import com.esri.natmoapp.model.ProductDetails;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.model.UserPoints;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.addproductseries.AddProductSeriesActivity;
import com.esri.natmoapp.ui.searchuser.SearchUserActivity;
import com.esri.natmoapp.ui.userdetails.UserDetailsActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.Constants;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductScannedActivityVM extends ActivityViewModel<ProductScannedActivity> {

    ProductScannedInteractor interactor;
    Map<String, Object> attributes = new HashMap<>();
    Map<String, Object> attributes_userpoints = new HashMap<>();
    private static final String TAG = ProductScannedActivity.class.getSimpleName();
    String productId = "";
    ServiceGenerator serviceGenerator;
    CommonFunctions commonFunctions = new CommonFunctions();
    public android.app.AlertDialog builder_DialogProSeriesSuccess;
    Button submit;

    public ProductScannedActivityVM(ProductScannedActivity activity, ProductScannedInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator = new ServiceGenerator();
        IntilaizeUserDetails();
    }

    public void ScanQRCode() {
        if (new CommonFunctions().isNetworkConnected(activity)) {
            try {
                IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                intentIntegrator.initiateScan();
                //ScanProduct("MNRERTRMNR12345TTTTYYT34343MNR123447");
            } catch (Exception e) {
                Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                activity.startActivity(marketIntent);
            }
        } else {
            new CommonFunctions().showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public void IntilaizeUserDetails() {
        SharedPref sharedPref = new SharedPref(activity, "");
        activity.getBinding().username.setText(sharedPref.get("Username").toUpperCase() + " " + sharedPref.get("LastName").toUpperCase());
        activity.getBinding().email.setText(sharedPref.get("EmailID"));
        activity.getBinding().phone.setText(sharedPref.get("Mobile"));
        activity.getBinding().gender.setText(sharedPref.get("Gender"));
        activity.getBinding().dob.setText(sharedPref.get("DOB"));
        activity.getBinding().address.setText(sharedPref.get("Address") + " , " + sharedPref.get("District") +
                " , " + sharedPref.get("State") + " (" + sharedPref.get("PIN") + " )");
        String usertype = sharedPref.get("UserType");
        activity.getBinding().orgname.setText(sharedPref.get("OrganizationName"));

        if (usertype.trim().toLowerCase().equals("admin")) {
            activity.image_dsh.setVisibility(View.VISIBLE);
            activity.addproductseries.setVisibility(View.VISIBLE);
            activity.userdetail.setVisibility(View.GONE);
            activity.getBinding().qrocdee.setVisibility(View.GONE);
            activity.getBinding().qrocdeeIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, activity.getResources().getDrawable(R.drawable.image_logo), null, null);
            activity.getBinding().qrocdeeIcon.setVisibility(View.VISIBLE);
        } else {
            activity.image_dsh.setVisibility(View.GONE);
            activity.addproductseries.setVisibility(View.GONE);
            activity.userdetail.setVisibility(View.VISIBLE);
            activity.getBinding().qrocdee.setVisibility(View.VISIBLE);
            activity.getBinding().qrocdeeIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, activity.getResources().getDrawable(R.drawable.progess1), null, null);
            activity.getBinding().qrocdeeIcon.setVisibility(View.VISIBLE);
            activity.getBinding().qrocdeeIcon.setText(String.valueOf(sharedPref.get("UserPoints", 0)+" Points"));
        }

        activity.image_dsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (commonFunctions.isNetworkConnected(activity)) {
                        GetAllUsers();
                    } else {
                        commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.addproductseries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (commonFunctions.isNetworkConnected(activity)) {
                        GetAllProductSeries();
                    } else {
                        commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LogoutWarningMsg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.userdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (commonFunctions.isNetworkConnected(activity)) {
                        GetUserDetails(1);
                    } else {
                        commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void GetUserDetails(int flagnavigation) {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        String userId = sharedPref.get("UserId");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        Call<Registration> call = serviceGenerator.getService().GetUserDetails_ById(userId, "Bearer " + token);
        call.enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {
                interactor.setHideProgress();
                Registration registration = response.body();
                if (response.code() == 200) {
                    if (flagnavigation == 1) {
                        Intent intent = new Intent(activity, UserDetailsActivity.class);
                        intent.putExtra("userregistrationobj", (Serializable) registration);
                        intent.putExtra("Credits", String.valueOf(registration.getUserPoints()+" Points"));
                        activity.startActivity(intent);
                    } else if (flagnavigation == 0) {
                        activity.getBinding().qrocdeeIcon.setText(String.valueOf(registration.getUserPoints()+" Points"));
                        sharedPref.set("UserPoints",registration.getUserPoints());
                    }
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
            userPointsList.add(userPoints);
        }
        userPointsListcopy.addAll(userPointsList);
        Intent intent = new Intent(activity, SearchUserActivity.class);
        intent.putExtra("list", (Serializable) userPointsList);
        intent.putExtra("listcopy", (Serializable) userPointsListcopy);
        activity.startActivity(intent);
    }


    public void LogoutWarningMsg() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity, R.style.alertdialog);
        alertDialogBuilder.setMessage("Are you sure want to logout !!");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.putExtra("usertype", "Publicuser");
                activity.startActivity(intent);
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() == null) {
                    Toast.makeText(activity, "Scanning operation is cancelled", Toast.LENGTH_LONG);
                } else {
                    String content = intentResult.getContents();
                    productId = content;
                    ScanProduct(content.toLowerCase().trim());
                    // getProductCodeInSeries(content);
                }
            }
        }
    }

    public void ScanProduct(String productId) {
        ProductDetails productDetails = new ProductDetails();
        productDetails.setProductId(productId.trim().toUpperCase());
        productDetails.setLatitude(String.valueOf(activity.latitude));
        productDetails.setLongitude(String.valueOf(activity.longitude));
        productDetails.setAppVersion(Constants.APP_VERSION);
        productDetails.setDeviceId(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        if (commonFunctions.isNetworkConnected(activity)) {
            ScanProductDetailsToServer(productDetails);
        } else {
            commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public void ScanProductDetailsToServer(ProductDetails productDetails) {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        String input = new Gson().toJson(productDetails);
        Call<Void> call = serviceGenerator.getService().ScanProduct(productDetails, "Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 201) {
                        activity.runOnUiThread(() -> logToUser(false, "product scanned successfully"));
                        show_ScanProductSuccessDialog();
                        GetUserDetails(0);
                    } else if (response.code() == 409 || response.code()==404) {
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

    public void show_ScanProductSuccessDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View pro_seriessuccess_popup = inflater.inflate(R.layout.dialog_scanproduct_success, null);
        IntializeRegSuccess_ViewPopup(pro_seriessuccess_popup);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(pro_seriessuccess_popup);
        builder_DialogProSeriesSuccess = builder.create();
        builder_DialogProSeriesSuccess.show();
        builder_DialogProSeriesSuccess.setCancelable(false);
        builder_DialogProSeriesSuccess.setCanceledOnTouchOutside(false);
    }

    private void IntializeRegSuccess_ViewPopup(View regsuccess_popup) {

        submit = (Button) regsuccess_popup.findViewById(R.id.scansuccess_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_DialogProSeriesSuccess.cancel();
            }
        });
    }

    private void logToUser(boolean isError, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

    public void GetAllProductSeries() {
        SharedPref sharedPref = new SharedPref(activity, "");
        String token = sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        Call<List<AddProductSeries>> call = serviceGenerator.getService().GetAllProductSeries("Bearer " + token);
        call.enqueue(new Callback<List<AddProductSeries>>() {
            @Override
            public void onResponse(Call<List<AddProductSeries>> call, Response<List<AddProductSeries>> response) {
                try {
                    interactor.setHideProgress();
                    if (response.code() == 200) {
                        List<AddProductSeries> productSeriesList = response.body();
                        List<AddProductSeries> productSeriesListcopy = new ArrayList<>();
                        if (productSeriesList != null) {
                            if (productSeriesList.size() != 0) {
                                productSeriesListcopy.addAll(productSeriesList);
                                Intent intent = new Intent(activity, AddProductSeriesActivity.class);
                                intent.putExtra("list", (Serializable) productSeriesList);
                                intent.putExtra("listcopy", (Serializable) productSeriesListcopy);
                                activity.startActivity(intent);
                            }
                        }
                    } else if (response.code() == 404) {
                        List<AddProductSeries> productSeriesList = new ArrayList<>();
                        List<AddProductSeries> productSeriesListcopy = new ArrayList<>();
                        Intent intent = new Intent(activity, AddProductSeriesActivity.class);
                        intent.putExtra("list", (Serializable) productSeriesList);
                        intent.putExtra("listcopy", (Serializable) productSeriesListcopy);
                        activity.startActivity(intent);
                    } else if (response.code() == 409) {
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
            public void onFailure(Call<List<AddProductSeries>> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


}
