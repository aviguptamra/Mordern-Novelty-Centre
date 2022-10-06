package com.esri.natmoapp.ui.addproductseries;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.natmoapp.R;
import com.esri.natmoapp.adapter.SearchUserListAdapter;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.AddProductSeries;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.Constants;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductSeriesActivityVM extends ActivityViewModel<AddProductSeriesActivity> {

    AddProductSeriesInteractor interactor;
    CommonFunctions commonFunctions = new CommonFunctions();
    private static final String TAG = AddProductSeriesActivity.class.getSimpleName();
    public android.app.AlertDialog builder_DialogProSeriesSuccess;
    Button submit;
    Map<String, Object> attributes = new HashMap<>();
    ServiceGenerator serviceGenerator;


    public AddProductSeriesActivityVM(AddProductSeriesActivity activity, AddProductSeriesInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        serviceGenerator=new ServiceGenerator();
        setDataInRecyclerView();
        IntializeListenersSearchEditText();
    }

    public AddProductSeries getAddProductSeries() {
        SharedPref sharedPref = new SharedPref(activity, "");
        String userId = sharedPref.get("UserId");
        AddProductSeries addProductSeries = new AddProductSeries();
        addProductSeries.setProductSeriesUniqueId(activity.getBinding().productseriestxt.getText().toString());
        addProductSeries.setPointsScored(activity.getBinding().pointsedt.getText().toString());
        addProductSeries.setActive("true");
        addProductSeries.setCreatedBy(String.valueOf(userId));
        addProductSeries.setModifiedBy(String.valueOf(userId));
        addProductSeries.setLatitude(String.valueOf(activity.latitude));
        addProductSeries.setLongitude(String.valueOf(activity.longitude));
        addProductSeries.setDeviceId(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        addProductSeries.setAppVersion(Constants.APP_VERSION);
        addProductSeries.setRemarks(activity.getBinding().remarksedt.getText().toString());
        return addProductSeries;
    }

    private void logToUser(boolean isError, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

    public void show_ProSeriesSuccessDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View pro_seriessuccess_popup = inflater.inflate(R.layout.dialog_addproductseries_success, null);
        IntializeRegSuccess_ViewPopup(pro_seriessuccess_popup);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(pro_seriessuccess_popup);
        builder_DialogProSeriesSuccess = builder.create();
        builder_DialogProSeriesSuccess.show();
        builder_DialogProSeriesSuccess.setCancelable(false);
        builder_DialogProSeriesSuccess.setCanceledOnTouchOutside(false);
        activity.getBinding().productseriestxt.setText("");
        activity.getBinding().pointsedt.setText("");
        activity.getBinding().remarksedt.setText("");
    }

    private void IntializeRegSuccess_ViewPopup(View regsuccess_popup) {

        submit = (Button) regsuccess_popup.findViewById(R.id.seriessucess_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder_DialogProSeriesSuccess.cancel();
                GetAllProductSeries();
            }
        });
    }

    public void showProductSeries_SubmissionAlert() {
        if (commonFunctions.isNetworkConnected(activity)) {
            if (validateProductSeriesform()) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity,R.style.alertdialog);
                alertDialogBuilder.setMessage("Are you sure want to add this product series !!");
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SubmitProductSeries();
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
        } else {
            commonFunctions.showInternetAlert(activity,1);
        }
    }
    private boolean validateProductSeriesform() {
        boolean valid = true;
        if (activity.getBinding().productseriestxt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().productseriestxt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter product series !!", false, activity);
        }
        else if (activity.getBinding().productseriestxt.getText().toString().length()<8) {
            valid = false;
            activity.getBinding().productseriestxt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Product series should be 8 characters  !!", false, activity);
        }
        else if (activity.getBinding().pointsedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().pointsedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter points !!", false, activity);
        }
        else if (activity.getBinding().pointsedt.getText().toString().equals("0")) {
            valid = false;
            activity.getBinding().pointsedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Points should be greater than 0 !!", false, activity);
        }
        else if (activity.getBinding().remarksedt.getText().toString().equals("")) {
            valid = false;
            activity.getBinding().remarksedt.requestFocus();
            showsoftKeyBoard(activity);
            new CommonFunctions().showSnackBar("Please enter remarks !!", false, activity);
        }
        return valid;
    }

    public void showsoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void SubmitProductSeries() {
        if (commonFunctions.isNetworkConnected(activity)) {
            AddProductSeries(getAddProductSeries());
        } else {
            commonFunctions.showMessage(activity, "Alert", "Please check your internet connection !!");
        }
    }

    public void AddProductSeries(AddProductSeries addProductSeries) {
        SharedPref sharedPref=new SharedPref(activity,"");
        String token=sharedPref.get("Token");
        activity.progressDialog.setMessage("Communicating from server");
        interactor.setShowProgress();
        String input = new Gson().toJson(addProductSeries);
        Call<AddProductSeries> call = serviceGenerator.getService().AddProductSeries(addProductSeries,"Bearer "+token);
        call.enqueue(new Callback<AddProductSeries>() {
            @Override
            public void onResponse(Call<AddProductSeries> call, Response<AddProductSeries> response) {
                try {
                    interactor.setHideProgress();
                    AddProductSeries addProductSeries_res = response.body();
                    if (response.code() == 201) {
                        activity.runOnUiThread(() -> logToUser(false, "product series added successfully"));
                        show_ProSeriesSuccessDialog();
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
            public void onFailure(Call<AddProductSeries> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void  IntializeListenersSearchEditText()
    {
        EvaluateNoRecords();
        activity.editsearch.setHint("Enter Series to Search");
        activity.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    filtertext(activity.editsearch.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(activity, ProductScannedActivity.class);
                    activity.startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    filtertext(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void EvaluateNoRecords()
    {
        if (activity.SearchListResponseData != null) {
            if (activity.SearchListResponseData.size() != 0) {
                activity.getBinding().recyclerViewLytAp.setVisibility(View.VISIBLE);
                activity.getBinding().norecordheadingSeries.setVisibility(View.GONE);
            } else {
                activity.getBinding().recyclerViewLytAp.setVisibility(View.GONE);
                activity.getBinding().norecordheadingSeries.setVisibility(View.VISIBLE);
            }
        } else {
            activity.getBinding().recyclerViewLytAp.setVisibility(View.GONE);
            activity.getBinding().norecordheadingSeries.setVisibility(View.VISIBLE);
        }
    }

    private void setDataInRecyclerView() {
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(activity);
        activity.recyclerView.setLayoutManager(gridLayoutManager);
        activity.recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        activity.searchUserListAdapter = new SearchUserListAdapter(activity,  activity.SearchListResponseData,activity.feild_Names,activity.feild_Labels,2);
        activity.recyclerView.setAdapter(activity.searchUserListAdapter);
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
                       if (productSeriesList != null) {
                           if (productSeriesList.size() != 0) {
                               activity.SearchListResponseDataActual = productSeriesList;
                               filtertext(activity.editsearch.getText().toString());
                           }
                       }
                   }
                   else if (response.code()==404) {
                       String response_error = response.errorBody().string();
                       APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                       activity.getBinding().norecordheadingSeries.setText(apiErrorResponse.getMessage());
                   }
                   else if (response.code() == 409 ) {
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
            public void onFailure(Call<List<AddProductSeries>> call, Throwable t) {
                interactor.setHideProgress();
                interactor.setServerError();
                Toast.makeText(activity, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void filtertext(String charText) throws NoSuchFieldException, IllegalAccessException {
        charText = charText.toLowerCase(Locale.getDefault());
        activity.SearchListResponseData.clear();
        if (charText.length() == 0) {
            activity.SearchListResponseData.addAll(activity.SearchListResponseDataActual);
        }
        else if (charText.equals("")) {
            activity.SearchListResponseData.addAll(activity.SearchListResponseDataActual);
        }
        else {
            for (Object wp : activity.SearchListResponseDataActual) {
                Field fields = wp.getClass().getDeclaredField(activity.feild_Names[0]);
                fields.setAccessible(true);
                Object fieldValue = fields.get(wp);
                if (fieldValue.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    activity.SearchListResponseData.add(wp);
                }
            }
        }
        activity.searchUserListAdapter.notifyDataSetChanged();
        if (activity.SearchListResponseData.size() == 0) {
            activity.getBinding().norecordheadingSeries.setVisibility(View.VISIBLE);
        } else {
            activity.getBinding().norecordheadingSeries.setVisibility(View.GONE);
        }
    }
}
