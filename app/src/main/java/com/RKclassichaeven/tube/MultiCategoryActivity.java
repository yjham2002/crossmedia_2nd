package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.RKclassichaeven.tube.models.CategoryBox;
import com.ccmheaven.tube.adapter.CategoryGridViewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.thread.CategoryDataThread;
import com.ccmheaven.tube.thread.CategoryDataUnitThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.CategorizedExpandableHeightGridView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import comm.SimpleCall;

public class MultiCategoryActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private List<CategoryListInfo> list = new ArrayList<CategoryListInfo>();
    private CategoryGridViewAdapter adapter;
    private ProgressDialog loagindDialog;
    private BottomView bottomview;
    private TopMenuView topMenuView;
    private ExitDialog exitDialog;
    private Activity context;

    private DrawerLayout mDrawer;

    private boolean isOpened = false;

    @Override
    public void onStart(){
        super.onStart();
        mDrawer = findViewById(R.id.drawer);
    }

    public void closeDrawer() {
        mDrawer.closeDrawer(Gravity.LEFT);
        isOpened = false;
    }

    public void openDrawer(){
        mDrawer.openDrawer(Gravity.LEFT);
        isOpened = true;
    }

    private void setMenuButtons(final Context context){
        View menu_home = findViewById(R.id.menu_new_home);
        View menu_favor = findViewById(R.id.menu_new_favor);
        View menu_review = findViewById(R.id.menu_new_review);
        View menu_query = findViewById(R.id.menu_new_query);
        View menu_invite = findViewById(R.id.menu_new_invite);
        View menu_timer = findViewById(R.id.menu_new_timer);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.menu_new_home:{
                        Intent intent = new Intent(context, RankActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                        finish();
                        break;
                    }
                    case R.id.menu_new_timer:{
                        Intent intent = new Intent(context, TimerActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.menu_new_review:{
                        Uri uri = Uri.parse("market://details?id=" + getResources().getString(R.string.package_link));
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                        break;
                    }
                    case R.id.menu_new_query:{
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",getResources().getString(R.string.mailto), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "문의");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(emailIntent, "문의하기"));
                        break;
                    }
                    case R.id.menu_new_invite:{
                        String shareBody = getResources().getString(R.string.store_prefix);
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.invite_prefix));
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "친구 초대하기"));
                        break;
                    }
                    case R.id.menu_new_favor:{
                        Intent intent = new Intent(context, MyPageActivity.class);
                        startActivity(intent);
//						overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                        finish();
                        break;
                    }
                    default: break;
                }
            }
        };

        menu_home.setOnClickListener(onClickListener);
        menu_favor.setOnClickListener(onClickListener);
        menu_review.setOnClickListener(onClickListener);
        menu_query.setOnClickListener(onClickListener);
        menu_timer.setOnClickListener(onClickListener);
        menu_invite.setOnClickListener(onClickListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_category);

        this.context = this;

        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.app_name));
        ((TopView) findViewById(R.id.topView1)).setViewVisibility(true, false);

        bottomview = (BottomView) findViewById(R.id.bottomView1);
        topMenuView = findViewById(R.id.topMenuView);
        bottomview.setActivity(this);
        bottomview.addAdView();

        setMenuButtons(this);

        scrollView = findViewById(R.id.gv_category);

        /**
         * Custom View Begin
         */
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        SimpleCall.getHttpJson(Constants.API_CATEGORY_HIER + "&cg_id=" + TopMenuView.categoryCurrent.getCg_id(), new HashMap<String, Object>(), new SimpleCall.CallBack(){
            @Override
            public void handle(JSONObject jsonObject) {
                try{
                    JSONArray result = jsonObject.getJSONArray("result");
                    for(int e = 0; e < result.length(); e++){
                        JSONObject item = result.getJSONObject(e);
                        CategoryBox categoryBox = new CategoryBox();
                        categoryBox.setCg_id(item.getInt("cg_id"));
                        categoryBox.setCg_name(item.getString("cg_name"));
                        categoryBox.setAlias(item.getString("cg_name"));
                        categoryBox.setCg_image_url(item.getString("cg_image_url"));
                        categoryBox.setCg_order(item.getInt("cg_order"));
                        categoryBox.setCg_parent(item.getInt("cg_parent"));
                        categoryBox.setCg_subname(item.getString("cg_subname"));
                        categoryBox.setCg_depth(item.getInt("cg_depth"));
                        CategorizedExpandableHeightGridView gridview = new CategorizedExpandableHeightGridView(MultiCategoryActivity.this);

                        gridview.setCategoryBox(categoryBox);

                        final List<CategoryListInfo> tempList = new Vector<>();

                        final CategoryGridViewAdapter tempAdapter = new CategoryGridViewAdapter(MultiCategoryActivity.this, tempList, gridview, null, e == 0);
                        Handler tempHandler = new Handler() {
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case Constants.INITDATA: {
                                        tempAdapter.notifyDataSetChanged();
//                            loagindDialog.dismiss();
                                    }
                                    break;
                                    case Constants.INITDATA_LOSE: {
//                            loagindDialog.dismiss();
                                        // Toast.makeText(CategoryActivity.this.getApplicationContext(),
                                        // "Data load fail!", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }

                        };

                        gridview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        gridview.setAdapter(tempAdapter);
                        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Intent intent = new Intent(MultiCategoryActivity.this, CategoryChildActivity.class);
                                intent.putExtra("cgid", tempList.get(arg2).getCgid());
                                intent.putExtra("title", tempList.get(arg2).getName());
                                intent.putExtra("img", tempList.get(arg2).getImageUrl());
                                startActivity(intent);
                            }
                        });

                        int loadLimit = 4;
                        int numOfColumn = 2;
                        if(e == 0) {
                            numOfColumn = 1;
                            loadLimit = 1;
                        }

                        new CategoryDataUnitThread(tempHandler, tempList, categoryBox.getCg_id(), loadLimit).startCategoryDataThread();

                        linearLayout.addView(gridview.getUnitView(numOfColumn));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        scrollView.addView(linearLayout);
        /**
         * Custom View End
         */

        topMenuView.buttonimg(2);

//        loadCategoryData();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                if(isOpened){
                    closeDrawer();
                    return true;
                }

                LogoActivity.isStart = false;
//				new AdHelper(this).loadInterstitialAd();
                if (exitDialog != null) {
                    exitDialog.dismiss();
                    exitDialog = null;
                } else {
                    exitDialog = new ExitDialog(this, new ExitDialog.ExitDialogListener() {
                        @Override
                        public void onConfirm() {
                            exitDialog.dismiss();
                            exitDialog = null;
                            ActivityCompat.finishAffinity(context);

                            System.exit(0);
                        }

                        @Override
                        public void onCancel() {
                            exitDialog.dismiss();
                            exitDialog = null;
                            // 전면광고 현시
                            new AdHelper(MultiCategoryActivity.this).loadInterstitialAd();
                        }
                    });
                    exitDialog.show();
                }
            }
            break;
        }
        return true;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.INITDATA: {
                    adapter.notifyDataSetChanged();
                    loagindDialog.dismiss();
                }
                break;
                case Constants.INITDATA_LOSE: {
                    loagindDialog.dismiss();
                    // Toast.makeText(CategoryActivity.this.getApplicationContext(),
                    // "Data load fail!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    };

    private void loadCategoryData() {
        CategoryDataThread.startCategoryDataThread(handler, list);
        loagindDialog = ProgressDialog.show(this,"Connecting", "Loading. Please wait...", true, true);
    }
}
