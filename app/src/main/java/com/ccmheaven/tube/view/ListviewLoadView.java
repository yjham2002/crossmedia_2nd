package com.ccmheaven.tube.view;

import com.RKclassichaeven.tube.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class ListviewLoadView extends LinearLayout {
	public ListviewLoadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.view_listview_loading, this);
	}
}
