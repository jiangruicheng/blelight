package com.jiangruicheng.btlight.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangruicheng.btlight.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {


    @BindView(R.id.alarm)
    TextView alarm;
    @BindView(R.id.alarm_show)
    TextView alarmShow;
    @BindView(R.id.alarm_next)
    TextView alarmNext;

    @OnClick(R.id.alarm_next)
    void alarm_next_onclick() {

    }

    @BindView(R.id.sleep_show)
    TextView sleepShow;
    @BindView(R.id.sleep_next)
    TextView sleepNext;

    @OnClick(R.id.sleep_next)
    void sleep_next_onclick() {

    }


    public TimerFragment() {
        // Required empty public constructor
    }

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
