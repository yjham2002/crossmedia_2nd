package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.adapter.CommonListviewAdapter;
import com.ccmheaven.tube.db.MyMusicDB;
import com.ccmheaven.tube.db.MyMusicDownDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.CustomDialog;
import com.ccmheaven.tube.pub.ListInfo;
import com.ccmheaven.tube.thread.CategoryChildDataThread;
import com.ccmheaven.tube.thread.DownLoadThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.CenterView;
import com.ccmheaven.tube.view.ListviewLoadView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bases.imageTransform.RoundedTransform;

public class CategoryChildActivity extends Activity {
//    private TextView[] m_tvTab = new TextView[2];
    private ListView listview;
    private CenterView centerView;
    private BottomView bottomview;
//    private TopMenuView topMenuView;

    private List<ListInfo> list = new ArrayList<ListInfo>();
    private MyMusicDB myMusicDB;
    private MyMusicDownDB myMusicDownDB;
    private CommonListviewAdapter listviewAdapter;
    private ProgressDialog loagindDialog;
    private CenterViewListener centerviewlistener = new CenterViewListener();
    private int page;
    private int totalPage;

    private String title;
    private String imgUrl;

    private ImageView thm, left_back, shufflePlay, normalPlay;
    private TextView cateName, cateNum;
    private String cgid;
    private ListviewLoadView listviewLoadView;
    private String orderType;

    // private Button listviewLoad;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorychild);
        myMusicDownDB = new MyMusicDownDB(this);
        Bundle data = getIntent().getExtras();
        if (data != null) {
            cgid = data.getString("cgid");
            title = data.getString("title");
            imgUrl = data.getString("img");
            page = 1;
            myMusicDB = new MyMusicDB(this);
            listviewLoadView = (ListviewLoadView) findViewById(R.id.listviewLoadView1);
            // listviewLoad = (Button) findViewById(R.id.listviewTextview);
//            m_tvTab[0] = (TextView) findViewById(R.id.tv_tab1);
//            m_tvTab[0].setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onTabSelected(0);
//                }
//            });
//            m_tvTab[1] = (TextView) findViewById(R.id.tv_tab2);
//            m_tvTab[1].setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onTabSelected(1);
//                }
//            });

            View header = getLayoutInflater().inflate(R.layout.layout_header_sub, null, false) ;

            listview = (ListView) findViewById(R.id.lv_category);
//            ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.app_name));

            bottomview = (BottomView) findViewById(R.id.bottomView1);
//            topMenuView = findViewById(R.id.topMenuView);
            bottomview.setActivity(this);
            bottomview.addAdView();
            shufflePlay = header.findViewById(R.id.shufflePlay);
            normalPlay = header.findViewById(R.id.normalPlay);

            normalPlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    goNormal();
                }
            });
            shufflePlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    goShuffle();
                }
            });

            left_back = findViewById(R.id.left_back);

            left_back.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            cateName = findViewById(R.id.cateName);
            cateName.setSelected(true);
            cateNum = header.findViewById(R.id.cateNum);

            thm = header.findViewById(R.id.thm);

            cateName.setText(title);

            if(imgUrl != null && !imgUrl.equals("")){
                Picasso
                        .get()
                        .load(imgUrl)
                        .centerCrop()
                        .resize(400, 170)
                        .placeholder(R.drawable.ico_hour_thin_all)
                        .transform(new RoundedTransform(12, 0)).into(thm);
            }

            centerView = (CenterView) findViewById(R.id.center_view);
            centerView.InitButtonListener(centerviewlistener);
            listviewAdapter = new CommonListviewAdapter(this, list, listview,
                    handler);

            listview.addHeaderView(header, null, false);

            listview.setAdapter(listviewAdapter);
            listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listview.setItemsCanFocus(false);
            listview.setOnTouchListener(new ListviewOnTouch());
            listview.setOnScrollListener(new ScrollListener());
            listview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    boolean haveSelect = false;

                    int realPos = arg2 - listview.getHeaderViewsCount();
                    listview.setItemChecked(realPos, !listview.isItemChecked(realPos));
                    listview.setItemChecked(arg2, !listview.isItemChecked(arg2));

                    for (int i = 0; i < listview.getCount() - 1; i++) {
                        if (listview.isItemChecked(i)) {
                            haveSelect = true;
                            break;
                        }
                    }
                    if (haveSelect) {
                        centerView.setVisibility(View.VISIBLE);
                    } else {
                        centerView.setVisibility(View.GONE);
                    }
                    listviewAdapter.notifyDataSetChanged();
                }
            });
//            topMenuView.buttonimg(2);

            onTabSelected(0);
        } else {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void onTabSelected(int index) {
//        if (m_tvTab[index].isSelected())
//            return;
//
//        m_tvTab[index].setSelected(true);
//        m_tvTab[index].setBackgroundColor(Color.WHITE);
//        m_tvTab[1 - index].setSelected(false);
//        m_tvTab[1 - index].setBackgroundColor(Color.parseColor("#eff0f5"));
        page = 1;
        totalPage = 1;
        list.clear();
        listviewAdapter.notifyDataSetChanged();
        listview.clearChoices();

        centerView.setVisibility(View.GONE);
        orderType = (index == 0) ? "popular" : "abc";
        CategoryChildDataThread.startCategoryChildDataThread(handler, list,
                orderType, cgid, page);
        loagindDialog = ProgressDialog.show(this,
                "Connecting", "Loading. Please wait...", true, true);
    }

    public final static int LOADINGBACK = 5000;
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CategoryChildActivity.LOADINGBACK: {
                    int top = Integer.parseInt(msg.obj.toString());
                    // listviewLoad.setPadding(listviewLoad.getPaddingLeft(), top,
                    // listviewLoad.getPaddingRight(),
                    // listviewLoad.getPaddingBottom());
                    // if (listviewLoad.getPaddingTop() <= 1) {
                    // listviewLoad.setVisibility(View.GONE);
                    // }
                }
                break;
                case Constants.INITDATA: {
                    listviewAdapter.notifyDataSetChanged();
                    // if (listviewLoadView.isShown()) {
                    // listview.setSelection(lastItem);
                    // }
                    listviewLoadView.setVisibility(View.GONE);
                    totalPage = msg.arg1;
                    if(list.size() > 0) cateNum.setText(list.get(0).getTotal() + " Songs");
                    else cateNum.setText("0 Songs");
                    Log.d("dev", "listviewLoadView GONE");
                    loagindDialog.dismiss();
                }
                break;
                case Constants.INITDATA_LOSE: {
                    listviewAdapter.notifyDataSetChanged();
                    if (page > 1) {
                        page--;
                    }
                    // if (listviewLoadView.isShown()) {
                    // listview.setSelection(lastItem);
                    // }
                    listviewLoadView.setVisibility(View.GONE);
                    loagindDialog.dismiss();
                }
                break;
                case Constants.DOWNLOADNO: {
                    Toast.makeText(CategoryChildActivity.this,
                            getResources().getText(R.string.download_failed),
                            Toast.LENGTH_SHORT).show();
                }
                break;
                case Constants.START_DOWN: {
                    ListInfo listinfo = (ListInfo) msg.obj;
                    File file = new File(path + "/gospel/down/"
                            + listinfo.getArtistName() + "_"
                            + listinfo.getVideoName() + ".mp4");
                    if (file.exists()) {
                        showDiag(DELETE, listinfo);
                    } else {
                        showDiag(DOWNLOAD, listinfo);
                    }
                }
                break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private String path = Environment.getExternalStorageDirectory().getPath();
    public static final int DOWNLOAD = 0;
    public static final int DELETE = 1;

    public void showDiag(int index, final ListInfo listinfo) {
        switch (index) {
            case DOWNLOAD: {
                String alertTitle = getResources().getText(
                        R.string.Children_nursery_rhymes)
                        + "-" + getResources().getText(R.string.download_file);
                String buttonMessage = "["
                        + listinfo.getVideoName()
                        + "] "
                        + getResources().getText(R.string.Confirm_to_download)
                        + "?\n\n"
                        + getResources().getText(
                        R.string.Download_the_traffic_would_be_used)
                        + "\n\n" + getResources().getText(R.string.save_path)
                        + ": /sdcard/gospel/down/";
                String buttonYes = getResources().getText(R.string.yes).toString();
                String buttonNo = getResources().getText(R.string.no).toString();
                new CustomDialog.Builder(this)
                        .setTitle(alertTitle)
                        .setMessage(buttonMessage)
                        .setPositiveButton(buttonYes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(CategoryChildActivity.this,
                                                DownLoadActivity.class);
                                        startActivity(intent);
                                        DownLoadThread down = new DownLoadThread(
                                                CategoryChildActivity.this, handler, listinfo);
                                        down.start();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(buttonNo,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
            }
            break;
            case DELETE: {
                String alertTitle = getResources().getText(
                        R.string.Children_nursery_rhymes).toString()
                        + "-"
                        + getResources().getText(R.string.file_exists).toString()
                        + "-" + getResources().getText(R.string.remove).toString();
                String buttonMessage = "["
                        + listinfo.getVideoName()
                        + "] "
                        + getResources().getText(R.string.file_already_exists)
                        + " \n\n"
                        + getResources().getText(
                        R.string.Overwrite_the_original_file) + "?";
                String buttonYes = getResources().getText(R.string.yes).toString();
                String buttonNo = getResources().getText(R.string.no).toString();
                new CustomDialog.Builder(CategoryChildActivity.this)
                        .setTitle(alertTitle)
                        .setMessage(buttonMessage)
                        .setPositiveButton(buttonYes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        deleteFile(listinfo);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(buttonNo,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
            }
            break;
        }

    }

    private void deleteFile(ListInfo listinfo) {
        File file = new File(path + "/gospel/down/" + listinfo.getArtistName()
                + "_" + listinfo.getVideoName() + ".mp4");
        file.delete();
        File fileimage = new File(path + "/gospel/down/imgdata/"
                + listinfo.getArtistName() + "_" + listinfo.getVideoName()
                + ".jpg");
        fileimage.delete();
        myMusicDownDB.deleteContact(listinfo);
        listviewAdapter.notifyDataSetChanged();
    }

    private boolean isAllSelect;

    private void goNormal(){
        for (int i = 0; i < listview.getCount() - 1; i++) {
            listview.setItemChecked(i, true);
        }
        List<ListInfo> templist = new ArrayList<ListInfo>();
        for (int i = 0; i < listview.getCount() - 1; i++) {
            if (listview.isItemChecked(i)) {
                templist.add(list.get(i));
            }
        }
        if (!templist.isEmpty()) {
            if(MyApplication.getMediaService() != null){
                MyApplication.getMediaService().setTracks(templist);
            }
            Gson gson = new Gson();
            String strJson=gson.toJson(templist);
            Intent intent = new Intent(CategoryChildActivity.this, YoutubePlayerActivity.class);
            intent.putExtra( "playlist", strJson );
            intent.putExtra("mode", 0);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
        }

        centerView.setVisibility(View.GONE);
        listview.clearChoices();
        listviewAdapter.notifyDataSetChanged();
    }

    private void goShuffle(){
        for (int i = 0; i < listview.getCount() - 1; i++) {
            listview.setItemChecked(i, true);
        }
        List<ListInfo> templist = new ArrayList<ListInfo>();
        for (int i = 0; i < listview.getCount() - 1; i++) {
            if (listview.isItemChecked(i)) {
                templist.add(list.get(i));
            }
        }
        if (!templist.isEmpty()) {
            if(MyApplication.getMediaService() != null){
                MyApplication.getMediaService().setTracks(templist);
            }
            Gson gson = new Gson();
            String strJson=gson.toJson(templist);
            Intent intent = new Intent(CategoryChildActivity.this, YoutubePlayerActivity.class);
            intent.putExtra( "playlist", strJson );
            intent.putExtra("mode", 1);
            startActivity(intent);
            overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
        }

        centerView.setVisibility(View.GONE);
        listview.clearChoices();
        listviewAdapter.notifyDataSetChanged();
    }

    private class CenterViewListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
//				case R.id.center_button1: {
//					if (!isAllSelect) {
//						for (int i = 0; i < listview.getCount(); i++) {
//							listview.setItemChecked(i, true);
//						}
//						isAllSelect = true;
//					} else {
//						for (int i = 0; i < listview.getCount(); i++) {
//							listview.setItemChecked(i, false);
//						}
//						isAllSelect = false;
//					}
//					listviewAdapter.notifyDataSetChanged();
//				}
//				break;
                case R.id.center_button2: {
                    List<ListInfo> templist = new ArrayList<ListInfo>();
                    for (int i = 0; i < listview.getCount(); i++) {
                        if (listview.isItemChecked(i)) {
                            templist.add(list.get(i));
                        }
                    }
                    if (!templist.isEmpty()) {
                        if(MyApplication.getMediaService() != null){
                            MyApplication.getMediaService().setTracks(templist);
                        }
                        Gson gson = new Gson();
                        String strJson=gson.toJson(templist);
                        Intent intent = new Intent(CategoryChildActivity.this, YoutubePlayerActivity.class);
                        intent.putExtra( "playlist", strJson );
                        startActivity(intent);
                        overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
                    }

                    centerView.setVisibility(View.GONE);
                    listview.clearChoices();
                    listviewAdapter.notifyDataSetChanged();
                }
                break;
                case R.id.center_button5: {
                    List<ListInfo> templist = new ArrayList<ListInfo>();

                    boolean empty = true;
                    for (int i = 0; i < listview.getCount(); i++) {
                        if (listview.isItemChecked(i)) {
                            empty = false;
                            myMusicDB.addContact(list.get(i));
                        }
                    }
                    if(empty){
                        Toast.makeText(getApplicationContext(), "보관함에 추가할 곡을 선택하세요.", Toast.LENGTH_LONG).show();
                    }else{
                        listview.clearChoices();
                        listviewAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), "보관함에 추가되었습니다.", Toast.LENGTH_LONG).show();
                    }

                    centerView.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private float mDown;
    private float mMove;

    private class ListviewOnTouch implements OnTouchListener {

        boolean isloadingdata = false;
        boolean isSlow = false;
        private float tMove;

        public boolean onTouch(View arg0, MotionEvent event) {
            // switch (event.getAction()) {
            // case MotionEvent.ACTION_DOWN: {
            // tMove = mDown = event.getY();
            // }
            // break;
            // case MotionEvent.ACTION_MOVE: {
            // mMove = event.getY();
            // if ((mDown - mMove) > 0
            // && (lastItem == listview.getCount() || isSlow)) {
            // isSlow = true;
            // isloadingdata = true;
            // listviewLoad.setVisibility(View.VISIBLE);
            // int cha = (int) (mDown - mMove);
            // if ((tMove - mMove) > 0) {
            // // cha = cha / 3;
            // }
            // cha = cha / 3;
            // listviewLoad.setPadding(listviewLoad.getPaddingLeft(),
            // (cha > listviewLoadView.getPaddingTop()) ? cha
            // : listviewLoadView.getPaddingTop(),
            // listviewLoad.getPaddingRight(), listviewLoad
            // .getPaddingBottom());
            // tMove = mMove;
            // } else {
            // isloadingdata = false;
            // }
            // }
            // break;
            // case MotionEvent.ACTION_UP: {
            // mDown = 0;
            // mMove = 0;
            // isSlow = false;
            // if (isloadingdata
            // && listviewLoad.getPaddingTop() >= listviewLoadView
            // .getPaddingTop()) {
            // listviewLoadView.setVisibility(View.VISIBLE);
            // CategoryChildDataThread.startCategoryChildDataThread(
            // handler, list, cgid, ++page);
            // }
            // new Thread() {
            // public void run() {
            // int top = listviewLoad.getPaddingTop();
            // while (listviewLoad.getPaddingTop() >= 0) {
            // top = top - 10;
            // Message msg = Message.obtain();
            // msg.what = CategoryChildActivity.LOADINGBACK;
            // msg.obj = top;
            // handler.sendMessage(msg);
            // try {
            // sleep(5);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            // }
            // };
            // }.start();
            // }
            // break;
            // }
            return false;
        }

    }

    private int mPullRefreshState = 0;
    private int lastItem;

    private class ScrollListener implements OnScrollListener {

        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            lastItem = firstVisibleItem + visibleItemCount;
            if (lastItem == totalItemCount && mPullRefreshState > 0 && page < totalPage) {
                listviewLoadView.setVisibility(View.VISIBLE);
                if (CategoryChildDataThread.intance == null)
                    CategoryChildDataThread.startCategoryChildDataThread(
                            handler, list, orderType, cgid, ++page);
            }
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mPullRefreshState = scrollState;
        }
    }

}
