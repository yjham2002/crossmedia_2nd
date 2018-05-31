package com.ccmheaven.tube.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.pub.ImageLoader;
import com.ccmheaven.tube.pub.RecommendInfo;
import com.ccmheaven.tube.pub.SettingInfo;

public class SettingListviewAdapter extends BaseAdapter implements OnScrollListener {

    public final int SECTION = 0;
    public final int ETC_ITEM = 1;
    public final int RECOMMEND_ITEM = 2;

    private Context context;
    private SettingInfo info;
    private ListView listview;
    private Handler handler;
    private ImageLoader asyncImageLoader;

    public SettingListviewAdapter(Context context, SettingInfo info, ListView listview, Handler handler) {
        this.info = info;
        this.context = context;
        this.listview = listview;
        this.handler = handler;
        listview.setOnScrollListener(this);
        // imageLoader = new LoadImage();
        asyncImageLoader = new ImageLoader(context);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 3)
            return SECTION;
        else if (position == 1 || position == 2)
            return ETC_ITEM;
        else
            return RECOMMEND_ITEM;
    }

    @Override
    public int getCount() {
        return info.getRecommend().size() + 4;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return this.context.getString(R.string.etc);
        } else if (position == 1) {
            return this.context.getString(R.string.setting_etc_text1);
        } else if (position == 2) {
            return this.context.getString(R.string.setting_etc_text2);
        } else if (position == 3) {
            return this.context.getString(R.string.recommend);
        } else {
            return info.getRecommend().get(position - 4);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HostView hostview = null;

        if (convertView == null) {
            if (getItemViewType(position) == SECTION) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_setting_section, null);
            } else if (getItemViewType(position) == ETC_ITEM) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_setting_etc, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_setting_recommend, null);
                hostview = new HostView();
                hostview.m_ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
                hostview.m_tvSubject = (TextView) convertView.findViewById(R.id.tv_subject);
                hostview.m_tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
                convertView.setTag(hostview);
            }
        } else {
            if (getItemViewType(position) == RECOMMEND_ITEM) {
                hostview = (HostView) convertView.getTag();
            }
        }

        if (getItemViewType(position) == SECTION) {
            TextView tv = (TextView) convertView
                    .findViewById(R.id.tv_title);
            tv.setText((String) getItem(position));
        } else if (getItemViewType(position) == ETC_ITEM) {
            TextView tv = (TextView) convertView.findViewById(R.id.tv_title);
            tv.setText((String) getItem(position));
        } else {
            RecommendInfo recommend = (RecommendInfo) getItem(position);
            asyncImageLoader.DisplayImage(recommend.getImg(), hostview.m_ivPhoto);
            hostview.m_tvSubject.setText(recommend.getSubject());
            hostview.m_tvDescription.setText(recommend.getDescription());
        }

        return convertView;
    }

    private class HostView implements OnClickListener {
        ImageView m_ivPhoto;
        TextView m_tvSubject, m_tvDescription;
        int m_nPosition;

        @Override
        public void onClick(View v) {
        }
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

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
