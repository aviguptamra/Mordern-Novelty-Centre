package com.esri.natmoapp.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.esri.natmoapp.R;
import com.esri.natmoapp.auth.login.LoginActivity;
import com.esri.natmoapp.model.APIErrorResponse;
import com.esri.natmoapp.model.Registration;
import com.esri.natmoapp.server.ServiceGenerator;
import com.esri.natmoapp.ui.userdetails.UserDetailsActivity;
import com.esri.natmoapp.util.CommonFunctions;
import com.esri.natmoapp.util.SharedPref;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserListAdapter extends RecyclerView.Adapter<SearchUserListAdapter.SearchViewHolder> {

    Context context;
    List SearchListResponseData;
    public List SearchListResponseDataSelected = new ArrayList<>();
    String feild_names[], feild_LabelText[];
    int FLAG_SearchOrActivityResult;
    public String mode;
    ViewGroup Viewgrp_parent;

    public SearchUserListAdapter(Context context, List SearchListResponseData, String feild_names[], String feild_LabelText[],
                                 int FLAG_SearchOrActivityResult) {
        this.SearchListResponseData = SearchListResponseData;
        this.context = context;
        this.feild_names = feild_names;
        this.feild_LabelText = feild_LabelText;
        this.FLAG_SearchOrActivityResult = FLAG_SearchOrActivityResult;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlist, parent, false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(view);
        Viewgrp_parent = parent;
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        try {
            if (FLAG_SearchOrActivityResult == 4) {
                holder.name.setText("Redeem Credit -  "+getValuefromFeld(feild_names[0], SearchListResponseData.get(position)).toString().toUpperCase()+" Points");
            } else {
                holder.name.setText(getValuefromFeld(feild_names[0], SearchListResponseData.get(position)).toString().toUpperCase());
            }
            if (getValuefromFeld(feild_names[1], SearchListResponseData.get(position)) != null) {
                holder.Label1Text.setText(getValuefromFeld(feild_names[1], SearchListResponseData.get(position)).toString());
            } else {
                holder.Label1Text.setText("");
            }
            if (getValuefromFeld(feild_names[2], SearchListResponseData.get(position)) != null) {
                holder.Label2Text.setText(getValuefromFeld(feild_names[2], SearchListResponseData.get(position)).toString());
            } else {
                holder.Label2Text.setText("");
            }

            if(FLAG_SearchOrActivityResult==1) {

                if (getValuefromFeld(feild_names[3], SearchListResponseData.get(position)) != null) {
                    holder.Label3Text.setText(getValuefromFeld(feild_names[3], SearchListResponseData.get(position)).toString());
                } else {
                    holder.Label3Text.setText("");
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            GetUserDetails(getValuefromFeld("UserId", SearchListResponseData.get(position)).toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (FLAG_SearchOrActivityResult == 2) {
                holder.deleteimgview.setVisibility(View.VISIBLE);
                holder.deleteimgview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String seriesid = getValuefromFeld(feild_names[0], SearchListResponseData.get(position)).toString().toUpperCase();
                            DeleteWarningMsg(seriesid, position);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                holder.deleteimgview.setVisibility(View.GONE);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void removeCard(int position) {
        SearchListResponseData.remove(position);
        int i = getItemCount();
        notifyDataSetChanged();
        if (getItemCount() == 0) {
            ViewParent view = Viewgrp_parent.getParent();
            LinearLayout linearLayout = (LinearLayout) view;
            linearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (SearchListResponseData != null) {
            return SearchListResponseData.size();
        } else {
            return 0;
        }
        // size of the list items
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView name, Label1Text, Label2Text,Label3Text;
        TextView Label1, Label2,Label3;
        CheckBox checkbox;
        ImageView deleteimgview, tickView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txtname_edt);
            Label1Text = (TextView) itemView.findViewById(R.id.txtLabel1Text);
            Label2Text = (TextView) itemView.findViewById(R.id.txtLabel2Text);
            Label3Text = (TextView) itemView.findViewById(R.id.txtLabel3Text);
            deleteimgview=(ImageView)itemView.findViewById(R.id.img_grp);

            Label1 = (TextView) itemView.findViewById(R.id.txtLabel1);
            Label2 = (TextView) itemView.findViewById(R.id.txtLabel2);
            Label3 = (TextView) itemView.findViewById(R.id.txtLabel3);


            if (FLAG_SearchOrActivityResult == 1) {
                Label1.setText(feild_LabelText[0] + " : ");
                Label2.setText(feild_LabelText[1] + " : ");
                Label3.setText(feild_LabelText[2] + " : ");
                Label3Text.setVisibility(View.VISIBLE);
                Label3.setVisibility(View.VISIBLE);
            } else {
                Label1.setText(feild_LabelText[0] + " : ");
                Label2.setText(feild_LabelText[1] + " : ");
                Label3Text.setVisibility(View.GONE);
                Label3.setVisibility(View.GONE);
            }

        }
    }

    public List getSearchListResponseDataSelected() {
        return SearchListResponseDataSelected;
    }

    public Object getValuefromFeld(String value, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Field fields = obj.getClass().getDeclaredField(value);
        fields.setAccessible(true);
        Object fieldValue = fields.get(obj);
        if (value.toLowerCase().trim().contains("date")) {
            if(fieldValue!=null) {
                if(!fieldValue.equals("")) {
                    String datevalue = fieldValue.toString();
                    String splitdate[] = datevalue.split("T");
                    String finaldate_time = splitdate[0] + " " + splitdate[1].substring(0, 5);
                    return finaldate_time;
                }
            }
        }
        return fieldValue;
    }

    public void DeleteWarningMsg(String serieschar, int position)
    {
        CommonFunctions commonFunctions=new CommonFunctions();
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context,R.style.alertdialog);
        alertDialogBuilder.setMessage("Are you sure want to delete this series !!");
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
                if (commonFunctions.isNetworkConnected((Activity) context)) {
                    DeleteProductSeries(serieschar, position);
                } else {
                    commonFunctions.showMessage((Activity) context, "Alert", "Please check your internet connection !!");
                }
            }
        });
        alertDialogBuilder.show();
    }

    public void DeleteProductSeries(String serieschar,int position) {
        SharedPref sharedPref=new SharedPref(context,"");
        CommonFunctions commonFunctions=new CommonFunctions();
        String token=sharedPref.get("Token");
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Communicating from server");
        progressDialog.show();
        Call<Void> call = new ServiceGenerator().getService().DeleteProductSeries(serieschar,"Bearer "+token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    progressDialog.cancel();
                    if (response.code() == 200 ||response.code() == 204) {
                        Toast.makeText(context, "Series deleted successfully", Toast.LENGTH_LONG);
                        SearchListResponseData.remove(position);
                        notifyDataSetChanged();
                    } else if (response.code() == 409) {
                        String response_error = response.errorBody().string();
                        APIErrorResponse apiErrorResponse = new Gson().fromJson(response_error, APIErrorResponse.class);
                        Toast.makeText(context, apiErrorResponse.getMessage(), Toast.LENGTH_LONG);
                        commonFunctions.showMessage(context, "Alert", apiErrorResponse.getMessage());
                    } else if (response.code() == 401) {
                        commonFunctions.showSessionExpired_Msg((Activity) context);
                    } else {
                        commonFunctions.showInternalError_Msg((Activity) context);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(context, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void GetUserDetails(String userId) {
        CommonFunctions commonFunctions=new CommonFunctions();
        SharedPref sharedPref = new SharedPref(context, "");
        String token = sharedPref.get("Token");
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Communicating from server");
        progressDialog.show();
        Call<Registration> call = new ServiceGenerator().getService().GetUserDetails_ById(userId, "Bearer " + token);
        call.enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {
                progressDialog.cancel();
                Registration registration = response.body();
                if (response.code() == 200) {
                        Intent intent = new Intent(context, UserDetailsActivity.class);
                        intent.putExtra("userregistrationobj", (Serializable) registration);
                        intent.putExtra("Credits", String.valueOf(registration.getUserPoints()+" Points"));
                        context.startActivity(intent);
                        Activity activity=(Activity) context;
                        activity.finish();
                } else if (response.code() == 401) {
                    commonFunctions.showSessionExpired_Msg((Activity) context);
                } else {
                    commonFunctions.showInternalError_Msg((Activity) context);
                }
            }

            @Override
            public void onFailure(Call<Registration> call, Throwable t) {
                progressDialog.cancel();
                Toast.makeText(context, "Unable to get response from server",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}