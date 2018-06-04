package com.ccmheaven.tube.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import com.RKclassichaeven.tube.LogoActivity;
import com.RKclassichaeven.tube.MyPageActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.YoutubePlayerActivity;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ImageLoader;
import com.ccmheaven.tube.pub.ListInfo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import bases.imageTransform.RoundedTransform;

public class ListviewAdapter extends BaseAdapter implements OnScrollListener {
    private String path = Environment.getExternalStorageDirectory().getPath();
    private Context context;
    private List<ListInfo> list;
    private ListView listview;
    private Handler handler;
    private boolean down;
    private String download;
    private String audio;
    private ImageLoader asyncImageLoader;

    public ListviewAdapter(Context context, List<ListInfo> list, ListView listview, Handler handler, boolean down) {
        this.list = list;
        this.down = down;
        this.context = context;
        this.listview = listview;
        this.handler = handler;
        listview.setOnScrollListener(this);
        // imageLoader = new LoadImage();
        asyncImageLoader = new ImageLoader(context);
        InitConfig(context);
    }

    private void InitConfig(Context context) {
        SharedPreferences edit = context.getSharedPreferences(LogoActivity.CONFIG_NAME, Context.MODE_PRIVATE);
        download = edit.getString("download", "Y");
        audio = edit.getString("audio", "Y");
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

    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        HostView hostview = null;
        if (arg1 == null) {
            hostview = new HostView();
            arg1 = LayoutInflater.from(context).inflate(R.layout.item_listview_adapter, null);
            hostview.image = (ImageView) arg1.findViewById(R.id.imageView1);
            hostview.text = (TextView) arg1.findViewById(R.id.textView1);
            hostview.text2 = (TextView) arg1.findViewById(R.id.textView2);
            hostview.text3 = (TextView) arg1.findViewById(R.id.textView3);
            hostview.down = (ImageView) arg1.findViewById(R.id.imageView2);
            hostview.play = (ImageView) arg1.findViewById(R.id.imageView3);
            hostview.remove = (ImageView) arg1.findViewById(R.id.imageView4);
            arg1.setTag(hostview);
        } else {
            hostview = (HostView) arg1.getTag();
        }

        if (listview.getId() == R.id.video_listView1) {
            hostview.remove.setVisibility(View.VISIBLE);
            int index = (YoutubePlayerActivity.index == 0) ? listview.getCount() - 1 : YoutubePlayerActivity.index - 1;
            if (arg0 == index) {
                arg1.setBackgroundColor(context.getResources().getColor(R.color.item_on));
            } else {
                arg1.setBackgroundColor(Color.WHITE);
            }
        } else {
            hostview.remove.setVisibility(View.GONE);
            if (listview.isItemChecked(arg0)) {
                arg1.setBackgroundColor(context.getResources().getColor(R.color.item_on));
            } else {
                arg1.setBackgroundColor(Color.WHITE);
            }
        }
        File file = new File(path + "/gospel/down/" + list.get(arg0).getArtistName() + "_" + list.get(arg0).getVideoName() + ".mp4");

        // "-"로 나눔
        String txt_tmp = list.get(arg0).getVideoName();
        int index = txt_tmp.indexOf("-");
        if (index > 0) {
            String[] txt_tmp2 = txt_tmp.split("-");
            hostview.text.setText(txt_tmp2[0].trim());
            hostview.text2.setText(txt_tmp2[1].trim());
        } else {
            hostview.text.setText(txt_tmp);
            hostview.text2.setText("");
        }

        hostview.text3.setText(list.get(arg0).getRuntime());

        /**
         * This Image-Loading Snippet is no longer available - PickleCode
         */
        // String imageUrl = list.get(arg0).getImageUrl();
        // imageLoader.addTask(imageUrl, hostview.image);
        // imageLoader.doTask();
        // Load the image and set it on the ImageView

//        if (file.exists() && new File(path + "/gospel/down/imgdata/"
//                + list.get(arg0).getArtistName() + "_"
//                + list.get(arg0).getVideoName() + ".jpg").exists()) {
//            hostview.image.setImageURI(Uri.parse(path + "/gospel/down/imgdata/"
//                    + list.get(arg0).getArtistName() + "_"
//                    + list.get(arg0).getVideoName() + ".jpg"));
//        } else {
//            asyncImageLoader.DisplayImage(list.get(arg0).getImageUrl(),
//                    hostview.image);
//        }

        if(list.get(arg0).getImageUrl() != null && !list.get(arg0).getImageUrl().trim().equals("")){
            Picasso
                    .get()
                    .load(list.get(arg0).getImageUrl())
                    .centerCrop()
                    .resize(100, 100)
                    .placeholder(R.drawable.icon_hour_glass)
                    .transform(new RoundedTransform(0, 0)).into(hostview.image);
        }

        if (file.exists()) {
            hostview.down.setImageResource(R.drawable.bdownend);
        } else {
            hostview.down.setImageResource(R.drawable.bdown);
        }

        if (download != null && download.equals("Y") && down) {
            hostview.down.setVisibility(View.VISIBLE);
        } else {
            hostview.down.setVisibility(View.GONE);
        }

        if (audio != null && audio.equals("Y")) {
            hostview.play.setVisibility(View.VISIBLE);
        } else {
            hostview.play.setVisibility(View.GONE);
        }

        //유튜브 플레이어일 경우엔 재생버튼 안보이게하고 x 버튼 보이게 함. 리스트를 클릭하면 재생되게 함 - 20150127
//        if (YoutubePlayerActivity.instance == null) {
//            hostview.play.setVisibility(View.VISIBLE);
//            //hostview.remove.setVisibility(View.GONE);
//        } else {
//            hostview.play.setVisibility(View.GONE);
//            //hostview.remove.setVisibility(View.GONE);
//        }

        //마이페이지에서의 처리
        if (MyPageActivity.intance != null) {
            hostview.play.setVisibility(View.GONE);
            hostview.remove.setVisibility(View.VISIBLE);
        }

        hostview.remove.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.v("remove", "here : 클릭");
//                if (YoutubePlayerActivity.instance != null) {
//                    Message msg = Message.obtain();
//                    msg.what = YoutubePlayerActivity.REMOVE_ITEM;
//                    msg.obj = arg0;
//                    handler.sendMessage(msg);
//                } else if (MyPageActivity.instance != null) {
//                    Message msg = Message.obtain();
//                    msg.what = MyPageActivity.REMOVE_ITEM;
//                    msg.obj = arg0;
//                    handler.sendMessage(msg);
//
//                }
            }
        });

        hostview.down.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Message msg = Message.obtain();
                msg.what = Constants.START_DOWN;
                msg.obj = list.get(arg0);
                handler.sendMessage(msg);
            }
        });
        hostview.play.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
//                if (YoutubePlayerActivity.instance == null) {
//                    if (YoutubePlayerActivity.list == null) {
//                        YoutubePlayerActivity.list = new ArrayList<ListInfo>();
//                    } else {
//                        YoutubePlayerActivity.list.clear();
//                    }
//                    Intent intent = new Intent(context, YoutubePlayerActivity.class);
//                    YoutubePlayerActivity.list.add(list.get(arg0));
//                    intent.putExtra("code", list.get(arg0).getVideoCode());
//                    context.startActivity(intent);
//                } else {
//                    //20150127 - 플레이버튼 안나오게하고 x 버튼을 넣음
//                    //Message msg = Message.obtain();
//                    //msg.what = YoutubePlayerActivity.VIDEOPLAY;
//                    //msg.obj = arg0;
//                    //handler.sendMessage(msg);
//                }
                // if (VideoActivity.instance == null) {
                // if (VideoActivity.list == null) {
                // VideoActivity.list = new ArrayList<ListInfo>();
                // } else {
                // VideoActivity.list.clear();
                // }
                // Intent intent = new Intent(context, VideoActivity.class);
                // VideoActivity.list.add(list.get(arg0));
                // intent.putExtra("code", list.get(arg0).getVideoCode());
                // context.startActivity(intent);
                // } else {
                // Message msg = Message.obtain();
                // msg.what = VideoActivity.VIDEOPLAY;
                // msg.obj = arg0;
                // handler.sendMessage(msg);
                // }
            }
        });
        return arg1;
    }

    private class HostView {
        ImageView image;
        TextView text, text2, text3;
        ImageView down, play, remove;
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
