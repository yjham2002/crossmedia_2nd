package com.ccmheaven.tube.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.RKclassichaeven.tube.CategoryActivity;
import com.RKclassichaeven.tube.MultiCategoryActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.RKclassichaeven.tube.models.CategoryBox;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.pub.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import comm.SimpleCall;

public class TopMenuView extends LinearLayout {

	private View view;
//    private LinearLayout llyLank, llyCategory, llySearch;
    private TabLayout tab;
//    private LinearLayout llyLank, llyCategory, llySearch, llyMyList;
//    private ImageView rank, category, search, myList;
//    private ImageView rank, category, search;
    private TextView tvRank, tvCategory, tvSearch, tvMyList;
    private ButtonListener buttonListener = new ButtonListener();

    private static CategoryBox categoryCurrent;
    private static HashMap<Integer, CategoryBox> cateMap = new HashMap<>();
    private static boolean isLoaded = false;
    private static int length = 0;

    private void addTabs(){
        for(int e = 0; e < length; e++){
            CategoryBox categoryBox = cateMap.get(e);
            TabLayout.Tab tabItem = tab.newTab();
            categoryBox.setManageNo(e);
            tabItem.setText(categoryBox.getAlias());
            tab.addTab(tabItem);
        }
    }

    private void refreshCurrent(CategoryBox categoryBox){
        if(cateMap.size() - 1 < categoryBox.getManageNo()) return;
        tab.getTabAt(categoryBox.getManageNo()).select();
    }

    /**
     * @param context
     * @param attrs
     */
    public TopMenuView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_top_menu, this);
//        llyLank = (LinearLayout) view.findViewById(R.id.lly_tab1);
//        llyCategory = (LinearLayout) view.findViewById(R.id.lly_tab2);
//        llySearch = (LinearLayout) view.findViewById(R.id.lly_tab3);

        tab = view.findViewById(R.id.tabs);

        if(!isLoaded){
            SimpleCall.getHttpJson(Constants.API_CATEGORY_HIER, new HashMap<String, Object>(), new SimpleCall.CallBack() {
                @Override
                public void handle(JSONObject jsonObject) {
                    try {
                        JSONArray result = jsonObject.getJSONArray("result");

                        length = result.length();

                        for(int e = 0; e < result.length(); e++){
                            JSONObject item = result.getJSONObject(e);
                            CategoryBox categoryBox = new CategoryBox();
                            categoryBox.setCg_id(item.getInt("cg_id"));
                            categoryBox.setCg_name(item.getString("cg_name"));
                            categoryBox.setAlias(item.getString("cg_name"));
                            categoryBox.setCg_image_url(item.getString("cg_image_url"));
                            categoryBox.setCg_order(item.getInt("cg_order"));
                            categoryBox.setCg_parent(item.getInt("cg_parent"));
                            categoryBox.setCg_subname(item.getString("cg_subname"));
                            categoryBox.setCg_depth(item.getInt("cg_depth"));

                            if(e == 0) {
                                categoryBox.setAlias("베스트");
                                categoryCurrent = categoryBox;
                            }
                            if(e == 1) categoryBox.setAlias("장르");

                            cateMap.put(e, categoryBox);
                        }

                        isLoaded = true;

                        addTabs();
                        refreshCurrent(categoryCurrent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else{
            addTabs();
            refreshCurrent(categoryCurrent);
        }

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                categoryCurrent = cateMap.get(tab.getPosition());

                switch (tab.getPosition()){
                    case 0:{
                        Intent intent = new Intent(getContext(), RankActivity.class);
                        intent.putExtra("pos", tab.getPosition());
                        getContext().startActivity(intent);
                        overrideTransition();
                        ActivityCompat.finishAffinity((Activity) getContext());
                        ((Activity) getContext()).finish();
                        break;
                    }
                    case 1:{
                        Intent intent = new Intent(getContext(), CategoryActivity.class);
                        getContext().startActivity(intent);
                        overrideTransition();
                        ActivityCompat.finishAffinity((Activity) getContext());
                        ((Activity) getContext()).finish();
                        break;
                    }
                    default:{
                        Intent intent = new Intent(getContext(), MultiCategoryActivity.class);
                        getContext().startActivity(intent);
                        overrideTransition();
                        ActivityCompat.finishAffinity((Activity) getContext());
                        ((Activity) getContext()).finish();
                        break;
                    }
                }
//                Toast.makeText(context, tab.getPosition() + " Tab", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

//        llyMyList = (LinearLayout) view.findViewById(R.id.lly_tab4);

//        rank = (ImageView) view.findViewById(R.id.bottom_image1);
//        category = (ImageView) view.findViewById(R.id.bottom_image2);
//        search = (ImageView) view.findViewById(R.id.bottom_image3);
//        myList = (ImageView) view.findViewById(R.id.bottom_image4);

//        tvRank = (TextView) view.findViewById(R.id.bottom_text1);
//        tvCategory = (TextView) view.findViewById(R.id.bottom_text2);
//        tvSearch = (TextView) view.findViewById(R.id.bottom_text3);
//        tvMyList = (TextView) view.findViewById(R.id.bottom_text4);
        
//        llyLank.setOnClickListener(buttonListener);
//        llyCategory.setOnClickListener(buttonListener);
//        llySearch.setOnClickListener(buttonListener);
//        llyMyList.setOnClickListener(buttonListener);
    }


    public void buttonimg(int index) {
        YoutubePlayerActivity.FromActivity = index;

//        llyLank.setBackgroundResource(R.drawable.rounded_rect);
//        llyCategory.setBackgroundResource(R.drawable.rounded_rect);
//        llySearch.setBackgroundResource(R.drawable.rounded_rect);
//        llyMyList.setBackgroundColor(Color.WHITE);
//        rank.setBackgroundResource(R.drawable.ic_rank_off);
//        category.setBackgroundResource(R.drawable.ic_category_off);
//        search.setBackgroundResource(R.drawable.ic_search_off);
//        myList.setBackgroundResource(R.drawable.ic_my_list_off);
//        tvRank.setTextColor(Color.parseColor("#a9afc0"));
//        tvCategory.setTextColor(Color.parseColor("#a9afc0"));
//        tvSearch.setTextColor(Color.parseColor("#a9afc0"));
//        tvMyList.setTextColor(Color.parseColor("#a9afc0"));

        switch (index) {
            case 1: {
//                llyLank.setBackgroundResource(R.drawable.rounded_rect_selected);
//                rank.setBackgroundResource(R.drawable.ic_rank_on);
//                tvRank.setTextColor(Color.WHITE);
            }
            break;
            case 2: {
//                llyCategory.setBackgroundResource(R.drawable.rounded_rect_selected);
//                category.setBackgroundResource(R.drawable.ic_category_on);
//                tvCategory.setTextColor(Color.WHITE);
            }
            break;
            case 3: {
//                llySearch.setBackgroundResource(R.drawable.rounded_rect_selected);
//                search.setBackgroundResource(R.drawable.ic_search_on);
//                tvSearch.setTextColor(Color.WHITE);
            }
            break;
            case 4: {
//                llyMyList.setBackgroundColor(Color.parseColor("#d41726"));
//                myList.setBackgroundResource(R.drawable.ic_my_list_on);
//                tvMyList.setTextColor(Color.WHITE);
            }
            break;
        }
    }

    public void overrideTransition(){
        ((Activity) getContext()).overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    private class ButtonListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.lly_tab1: {
//                    Intent intent = new Intent(getContext(), RankActivity.class);
//                    getContext().startActivity(intent);
//                    overrideTransition();
//                    ActivityCompat.finishAffinity((Activity) getContext());
//                    ((Activity) getContext()).finish();
//                }
//                break;
//                case R.id.lly_tab2: {
//                    Intent intent = new Intent(getContext(), CategoryActivity.class);
//                    getContext().startActivity(intent);
//                    overrideTransition();
//                    ActivityCompat.finishAffinity((Activity) getContext());
//                    ((Activity) getContext()).finish();
//                }
//                break;
//                case R.id.lly_tab3: {
//                    Intent intent = new Intent(getContext(), SearchActivity.class);
//                    getContext().startActivity(intent);
////                    overrideTransition();
////                    ((Activity) getContext()).finish();
//                }
//                break;
//                case R.id.lly_tab4: {
//                    Intent intent = new Intent(getContext(), MyPageActivity.class);
//                    getContext().startActivity(intent);
//                    overrideTransition();
//                    ((Activity) getContext()).finish();
//                }
//                break;
            }
        }

    }

}
