package com.uniquestudio.guide;

import com.uniquestudio.quick.QuicK;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Guide extends Activity {
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        sharedPreferences = getSharedPreferences("guide_number",
                Activity.MODE_PRIVATE);
        GoToWhickActivity();
        super.onCreate(savedInstanceState);
    }

    void GoToWhickActivity() {
        Intent intent = new Intent();
        if (sharedPreferences.getBoolean("isFirstTime", true)) {
            intent.setClass(Guide.this, GuideViewActivity.class);
            sharedPreferences.edit().putBoolean("isFirstTime", false).commit();
        } else {
            intent.setClass(Guide.this, QuicK.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        GoToWhickActivity();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        GoToWhickActivity();
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

}
