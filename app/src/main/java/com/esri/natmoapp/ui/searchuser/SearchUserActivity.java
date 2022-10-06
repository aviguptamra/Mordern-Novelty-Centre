package com.esri.natmoapp.ui.searchuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.adapter.SearchUserListAdapter;
import com.esri.natmoapp.databinding.SearchuserBinding;
import com.esri.natmoapp.ui.productscanned.ProductScannedActivity;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends BindingActivity<SearchuserBinding, SearchUserActivityVM>
        implements SearchUserInteractor {

    ListView list;
    RecyclerView recyclerView;
    ImageView search;
    AppCompatAutoCompleteTextView editsearch;
    ProgressDialog progressDialog;
    List SearchListResponseData;
    List SearchListResponseDataActual;
    SearchUserListAdapter searchUserListAdapter;
    TextView Toolbar_Heading;
    ImageView back_searchuser;
    public static String[] feild_Names = {"Username", "Points_Scored", "CreatedDate","User_Organization","UserId"};
    public static String[] feild_Labels = {"Scored Credits", "Last Active Date","Organization Name"};

    @Override
    public SearchUserActivityVM onCreate() {

        SearchListResponseData = (ArrayList) getIntent().getSerializableExtra("list");
        SearchListResponseDataActual = (ArrayList) getIntent().getSerializableExtra("listcopy");

        Toolbar_Heading=(TextView)findViewById(R.id.Search_userHeading);
        search=(ImageView)findViewById(R.id.seachimg);
        editsearch = (AppCompatAutoCompleteTextView) findViewById(R.id.search);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView) ;
        progressDialog=new ProgressDialog(this);
        back_searchuser=findViewById(R.id.backSearch_user);
        back_searchuser.setOnClickListener(view -> onBackPressed());
        return new SearchUserActivityVM(this, this);
    }

    @Override
    public int getVariable()
    {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.searchuser;
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
        Toast.makeText(SearchUserActivity.this,getResources().getString(R.string.server_error),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProductScannedActivity.class);
        startActivity(intent);
        finish();
    }

}
