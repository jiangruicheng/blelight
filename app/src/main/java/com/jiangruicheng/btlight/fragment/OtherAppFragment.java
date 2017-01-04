package com.jiangruicheng.btlight.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiangruicheng.btlight.R;
import com.jiangruicheng.btlight.Utill;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherAppFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.wangyi)
    ImageView wangyi;
    @BindView(R.id.qq)
    ImageView qq;
    @BindView(R.id.kugou)
    ImageView kugou;
    @BindView(R.id.kuwo)
    ImageView kuwo;
    @BindView(R.id.spotify)
    ImageView spotify;

    public OtherAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_other_app, container, false);
        ButterKnife.bind(this, view);
        qq.setOnClickListener(this);
        wangyi.setOnClickListener(this);
        kugou.setOnClickListener(this);
        kuwo.setOnClickListener(this);
        spotify.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qq:
                Utill.launchApp(getContext(), Utill.QQPLAY);
                break;
            case R.id.wangyi:
                Utill.launchApp(getContext(), Utill.WANGYI);
                break;
            case R.id.kugou:
                Utill.launchApp(getContext(), Utill.KUGOU);
                break;
            case R.id.kuwo:
                Utill.launchApp(getContext(), Utill.KUWO);
                break;
            case R.id.spotify:
                Utill.launchApp(getContext(), Utill.SPOTIFY_PACKAGE_NAME);
                break;

        }
    }
}
