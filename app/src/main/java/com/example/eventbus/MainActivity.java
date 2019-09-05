package com.example.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().regist(this);

        startActivity(new Intent(this,SecondActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  getMessage(EventBean bean){
        Log.e("====》",bean.getOne()+bean.getTwo());
    }
}
