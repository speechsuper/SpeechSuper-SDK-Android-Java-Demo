package com.example.recorder;

import android.content.Context;
import android.util.Log;
import com.speechsuper.SkEgn;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class SkegnManager {
    private static String TAG = "SkegnManager";
    private static  SkegnManager mSkegnManager;
    private SuperAudioManager mSuperAudioManager;

    private static long engine = 0L;

    private boolean isRunning = false;
    private Context mContext;
    private String recordPath;

    private SkegnManager(Context ctx) {
        mContext = ctx.getApplicationContext();
    }

    public static SkegnManager getInstance(Context context) {
        return mSkegnManager == null ? (mSkegnManager = new SkegnManager(context)) : mSkegnManager;
    }

    private String getExternalFilesDir(Context ctx){
        File targetDir = ctx.getExternalFilesDir((String)null);
        if (targetDir == null || !targetDir.exists()) {
            targetDir = ctx.getFilesDir();
        }
        return targetDir.getAbsolutePath();
    }

    private Boolean writeResToExternalStorage(String resName) {
        try {
            InputStream is = null;
            try {
                is = mContext.getAssets().open(resName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File file = new File(getExternalFilesDir(mContext), resName);
            File parentdir = file.getParentFile();
            if (parentdir != null && !parentdir.exists()) {
                parentdir.mkdirs();
            }
            BufferedInputStream bis = new BufferedInputStream(is, 8192);
            byte[] buf = new byte[8192];
            FileOutputStream bos = new FileOutputStream(file);

            int pos;
            while ((pos = bis.read(buf, 0, 8192)) > 0) {
                bos.write(buf, 0, pos);
            }

            bos.flush();
            bos.close();
            bis.close();
            is.close();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int initEngine(String appKey, String secretKey){
        if(engine != 0 ) {
            return 0;
        }
        if(mSuperAudioManager == null) {
            mSuperAudioManager = new SuperAudioManager(mContext);
        }
        JSONObject cfg = new JSONObject();
        try {
            cfg.put("appKey", appKey);
            cfg.put("secretKey", secretKey);

            // disable vad
            JSONObject vadObj = new JSONObject();
            vadObj.put("enable", 0);
            cfg.put("vad",vadObj);

            // enable sdkLog
            JSONObject sdkLogObj = new JSONObject();
            sdkLogObj.put("enable", 1);
            sdkLogObj.put("output", getExternalFilesDir(mContext) + "/sdklog.txt");
            sdkLogObj.put("level", 3);
            cfg.put("sdkLog", sdkLogObj);

            // Write native.res from assets to external storage
            if(!writeResToExternalStorage("native.res")) {
                Log.e(TAG, "Failed to write native.res");
                return -1;
            }
            cfg.put("native", getExternalFilesDir(mContext) + "/native.res");

            // Write native_cn.res from assets to external storage
            if(!writeResToExternalStorage("native_cn.res")) {
                Log.e(TAG, "Failed to write native_cn.res");
                return -1;
            }
            cfg.put("native_cn", getExternalFilesDir(mContext) + "/native_cn.res");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        engine = SkEgn.skegn_new(cfg.toString(), mContext);

        if(engine == 0){
            Log.e(TAG, "skegn_new failed");
            return -1;
        }
        return 0;
    }

    private SkEgn.skegn_callback mkCallback(final CallbackResult callbackResult){
        return new SkEgn.skegn_callback() {
            public int run(byte[] id, int type, byte[] data, int size) {
                if (type == SkEgn.SKEGN_MESSAGE_TYPE_JSON) {
                    String result = (new String(data, 0, size)).trim();
                    if (result.indexOf("errId") > -1) {
                        cancelSkegn();
                    }

                    callbackResult.run(result);
                }
                return 0;
            }
        };
    }


    public int startSkegn(JSONObject requests, CallbackResult callbackResult)  {
        if (isRunning) {
            Log.e(TAG, "wait record end");
            return -1;
        }

        try {
            JSONObject params = new JSONObject();
            JSONObject appObj = new JSONObject();
            appObj.put("userId", "123");

            JSONObject audioObj = new JSONObject();
            audioObj.put("audioType", "wav");
            audioObj.put("sampleRate", 16000);
            audioObj.put("channel", 1);
            audioObj.put("sampleBytes", 2);
            params.put("audio", audioObj);

            JSONObject vad = new JSONObject();
            vad.put("seek",200);
            vad.put("ref_length",1000);
            params.put("vad",vad);

            params.put("request", requests);

            params.put("coreProvideType", "native");

            byte[] id = new byte[64];
            int rv = 0;
            try {
                cancelSkegn();
                SkEgn.skegn_callback callback = mkCallback(callbackResult);

                rv = SkEgn.skegn_start(engine, params.toString(), id, callback, mContext);

            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (rv == -1) {
                Log.e(TAG, "skegn_start failed");
                return rv;
            }
            isRunning = true;

            recordPath = getExternalFilesDir(mContext) + File.separator + new String(id).trim() + ".pcm";
            mSuperAudioManager.startRecord(recordPath, new SuperAudioManager.Callback() {
                public void run(byte[] data, int size) {
                    if(isRunning && size >= 0) {
                        SkEgn.skegn_feed(engine, data, size);
                    }
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void stopSkegn(){
        if(isRunning) {
            isRunning = false;
            if (mSuperAudioManager != null) {
                mSuperAudioManager.stopRecord();
            }
            SkEgn.skegn_stop(engine);
        }
    }

    public void cancelSkegn(){
        if(isRunning) {
            isRunning = false;
            if (mSuperAudioManager != null) {
                mSuperAudioManager.stopRecord();
            }

        }
        if(engine != 0) {
            SkEgn.skegn_cancel(engine);
        }
    }

    public void deleteSkegn(){
        if(isRunning) {
            isRunning = false;
            if (mSuperAudioManager != null) {
                mSuperAudioManager.stopRecord();
                mSuperAudioManager.destory();
                mSuperAudioManager = null;
            }
        }
        if(engine != 0){
            SkEgn.skegn_delete(engine);
            engine = 0;
        }
    }


    public void replay(){
        if (mSuperAudioManager != null) {
            mSuperAudioManager.play(recordPath);
        }
    }

    public interface CallbackResult {
        void run(String data);
    }

}
