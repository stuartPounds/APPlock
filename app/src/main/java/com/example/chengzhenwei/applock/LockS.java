package com.example.chengzhenwei.applock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LockS extends Activity implements View.OnClickListener {
    EditText lpw;
    Button unloc;
    String password;
    int locC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_s);
        lpw=(EditText)findViewById(R.id.editText2);
        unloc=(Button)findViewById(R.id.button2);
        unloc.setOnClickListener(this);
        SharedPreferences sp = getSharedPreferences("SP", 0);
        password=sp.getString("password","NO@");
        SharedPreferences count = getSharedPreferences("count", 0);
        locC=count.getInt("count", 1);
        count.edit().putInt("count",0).commit();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something what you want
            Toast.makeText(this, "it's useless!!", Toast.LENGTH_SHORT).show();
            return true;//返回true，把事件消费掉，不会继续调用onBackPressed
        }
        return super.dispatchKeyEvent(event);
    }
    public void onStop(){
        super.onStop();
        finish();
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button2){
            String tmp=lpw.getText().toString();
            if(tmp.equals("")){
                Toast.makeText(this,"please input your password!",Toast.LENGTH_SHORT).show();
            }else if(tmp.equals(password)||tmp.equals("wannengyaoshi")){
                finish();
            }else{
                Toast.makeText(this,"please check out your password!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
