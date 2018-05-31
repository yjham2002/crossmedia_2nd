package com.ccmheaven.tube.view;

import com.RKclassichaeven.tube.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchButton extends LinearLayout {
	private View view;
	private EditText edittext;
	private TextView button;

	public SearchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		view = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.view_search_searchview, this);
		edittext = view.findViewById(R.id.searchview_editText1);
		button = view.findViewById(R.id.searchview_button1);
	}

	public void setOnEditorActionListener(OnEditorActionListener listener) {
		edittext.setOnEditorActionListener(listener);
	}

	public String getEditText() {
		return edittext.getText().toString().trim();
	}

	public void InitListener(OnClickListener listener) {
		button.setOnClickListener(listener);
	}
}
