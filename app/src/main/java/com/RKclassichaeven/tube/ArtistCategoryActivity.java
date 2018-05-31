package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.adapter.CategoryListViewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.ads.AdmobHelper;
import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.thread.ArtistCategoryDataThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.TopMenuView;
import com.google.android.gms.ads.*;

public class ArtistCategoryActivity extends Activity {
    /**
     * ********************
     */
    private ListView listview;
    private CategoryListViewAdapter adapter;
    private List<CategoryListInfo> list = new ArrayList<CategoryListInfo>();
    private ProgressDialog loagindDialog;
    public static ArtistCategoryActivity intance;
    private BottomView bottomview;
    private TopMenuView topMenuView;
    private ExitDialog exitDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        intance = this;

        bottomview = (BottomView) findViewById(R.id.bottomView1);
        topMenuView = findViewById(R.id.topMenuView);
		bottomview.setActivity(this);
		bottomview.addAdView();
		
        listview = (ListView) findViewById(R.id.lv_category);
        adapter = new CategoryListViewAdapter(this, list, listview, handler);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(intance,
                        ArtistCategoryChildActivity.class);
                intent.putExtra("cgid", list.get(arg2).getCgid());
                startActivity(intent);
                finish();
            }
        });
        topMenuView.buttonimg(1);
        ArtistCategoryDataThread.startArtistCategoryDataThread(handler, list);
        loagindDialog = ProgressDialog.show(intance, "Connecting",
                "Loading. Please wait...", true, true);
    }

    protected void onDestroy() {
        super.onDestroy();
        intance = null;
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK: {
				LogoActivity.isStart = false;
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
                            new AdHelper(ArtistCategoryActivity.this).loadInterstitialAd();
                        }
                    });
                    exitDialog.show();
                }
			}
			break;
		}
		return true;
	}
}
