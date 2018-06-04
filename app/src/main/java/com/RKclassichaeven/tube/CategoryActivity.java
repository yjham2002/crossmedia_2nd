package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.adapter.CategoryGridViewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.thread.CategoryDataThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends Activity {

    private GridView gridview;
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
        ImageView menu_home = findViewById(R.id.menu_home);
        ImageView menu_sleep = findViewById(R.id.menu_sleep);
        ImageView menu_review = findViewById(R.id.menu_review);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.menu_home:{
                        Intent intent = new Intent(context, RankActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                        finish();
                        break;
                    }
                    case R.id.menu_sleep:{
                        Intent intent = new Intent(context, TimerActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.menu_review:{
                        Uri uri = Uri.parse("market://details?id=com.tube.sample");
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                        break;
                    }
                    default: break;
                }
            }
        };

        menu_home.setOnClickListener(onClickListener);
        menu_sleep.setOnClickListener(onClickListener);
        menu_review.setOnClickListener(onClickListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        this.context = this;
        
        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.app_name));
        ((TopView) findViewById(R.id.topView1)).setViewVisibility(true, false);

        bottomview = (BottomView) findViewById(R.id.bottomView1);
        topMenuView = findViewById(R.id.topMenuView);
		bottomview.setActivity(this);
		bottomview.addAdView();

		setMenuButtons(this);

        gridview = (GridView) findViewById(R.id.gv_category);
        adapter = new CategoryGridViewAdapter(this, list, gridview, handler);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(CategoryActivity.this, CategoryChildActivity.class);
                intent.putExtra("cgid", list.get(arg2).getCgid());
                intent.putExtra("title", list.get(arg2).getName());
                intent.putExtra("img", list.get(arg2).getImageUrl());
                startActivity(intent);
            }
        });
        topMenuView.buttonimg(2);

        loadCategoryData();
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
                            new AdHelper(CategoryActivity.this).loadInterstitialAd();
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
        loagindDialog = ProgressDialog.show(this,
                "Connecting", "Loading. Please wait...", true, true);
    }
}
