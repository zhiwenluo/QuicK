package com.uniquestudio.guide;

import java.util.ArrayList;

import com.uniquestudio.quick.QuicK;
import com.uniquestudio.quick.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GuideViewActivity extends Activity{
	
    private ViewPager viewPager;  
    private ArrayList<View> pageViews;  
    private ViewGroup main;  
    private ImageView[] imageViews ; 
    private ImageView imageView; 
    private ImageView firImageView , secondImageView , thirdImageView;
    private ImageButton lastGuide;
    private View lastViewPager, firstViewPager , secondViewPager , thirdViewPager;
	private Animation alphaAnimation = null;
	private ViewGroup group;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
        
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 1);
        
        LayoutInflater inflater = getLayoutInflater();  
        pageViews = new ArrayList<View>();  
        firstViewPager = inflater.inflate(R.layout.guide_item01, null);
        secondViewPager = inflater.inflate(R.layout.guide_item02, null);
        thirdViewPager = inflater.inflate(R.layout.guide_item03, null);
        lastViewPager = inflater.inflate(R.layout.guide_item04, null);
        
        
        if(id == 0) {
            
            pageViews.add(firstViewPager);  
            pageViews.add(secondViewPager);  
            pageViews.add(thirdViewPager);  
            pageViews.add(lastViewPager);   
        }else {
            pageViews.add(secondViewPager);  
            pageViews.add(thirdViewPager);  
            pageViews.add(lastViewPager);   
        }

  
        imageViews = new ImageView[pageViews.size()];  
        
        main = (ViewGroup)inflater.inflate(R.layout.guide_main, null);  
        group = (ViewGroup)main.findViewById(R.id.viewGroup);  
        alphaAnimation = AnimationUtils.loadAnimation(this  , R.anim.alpha_guide);
        
        firImageView = (ImageView) this.firstViewPager.findViewById(R.id.colorView);
        secondImageView = (ImageView) this.secondViewPager.findViewById(R.id.second_guide);
        thirdImageView = (ImageView) this.thirdViewPager.findViewById(R.id.third_guide);
        lastGuide = (ImageButton) this.lastViewPager.findViewById(R.id.last_guide_page);
        viewPager = (ViewPager)main.findViewById(R.id.guidePages);   
        
        if (id == 0) {
            secondImageView.setBackgroundResource(R.drawable.guide_2);
            thirdImageView.setBackgroundResource(R.drawable.guide_3);
            lastGuide.setBackgroundResource(R.drawable.guide_4);
        }else {
            secondImageView.setBackgroundResource(R.drawable.help_1);
            thirdImageView.setBackgroundResource(R.drawable.help_2);
            lastGuide.setBackgroundResource(R.drawable.help_3);
        }
        
        for (int i = 0; i < pageViews.size(); i++) {  
            imageView = new ImageView(GuideViewActivity.this);  
            imageView.setLayoutParams(new LayoutParams(20,20));  
            imageView.setPadding(20, 0, 20, 0);  
            imageViews[i] = imageView;  
            
            if (i == 0) {  
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.page_indicator);  
            }  
            
            group.addView(imageViews[i]);  
        }  
        
        setContentView(main); 
        alphaAnimation.setAnimationListener(new myAnimationListener());
        firImageView.startAnimation(alphaAnimation);
        
        viewPager.setAdapter(new GuidePageAdapter());  
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());  
        lastGuide.setOnClickListener(new buttonClickListener());
        
    }
    
    private class myAnimationListener implements AnimationListener{

        @Override
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub
            firImageView.startAnimation(animation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    class buttonClickListener implements android.view.View.OnClickListener{

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            GoToMainActivity();
        }
        
    }
    
    void GoToMainActivity() {
        // TODO Auto-generated method stub
        Intent i = new Intent(GuideViewActivity.this,QuicK.class);
        startActivity(i);
        finish();
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            firImageView.clearAnimation();
            GoToMainActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    /** 指引页面Adapter */
    class GuidePageAdapter extends PagerAdapter {  
    	  
        @Override  
        public int getCount() {  
            return pageViews.size();  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(pageViews.get(arg1));  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).addView(pageViews.get(arg1));  
            return pageViews.get(arg1);  
        }  
        private android.view.View.OnClickListener myOnclickListener() {
            // TODO Auto-generated method stub
            GoToMainActivity();
            return null;
        }
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    } 
    
    /** 指引页面改监听器 */
    class GuidePageChangeListener implements OnPageChangeListener {  
  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageSelected(int arg0) {  
            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[arg0].setBackgroundResource(R.drawable.page_indicator_focused);
                if (arg0 != i) {  
                    imageViews[i].setBackgroundResource(R.drawable.page_indicator);  
                }  
            }

        }  
    }

 
    
    
}