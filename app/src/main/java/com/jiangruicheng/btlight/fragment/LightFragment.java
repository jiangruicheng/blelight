package com.jiangruicheng.btlight.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangruicheng.btlight.R;
import com.jiangruicheng.btlight.RXbus.RxBus;
import com.jiangruicheng.btlight.data.Command;
import com.jiangruicheng.btlight.eventtype.SendCmd;
import com.jiangruicheng.btlight.myview.ColorPickView;
import com.jiangruicheng.btlight.myview.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class LightFragment extends Fragment {

    @BindView(R.id.color_picker_view)
    ColorPickView colorPickerView;
    @BindView(R.id.brightness)
    DiscreteSeekBar brightness;
    @BindView(R.id.led_imag)
    ImageView ledImag;

    @OnClick(R.id.led_imag)
    void onledimag() {
        isLED = ON;
        RxBus.getDefault().post(new SendCmd().setCmd(Command.SetMode(Command.DataByte.Mode_LED)));
        ledText.setTextColor(0xffff8000);
        colorText.setTextColor(0xff000000);
        romanticText.setTextColor(0xff000000);
        discoText.setTextColor(0xff000000);
    }

    @BindView(R.id.led_text)
    TextView ledText;
    @BindView(R.id.color_imag)
    ImageView colorImag;

    @OnClick(R.id.color_imag)
    void oncolorimag() {
        isLED = OFF;
        RxBus.getDefault().post(new SendCmd().setCmd(Command.SetMode(Command.DataByte.Mode_RGB)));
        ledText.setTextColor(0xff000000);
        colorText.setTextColor(0xffff8000);
        romanticText.setTextColor(0xff000000);
        discoText.setTextColor(0xff000000);
    }

    @BindView(R.id.color_text)
    TextView colorText;
    @BindView(R.id.romantic_imag)
    ImageView romanticImag;

    @OnClick(R.id.romantic_imag)
    void onromanticimag() {
        isLED = OFF;
        RxBus.getDefault().post(new SendCmd().setCmd(Command.SetMode(Command.DataByte.Mode_ROMANTIC)));
        ledText.setTextColor(0xff000000);
        colorText.setTextColor(0xff000000);
        romanticText.setTextColor(0xffff8000);
        discoText.setTextColor(0xff000000);
    }

    @BindView(R.id.romantic_text)
    TextView romanticText;
    @BindView(R.id.disco_imag)
    ImageView discoImag;

    @OnClick(R.id.disco_imag)
    void ondiscimag() {
        isLED = OFF;
        RxBus.getDefault().post(new SendCmd().setCmd(Command.SetMode(Command.DataByte.Mode_DISC)));
        ledText.setTextColor(0xff000000);
        colorText.setTextColor(0xff000000);
        romanticText.setTextColor(0xff000000);
        discoText.setTextColor(0xffff8000);
    }

    @BindView(R.id.disco_text)
    TextView discoText;
    @BindView(R.id.pw_sw)
    ImageView pwSw;
    private boolean PW = true;
    private boolean ON = true;
    private boolean OFF = false;
    private boolean isLED = false;

    @OnClick(R.id.pw_sw)
    void onpw() {
        if (PW) {
            PW = OFF;
            colorPickerView.setEnabled(false);
            RxBus.getDefault().post(new SendCmd().setCmd(Command.SetPower((byte) 0x00)));
        } else {
            PW = ON;
            colorPickerView.setEnabled(true);
            RxBus.getDefault().post(new SendCmd().setCmd(Command.SetPower((byte) 0x01)));
        }
    }

    public LightFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault().post(new SendCmd().setCmd(Command.SetMode(Command.DataByte.Mode_RGB)));
    }

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_light, container, false);
        unbinder = ButterKnife.bind(this, view);

        colorPickerView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(int color) {
                RxBus.getDefault().post(new SendCmd().setCmd(Command.SetRGB(color)));
            }
        });
        brightness.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (isLED) {
                    RxBus.getDefault().post(new SendCmd().setCmd(Command.SetBright((byte) seekBar.getProgress())));
                } else {
                    RxBus.getDefault().post(new SendCmd().setCmd(Command.SetLightRate((byte) seekBar.getProgress())));
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }


}
