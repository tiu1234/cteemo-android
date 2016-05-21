package com.example.duang;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;



public class NewsScrollView extends ScrollView{
    public interface ScrollViewListener{
        void onScrollChanged(NewsScrollView scrollView, 
                             int x, int y, int oldx, int oldy);
    }
    private ScrollViewListener scrollViewListener = null;
    public NewsScrollView(Context context) {
        super(context);
    }

    public NewsScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NewsScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}