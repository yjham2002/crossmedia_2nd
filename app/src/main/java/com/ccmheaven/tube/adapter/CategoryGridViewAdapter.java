package com.ccmheaven.tube.adapter;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

import bases.imageTransform.RoundedTransform;

//import android.widget.LinearLayout;

public class CategoryGridViewAdapter extends BaseAdapter {
    private String path = Environment.getExternalStorageDirectory().getPath();
    private Context context;
    private List<CategoryListInfo> list;
    private ImageLoader imageLoader;
    private GridView gridview;
    private Handler handler;
    private boolean isSlim = false;

    public CategoryGridViewAdapter(Context context,
                                   List<CategoryListInfo> list, GridView gridview, Handler handler) {
        this.list = list;
        this.context = context;
        this.gridview = gridview;
        this.handler = handler;
        imageLoader = new ImageLoader(context);
        this.isSlim = false;
    }

    public CategoryGridViewAdapter(Context context,
                                   List<CategoryListInfo> list, GridView gridview, Handler handler, boolean isSlim) {
        this.list = list;
        this.context = context;
        this.gridview = gridview;
        this.handler = handler;
        imageLoader = new ImageLoader(context);
        this.isSlim = isSlim;
    }

    public boolean isSlim() {
        return isSlim;
    }

    public void setSlim(boolean slim) {
        isSlim = slim;
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

            if(isSlim){
                arg1 = LayoutInflater.from(context).inflate(R.layout.item_gridview_category_slim, null, true);
            }else{
                arg1 = LayoutInflater.from(context).inflate(R.layout.item_gridview_category, null, true);
            }

            hostview.m_tvName = (TextView) arg1.findViewById(R.id.tv_name);
            hostview.m_ivPhoto = (ImageView) arg1.findViewById(R.id.iv_photo);

            arg1.setTag(hostview);
        } else {
            //textview = (TextView) arg1.getTag();
            hostview = (HostView) arg1.getTag();
        }

        hostview.m_tvName.setText(list.get(arg0).getName());

        /**
         * This Image-Loading Snippet is no longer available - PickleCode
         */
//        imageLoader.DisplayImage(list.get(arg0).getImageUrl(), hostview.m_ivPhoto);

        if(isSlim){
            hostview.m_ivPhoto.setImageResource(R.drawable.ico_hour_thin);
        }

        if(list.get(arg0).getImageUrl() == null || list.get(arg0).getImageUrl().trim().equals("")){
            list.get(arg0).setImageUrl("http://null");
        }
        Log.e("sizeInfo", hostview.m_ivPhoto.getHeight() + "/" + hostview.m_ivPhoto.getMeasuredHeight() + "/" + hostview.m_ivPhoto.getWidth() + "/" + hostview.m_ivPhoto.getMeasuredWidth());

        int ht = 320;
        int rad = 17;
        int placeHolder = R.drawable.icon_hour_glass_no_round;
        if(isSlim) {
            ht = 190;
            rad = 12;
            placeHolder = R.drawable.ico_hour_thin;
        }

        Picasso
                .get()
                .load(list.get(arg0).getImageUrl())
                .centerCrop()
                .resize(400, ht)
                .placeholder(placeHolder)
                .transform(new RoundedTransform(rad, 0, true, true, false, false)).into(hostview.m_ivPhoto);

        return arg1;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /*
    private class HostView {
        LinearLayout lin;
        TextView textview;
    }
    */
    private class HostView {
        ImageView m_ivPhoto;
        TextView m_tvName;
    }

}
