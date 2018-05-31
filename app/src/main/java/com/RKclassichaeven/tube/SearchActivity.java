package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
import com.ccmheaven.tube.thread.InitDataThread;
import com.ccmheaven.tube.thread.SearchDataThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.CenterView;
import com.ccmheaven.tube.view.ListviewLoadView;
import com.ccmheaven.tube.view.SearchButton;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {
    /**
     * ********************
     */
    private ListView listview;
    private CenterView centerview;
    private SearchButton search;
    private CommonListviewAdapter listviewAdapter;
    private List<ListInfo> list = new ArrayList<ListInfo>();
    private MyMusicDB myMusicDB;
    private CenterViewListener centerviewListener = new CenterViewListener();
    private ProgressDialog loagindDialog;
    private MyMusicDownDB myMusicDownDB;
    private BottomView bottomview;
//    private TopMenuView topMenuView;
    private ExitDialog exitDialog;
    private int page;

    private ListviewLoadView listviewLoadView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        page = 1;

        myMusicDB = new MyMusicDB(this);
        myMusicDB = new MyMusicDB(this);
        myMusicDownDB = new MyMusicDownDB(this);
        listviewLoadView = (ListviewLoadView) findViewById(R.id.listviewLoadView1);
        // listviewLoad = (Button) findViewById(R.id.listviewTextview);
        listview = (ListView) findViewById(R.id.search_listView1);
        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.search));
        ((TopView) findViewById(R.id.topView1)).setViewVisibility(false, true);
        centerview = (CenterView) findViewById(R.id.search_centerView1);

        bottomview = (BottomView) findViewById(R.id.bottomView1);
//        topMenuView = findViewById(R.id.topMenuView);
        bottomview.setActivity(this);
        bottomview.addAdView();

        search = (SearchButton) findViewById(R.id.searchbutton);
        search.InitListener(new OnClickListener() {
            public void onClick(View v) {
                startSearch();
            }
        });
        search.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
        centerview.InitButtonListener(centerviewListener);
        listviewAdapter = new CommonListviewAdapter(this, list, listview, handler);
        listview.setAdapter(listviewAdapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setItemsCanFocus(false);
        //listview.setOnTouchListener(new ListviewOnTouch());
        listview.setOnScrollListener(new ScrollListener());
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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
                listviewAdapter.notifyDataSetChanged();
            }
        });
//        topMenuView.buttonimg(3);
        InitDataThread.startInitDataThread(handler, list, page);
        loagindDialog = ProgressDialog.show(this,
                "Connecting", "Loading. Please wait...", true, false);
    }

    private boolean isSearch;

    private void startSearch() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        String edit = search.getEditText();
        loagindDialog = ProgressDialog.show(this,
                "Connecting", "Loading. Please wait...", true, false);
        if (InitDataThread.isConnect) {
            lastItem = 0;
            listview.setAdapter(listviewAdapter);
            if (edit.equals("")) {
                page = 1;
                InitDataThread.startInitDataThread(handler, list, page);
                isSearch = false;
            } else {
                isSearch = true;
                SearchDataThread.startSearchDataThread(handler, list, edit);
            }
        } else {
            page = 0;
            InitDataFromDBThread.startInitDataFromDBThread(this, handler,
                    list, edit);
        }

    }

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
                    if (listviewLoadView.isShown()) {
                        listview.setSelection(lastItem);
                        listviewLoadView.setVisibility(View.GONE);
                    }
                    listviewAdapter.notifyDataSetChanged();
                    loagindDialog.dismiss();
                }
                break;
                case Constants.INITDATA_LOSE: {
                    listviewAdapter.notifyDataSetChanged();
                    if (page > 1) {
                        page--;
                    }
                    Log.d("zpf", page + "");
                    if (listviewLoadView.isShown()) {
                        listview.setSelection(lastItem);
                        listviewLoadView.setVisibility(View.GONE);
                    }
                    loagindDialog.dismiss();
                    // Toast.makeText(SearchActivity.this.getApplicationContext(),
                    // "Data load fail!", Toast.LENGTH_SHORT).show();
                    // InitDataFromDBThread.startInitDataFromDBThread(intance,
                    // handler, list, InitDataFromDBThread.FROM_DOWNDB);
                }
                break;
                case Constants.DOWNLOADNO: {
                    Toast.makeText(SearchActivity.this,
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
                new CustomDialog.Builder(SearchActivity.this)
                        .setTitle(alertTitle)
                        .setMessage(buttonMessage)
                        .setPositiveButton(buttonYes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(SearchActivity.this,
                                                DownLoadActivity.class);
                                        startActivity(intent);
                                        DownLoadThread down = new DownLoadThread(
                                                SearchActivity.this, handler, listinfo);
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
                new CustomDialog.Builder(SearchActivity.this)
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

    private class CenterViewListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.center_button1: {
                    if (!isAllSelect) {
                        for (int i = 0; i < listview.getCount(); i++) {
                            listview.setItemChecked(i, true);
                        }
                        isAllSelect = true;
                    } else {
                        for (int i = 0; i < listview.getCount(); i++) {
                            listview.setItemChecked(i, false);
                        }
                        isAllSelect = false;
                        centerview.setVisibility(View.GONE);
                    }
                    listviewAdapter.notifyDataSetChanged();
                }
                break;
                case R.id.center_button2: {

                    List<ListInfo> templist = new ArrayList<ListInfo>();
                    for (int i = 0; i < listview.getCount(); i++) {
                        if (listview.isItemChecked(i)) {
                            templist.add(list.get(i));
                        }
                    }
                    if (!templist.isEmpty()) {
                        Gson gson = new Gson();
                        String strJson=gson.toJson(templist);
                        Intent intent = new Intent(SearchActivity.this, YoutubePlayerActivity.class);
                        intent.putExtra( "playlist", strJson );
                        startActivity(intent);
                    }
                    centerview.setVisibility(View.GONE);
                    listview.clearChoices();
                    listviewAdapter.notifyDataSetChanged();
                }
                break;
                case R.id.center_button4: {
                    boolean unhaveSelect = false;
                    for (int i = 0; i < list.size(); i++) {
                        if (listview.isItemChecked(i)) {
                            unhaveSelect = true;
                            myMusicDB.addContact(list.get(i));
                        }
                    }
                    if (!unhaveSelect) {
                        Toast.makeText(
                                SearchActivity.this,
                                getResources()
                                        .getText(R.string.At_least_1_selected)
                                        .toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(
                                SearchActivity.this,
                                getResources().getText(
                                        R.string.Has_been_saved_to_mylist)
                                        .toString(), Toast.LENGTH_SHORT).show();
                        centerview.setVisibility(View.GONE);
                        listview.clearChoices();
                        listviewAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
        }
    }

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
//                            new AdHelper(SearchActivity.this).loadInterstitialAd();
//                        }
//                    });
//                    exitDialog.show();
//                }
//            }
//            break;
//        }
//        return true;
//    }

    private int mPullRefreshState = 0;
    private int lastItem;

    private class ScrollListener implements OnScrollListener {

        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            lastItem = firstVisibleItem + visibleItemCount;
            if (lastItem == totalItemCount && mPullRefreshState > 0) {
                listviewLoadView.setVisibility(View.VISIBLE);
                if (InitDataThread.intance == null)
                    InitDataThread.startInitDataThread(handler, list, ++page);
            }
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mPullRefreshState = scrollState;
            Log.d("zpf", "scrollState:" + scrollState);
        }
    }
}
