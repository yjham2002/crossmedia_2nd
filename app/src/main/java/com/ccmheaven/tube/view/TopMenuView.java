package com.ccmheaven.tube.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.RKclassichaeven.tube.CategoryActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.ccmheaven.tube.ads.AdHelper;

public class TopMenuView extends LinearLayout {

	private View view;
    private LinearLayout llyLank, llyCategory, llySearch;
//    private LinearLayout llyLank, llyCategory, llySearch, llyMyList;
//    private ImageView rank, category, search, myList;
//    private ImageView rank, category, search;
    private TextView tvRank, tvCategory, tvSearch, tvMyList;
    private ButtonListener buttonListener = new ButtonListener();

    /**
     * @param context
     * @param attrs
     */
    public TopMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_top_menu, this);
        llyLank = (LinearLayout) view.findViewById(R.id.lly_tab1);
        llyCategory = (LinearLayout) view.findViewById(R.id.lly_tab2);
        llySearch = (LinearLayout) view.findViewById(R.id.lly_tab3);
//        llyMyList = (LinearLayout) view.findViewById(R.id.lly_tab4);

//        rank = (ImageView) view.findViewById(R.id.bottom_image1);
//        category = (ImageView) view.findViewById(R.id.bottom_image2);
//        search = (ImageView) view.findViewById(R.id.bottom_image3);
//        myList = (ImageView) view.findViewById(R.id.bottom_image4);

        tvRank = (TextView) view.findViewById(R.id.bottom_text1);
        tvCategory = (TextView) view.findViewById(R.id.bottom_text2);
        tvSearch = (TextView) view.findViewById(R.id.bottom_text3);
//        tvMyList = (TextView) view.findViewById(R.id.bottom_text4);
        
        llyLank.setOnClickListener(buttonListener);
        llyCategory.setOnClickListener(buttonListener);
        llySearch.setOnClickListener(buttonListener);
//        llyMyList.setOnClickListener(buttonListener);
    }


    public void buttonimg(int index) {
        YoutubePlayerActivity.FromActivity = index;

        llyLank.setBackgroundResource(R.drawable.rounded_rect);
        llyCategory.setBackgroundResource(R.drawable.rounded_rect);
        llySearch.setBackgroundResource(R.drawable.rounded_rect);
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
                llyLank.setBackgroundResource(R.drawable.rounded_rect_selected);
//                rank.setBackgroundResource(R.drawable.ic_rank_on);
//                tvRank.setTextColor(Color.WHITE);
            }
            break;
            case 2: {
                llyCategory.setBackgroundResource(R.drawable.rounded_rect_selected);
//                category.setBackgroundResource(R.drawable.ic_category_on);
//                tvCategory.setTextColor(Color.WHITE);
            }
            break;
            case 3: {
                llySearch.setBackgroundResource(R.drawable.rounded_rect_selected);
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
                case R.id.lly_tab1: {
                    Intent intent = new Intent(getContext(), RankActivity.class);
                    getContext().startActivity(intent);
                    overrideTransition();
                    ((Activity) getContext()).finish();
                }
                break;
                case R.id.lly_tab2: {
                    Intent intent = new Intent(getContext(), CategoryActivity.class);
                    getContext().startActivity(intent);
                    overrideTransition();
                    ((Activity) getContext()).finish();
                }
                break;
                case R.id.lly_tab3: {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    getContext().startActivity(intent);
//                    overrideTransition();
//                    ((Activity) getContext()).finish();
                }
                break;
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
