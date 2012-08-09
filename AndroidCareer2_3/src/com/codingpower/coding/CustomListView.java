package com.codingpower.coding;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 自定义下拉刷新listView
 * @author fortransit
 *
 */
public class CustomListView extends ListView implements OnScrollListener {

    private Context context;

    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    private final static int RATIO = 3;

    public LinearLayout headView;
    private TextView headTipsTextview;
    private TextView headLastUpdatedTextView;
    private ImageView headArrowImageView;
    private ProgressBar headProgressBar;

    private LinearLayout footView;
    private ProgressBar footProgressBar;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private boolean isRecored;

    private int headContentWidth;
    private int headContentHeight;

    private int startY;
    private int firstItemIndex;

    public int state;

    private boolean isBack;

    private OnUpdateListener updateListener;

    private boolean isRefreshable;

    public CustomListView(Context context) {
        super(context);
        init(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setCacheColorHint(context.getResources().getColor(android.R.color.transparent));
        setFooterDividersEnabled(true);
        setHeaderDividersEnabled(true);
        initHead();
        initFoot();
        state = DONE;
        isRefreshable = false;
        setOnScrollListener(this);

    }

    private void initHead() {
        headView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.customlist_head, null);
        headArrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
        headArrowImageView.setMinimumWidth(70);
        headArrowImageView.setMinimumHeight(50);
        headProgressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
        headTipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
        headLastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

        measureView(headView);
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        Log.v("size", "width:" + headContentWidth + " height:" + headContentHeight);

        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        addHeaderView(headView, null, false);
    }

    private void initFoot() {
        footView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.customlist_foot, null);
        footView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdateMore();
                footProgressBar.setVisibility(View.VISIBLE);
            }
        });
        footProgressBar = (ProgressBar) footView.findViewById(R.id.foot_progressBar);
        addFooterView(footView, null, false);
    }

    @Override
    public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) {
        firstItemIndex = firstVisiableItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isRefreshable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN :
                    if ((firstItemIndex == 0) && !isRecored) {
                        isRecored = true;
                        startY = (int) event.getY();
                    }
                    break;

                case MotionEvent.ACTION_UP :

                    if ((state != REFRESHING) && (state != LOADING)) {
                        if (state == DONE) {
                        }
                        if (state == PULL_To_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();

                        }
                        if (state == RELEASE_To_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onUpdateLast();

                        }
                    }

                    isRecored = false;
                    isBack = false;

                    break;

                case MotionEvent.ACTION_MOVE :
                    int tempY = (int) event.getY();

                    if (!isRecored && (firstItemIndex == 0)) {
                        isRecored = true;
                        startY = tempY;
                    }

                    if ((state != REFRESHING) && isRecored && (state != LOADING)) {

                        if (state == RELEASE_To_REFRESH) {

                            setSelection(0);

                            if ((((tempY - startY) / RATIO) < headContentHeight) && ((tempY - startY) > 0)) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            } else if ((tempY - startY) <= 0) {
                                state = DONE;
                                changeHeaderViewByState();

                            } else {
                            }
                        }
                        if (state == PULL_To_REFRESH) {

                            setSelection(0);

                            if (((tempY - startY) / RATIO) >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();

                            } else if ((tempY - startY) <= 0) {
                                state = DONE;
                                changeHeaderViewByState();

                            }
                        }

                        if (state == DONE) {
                            if ((tempY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                        }

                        if (state == PULL_To_REFRESH) {
                            headView.setPadding(0, (-1 * headContentHeight) + ((tempY - startY) / RATIO), 0, 0);

                        }

                        if (state == RELEASE_To_REFRESH) {
                            headView.setPadding(0, ((tempY - startY) / RATIO) - headContentHeight, 0, 0);
                        }

                    }

                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    public void start() {
        state = REFRESHING;
        changeHeaderViewByState();
    }

    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH :
                headArrowImageView.setVisibility(View.VISIBLE);
                headProgressBar.setVisibility(View.GONE);
                headTipsTextview.setVisibility(View.VISIBLE);
                headLastUpdatedTextView.setVisibility(View.VISIBLE);

                headArrowImageView.clearAnimation();
                headArrowImageView.startAnimation(animation);

                headTipsTextview.setText(R.string.custom_list_lossen_refresh);

                break;
            case PULL_To_REFRESH :
                headProgressBar.setVisibility(View.GONE);
                headTipsTextview.setVisibility(View.VISIBLE);
                headLastUpdatedTextView.setVisibility(View.VISIBLE);
                headArrowImageView.clearAnimation();
                headArrowImageView.setVisibility(View.VISIBLE);
                if (isBack) {
                    isBack = false;
                    headArrowImageView.clearAnimation();
                    headArrowImageView.startAnimation(reverseAnimation);
                }
                headTipsTextview.setText(R.string.custom_list_down_refresh);
                break;

            case REFRESHING :

                headView.setPadding(0, 0, 0, 0);

                headProgressBar.setVisibility(View.VISIBLE);
                headArrowImageView.clearAnimation();
                headArrowImageView.setVisibility(View.GONE);
                headTipsTextview.setText(R.string.custom_list_refresh);
                headLastUpdatedTextView.setVisibility(View.VISIBLE);

                break;
            case DONE :
                headView.setPadding(0, -1 * headContentHeight, 0, 0);

                headProgressBar.setVisibility(View.GONE);
                headArrowImageView.clearAnimation();
                headArrowImageView.setImageResource(R.drawable.arrow);
                headTipsTextview.setText(R.string.custom_list_down_refresh);
                headLastUpdatedTextView.setVisibility(View.VISIBLE);

                break;
        }
    }

    public void setonRefreshListener(OnUpdateListener updateListener) {
        this.updateListener = updateListener;
        isRefreshable = true;
    }

    public interface OnUpdateListener {
        public void onUpdateLast();

        public void onUpdateMore();
    }

    public void onUpdateLastComplete() {
        state = DONE;
        headLastUpdatedTextView.setText(context.getString(R.string.custom_list_last_updated) + new Date().toLocaleString());
        changeHeaderViewByState();
        footProgressBar.setVisibility(View.GONE);
    }

    public void onUpdateMoreComplete() {
        state = DONE;
        footProgressBar.setVisibility(View.GONE);
    }

    private void onUpdateLast() {
        if (updateListener != null) {
            updateListener.onUpdateLast();
        }
    }

    private void onUpdateMore() {
        if (updateListener != null) {
            updateListener.onUpdateMore();
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {
        headLastUpdatedTextView.setText(context.getString(R.string.custom_list_last_updated) + new Date().toLocaleString());
        super.setAdapter(adapter);
    }

}
