package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.adapter.SettingListviewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.SettingInfo;
import com.ccmheaven.tube.thread.SettingDataThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;

public class SettingActivity extends Activity {

    private ListView listview;

    private SettingInfo info = new SettingInfo();
    private SettingListviewAdapter adapter;
    private ProgressDialog loadingDialog;
    private BottomView bottomview;
//    private TopMenuView topMenuView;
    private ExitDialog exitDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ((TopView) findViewById(R.id.topView1)).setTitleName(getString(R.string.setting));

        bottomview = (BottomView) findViewById(R.id.bottomView1);
//        topMenuView = findViewById(R.id.topMenuView);
		bottomview.setActivity(this);
		bottomview.addAdView();

        listview = (ListView) findViewById(R.id.list_view);
        adapter = new SettingListviewAdapter(this, info, listview, handler);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (arg2 == 1) {
                    Intent it = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    it.putExtra(Intent.EXTRA_EMAIL, new String[]{info.getEmail()});
                    it.setType("text/plain");

                    // 메일 인텐트를 현시한다.
                    startActivity(Intent.createChooser(it, "Send email"));
                } else if (arg2 == 2) {
                    Uri uri = Uri.parse("market://details?id=com.tube.sample");

                    Intent it = new Intent(Intent.ACTION_VIEW, uri);

                    startActivity(it);
                } else if (arg2 > 3) {
                	//recommend 클릭함
                    Uri uri = Uri.parse( info.getRecommend().get(arg2-4).getLink() );
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);                	
                }
            }
        });
//        topMenuView.buttonimg(0);

        loadingDialog = ProgressDialog.show(this, "Connecting", "Loading. Please wait...", true, true);
        SettingDataThread.startSettingDataThread(handler, info);
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
                            new AdHelper(SettingActivity.this).loadInterstitialAd();
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
                    loadingDialog.dismiss();
                }
                break;
                case Constants.INITDATA_LOSE: {
                    loadingDialog.dismiss();
                }
                break;
            }
        }

    };
}
