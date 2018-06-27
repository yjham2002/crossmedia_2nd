package com.ccmheaven.tube.view;

import com.RKclassichaeven.tube.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class CenterView extends LinearLayout {
	private View view;
	private ImageButton select, play, favor;

	public CenterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		view = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.view_center, this);
//		select = (ImageButton) view.findViewById(R.id.center_button1);
		play = (ImageButton) view.findViewById(R.id.center_button2);
		favor = (ImageButton) view.findViewById(R.id.center_button5);

	}

	public void changeBackground() {
//		page.setImageResource(R.drawable.removefavorite_03);
	}

	public void InitButtonListener(OnClickListener listener) {
//		select.setOnClickListener(listener);
		play.setOnClickListener(listener);
		favor.setOnClickListener(listener);
	}

}
