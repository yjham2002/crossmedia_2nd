package com.ccmheaven.tube.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.RKclassichaeven.tube.CategoryChildActivity;
import com.RKclassichaeven.tube.LogoActivity;
import com.RKclassichaeven.tube.MyPageActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.ccmheaven.tube.db.MyMusicDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ImageLoader;
import com.ccmheaven.tube.pub.ListInfo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import bases.imageTransform.RoundedTransform;

public class CommonListviewAdapter extends BaseAdapter implements OnScrollListener {
    private String path = Environment.getExternalStorageDirectory().getPath();
    private Context context;
    private List<ListInfo> list;
    private ListView listview;
    private MyMusicDB myMusicDB;
    private Handler handler;
    private String download;
    private String audio;
    private ImageLoader asyncImageLoader;

    public CommonListviewAdapter(Context context, List<ListInfo> list, ListView listview, Handler handler) {
        this.list = list;
        this.context = context;
        this.listview = listview;
        this.handler = handler;
        myMusicDB = new MyMusicDB(context);
        listview.setOnScrollListener(this);
        // imageLoader = new LoadImage();
        asyncImageLoader = new ImageLoader(context);
        InitConfig(context);
    }

    private void InitConfig(Context context) {
        SharedPreferences edit = context.getSharedPreferences(LogoActivity.CONFIG_NAME, Context.MODE_PRIVATE);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    public long getItemId(int arg0) {
        Log.e("CMAdapter", "getItemId : " + arg0);
        return arg0;
    }

    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        HostView hostview = null;
        if (arg1 == null) {
            hostview = new HostView();
            arg1 = LayoutInflater.from(context).inflate(R.layout.item_listview_rank, null);
            hostview.m_tvNumber = (TextView) arg1.findViewById(R.id.tv_number);
            hostview.m_ivPhoto = (ImageView) arg1.findViewById(R.id.iv_photo);
            hostview.m_tvRunTime = (TextView) arg1.findViewById(R.id.tv_run_time);
            hostview.m_tvName1 = (TextView) arg1.findViewById(R.id.tv_name);
            hostview.spin = arg1.findViewById(R.id.spin_kit);
            hostview.m_tvCategory = (TextView) arg1.findViewById(R.id.tv_category);
//            hostview.m_tvViews = (TextView) arg1.findViewById(R.id.tv_views);
            hostview.m_ivStar = (ImageView) arg1.findViewById(R.id.iv_star);
            arg1.setTag(hostview);
        } else {
            hostview = (HostView) arg1.getTag();
        }
        hostview.m_nPosition = arg0;

        int index = (YoutubePlayerActivity.index == 0) ? listview.getCount() - 1 : YoutubePlayerActivity.index - 1;
        if (arg0 == index) {
            arg1.setBackgroundColor(context.getResources().getColor(R.color.item_on));
        } else {
            arg1.setBackgroundColor(Color.WHITE);
        }
//        File file = new File(path + "/gospel/down/" + list.get(arg0).getArtistName() + "_" + list.get(arg0).getVideoName() + ".mp4");

        if (listview.isItemChecked(arg0)) {
//            arg1.findViewById(R.id.rly_back).setBackgroundColor(Color.parseColor("#eff0f5"));
//            arg1.findViewById(R.id.rly_back).setBackgroundColor(context.getResources().getColor(R.color.selectedList));
            if(context instanceof CategoryChildActivity){
                arg1.findViewById(R.id.rly_back).setBackgroundColor(context.getResources().getColor(R.color.listNewCheck));
            }else{
                arg1.findViewById(R.id.rly_back).setBackgroundColor(context.getResources().getColor(R.color.listNewCheck));
            }
            if(context instanceof YoutubePlayerActivity){
                hostview.spin.setVisibility(View.VISIBLE);
            }
        } else {
            arg1.findViewById(R.id.rly_back).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            hostview.spin.setVisibility(View.INVISIBLE);
        }

        // 랭킹 번호 현시
        hostview.m_tvNumber.setText("" + (arg0 + 1));
        if(context instanceof RankActivity && arg0 < 3){
            hostview.m_tvNumber.setTextColor(context.getResources().getColor(R.color.numberHighlight));
        }else{
            hostview.m_tvNumber.setTextColor(context.getResources().getColor(R.color.defRank));
        }

        // "-"로 나눔
//        String txt_tmp = list.get(arg0).getVideoName();
//        index = txt_tmp.indexOf("-");

        /**
         * PickleCode - Displaying Contents modified
         */
//        if (index > 0) {
//            String[] txt_tmp2 = txt_tmp.split("-");
//            hostview.m_tvName1.setText(txt_tmp2[0].trim());
//            hostview.m_tvCategory.setText(txt_tmp2[1].trim());
//        } else {
//            hostview.m_tvName1.setText(txt_tmp);
//            hostview.m_tvCategory.setText("");
//        }
        hostview.m_tvName1.setText(list.get(arg0).getVideoName());
        hostview.m_tvCategory.setText(list.get(arg0).getArtistName());

//        hostview.m_tvViews.setText(makeMoneyType(list.get(arg0).getViews()) + " VIEWS");

        hostview.m_tvRunTime.setText(list.get(arg0).getRuntime());

        // String imageUrl = list.get(arg0).getImageUrl();
        // imageLoader.addTask(imageUrl, hostview.image);
        // imageLoader.doTask();
        // Load the image and set it on the ImageView


        /**
         * This Image-Loading Snippet is no longer available - PickleCode
         */
//        if (file.exists() && new File(path + "/gospel/down/imgdata/"
//                + list.get(arg0).getArtistName() + "_"
//                + list.get(arg0).getVideoName() + ".jpg").exists()) {
//            hostview.m_ivPhoto.setImageURI(Uri.parse(path + "/gospel/down/imgdata/"
//                    + list.get(arg0).getArtistName() + "_"
//                    + list.get(arg0).getVideoName() + ".jpg"));
//        } else {
//            asyncImageLoader.DisplayImage(list.get(arg0).getImageUrl(),
//                    hostview.m_ivPhoto);
//        }

        if(list.get(arg0).getImageUrl() != null && !list.get(arg0).getImageUrl().trim().equals("")) {
            Picasso
                    .get()
                    .load(list.get(arg0).getImageUrl())
                    .centerCrop()
                    .resize(200, 200)
                    .placeholder(R.drawable.icon_hour_glass)
                    .transform(new RoundedTransform(0, 0)).into(hostview.m_ivPhoto);
        }

        if (!this.context.getClass().equals(RankActivity.class)) {
            if (this.context.getClass().equals(YoutubePlayerActivity.class) ||
                    this.context.getClass().equals(MyPageActivity.class)) {
                hostview.m_ivStar.setImageResource(R.drawable.ico_delete);
            }
            hostview.m_tvNumber.setVisibility(View.GONE);
            LinearLayout.LayoutParams llp;
            llp = (LinearLayout.LayoutParams) arg1.findViewById(R.id.rly_photo).getLayoutParams();
            llp.leftMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10, this.context.getResources().getDisplayMetrics());
        }
        if (this.context.getClass().equals(RankActivity.class) ||
                this.context.getClass().equals(SearchActivity.class) ||
                this.context.getClass().equals(CategoryChildActivity.class)) {
            hostview.m_ivStar.setSelected(myMusicDB.getVideoId(list.get(arg0).getVideoId()) > 0);
        }
        hostview.m_ivStar.setOnClickListener(hostview);

        return arg1;
    }

    /**
     * 금액(double)을 금액표시타입(소숫점2자리)으로 변환한다.
     *
     * @param moneyString 금액(double 형)
     * @return 변경된 금액 문자렬
     */
    public static String makeMoneyType(String moneyString) {
        String format = "#,###"/* "#.##0.00" */;
        DecimalFormat df = new DecimalFormat(format);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();

        dfs.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setDecimalFormatSymbols(dfs);

        try {
            return (df.format(Float.parseFloat(moneyString))).toString();
        } catch (Exception e) {
            return moneyString;
        }
    }

    private class HostView implements OnClickListener {
        ImageView m_ivPhoto;
        TextView m_tvNumber, m_tvRunTime, m_tvName1, m_tvCategory, m_tvViews;
        ImageView m_ivStar;
        View spin;
        int m_nPosition;

        @Override
        public void onClick(View v) {
            if (CommonListviewAdapter.this.context.getClass().equals(YoutubePlayerActivity.class) ||
                    CommonListviewAdapter.this.context.getClass().equals(MyPageActivity.class)) {
                Message msg = Message.obtain();
                msg.what = Constants.REMOVE_ITEM;
                msg.obj = m_nPosition;
                handler.sendMessage(msg);
            } else {
                if (myMusicDB.getVideoId(list.get(m_nPosition).getVideoId()) > 0) {
                    myMusicDB.deleteContact(list.get(m_nPosition));
                    m_ivStar.setSelected(false);
                } else {
                    myMusicDB.addContact(list.get(m_nPosition));
                    m_ivStar.setSelected(true);
                }
            }
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
