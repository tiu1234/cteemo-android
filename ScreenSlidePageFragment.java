
package com.example.duang;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.duang.R;
import com.example.duang.NewsScrollView.ScrollViewListener;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScreenSlidePageFragment extends Fragment{
	//public static int page = 0;
    public ViewGroup news_page;
	//private static JSONArray news_list;
    
    public static ScreenSlidePageFragment create(JSONArray new_news_list){
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        //news_list = new_news_list;
        return fragment;
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) LayoutInflater.from(container.getContext()).inflate(
                R.layout.news_page, container, false);
        news_page = rootView;
        if(NewsActivity.news_list == null){
			NewsActivity.update_news();
			if(NewsActivity.news_list == null){
				return rootView;
			}
        }
        //if(page == 0){
        	int i;
        	for(i = 0; i < NewsActivity.news_list.length(); i++){
        		try{
					NewsActivity.add_news((JSONObject)NewsActivity.news_list.get(i));
				}
        		catch(JSONException e){
					
				}
        	}
        	NewsScrollView scroll_news = (NewsScrollView)news_page.findViewById(R.id.scroll_news);
        	scroll_news.setScrollViewListener(new ScrollViewListener(){
				@Override
				public void onScrollChanged(NewsScrollView scrollView, int x,
						int y, int oldx, int oldy){
					View news_view = scrollView.getChildAt(scrollView.getChildCount()-1);
					if((news_view.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()+news_view.getTop()))<=0){
						//NewsActivity.post_toast("Bottom.");
					}
					news_view = scrollView.getChildAt(0);
					if((news_view.getTop()-(scrollView.getScrollY()))>=0){
						//NewsActivity.post_toast("top.");
						NewsActivity.update_news();
					}
					
				}
        	});
			NewsActivity.update_news();
        	//page++;
        //}
    	return rootView;
    }

}
