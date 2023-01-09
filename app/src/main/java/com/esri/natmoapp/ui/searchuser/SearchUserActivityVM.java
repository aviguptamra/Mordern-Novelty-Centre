package com.esri.natmoapp.ui.searchuser;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.natmoapp.adapter.SearchUserListAdapter;
import com.esri.natmoapp.model.UserPoints;
import com.esri.natmoapp.util.SharedPref;
import com.google.android.material.tabs.TabLayout;
import com.stfalcon.androidmvvmhelper.mvvm.activities.ActivityViewModel;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class SearchUserActivityVM extends ActivityViewModel<SearchUserActivity> {

    SearchUserInteractor interactor;

    public SearchUserActivityVM(SearchUserActivity activity, SearchUserInteractor interactor) {
        super(activity);
        this.interactor = interactor;
        SeperateList_OfUsers();
        setDataInRecyclerView(activity.SearchListResponseData);
        IntializeListenersSearchEditText();
    }

    public void SeperateList_OfUsers() {
        try {
            activity.SearchListAllUsers.addAll(activity.SearchListResponseData);
            for (Object wp : activity.SearchListResponseData) {
                Field fields = wp.getClass().getDeclaredField("isActive");
                fields.setAccessible(true);
                Object fieldValue = fields.get(wp);
                if (fieldValue.toString().equals("true")) {
                    activity.SearchListActiveUsers.add(wp);
                } else {
                    activity.SearchListInActiveUsers.add(wp);
                }
            }
            activity.tabLayout.getTabAt(0).setText("All ("+activity.SearchListAllUsers.size()+")");
            activity.tabLayout.getTabAt(1).setText("Active ("+activity.SearchListActiveUsers.size()+")");
            activity.tabLayout.getTabAt(2).setText("InActive ("+activity.SearchListInActiveUsers.size()+")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void IntializeListenersSearchEditText() {
        EvaluateNoRecords();
        activity.Toolbar_Heading.setText("Admin Dashboard");
        activity.editsearch.setHint("Enter User Name to Search");
        activity.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (activity.tabLayout.getSelectedTabPosition() == 0) {
                        filtertext(activity.editsearch.getText().toString(), activity.SearchListAllUsers);
                    }
                    if (activity.tabLayout.getSelectedTabPosition() == 1) {
                        filtertext(activity.editsearch.getText().toString(), activity.SearchListActiveUsers);
                    }
                    if (activity.tabLayout.getSelectedTabPosition() == 2) {
                        filtertext(activity.editsearch.getText().toString(), activity.SearchListInActiveUsers);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        activity.editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    if (activity.tabLayout.getSelectedTabPosition() == 0) {
                        filtertext(activity.editsearch.getText().toString(), activity.SearchListAllUsers);
                    }
                    if (activity.tabLayout.getSelectedTabPosition() == 1) {
                        filtertext(activity.editsearch.getText().toString(), activity.SearchListActiveUsers);
                    }
                    if (activity.tabLayout.getSelectedTabPosition() == 2) {
                        filtertext(activity.editsearch.getText().toString(), activity.SearchListInActiveUsers);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        activity.tabLayout.getTabAt(0).select();
        activity.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int postion = tab.getPosition();
                if (postion == 0) {
                    activity.SearchListResponseData.clear();
                    if (activity.SearchListAllUsers != null) {
                        if (activity.SearchListAllUsers.size() != 0) {
                            activity.SearchListResponseData.addAll(activity.SearchListAllUsers);
                            activity.searchUserListAdapter.notifyDataSetChanged();
                            activity.getBinding().recyclerViewLyt.setVisibility(View.VISIBLE);
                            activity.getBinding().norecordheadingUser.setVisibility(View.GONE);
                        } else {
                            activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                            activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
                        }
                    } else {
                        activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                        activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
                    }
                } else if (postion == 1) {
                    activity.SearchListResponseData.clear();
                    if (activity.SearchListActiveUsers != null) {
                        if (activity.SearchListActiveUsers.size() != 0) {
                            activity.SearchListResponseData.addAll(activity.SearchListActiveUsers);
                            activity.searchUserListAdapter.notifyDataSetChanged();
                            activity.getBinding().recyclerViewLyt.setVisibility(View.VISIBLE);
                            activity.getBinding().norecordheadingUser.setVisibility(View.GONE);

                        } else {
                            activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                            activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
                        }
                    } else {
                        activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                        activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
                    }
                } else if (postion == 2) {
                    activity.SearchListResponseData.clear();
                    if (activity.SearchListInActiveUsers != null) {
                        if (activity.SearchListInActiveUsers.size() != 0) {
                            activity.SearchListResponseData.addAll(activity.SearchListInActiveUsers);
                            activity.searchUserListAdapter.notifyDataSetChanged();
                            activity.getBinding().recyclerViewLyt.setVisibility(View.VISIBLE);
                            activity.getBinding().norecordheadingUser.setVisibility(View.GONE);
                        } else {
                            activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                            activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
                        }
                    } else {
                        activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                        activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
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
    }


    private void EvaluateNoRecords() {
        if (activity.SearchListResponseData != null) {
            if (activity.SearchListResponseData.size() != 0) {
                activity.getBinding().recyclerViewLyt.setVisibility(View.VISIBLE);
                activity.getBinding().norecordheadingUser.setVisibility(View.GONE);
            } else {
                activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
                activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
            }
        } else {
            activity.getBinding().recyclerViewLyt.setVisibility(View.GONE);
            activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
        }
    }

    private void setDataInRecyclerView(List SearchListResponseData) {
        SharedPref sharedPref = new SharedPref(activity, "");
        //int userId = sharedPref.get("UserId", 0);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(activity);
        activity.recyclerView.setLayoutManager(gridLayoutManager);
        activity.recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        activity.searchUserListAdapter = new SearchUserListAdapter(activity, SearchListResponseData, activity.feild_Names, activity.feild_Labels, 1);
        activity.recyclerView.setAdapter(activity.searchUserListAdapter);
        activity.getBinding().recyclerViewLyt.setVisibility(View.VISIBLE);
        activity.getBinding().norecordheadingUser.setVisibility(View.GONE);
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

    public void filtertext(String charText,List SearchListResponseDataActual) throws NoSuchFieldException, IllegalAccessException {
        charText = charText.toLowerCase(Locale.getDefault());
        activity.SearchListResponseData.clear();
        if (charText.length() == 0) {
            activity.SearchListResponseData.addAll(SearchListResponseDataActual);
        } else {
            for (Object wp : SearchListResponseDataActual) {
                Field fields = wp.getClass().getDeclaredField(activity.feild_Names[0]);
                fields.setAccessible(true);
                Object fieldValue = fields.get(wp);

                Field fields1 = wp.getClass().getDeclaredField(activity.feild_Names[3]);
                fields1.setAccessible(true);
                Object fieldValue1 = fields1.get(wp);

                if (fieldValue.toString().toLowerCase(Locale.getDefault()).contains(charText) ||
                        fieldValue1.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    activity.SearchListResponseData.add(wp);
                }
            }
        }
        activity.searchUserListAdapter.notifyDataSetChanged();
        if (activity.SearchListResponseData.size() == 0) {
            activity.getBinding().norecordheadingUser.setVisibility(View.VISIBLE);
        } else {
            activity.getBinding().norecordheadingUser.setVisibility(View.GONE);
        }
    }


}
