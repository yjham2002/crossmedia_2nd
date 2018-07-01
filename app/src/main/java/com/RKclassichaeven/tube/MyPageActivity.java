package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.adapter.CommonListviewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.ads.AdmobHelper;
import com.ccmheaven.tube.db.MyMusicDB;
import com.ccmheaven.tube.db.MyMusicDownDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.CustomDialog;
import com.ccmheaven.tube.pub.ListInfo;
import com.ccmheaven.tube.thread.DownLoadThread;
import com.ccmheaven.tube.thread.InitDataFromDBThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.CenterView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;
import com.google.android.gms.ads.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bases.SimpleCallback;

public class MyPageActivity extends Activity {
    /**
     * **********************
     */
    private ListView listview;
    private CommonListviewAdapter listviewadapter;
    private List<ListInfo> list = new ArrayList<ListInfo>();
    public static MyPageActivity intance;
    private CenterView centerview;
    private MyMusicDB myMusicDB;
    private MyMusicDownDB myMusicDownDB;
    private ProgressDialog loagindDialog;
    private CenterViewListener centerviewlistener = new CenterViewListener();
    private BottomView bottomview;
//    private TopMenuView topMenuView;
    private ExitDialog exitDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        intance = this;

        myMusicDB = new MyMusicDB(this);
        myMusicDownDB = new MyMusicDownDB(this);
        listview = (ListView) findViewById(R.id.mypage_listView1);
        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.my_list));
        ((TopView) findViewById(R.id.topView1)).setViewVisibility(false, true);
        centerview = (CenterView) findViewById(R.id.page_centerView1);

        bottomview = (BottomView) findViewById(R.id.bottomView1);
//        topMenuView = findViewById(R.id.topMenuView);
        bottomview.setActivity(this);
        bottomview.addAdView();

        bottomview.setNewListCall(new SimpleCallback() {
            @Override
            public void callback() {
                List<ListInfo> templist = new ArrayList<ListInfo>();
                for (int i = 0; i < listview.getCount(); i++) {
                    if (listview.isItemChecked(i)) {
                        templist.add(list.get(i));
                    }
                }
                if (!templist.isEmpty()) {
                    if(MyApplication.getMediaService() != null){
                        Gson gson = new Gson();
                        String strJson=gson.toJson(templist);
                        Type listType = new TypeToken<List<ListInfo>>() {
                        }.getType();

                        List<ListInfo> newList = (List<ListInfo>) gson.fromJson(strJson, listType);
                        MyApplication.getMediaService().getSyncInfo().release();
                        Log.e("MediaServiceSent", strJson);
                        MyApplication.getMediaService().setTracks(newList);
                        MyApplication.getMediaService().getSyncInfo().setBySong(newList.get(0));
                    }
                }
                centerview.setVisibility(View.GONE);
                listview.clearChoices();
                listviewadapter.notifyDataSetChanged();
            }
        });

        centerview.InitButtonListener(centerviewlistener);
        listviewadapter = new CommonListviewAdapter(this, list, listview, handler);
        listview.setAdapter(listviewadapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setItemsCanFocus(false);
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                boolean haveSelect = false;
                for (int i = 0; i < listview.getCount(); i++) {
                    if (listview.isItemChecked(i)) {
                        haveSelect = true;
                        break;
                    }
                }
                if (haveSelect) {
                    centerview.setVisibility(View.VISIBLE);
                } else {
                    centerview.setVisibility(View.GONE);
                }
                listviewadapter.notifyDataSetChanged();
            }
        });
//        topMenuView.buttonimg(4);
        centerview.changeBackground();
        InitDataFromDBThread.startInitDataFromDBThread(this, handler, list,
                InitDataFromDBThread.FROM_PAGEDB);
    }

    protected void onDestroy() {
        super.onDestroy();
        intance = null;
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Constants.REMOVE_ITEM: {
                    int num = Integer.parseInt(msg.obj.toString());
                    Log.v("here", "here : remove_item : " + num);
                    myMusicDB.deleteContact(list.get(num));

                    //item 제거
                    int pos = num;
                    if (pos != listview.INVALID_POSITION) {
                        list.remove(pos);
                        listview.clearChoices();
                        listviewadapter.notifyDataSetChanged();
                    }
                }

                case Constants.INITDATA: {
                    listviewadapter.notifyDataSetChanged();
                    if (loagindDialog != null) {
                        loagindDialog.dismiss();
                    }
                }
                break;
                case Constants.INITDATA_LOSE: {
                    if (loagindDialog != null) {
                        loagindDialog.dismiss();
                    }
                }
                break;
                case Constants.DOWNLOADNO: {
                    Toast.makeText(MyPageActivity.this.getApplicationContext(),
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
                new CustomDialog.Builder(intance)
                        .setTitle(alertTitle)
                        .setMessage(buttonMessage)
                        .setPositiveButton(buttonYes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(intance,
                                                DownLoadActivity.class);
                                        startActivity(intent);
                                        DownLoadThread down = new DownLoadThread(
                                                intance, handler, listinfo);
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
                new CustomDialog.Builder(intance)
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
        listviewadapter.notifyDataSetChanged();
    }

    private boolean isAllSelect;

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
                        Intent intent = new Intent(intance, YoutubePlayerActivity.class);
                        intent.putExtra( "playlist", strJson );
                        intance.startActivity(intent);
                        overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
                    }

                    centerview.setVisibility(View.GONE);

                    listview.clearChoices();
                    listviewadapter.notifyDataSetChanged();
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
                        listviewadapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), "보관함에 추가되었습니다.", Toast.LENGTH_LONG).show();
                    }

                    centerview.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK: {
//                LogoActivity.isStart = false;
////				new AdHelper(this).loadInterstitialAd();
//                if (exitDialog != null) {
//                    exitDialog.dismiss();
//                    exitDialog = null;
//                } else {
//                    exitDialog = new ExitDialog(this, new ExitDialog.ExitDialogListener() {
//                        @Override
//                        public void onConfirm() {
//                            exitDialog.dismiss();
//                            exitDialog = null;
//                            finish();
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            exitDialog.dismiss();
//                            exitDialog = null;
//                            // 전면광고 현시
//                            new AdHelper(MyPageActivity.this).loadInterstitialAd();
//                        }
//                    });
//                    exitDialog.show();
//                }
//            }
//            break;
//        }
//        return true;
//    }

}
