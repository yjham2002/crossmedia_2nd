package com.RKclassichaeven.tube;

import com.RKclassichaeven.tube.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownLoadActivity extends Activity {
	public static final int Progressbarsh = 1500;
	public static final int End = 1501;
	public static final int InitName = 1502;
	/*******************************************************/
	private static ProgressBar progressbar;
	private static TextView text, name;
	private Button button;
	private static DownLoadActivity instance;
	public static boolean isStop;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_download);
		instance = this;
		isStop = false;
		progressbar = (ProgressBar) findViewById(R.id.download_progressBar1);
		text = (TextView) findViewById(R.id.download_textView2);
		name = (TextView) findViewById(R.id.download_name);
		button = (Button) findViewById(R.id.download_button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(instance)
						.setTitle("Download interrupted")
						.setMessage("Are you sure you interrupt to download?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										isStop = true;
										instance.finish();
									}
								}).setNegativeButton("No", null).show();
			}
		});
	}

	public static Handler prohandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Progressbarsh: {
				if (instance != null && !instance.isFinishing()) {
					int temp = Integer.parseInt(msg.obj.toString());
					progressbar.setProgress(temp);
					text.setText(temp + "%");
				}
			}
				break;
			case End: {
				if (instance != null && !instance.isFinishing())
					instance.finish();
			}
				break;
			case InitName: {
				if (name != null) {
					name.setText(msg.obj.toString());
				}
			}
				break;
			}
		}
	};

	protected void onDestroy() {
		instance = null;
		isStop = true;
		super.onDestroy();
	}

}
