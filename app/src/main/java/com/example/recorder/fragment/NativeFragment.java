package com.example.recorder.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.recorder.R;
import com.example.recorder.TestActivity;


public class NativeFragment extends Fragment implements View.OnClickListener{

    private Button mBtn_word;
    private Button mBtn_sentence;
    private Button mBtn_paragraph;


    private Button mBtn_cn_word;
    private Button mBtn_cn_sent;
    private Button mBtn_cn_para;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_native, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtn_word = (Button) view.findViewById(R.id.mBtn_word);
        mBtn_sentence = (Button) view.findViewById(R.id.mBtn_sentence);
        mBtn_paragraph = (Button) view.findViewById(R.id.mBtn_paragraph);

        mBtn_cn_word = (Button) view.findViewById(R.id.mBtn_chineseword);
        mBtn_cn_sent = (Button) view.findViewById(R.id.mBtn_cn_sent);
        mBtn_cn_para = (Button) view.findViewById(R.id.mBtn_cn_para);


        mBtn_word.setOnClickListener(this);
        mBtn_sentence.setOnClickListener(this);
        mBtn_paragraph.setOnClickListener(this);

        mBtn_cn_word.setOnClickListener(this);
        mBtn_cn_sent.setOnClickListener(this);
        mBtn_cn_para.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mBtn_word:

                //English word evaluation
                TestActivity.gotoTestActivity(getActivity(), "word.eval", "English word");
                break;
            case R.id.mBtn_sentence:
                //English sentence evaluation
                TestActivity.gotoTestActivity(getActivity(),"sent.eval", "English sentence");
                break;
            case R.id.mBtn_paragraph:
                //English paragraph evaluation
                TestActivity.gotoTestActivity(getActivity(), "para.eval", "English paragraph");
                break;
            case R.id.mBtn_chineseword:
                //Chinese word evaluation
                TestActivity.gotoTestActivity(getActivity(), "word.eval.cn", "Chinese word");
                break;
            case R.id.mBtn_cn_sent:
                //Chinese sentence evaluation
                TestActivity.gotoTestActivity(getActivity(),"sent.eval.cn", "Chinese sentence");
                break;
            case R.id.mBtn_cn_para:
                //Chinese paragraph evaluation
                TestActivity.gotoTestActivity(getActivity(), "para.eval.cn", "Chinese paragraph");

                break;
        }
    }
}
