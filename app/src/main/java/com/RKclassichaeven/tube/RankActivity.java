package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.ccmheaven.tube.adapter.CommonListviewAdapter;
import com.ccmheaven.tube.ads.AdHelper;
import com.ccmheaven.tube.db.MyMusicDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.ListInfo;
import com.ccmheaven.tube.thread.RankDataThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.CenterView;
import com.ccmheaven.tube.view.ListviewLoadView;
import com.ccmheaven.tube.view.TopMenuView;
import com.ccmheaven.tube.view.TopView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RankActivity extends Activity {

	Activity __act;

	public static final int MY_LIST_DATA_LOAD = 4000;
	public static final int FAVORITE_ITEM = 4002;
	public static final int UNFAVORITE_ITEM = 4003;

	private ListView listview;
	private CenterView centerView;
	private ListviewLoadView listviewLoadView;
	private List<ListInfo> list = new ArrayList<ListInfo>();
	private boolean isLockListView = false;
	private int page = 1;
	private int totalPage = 1;

	private Activity activity;

    private DrawerLayout mDrawer;

	private CommonListviewAdapter adapter;
	private ProgressDialog loadingDialog;
	private BottomView bottomview;
	private TopMenuView topMenuView;
	private CenterViewListener centerviewlistener = new CenterViewListener();
	private MyMusicDB myMusicDB;
	private ExitDialog exitDialog;

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
		__act = this;

		new ConfigThread().start();

		Log.d("dev", "RankActivity.onCreate() start");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);
		myMusicDB = new MyMusicDB(this);

		this.activity = this;

        ((TopView) findViewById(R.id.topView1)).setTitleName(getResources().getString(R.string.app_name));
        ((TopView) findViewById(R.id.topView1)).setViewVisibility(true, false);

		bottomview = (BottomView) findViewById(R.id.bottomView1);
        topMenuView = findViewById(R.id.topMenuView);

		listview = (ListView) findViewById(R.id.lv_rank);
		listviewLoadView = (ListviewLoadView) findViewById(R.id.listviewLoadView1);
		centerView = (CenterView) findViewById(R.id.center_view);
		centerView.InitButtonListener(centerviewlistener);

        setMenuButtons(this);

		adapter = new CommonListviewAdapter(this, list, listview, handler);
		listview.setAdapter(adapter);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
					centerView.setVisibility(View.VISIBLE);
				} else {
					centerView.setVisibility(View.GONE);
				}
				adapter.notifyDataSetChanged();
			}
		});

		listview.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int count = totalItemCount - visibleItemCount;
				if ((firstVisibleItem >= count) && (totalItemCount != 1) && (page < totalPage && !isLockListView)){
					page = page + 1;
					listviewLoadView.setVisibility(View.VISIBLE);
					loadRankData();
				}
			}
		});
        topMenuView.buttonimg(1);
		loadingDialog = ProgressDialog.show(this, "Connecting", "Loading. Please wait...", true, true);

	}

	private void loadRankData() {
		isLockListView = true;
		RankDataThread.startRankDataThread(handler, list, page);
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
				if (exitDialog != null) {
					exitDialog.dismiss();
					exitDialog = null;
				} else {
					exitDialog = new ExitDialog(this, new ExitDialog.ExitDialogListener() {
						@Override
						public void onConfirm() {
							exitDialog.dismiss();
							exitDialog = null;
							ActivityCompat.finishAffinity(activity);

							System.exit(0);
						}

						@Override
						public void onCancel() {
							exitDialog.dismiss();
							exitDialog = null;
							// 전면광고 현시
							new AdHelper(RankActivity.this).loadInterstitialAd();
						}
					});
					exitDialog.show();
				}
				break;
			}
		}
		return true;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constants.INITDATA: {
					listviewLoadView.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					loadingDialog.dismiss();
					totalPage = msg.arg1;
					isLockListView = false;
				}
				break;
				case Constants.INITDATA_LOSE: {
					listviewLoadView.setVisibility(View.GONE);
					loadingDialog.dismiss();
					isLockListView = false;
					if (page > 1) {
						page--;
					}
					// Toast.makeText(CategoryActivity.this.getApplicationContext(),
					// "Data load fail!", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}

	};

	private boolean isAllSelect;

	private class CenterViewListener implements View.OnClickListener {
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
						centerView.setVisibility(View.GONE);
					}
					adapter.notifyDataSetChanged();
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
						Intent intent = new Intent(RankActivity.this, YoutubePlayerActivity.class);
						intent.putExtra( "playlist", strJson );
						startActivity(intent);
                        overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
					}
					centerView.setVisibility(View.GONE);
					listview.clearChoices();
					adapter.notifyDataSetChanged();
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
								RankActivity.this,
								getResources()
										.getText(R.string.At_least_1_selected)
										.toString(), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								RankActivity.this,
								getResources().getText(
										R.string.Has_been_saved_to_mylist)
										.toString(), Toast.LENGTH_SHORT).show();
						centerView.setVisibility(View.GONE);
						listview.clearChoices();
						adapter.notifyDataSetChanged();
					}
				}
				break;
			}
		}
	}

	public static final String CONFIG_NAME = "gospel_config";
	//private static final int CONFIG_OK = 1;
	//private static final int CONFIG_NO = 0;

	private String download;
	private String audio;
	private String ad;
	private String ad_interstital;
	private String ad_fix;
	private String ad_type;
	private String adfit_banner;
	private String adfit_interstital;
	private int version;

	public int getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			Log.d("dev", Log.getStackTraceString(e));
			return 0;
		}
	}

	private class ConfigThread extends Thread {
		public void run() {
			Log.d("dev", "RankActivity.ConfigThread.run() started");

			ad = "N";
			ad_interstital = "N";
			audio = "Y";
			download = "Y";
			ad_fix = "N";
			URL url;

			InputStream inStream = null;
			ByteArrayOutputStream data = null;
			String datastr = null;

			HttpURLConnection connection = null;
			try {

				url = new URL(Constants.API_APP_CONFIG);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(3000);
				connection.setReadTimeout(3000);
				inStream = connection.getInputStream();
				data = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inStream.read(buffer)) != -1) {
					data.write(buffer, 0, len);
				}
				datastr = new String(data.toByteArray(), "utf-8");
				JSONObject object = new JSONObject(datastr).getJSONObject("result");

				download = object.getString("btn_download");
				audio = object.getString("btn_audio");
				ad = object.getString("btn_ad");
				ad_interstital = object.getString("ad_interstital");
				ad_type = object.getString("adtype");
				if("adfit".equals(ad_type)){
					adfit_banner = object.getString("adfit_banner");
					adfit_interstital = object.getString("adfit_interstital");
				}
				version = Integer.valueOf(object.getString("app_ver"));
				ad_fix = object.getString("fix_video_ad");

				//configHandler.sendEmptyMessage(RankActivity.CONFIG_OK);
				Log.d("dev", "RankActivity.ConfigThread.run() ad: "+ ad +", ad_interstital: " + ad_interstital +", adtype: " + ad_type + ", version: " + version);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				SharedPreferences edit = getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
				download = "N";
				ad_fix = "N";
				edit.edit().putString("download", download).commit();
				edit.edit().putString("audio", audio).commit();
				edit.edit().putString("ad", ad).commit();
				edit.edit().putString("ad_interstital", ad_interstital).commit();
				edit.edit().putString("ad_fix", ad_fix).commit();
				edit.edit().putString("ad_type", ad_type).commit();
				edit.edit().putString("adfit_banner", adfit_banner).commit();
				edit.edit().putString("adfit_interstital", adfit_interstital).commit();

				if (getVersion() < version) {

					new AlertDialog.Builder(RankActivity.this)
							.setTitle("Remind")
							.setMessage("Find new update，update now?")
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Uri uri = Uri.parse("https://play.google.com/store");
									Intent it = new Intent(Intent.ACTION_VIEW, uri);
									startActivity(it);
									finish();
								}
							})
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									loadRankData();
								}
							}).show();
				} else {
					loadRankData();
				}

				// to avoid Exception
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						bottomview.setActivity(__act);
						bottomview.addAdView();
					}
				});

				if (connection != null) { connection.disconnect(); }
				try {
					if (inStream != null) { inStream.close(); }
					if (data != null) { data.close(); }
				} catch (IOException e) { }
			}

		}
	}
}
