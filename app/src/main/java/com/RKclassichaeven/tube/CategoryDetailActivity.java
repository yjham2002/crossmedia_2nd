package com.RKclassichaeven.tube;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ccmheaven.tube.adapter.CategoryGridViewAdapter;
import com.ccmheaven.tube.pub.CategoryListInfo;
import com.ccmheaven.tube.pub.Constants;
import com.ccmheaven.tube.thread.CategoryDataThread;
import com.ccmheaven.tube.thread.CategoryDataUnitThread;
import com.ccmheaven.tube.view.BottomView;
import com.ccmheaven.tube.view.TopView;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailActivity extends AppCompatActivity {

    private GridView gridview;
    private List<CategoryListInfo> list = new ArrayList<CategoryListInfo>();
    private CategoryGridViewAdapter adapter;
    private BottomView bottomview;
    private ProgressDialog loagindDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        ((TopView) findViewById(R.id.topView1)).setViewVisibility(false, true);

        bottomview = findViewById(R.id.bottomView1);
        bottomview.setActivity(this);
        bottomview.addAdView();

        gridview = (GridView) findViewById(R.id.gv_category);
        adapter = new CategoryGridViewAdapter(this, list, gridview, handler, true);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(CategoryDetailActivity.this, CategoryChildActivity.class);
                intent.putExtra("cgid", list.get(arg2).getCgid());
                intent.putExtra("title", list.get(arg2).getName());
                intent.putExtra("img", list.get(arg2).getImageUrl());
                startActivity(intent);
            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle.containsKey("cgid") && bundle.containsKey("cgname")){
            loadCategoryData(bundle.getInt("cgid"));
            ((TopView) findViewById(R.id.topView1)).setTitleName(bundle.getString("cgname"));
        }
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

    private void loadCategoryData(int cgId) {
        new CategoryDataUnitThread(handler, list, cgId, CategoryDataUnitThread.LOAD_INFINITE).start();
        loagindDialog = ProgressDialog.show(this,
                "Connecting", "Loading. Please wait...", true, true);
    }

}
