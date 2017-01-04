package com.jiangruicheng.btlight.fragment;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiangruicheng.btlight.R;
import com.jiangruicheng.btlight.activity.MusicPlayService;
import com.jiangruicheng.btlight.adapter.MusicInfo;
import com.jiangruicheng.btlight.adapter.PlayStatu;
import com.jiangruicheng.btlight.adapter.PreData;
import com.jiangruicheng.btlight.adapter.SetMusicInFo;
import com.jiangruicheng.btlight.myview.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicScreen extends Fragment implements View.OnClickListener, OnGestureListener, DiscreteSeekBar.OnProgressChangeListener {
    private GestureDetector detector;
    private boolean PLAYSTATU = true;
    private ImageButton play;
    private ImageButton next;
    private ImageButton last;
    private ImageButton playmode;
    private View eq;
    private DiscreteSeekBar prog;
    private TextView timeshow;
    private TextView name;
    private Messenger messenger;
    private boolean ISconn;
    private RadioGroup musictable, otherapp;
    /*private MainActivity.MyOnTouchListen listener;*/
    private MusicList ablum_list;
    private MusicList allmusic_list;
    private MusicList artist_list;
    private OtherAppFragment otherAppFragment;

    private int JAZZ = 0;
    private int ROCK = 1;
    private int CLAZZ = 2;
    private int POP = 3;
    private int FLAT = 4;

    private String EQ_JAZZ = "JAZZ";
    private String EQ_CLA = "CLAS";
    private String EQ_FLAT = "OFF";
    private String EQ_ROCK = "ROCK";
    private String EQ_POP = "POP";

    private String EQ_SET = EQ_FLAT;


    PhoneBroadcastReceiver pbr = new PhoneBroadcastReceiver();

    public MusicScreen() {
    }

    private void setMessenger() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    if (ISconn) {
                        try {
                            Message sendmsg = new Message();
                            sendmsg.replyTo = mmeMessenger;
                            sendmsg.what = 0x02;
                            messenger.send(sendmsg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }.start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new GestureDetector(getActivity(), this);
        /*sendmsg = new Message();
        sendmsg.replyTo = mmeMessenger;*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        getActivity().registerReceiver(pbr, intentFilter);
        ablum_list = new MusicList(SetMusicInFo.getMedioList
                (getActivity(), SetMusicInFo.ALBUMS), null,
                MusicList.ABLUM);
        allmusic_list = new MusicList(SetMusicInFo.getMedioList
                (getActivity(), SetMusicInFo.ALLMUSICNAME), null,
                MusicList.MUSIC);
        artist_list = new MusicList(SetMusicInFo.getMedioList
                (getActivity(), SetMusicInFo.ARTIST), null, MusicList
                .ARTIST);
        otherAppFragment = new OtherAppFragment();
        Intent intent = new Intent(getActivity(), MusicPlayService.class);
        //  getActivity().startService(intent);
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id
                .music_screen, allmusic_list).commit();
        /*listener = new MainActivity.MyOnTouchListen() {
            @Override
            public boolean ontouch(MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        };*/
        setMessenger();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*((MainActivity) getActivity()).registerMyOnTouchListener(listener);*/
        /*if (!ISconn) {
            setMessenger();
        }*/
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("frag", "destroy");
        getActivity().unregisterReceiver(pbr);
        /*((MainActivity) getActivity()).unregisterMyOnTouchListener(listener);*/
        Intent intent = new Intent(getActivity(), MusicPlayService.class);
        intent.setAction(PlayStatu.PLAY_STOP);
        getActivity().startService(intent);
        getActivity().unbindService(conn);
    }

    private void set_EQ(int EQ, String text) {
        EQ_SET = text;
        Message message = Message.obtain();
        message.what = 0x03;
        Bundle bundle = new Bundle();
        bundle.putInt("EQ", EQ);
        message.setData(bundle);
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ((TextView) eq).setText(text);
        //((MainActivity) getActivity()).write(bytes, ListenTo.MODE_WRITEANDREPEAT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_music_screen, container, false);
/*        getActivity().getFragmentManager().beginTransaction().add(R.id.music_screen,
                music).commit();*/
        play = (ImageButton) v.findViewById(R.id.play);
        next = (ImageButton) v.findViewById(R.id.next);
        last = (ImageButton) v.findViewById(R.id.latest);
        prog = (DiscreteSeekBar) v.findViewById(R.id.seekBar);
        timeshow = (TextView) v.findViewById(R.id.time);
        name = (TextView) v.findViewById(R.id.musicname);
        eq = v.findViewById(R.id.eq);
        musictable = (RadioGroup) v.findViewById(R.id.musictable);
        playmode = (ImageButton) v.findViewById(R.id.playmode);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        last.setOnClickListener(this);
        prog.setOnProgressChangeListener(this);
        musictable.check(R.id.allmusic);
        eq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EQ_SET.equals(EQ_FLAT)) {
                    set_EQ(JAZZ, EQ_JAZZ);
                } else if (EQ_SET.equals(EQ_JAZZ)) {
                    set_EQ(POP, EQ_POP);
                } else if (EQ_SET.equals(EQ_POP)) {
                    set_EQ(ROCK, EQ_ROCK);
                } else if (EQ_SET.equals(EQ_ROCK)) {
                    set_EQ(CLAZZ, EQ_CLA);
                } else if (EQ_SET.equals(EQ_CLA)) {
                    set_EQ(FLAT, EQ_FLAT);
                }
            }
        });
        playmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreData preData = new PreData(getActivity());
                int mode = preData.getData().getInt("playmode", -1);
                Message message = new Message();
                message.what = 0x01;
                Bundle data = new Bundle();
                switch (mode) {
                    case 0:
                        playmode.setBackgroundResource(R.drawable.player_btn_random_normal);
                        preData.setData().putInt("playmode", 1).commit();
                        data.putString("playmode", PlayStatu.PLAYORDER_RANDOM);
                        message.setData(data);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        playmode.setBackgroundResource(R.drawable.player_btn_repeat_normal);
                        preData.setData().putInt("playmode", 2).commit();
                        data.putString("playmode", PlayStatu.PLAYORDER_ORDER);
                        message.setData(data);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        playmode.setBackgroundResource(R.drawable.player_btn_repeatone_normal);
                        preData.setData().putInt("playmode", 0).commit();
                        data.putString("playmode", PlayStatu.PLAYORDER_ONLYONE);
                        message.setData(data);
                        try {
                            messenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

        musictable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                switch (checkedId) {
                    case R.id.allmusic:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.music_screen, allmusic_list).commit();
                        break;
                    case R.id.artist:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.music_screen, artist_list).commit();
                        break;
                    case R.id.album:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.music_screen, ablum_list).commit();
                        break;
                    case R.id.otherapp:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.music_screen, otherAppFragment).commit();
                        break;
                }
            }
        });
        initplaymode();
        return v;
    }

    private void initplaymode() {
        PreData preData = new PreData(getActivity());
        int mode = preData.getData().getInt("playmode", -1);
        if (mode == -1) {
            preData.setData().putInt("playmode", 0).commit();
            mode = 0;
        }
       /* Message message = new Message();
        message.what = 0x01;*/
        //Bundle data = new Bundle();
        switch (mode) {
            case 0:
                playmode.setBackgroundResource(R.drawable.player_btn_repeatone_normal);

                /*data.putString("playmode", PlayStatu.PLAYORDER_ORDER);
                message.setData(data);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
                break;
            case 1:
                playmode.setBackgroundResource(R.drawable.player_btn_random_normal);
                // data.putString("playmode", PlayStatu.PLAYORDER_ONLYONE);
                /*message.setData(data);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
                break;
            case 2:
                // data.putString("playmode", PlayStatu.PLAYORDER_RANDOM);
                /*message.setData(data);
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
                playmode.setBackgroundResource(R.drawable.player_btn_repeat_normal);

                break;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("music", "stop");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), MusicPlayService.class);
        switch (v.getId()) {
            case R.id.play:
                if (PLAYSTATU) {
                    intent.setAction(PlayStatu.PLAYSTATU_PAUSE);
                    getActivity().startService(intent);
                    if (MusicPlayService.ISplay) {
                        play.setBackgroundResource(R.drawable.play);
                        PLAYSTATU = false;
                    }
                    break;
                } else {
                    intent.setAction(PlayStatu.PLAYSTATU_START);
                    getActivity().startService(intent);
                    play.setBackgroundResource(R.drawable.pause);
                    PLAYSTATU = true;
                    break;
                }

            case R.id.next:
                intent.setAction(PlayStatu.PLAYSTATU_NEXT);
                getActivity().startService(intent);
                break;
            case R.id.latest:
                intent.setAction(PlayStatu.PLAYSTATU_LAST);
                getActivity().startService(intent);
                break;

        }

    }


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            ISconn = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            ISconn = false;
        }
    };


/*
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent intent = new Intent(getActivity(), MusicPlayService.class);
        intent.putExtra("seek", seekBar.getProgress());
        intent.setAction(PlayStatu.PLAYSTATU_SEEK);
        getActivity().startService(intent);
    }
*/

    Messenger mmeMessenger = new Messenger(new Handler() {
        int time;
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PlayStatu.PLAY_MUSICNAME:
                    Bundle bundle = msg.getData();
                    MusicInfo musicInfo = (MusicInfo) bundle.getSerializable("musicinfo");
                    name.setText(musicInfo.getTitle());
                    time = bundle.getInt("time");
                    prog.setMax(time);
                    play.setBackgroundResource(R.drawable.pause);
                    PLAYSTATU = true;
                    break;
                case PlayStatu.PLAY_MUSICPROGRESS:
                    prog.setProgress(msg.arg1);
                    Date currentTime = new Date(time - msg.arg1);
                    String dateString = formatter.format(currentTime);
                    timeshow.setText(dateString);
                    break;
                case PlayStatu.PLAYSTATU_COMPLET:

                    break;
                case PlayStatu.PLAY_VISUALIZER:
                    /*((MainActivity) getActivity()).write(Command.SetBright((byte) msg.arg1), ListenTo.MODE_WRITE);*/
                    break;
            }
        }
    });


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float
            distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float
            velocityY) {
        if (e1.getX() - e2.getX() > 240) {
            switch (musictable.getCheckedRadioButtonId()) {
                case R.id.allmusic:
                    musictable.check(R.id.album);
                    break;
                case R.id.album:
                    musictable.check(R.id.artist);
                    break;
                case R.id.artist:
                    musictable.check(R.id.allmusic);
                    break;

            }

        }
        if (e2.getX() - e1.getX() > 240) {
            switch (musictable.getCheckedRadioButtonId()) {
                case R.id.allmusic:
                    musictable.check(R.id.artist);
                    break;
                case R.id.album:
                    musictable.check(R.id.allmusic);
                    break;
                case R.id.artist:
                    musictable.check(R.id.album);
                    break;

            }

        }
        return false;
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        Intent intent = new Intent(getActivity(), MusicPlayService.class);
        intent.putExtra("seek", seekBar.getProgress());
        intent.setAction(PlayStatu.PLAYSTATU_SEEK);
        getActivity().startService(intent);
    }

    public class PhoneBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "message";
        private boolean mIncomingFlag = false;
        private String mIncomingNumber = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            // 如果是拨打电话
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                if (PLAYSTATU) {
                    Intent i = new Intent(getActivity(), MusicPlayService.class);
                    i.setAction(PlayStatu.PLAYSTATU_PAUSE);
                    getActivity().startService(i);
                    if (MusicPlayService.ISplay) {
                        play.setBackgroundResource(R.drawable.play);
                        PLAYSTATU = false;
                    }
                }
                mIncomingFlag = false;

                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.i(TAG, "call OUT:" + phoneNumber);

            } else {
                // 如果是来电
                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tManager.getCallState()) {

                    case TelephonyManager.CALL_STATE_RINGING:
                        mIncomingNumber = intent.getStringExtra("incoming_number");
                        Log.i(TAG, "RINGING :" + mIncomingNumber);
                        if (PLAYSTATU) {
                            Intent i = new Intent(getActivity(), MusicPlayService.class);
                            i.setAction(PlayStatu.PLAYSTATU_PAUSE);
                            getActivity().startService(i);
                            if (MusicPlayService.ISplay) {
                                play.setBackgroundResource(R.drawable.play);
                                PLAYSTATU = false;
                            }
                            break;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        /*if (mIncomingFlag) {
                            Log.i(TAG, "incoming ACCEPT :" + mIncomingNumber);
                        }
                        if (true) {
                            intent.setAction(PlayStatu.PLAYSTATU_PAUSE);
                            getActivity().startService(intent);
                            if (MusicPlayService.ISplay) {
                                play.setBackgroundResource(R.drawable.play);
                                PLAYSTATU = false;
                            }
                            break;
                        }*/
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mIncomingFlag) {
                            Log.i(TAG, "incoming IDLE");
                        }
                        break;
                }
            }
        }

    }

}
