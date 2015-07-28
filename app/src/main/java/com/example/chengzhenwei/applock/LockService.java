package com.example.chengzhenwei.applock;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

public class LockService extends Service {
    private final String TAG = "LockService";

    private Handler mHandler = null;
    private final static int LOOPHANDLER = 0;
    private HandlerThread handlerThread = null;

    private final List<String> lockName = new ArrayList<String>();

    private boolean isUnLockActivity = false;

    int locC=0;
    //每隔100ms检查一次
    private static long cycleTime = 1000;
    public LockService() {
    }
    public void onCreate(){
        super.onCreate();
        handlerThread = new HandlerThread("count_thread");
        handlerThread.start();
        lockName.add("com.example.chengzhenwei.applock");

        //开始循环检查
        mHandler = new Handler(handlerThread.getLooper()) {
            public void dispatchMessage(android.os.Message msg) {
                switch (msg.what) {
                    case LOOPHANDLER:
                        Log.i(TAG,"do something..."+(System.currentTimeMillis()/1000));
                        /**
                         * 这里需要注意的是：isLockName是用来判断当前的topActivity是不是我们需要加锁的应用
                         * 同时还是需要做一个判断，就是是否已经对这个app加过锁了，不然会出现一个问题
                         * 当我们打开app时，启动我们的加锁界面，解锁之后，回到了app,但是这时候又发现栈顶app是
                         * 需要加锁的app,那么这时候又启动了我们加锁界面，这样就出现死循环了。
                         * 可以自行的实验一下
                         * 所以这里用isUnLockActivity变量来做判断的
                         */
                        SharedPreferences count = getSharedPreferences("count", 0);
                        int locC=count.getInt("count", 0);
                        if(isLockName() && !isUnLockActivity&&locC==0){
                            Log.i(TAG, "locking...");
                            Intent intent = new Intent(LockService.this,LockS.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //调用了解锁界面之后，需要设置一下isUnLockActivity的值
                            isUnLockActivity = true;
                        }
                        break;
                }
                mHandler.sendEmptyMessageDelayed(LOOPHANDLER, cycleTime);

            }
        };
        mHandler.sendEmptyMessage(LOOPHANDLER);

    }


    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * 判断当前的Activity是不是我们开启解锁界面的app
     * @return
     */
    private boolean isLockName(){
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String packageName = topActivity.getPackageName();

        //如果当前的Activity是桌面app,那么就需要将isUnLockActivity清空值,locc值为0
        if(getHomes().contains(packageName)||(!packageName.equals("com.example.chengzhenwei.applock"))){
            isUnLockActivity = false;
            SharedPreferences count = getSharedPreferences("count", 0);
            locC=count.getInt("count", 0);
        }
        Log.v("LockService", "packageName == " + packageName);
        if("com.example.chengzhenwei.applock".equals(packageName)){
            return true;
        }
        return false;
    }
    /**
     * 返回所有桌面app的包名
     * @return
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        //属性

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo){
            names.add(ri.activityInfo.packageName);
            System.out.println(ri.activityInfo.packageName);
        }
        return names;
    }



}
