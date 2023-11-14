package com.example.recorder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recorder.adapter.TestWordListAdapter;
import com.example.recorder.entity.TestType;
import com.example.recorder.utils.JSONTool;
import com.example.recorder.widget.audiodialog.AudioRecoderDialog;
import com.example.recorder.widget.audiodialog.AudioRecoderUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Word, sentence, and passage reading evaluation.
 */

public class TestActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private Toolbar mToolbar;
    private TextView txt_test_content_name;
    private TextView txt_colorful_result;
    private TextView txt_result;

    private LinearLayout llayout_container;
    private TextView txt_total_result_expand;
    private TextView txt_total_result;
    private boolean isShowingTotalResult;

    private Button mBtn_start_test;
    private Button mBtn_replay;
    private LinearLayout rootView;
    private AudioRecoderDialog mRecoderDialog;
    private AudioRecoderUtils mRecoderUtils;

    private ListView mListView;
    private ArrayList<TestType> mTestTypeList = new ArrayList<>();
    private TestWordListAdapter mAdapter;

    private EditText mRefText;

    private String mTestContent = "";//Content to be evaluated.

    private static String mCoreType;
    private static String mTitle;

    public static void gotoTestActivity(Context context, String coreType, String title) {
        mCoreType = coreType;
        mTitle = title;
        Intent intent = new Intent();
        intent.setClass(context, TestActivity.class);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_word);
        initView();
        initData();
        initAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(mTitle);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRefText = (EditText) findViewById(R.id.mRefText);

        Paint paint = new Paint();
        Canvas canvas = new Canvas();
        int sroke_width = 1;
        //  Draw the 4 edges of TextView.
        canvas.drawLine(0, 0, mRefText.getWidth() - sroke_width, 0, paint);
        canvas.drawLine(0, 0, 0, mRefText.getHeight() - sroke_width, paint);
        canvas.drawLine(mRefText.getWidth() - sroke_width, 0, mRefText.getWidth() - sroke_width, mRefText.getHeight() - sroke_width, paint);
        canvas.drawLine(0, mRefText.getHeight() - sroke_width, mRefText.getWidth() - sroke_width, mRefText.getHeight() - sroke_width, paint);

        mRefText.onDrawForeground(canvas);

        txt_test_content_name = (TextView) findViewById(R.id.txt_test_content_name);
        //Enable textview to display phonetic symbols normally.
        Typeface mFace= Typeface.createFromAsset(getAssets(), "font/segoeui.ttf");
        txt_test_content_name.setTypeface(mFace);

        txt_colorful_result = (TextView) findViewById(R.id.txt_colorful_result);
        if(mCoreType.equals("sent.eval")){
            txt_colorful_result.setVisibility(View.VISIBLE);
        }else{
            txt_colorful_result.setVisibility(View.GONE);
        }

        txt_result = (TextView) findViewById(R.id.txt_result);
        txt_result.setTypeface(mFace);
        txt_result.setTextIsSelectable(true);
        llayout_container = (LinearLayout) findViewById(R.id.llayout_container);
        mListView = (ListView) findViewById(R.id.mListView);
        mBtn_start_test = (Button) findViewById(R.id.mBtn_start_test);
        mBtn_replay = (Button) findViewById(R.id.mBtn_replay);
        mBtn_start_test.setOnTouchListener(this);
        mBtn_replay.setOnClickListener(this);

        rootView = (LinearLayout) findViewById(R.id.rootView);
        //Popwindow displayed during initialization of recording.
        mRecoderUtils = new AudioRecoderUtils();
        mRecoderUtils.setOnAudioStatusUpdateListener(mOnAudioStatusUpdateListener);
        mRecoderDialog = new AudioRecoderDialog(this);
        mRecoderDialog.setShowAlpha(0.98f);
    }

    AudioRecoderUtils.OnAudioStatusUpdateListener mOnAudioStatusUpdateListener = new AudioRecoderUtils.OnAudioStatusUpdateListener() {
        @Override
        public void onUpdate(double db) {
            if (null != mRecoderDialog) {
                mRecoderDialog.setLevel((int) db);
            }
        }
    };

    private void initData() {
        TestType testType;
        if(mCoreType.equals("word.eval")){ // English word
            testType = new TestType(mCoreType, "hello");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "classmate");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "happy");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "new");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "year");
            mTestTypeList.add(testType);
        }else if(mCoreType.equals("sent.eval")){ // English sentence
            testType = new TestType(mCoreType, "Welcome to beijing.");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "You can go as far as you want to go.");
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "She is going to play volleyball.");
            mTestTypeList.add(testType);
        }else if(mCoreType.equals("para.eval")){ // English paragraph
            testType = new TestType(mCoreType, getString(R.string.passage_demo1));
            mTestTypeList.add(testType);
            testType = new TestType(mCoreType, getString(R.string.passage_demo2));
            mTestTypeList.add(testType);
        }
        else if(mCoreType.equals("word.eval.cn")){ // Chinese word
            testType = new TestType(mCoreType,"海");mTestTypeList.add(testType);
            testType = new TestType(mCoreType,"上");mTestTypeList.add(testType);
            testType = new TestType(mCoreType,"升");mTestTypeList.add(testType);
            testType = new TestType(mCoreType,"明");mTestTypeList.add(testType);
        }
        else if(mCoreType.equals("sent.eval.cn")){ // Chinese sentence
            testType = new TestType(mCoreType, "漂亮"); mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "一日之际在于晨"); mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "一年之际在于春"); mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "天地玄黄，宇宙洪荒。"); mTestTypeList.add(testType);
        }
        else if(mCoreType.equals("para.eval.cn")){ // Chinese paragraph
            testType = new TestType(mCoreType, "两个黄鹂鸣翠柳， 一行白鹭上青天。 窗含西岭千秋雪， 门泊东吴万里船"); mTestTypeList.add(testType);
            testType = new TestType(mCoreType, "白日依山近，黄河入海流，欲穷千里目，更上一层楼"); mTestTypeList.add(testType);
        }
        //By default, the first element is selected.
        mTestContent = mTestTypeList.get(0).getRefText();

        txt_test_content_name.setText("Current evaluation content: "+ mTestContent);
    }

    private void initAdapter() {
        mRefText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String refText = mRefText.getText()!=null ? mRefText.getText().toString().trim() : "";
                mTestContent = refText;
                txt_test_content_name.setText("Current evaluation content: "+ refText);
            }
        });

        mAdapter = new TestWordListAdapter(this, mTestTypeList);
        mAdapter.setSelectItem(0);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectItem(position);
                mAdapter.notifyDataSetInvalidated();

                mTestContent = mTestTypeList.get(position).getRefText();
                txt_test_content_name.setText("Current evaluation content: "+ mTestTypeList.get(position).getRefText());
                txt_result.setText("");
                txt_colorful_result.setText("");

                if(txt_total_result_expand != null){
                    txt_total_result_expand.setVisibility(View.GONE);
                    txt_total_result_expand = null;
                }
                if(txt_total_result != null){
                    txt_total_result.setVisibility(View.GONE);
                    txt_total_result = null;
                }
            }
        });
    }

    private void setColorfulResultForWord(JSONObject jsonObject){
        txt_colorful_result.setText("");
        try {
            JSONArray words = jsonObject.getJSONArray("words");
            SpannableStringBuilder styleWord0;
            JSONObject scoreJSONObject;
            JSONObject wordJSONObject;
            wordJSONObject = words.getJSONObject(0);
            JSONArray phonics= wordJSONObject.getJSONArray("phonemes");
            for (int i = 0; i < phonics.length(); i++){
                scoreJSONObject = phonics.getJSONObject(i);
                String phoneme= "/" + scoreJSONObject.getString("phoneme") + "/";
                int score = scoreJSONObject.getInt("pronunciation");
                styleWord0 = new SpannableStringBuilder(phoneme);
                if(score < 50){
                    styleWord0.setSpan(new ForegroundColorSpan(Color.RED), 0, phoneme.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if(score < 70){
                    styleWord0.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, phoneme.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    styleWord0.setSpan(new ForegroundColorSpan(Color.GREEN), 0, phoneme.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                txt_colorful_result.append(styleWord0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setColorfulResultForSent(JSONObject jsonObject) {
        txt_colorful_result.setText("");
        try {
            JSONArray wjsono = jsonObject.getJSONArray("words");
            JSONObject wordJSONObject;
            JSONObject scoreJSONObject;
            SpannableStringBuilder styleWord1;

            for(int j=0; j<wjsono.length(); j++){
                wordJSONObject = wjsono.getJSONObject(j);
                scoreJSONObject = wordJSONObject.getJSONObject("scores");
                String word = wordJSONObject.getString("word") + " ";
                int score = scoreJSONObject.getInt("overall");
                styleWord1 = new SpannableStringBuilder(word);
                if(score < 50){
                    styleWord1.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else if(score < 70){
                    styleWord1.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }else{
                    styleWord1.setSpan(new ForegroundColorSpan(Color.GREEN), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                txt_colorful_result.append(styleWord1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setColorfulResultForPara(JSONObject jsonObject) {
        txt_colorful_result.setText("");
        try {
            JSONArray sentjsono = jsonObject.getJSONArray("sentences");
            SpannableStringBuilder styleWord2;
            for(int j=0; j<sentjsono.length(); j++){
                JSONArray wordjsono = ((JSONObject)sentjsono.get(j)).getJSONArray("details");
                for(int i= 0;  i< wordjsono.length(); i++) {
                    String word = ((JSONObject)wordjsono.get(i)).getString("word") + " ";
                    int score = ((JSONObject)wordjsono.get(i)).getInt("overall");
                    styleWord2 = new SpannableStringBuilder(word);
                    if (score < 50) {
                        styleWord2.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (score < 70) {
                        styleWord2.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        styleWord2.setSpan(new ForegroundColorSpan(Color.GREEN), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    txt_colorful_result.append(styleWord2);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    SkegnManager.CallbackResult callbackResult = new SkegnManager.CallbackResult() {
        @Override
        public void run(final String result) {
            if(result.contains("vad_status"))
                return;

            TestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Stop recrord.
                    try {
                        final JSONObject json = new JSONObject(result);
                        setResult(result); //Parsing callback results.
                        if(mCoreType.equals("word.eval.cn") || mCoreType.equals("word.eval")){
                            if(json != null && json.has("result")){
                                setColorfulResultForWord(json.getJSONObject("result"));
                            }
                        }
                        if(mCoreType.equals("sent.eval.cn") || mCoreType.equals("sent.eval")){
                            if(json != null && json.has("result")){
                                setColorfulResultForSent(json.getJSONObject("result"));
                            }
                        }
                        if(mCoreType.equals("para.eval.cn") || mCoreType.equals("para.eval")){
                            if(json != null && json.has("result")){
                                setColorfulResultForPara(json.getJSONObject("result"));
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mBtn_start_test.setText(R.string.txt_start_test);
                    if (mRecoderDialog != null && mRecoderDialog.isShowing()) {
                        mRecoderDialog.dismiss();
                    }
                }
            });
        }
    };

    private void setResult(final String result) {
        StringBuilder keyValue = new StringBuilder();
        try{
            JSONObject returnObj = new JSONObject(result);
            JSONObject resultJSONObject = returnObj.getJSONObject("result");
            if(resultJSONObject.has("overall")){
                keyValue.append("Total score:" + resultJSONObject.getString("overall") + "\n");
            }
            if(mCoreType.contains("word.eval") || mCoreType.contains("word.eval.cn")){
                keyValue.append("Phoneme score:/");
                JSONArray wjsono = resultJSONObject.getJSONArray("words");
                JSONArray wjson = wjsono.getJSONObject(0).getJSONArray("phonemes");
                for(int i=0; i<wjson.length(); i++)
                {
                    keyValue.append(wjson.getJSONObject(i).getString("phoneme") + ":" + wjson.getJSONObject(i).getString("pronunciation") + " /");
                }
                keyValue.append("\n");
                if(resultJSONObject.has("stress")){
                    boolean stress = true;
                    keyValue.append("Is the rereading correct:");
                    String stressAll = resultJSONObject.getString("stress");
                    if("0".equals(stressAll)){
                        stress = false;
                    }
                    keyValue.append(stress?"Correct stress syllables":"Incorrect stressed syllables");
                    keyValue.append("\n");
                }

                if(wjsono.getJSONObject(0).getJSONObject("scores").has("stress")) {
                    JSONArray stressJson = wjsono.getJSONObject(0).getJSONObject("scores").getJSONArray("stress");
                    StringBuilder stressStr = new StringBuilder("Details of stressed syllables: ");
                    for (int j=0; j < stressJson.length(); j++){
                        String refStressStr = "";
                        String realStressStr = "";
                        int refStress =   stressJson.getJSONObject(j).getInt("ref_stress");
                        int realStress  = stressJson.getJSONObject(j).getInt("stress");
                        if("1".equals(refStress)){
                            refStressStr = "stressed syllable";
                        } else if ("2".equals(refStress)){
                            refStressStr = "Hypobaric syllable";
                        }else{
                            refStressStr = "Unstressed reading";
                        }
                        if(refStress == realStress){
                            realStressStr = "(correct)";
                        }else{
                            realStressStr = "(error)";
                        }
                        stressStr.append("/"+stressJson.getJSONObject(j).getString("phonetic")+"/" +" ");
                        stressStr.append(refStressStr );
                        stressStr.append( realStressStr  +" ");

                    }
                    keyValue.append(stressStr);
                }
            } else if(mCoreType.contains("sent.eval") || mCoreType.contains("sent.eval.cn")){
                if(resultJSONObject.has("pronunciation")){
                    keyValue.append("pronunciation score:" + resultJSONObject.getString("pronunciation") + "\n");
                }
                if(resultJSONObject.has("integrity")){
                    keyValue.append("integrity: " + resultJSONObject.getString("integrity") + "\n");
                }
                if(resultJSONObject.has("fluency")){
                    keyValue.append("fluency: " + resultJSONObject.getString("fluency") + "\n");
                }
                if(resultJSONObject.has("rhythm")){
                    keyValue.append("rhythm:" + resultJSONObject.getString("rhythm") + "\n");
                }
                keyValue.append("Word Score:\n");
                JSONArray wjsono = resultJSONObject.getJSONArray("words");
                for(int i = 0; i < wjsono.length(); i++){
                    String word = wjsono.getJSONObject(i).getString("word").replaceAll("\\.|\\,|\\!|\\;|\\?|\"", "");
                    if(word.startsWith("\'") || word.endsWith("\'")){
                        word = word.replace("\'", "");
                    }
                    keyValue.append(word + ": ");
                    keyValue.append(wjsono.getJSONObject(i).getJSONObject("scores").getString("overall") + " ");
                }
                keyValue.append("\n");
            } else if(mCoreType.equals("para.eval") || mCoreType.contains("para.eval.cn")){
                if(resultJSONObject.has("pronunciation")){
                    keyValue.append("pronunciation score:" + resultJSONObject.getString("pronunciation") + "\n");
                }
                if(resultJSONObject.has("integrity")){
                    keyValue.append("integrity score: " + resultJSONObject.getString("integrity") + "\n");
                }
                if(resultJSONObject.has("fluency")){
                    keyValue.append("fluency score: " + resultJSONObject.getString("fluency") + "\n");
                }
                if(resultJSONObject.has("rhythm")){
                    keyValue.append("rhythm score:" + resultJSONObject.getString("rhythm") + "\n");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        txt_result.setText(keyValue);

        txt_total_result_expand = new TextView(this);
        txt_total_result_expand.setText(Html.fromHtml("<u>"+"Click to view the complete results"+"</u>"));
        txt_total_result_expand.setTextColor(getResources().getColor(R.color.white));
        txt_total_result_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowingTotalResult){
                    txt_total_result.setText("");
                    txt_total_result.setVisibility(View.VISIBLE);
                    isShowingTotalResult = false;
                }else{
                    txt_total_result.setText(JSONTool.stringToJSON(result));
                    txt_total_result.setVisibility(View.VISIBLE);
                    isShowingTotalResult = true;
                }
            }
        });

        txt_total_result = new TextView(this);
        txt_total_result.setTextColor(getResources().getColor(R.color.white));
        txt_total_result.setTextIsSelectable(true);

        if(result.contains("errId")) {
            txt_total_result.setText(JSONTool.stringToJSON(result));
            txt_total_result.setVisibility(View.VISIBLE);
            isShowingTotalResult = true;
        }
        llayout_container.addView(txt_total_result_expand);
        llayout_container.addView(txt_total_result);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(txt_total_result_expand != null){
                    llayout_container.removeView(txt_total_result_expand);
                }
                if(txt_total_result != null){
                    llayout_container.removeView(txt_total_result);
                }
                txt_test_content_name.setText("Current evaluation content: " + mTestContent);
                try {
                    JSONObject requestObj = new JSONObject();
                    requestObj.put("coreType", mCoreType);
                    requestObj.put("refText", mTestContent);
                    int ret = SkegnManager.getInstance(this).startSkegn(requestObj, callbackResult);
                    if(ret == -1) {
                        Toast.makeText(this, "Failed to enable evaluation", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    mBtn_start_test.setText(R.string.txt_stop_test);
                    mRecoderUtils.startRecord();
                    mRecoderDialog.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            case MotionEvent.ACTION_UP:
                SkegnManager.getInstance(this).stopSkegn(); //Stop evaluation.
                mBtn_start_test.setText(R.string.txt_start_test);
                mRecoderUtils.stopRecord();
                mRecoderDialog.dismiss();
                return true;
            case MotionEvent.ACTION_CANCEL:
                //The operation here is to solve the problem of clicking the evaluation button in Android 5. x version before popping up the permission pop-up box, which causes the recording and animation to be unable to be cancelled.
                SkegnManager.getInstance(this).stopSkegn(); //Stop evaluation.
                mBtn_start_test.setText(R.string.txt_start_test);
                mRecoderUtils.stopRecord();
                mRecoderDialog.dismiss();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mBtn_replay:
                //playback
                SkegnManager.getInstance(this).replay();
                break;
        }
    }

}