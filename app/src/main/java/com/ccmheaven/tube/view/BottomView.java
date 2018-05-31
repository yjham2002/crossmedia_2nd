package com.ccmheaven.tube.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.RKclassichaeven.tube.CategoryActivity;
import com.RKclassichaeven.tube.MyPageActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.ccmheaven.tube.ads.AdHelper;

public class BottomView extends LinearLayout {

	private View view;
    private LinearLayout llAdview;
    private LinearLayout llyLank, llyCategory, llySearch;
//    private LinearLayout llyLank, llyCategory, llySearch, llyMyList;
//    private ImageView rank, category, search, myList;
//    private ImageView rank, category, search;
    private TextView tvRank, tvCategory, tvSearch, tvMyList;
	private Activity activity;
	
	AdHelper adHelper;
	
	/**
	 * @param activity
	 */
	public void setActivity(Activity activity){
		this.activity = activity;
		if(this.activity != null){
			adHelper = new AdHelper(this.activity);
		}
	}
	
    /**
     * @param context
     * @param attrs
     */
    public BottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_bottom, this);
        // ad
        llAdview = (LinearLayout) view.findViewById(R.id.llAdview);
    }

    public void addAdView(){
    	Log.d("dev", "BottomView.addAdView() entered");
    	if(adHelper != null && llAdview != null){
    		Log.d("dev", "BottomView.addAdView() call adAdView()");
    		adHelper.addAdView(llAdview);
    	}
    }

}
