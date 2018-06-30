package com.RKclassichaeven.tube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.RKclassichaeven.tube.R;
import com.ccmheaven.tube.adapter.ListviewAdapter;
import com.ccmheaven.tube.db.MyMusicDB;
import com.ccmheaven.tube.db.MyMusicDownDB;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.pub.CustomDialog;
import com.ccmheaven.tube.pub.ListInfo;
import com.ccmheaven.tube.thread.ArtistCategoryChildDataThread;
import com.ccmheaven.tube.thread.DownLoadThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.CenterView;
import com.ccmheaven.tube.view.ListviewLoadView;
import com.ccmheaven.tube.view.TopMenuView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArtistCategoryChildActivity extends Activity {
	public static ArtistCategoryChildActivity intance;
	private ListView listview;
	private CenterView centerView;
	private BottomView bottomview;
	private TopMenuView topMenuView;
	private List<ListInfo> list = new ArrayList<ListInfo>();
	private MyMusicDB myMusicDB;
	private MyMusicDownDB myMusicDownDB;
	private ListviewAdapter listviewAdapter;
	private ProgressDialog loagindDialog;
	private CenterViewListener centerviewlistener = new CenterViewListener();
	private int page;
	private String cgid;

	// private Button listviewLoad;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categorychild);
		myMusicDownDB = new MyMusicDownDB(this);
		Bundle data = getIntent().getExtras();
		if (data != null) {
			cgid = data.getString("cgid");
			intance = this;
			page = 1;
			myMusicDB = new MyMusicDB(this);
			listviewLoadView = (ListviewLoadView) findViewById(R.id.listviewLoadView1);
			// listviewLoad = (Button) findViewById(R.id.listviewTextview);
			listview = (ListView) findViewById(R.id.lv_category);

			bottomview = (BottomView) findViewById(R.id.bottomView1);
			topMenuView = findViewById(R.id.topMenuView);
			bottomview.setActivity(this);
			bottomview.addAdView();

			centerView = (CenterView) findViewById(R.id.center_view);
			centerView.InitButtonListener(centerviewlistener);
			listviewAdapter = new ListviewAdapter(this, list, listview, handler, true);
			listview.setAdapter(listviewAdapter);
			listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			listview.setItemsCanFocus(false);
			// listview.setOnTouchListener(new ListviewOnTouch());
			listview.setOnScrollListener(new ScrollListener());
			listview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
										int arg2, long arg3) {
					listviewAdapter.notifyDataSetChanged();
				}
			});
			topMenuView.buttonimg(1);
			ArtistCategoryChildDataThread.startArtistCategoryChildDataThread(
					handler, list, cgid, page);
			loagindDialog = ProgressDialog.show(
					ArtistCategoryChildActivity.intance, "Connecting",
					"Loading. Please wait...", true, true);
		} else {
			Intent intent = new Intent(this, CategoryActivity.class);
			startActivity(intent);
			finish();
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
					listviewAdapter.notifyDataSetChanged();
					// if (listviewLoadView.isShown()) {
					// listview.setSelection(lastItem);
					// }
					listviewLoadView.setVisibility(View.GONE);
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
					// Toast.makeText(
					// CategoryChildActivity.this.getApplicationContext(),
					// "Data load fail!", Toast.LENGTH_SHORT).show();
					loagindDialog.dismiss();
				}
				break;
				case Constants.DOWNLOADNO: {
					Toast.makeText(ArtistCategoryChildActivity.this,
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
			Intent intent = new Intent(this, ArtistCategoryActivity.class);
			startActivity(intent);
		}
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
		intance = null;
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
				new CustomDialog.Builder(intance)
						.setTitle(alertTitle)
						.setMessage(buttonMessage)
						.setPositiveButton(buttonYes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(intance, DownLoadActivity.class);
										startActivity(intent);
										DownLoadThread down = new DownLoadThread(intance, handler, listinfo);
										down.start();
										dialog.dismiss();
									}
								})
						.setNegativeButton(buttonNo,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
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
		listviewAdapter.notifyDataSetChanged();
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
			// mDown = event.getY();
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
			// cha = cha / 3;
			// listviewLoad.setPadding(listviewLoad.getPaddingLeft(),
			// (cha > listviewLoadView.getPaddingTop()) ? cha
			// : listviewLoadView.getPaddingTop(),
			// listviewLoad.getPaddingRight(), listviewLoad
			// .getPaddingBottom());
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
			// ArtistCategoryChildDataThread
			// .startArtistCategoryChildDataThread(handler, list,
			// cgid, ++page);
			// }
			// new Thread() {
			// public void run() {
			// int top = listviewLoad.getPaddingTop();
			// while (listviewLoad.getPaddingTop() > 1) {
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

	private ListviewLoadView listviewLoadView;

	private int mPullRefreshState = 0;
	private int lastItem;

	private class ScrollListener implements OnScrollListener {

		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount;
			if (lastItem == totalItemCount && mPullRefreshState > 0) {
				listviewLoadView.setVisibility(View.VISIBLE);
				if (ArtistCategoryChildDataThread.intance == null)
					ArtistCategoryChildDataThread
							.startArtistCategoryChildDataThread(handler, list,
									cgid, ++page);
			}
		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			mPullRefreshState = scrollState;
		}
	}
}
