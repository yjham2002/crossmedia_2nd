package com.ccmheaven.tube.view;

import com.RKclassichaeven.tube.CategoryActivity;
import com.RKclassichaeven.tube.CategoryDetailActivity;
import com.RKclassichaeven.tube.MyPageActivity;
import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.RankActivity;
import com.RKclassichaeven.tube.SearchActivity;
import com.RKclassichaeven.tube.SettingActivity;
import com.RKclassichaeven.tube.TimerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopView extends RelativeLayout {

    private View view;
    private TextView m_tvTitle;
    private ImageView tv_title_img;
    private ImageView share;
    private ImageView left_menu;
    private ImageView left_back;
    private ImageView right_search;
    private ImageViewListener imageviewListener = new ImageViewListener();

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_top, this);
        m_tvTitle = findViewById(R.id.tv_title);
        tv_title_img = findViewById(R.id.tv_title_img);
        share = (ImageView) findViewById(R.id.right_my);

        /**
         * Hide share button when the context is mypageActivity
         */
        if (getContext().getClass().equals(MyPageActivity.class) || getContext().getClass().equals(TimerActivity.class) || getContext().getClass().equals(CategoryDetailActivity.class)) {
            share.setVisibility(View.INVISIBLE);
        }

        right_search = findViewById(R.id.right_search);
        left_back = findViewById(R.id.left_back);
        left_menu = findViewById(R.id.left_menu);

        share.setOnClickListener(imageviewListener);
        left_back.setOnClickListener(imageviewListener);
        left_menu.setOnClickListener(imageviewListener);
        right_search.setOnClickListener(imageviewListener);
    }

    public void setTitleName(String title) {
        m_tvTitle.setText(title);
        if(title.equals(getResources().getString(R.string.app_name))){
            tv_title_img.setVisibility(View.VISIBLE);
            m_tvTitle.setVisibility(View.INVISIBLE);
        }else{
            tv_title_img.setVisibility(View.INVISIBLE);
            m_tvTitle.setVisibility(View.VISIBLE);
        }
    }

    public void setViewVisibility(boolean showMenu, boolean showBack){
        if(showMenu) {
            left_menu.setVisibility(View.VISIBLE);
            right_search.setVisibility(View.VISIBLE);
        } else {
            left_menu.setVisibility(View.INVISIBLE);
            right_search.setVisibility(View.GONE);
        }
        if(showBack) left_back.setVisibility(View.VISIBLE);
        else left_back.setVisibility(View.INVISIBLE);
    }

    private class ImageViewListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.right_search : {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    getContext().startActivity(intent);
                    break;
                }
                case R.id.right_my: {
                    if (getContext().getClass().equals(MyPageActivity.class)) {
                        return;
                    }
                    Intent i = new Intent(getContext(), MyPageActivity.class);
                    getContext().startActivity(i);
//                    ((Activity) getContext()).finish();
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("image/*");
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "<" + v.getContext().getPackageManager().getApplicationLabel(v.getContext().getApplicationInfo()) + ">");
//                    intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + v.getContext().getPackageName());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getContext().startActivity(
//                            Intent.createChooser(intent, "Spreading the gospel"));
                }
                break;
                case R.id.left_back: {
                    ((Activity) getContext()).finish();
                }
                break;
                case R.id.left_menu: {
                    if(getContext() instanceof RankActivity){
                        ((RankActivity) getContext()).openDrawer();
                    }else if(getContext() instanceof CategoryActivity){
                        ((CategoryActivity) getContext()).openDrawer();
                    }
                }
            }
        }

    }
}
