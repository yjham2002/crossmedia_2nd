package com.ccmheaven.tube.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.RKclassichaeven.tube.R;
import com.RKclassichaeven.tube.models.CategoryBox;

/**
 * @author PickleCode
 * @description GridView for being inserted inside the scrollView with a category
 */

public class CategorizedExpandableHeightGridView extends GridView {

    private Context context;
    private CategoryBox categoryBox;
    private boolean expanded = false;

    public CategorizedExpandableHeightGridView(Context context){
        super(context);
        this.context = context;
    }

    public CategorizedExpandableHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CategorizedExpandableHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public CategoryBox getCategoryBox() {
        return categoryBox;
    }

    public void setCategoryBox(CategoryBox categoryBox) {
        this.categoryBox = categoryBox;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public LinearLayout getUnitView(CategoryBox categoryBox, int numOfColumn){
        setCategoryBox(categoryBox);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        this.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setNumColumns(numOfColumn);
        this.setHorizontalSpacing(8);
        this.setVerticalSpacing(8);
        this.setSelector(R.color.transparent);
        this.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        this.setExpanded(true);

        View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.unit_grid_header_layout, null);
        TextView caption = view.findViewById(R.id.caption);
        ImageView btn_caption = view.findViewById(R.id.btn_caption);

        btn_caption.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        caption.setText(categoryBox.getAlias());

        linearLayout.addView(view);
        linearLayout.addView(this);

        return linearLayout;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if (isExpanded()){
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}