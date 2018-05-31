package com.ccmheaven.tube.adapter;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.ImageLoader;

import java.util.List;

//import android.widget.LinearLayout;

public class CategoryListViewAdapter extends BaseAdapter implements
		OnScrollListener {
	private String path = Environment.getExternalStorageDirectory().getPath();
	private Context context;
	private List<CategoryListInfo> list;
	private ImageLoader imageLoader;
	private ListView listview;
	private Handler handler;

	public CategoryListViewAdapter(Context context,
			List<CategoryListInfo> list, ListView listview, Handler handler) {
		this.list = list;
		this.context = context;
		this.listview = listview;
		this.handler = handler;
		listview.setOnScrollListener(this);
		imageLoader = new ImageLoader(context);
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		HostView hostview = null;
		TextView textview;
		ImageView imageview1;
		
		if (arg1 == null) {
			hostview = new HostView();
			
			arg1 = LayoutInflater.from(context).inflate(
					R.layout.item_listview_category, null, true);
			hostview.textview = (TextView) arg1.findViewById(R.id.textView1);
			hostview.imageView1 = (ImageView) arg1.findViewById(R.id.imageView1);
			
			arg1.setTag(hostview);
		} else {
			//textview = (TextView) arg1.getTag();
			hostview = (HostView) arg1.getTag();
		}
		hostview.textview.setText(list.get(arg0).getName());
		imageLoader.DisplayImage(list.get(arg0).getImageUrl(), hostview.imageView1);		
		
				
		return arg1;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}
	
	/*
	private class HostView {
		LinearLayout lin;
		TextView textview;
	}
	*/
	private class HostView {
		ImageView imageView1;
		TextView textview;
	}
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;

		case OnScrollListener.SCROLL_STATE_FLING:
			break;

		case OnScrollListener.SCROLL_STATE_IDLE:
			break;

		}
	}

}
