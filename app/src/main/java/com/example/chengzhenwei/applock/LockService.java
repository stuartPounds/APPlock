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
    //ÿ��100ms���һ��
    private static long cycleTime = 1000;
    public LockService() {
    }
    public void onCreate(){
        super.onCreate();
        handlerThread = new HandlerThread("count_thread");
        handlerThread.start();
        lockName.add("com.example.chengzhenwei.applock");

        //��ʼѭ�����
        mHandler = new Handler(handlerThread.getLooper()) {
            public void dispatchMessage(android.os.Message msg) {
                switch (msg.what) {
                    case LOOPHANDLER:
                        Log.i(TAG,"do something..."+(System.currentTimeMillis()/1000));
                        /**
                         * ������Ҫע����ǣ�isLockName�������жϵ�ǰ��topActivity�ǲ���������Ҫ������Ӧ��
                         * ͬʱ������Ҫ��һ���жϣ������Ƿ��Ѿ������app�ӹ����ˣ���Ȼ�����һ������
                         * �����Ǵ�appʱ���������ǵļ������棬����֮�󣬻ص���app,������ʱ���ַ���ջ��app��
                         * ��Ҫ������app,��ô��ʱ�������������Ǽ������棬�����ͳ�����ѭ���ˡ�
                         * �������е�ʵ��һ��
                         * ����������isUnLockActivity���������жϵ�
                         */
                        SharedPreferences count = getSharedPreferences("count", 0);
                        int locC=count.getInt("count", 0);
                        if(isLockName() && !isUnLockActivity&&locC==0){
                            Log.i(TAG, "locking...");
                            Intent intent = new Intent(LockService.this,LockS.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //�����˽�������֮����Ҫ����һ��isUnLockActivity��ֵ
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
     * �жϵ�ǰ��Activity�ǲ������ǿ������������app
     * @return
     */
    private boolean isLockName(){
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String packageName = topActivity.getPackageName();

        //�����ǰ��Activity������app,��ô����Ҫ��isUnLockActivity���ֵ,loccֵΪ0
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
     * ������������app�İ���
     * @return
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        //����

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
