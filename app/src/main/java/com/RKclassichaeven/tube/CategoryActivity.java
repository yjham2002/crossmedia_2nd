package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        
        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.app_name));

        bottomview = (BottomView) findViewById(R.id.bottomView1);
        topMenuView = findViewById(R.id.topMenuView);
		bottomview.setActivity(this);
		bottomview.addAdView();

        gridview = (GridView) findViewById(R.id.gv_category);
        adapter = new CategoryGridViewAdapter(this, list, gridview, handler);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(CategoryActivity.this, CategoryChildActivity.class);
                intent.putExtra("cgid", list.get(arg2).getCgid());
                startActivity(intent);
                finish();
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
                            finish();
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
