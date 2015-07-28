package com.example.chengzhenwei.applock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener {
    String password;
    TextView explain;
    EditText pw;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        explain=(TextView)findViewById(R.id.textView);
        pw=(EditText)findViewById(R.id.editText);
        btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(this);

        if(checkPassword()){
            SharedPreferences sp = getSharedPreferences("SP", 0);
            password=sp.getString("password","default");
            Log.d("获取已存在密码", "获取已存在密码 "+password);
            Intent intent=new Intent();
            intent.putExtra("password",password);
            intent.setClass(this,LockS.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"you have no password, please create one!",Toast.LENGTH_SHORT).show();
        }

    }


    private boolean checkPassword() {
        SharedPreferences sp = getSharedPreferences("SP",0);
        if(sp.getString("password","NO@").equals("NO@")){
            return false;
        }else{
            return true;
        }
    }
    public void onStop(){
        super.onStop();
        Intent intent=new Intent();
        intent.setClass(this,LockService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.button){
            String tmp=pw.getText().toString();
            if(tmp.equals("")){
                Toast.makeText(this,"you have no password, please create one!",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"new password completed!!!",Toast.LENGTH_SHORT).show();
                SharedPreferences sp = getSharedPreferences("SP", 0);
                sp.edit().putString("password",tmp).commit();
                Log.d("new password commit",tmp);
            }
        }
    }
}
