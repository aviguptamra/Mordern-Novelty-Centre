package com.esri.natmoapp.ui.introslide;

import androidx.viewpager.widget.ViewPager;

import com.esri.natmoapp.BR;
import com.esri.natmoapp.R;
import com.esri.natmoapp.adapter.The_Slide_items_Pager_Adapter;
import com.esri.natmoapp.databinding.ActvityIntroslideBinding;
import com.esri.natmoapp.model.The_Slide_Items_Model_Class;
import com.esri.natmoapp.util.CommonFunctions;
import com.google.android.material.tabs.TabLayout;
import com.stfalcon.androidmvvmhelper.mvvm.activities.BindingActivity;

import java.util.ArrayList;
import java.util.List;

public class IntroSlideActivity extends BindingActivity<ActvityIntroslideBinding, IntroSlideActivityVM>
        implements IntroSlideInteractor {

    public String imgname="";
    CommonFunctions commonFunctions=new CommonFunctions();
    String imagetype;
    private List<The_Slide_Items_Model_Class> listItems;
    private ViewPager page;
    public TabLayout tabLayout;

    @Override
    public IntroSlideActivityVM onCreate() {
        page = findViewById(R.id.my_pager) ;
        tabLayout = findViewById(R.id.my_tablayout);
        listItems = new ArrayList<>() ;
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.slide1,getResources().getString(R.string.natmoinfo11),getResources().getString(R.string.natmoinfo12)));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.slide2,getResources().getString(R.string.natmoinfo21),getResources().getString(R.string.natmoinfo22)));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.slide3,getResources().getString(R.string.natmoinfo31),getResources().getString(R.string.natmoinfo32)));
        The_Slide_items_Pager_Adapter itemsPager_adapter = new The_Slide_items_Pager_Adapter(this, listItems);
        page.setAdapter(itemsPager_adapter);
        tabLayout.setupWithViewPager(page,true);
        return new IntroSlideActivityVM(this, this);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.actvity_introslide;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
