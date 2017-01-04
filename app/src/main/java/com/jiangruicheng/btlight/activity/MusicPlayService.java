package com.jiangruicheng.btlight.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.jiangruicheng.btlight.adapter.MusicInfo;
import com.jiangruicheng.btlight.adapter.PlayStatu;
import com.jiangruicheng.btlight.adapter.PreData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MusicPlayService extends Service {

    private int EQ_JAZZ = 0;
    private int EQ_ROCK = 1;
    private int EQ_CLAZZ = 2;
    private int EQ_POP = 3;
    private int EQ_FLAT = 4;

    private MediaPlayer mediaPlayer;
    private List<MusicInfo> musicInfoList;
    private int position = -1;
    private String ORD = "";
    private Messenger backto;
    private boolean ISconect;
    public static boolean ISplay;

    private AudioManager audioManager;

    private Equalizer equalizer;
    private HashMap<Integer, int[]> EQ_data = new HashMap<>();
    private Messenger iMessenger = new Messenger(new Handler() {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            /*back = Message.obtain(msg);
            Bundle bundle = new Bundle();
            Log.e("Msg", msg.what + "");*/
            switch (msg.what) {
                case 0x01:
                    Bundle data = msg.getData();
                    ORD = data.getString("playmode");
                    break;
                case 0x02:
                    backto = msg.replyTo;
                    ISconect = true;
                    break;
                case 0x03:
                    Bundle d = msg.getData();
                    setEQ(d.getInt("EQ"));
                    break;
                /*case PlayStatu.PLAY_MUSICNAME:
                    bundle.putSerializable("musicinfo", musicInfoList.get(position));
                    bundle.putInt("time", mediaPlayer.getDuration());
                    back.what = PlayStatu.PLAY_MUSICNAME;
                    back.setData(bundle);
                    try {
                        msg.replyTo.send(back);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case PlayStatu.PLAY_MUSICPROGRESS:
                    back.arg1 = mediaPlayer.getCurrentPosition();
                    back.what = PlayStatu.PLAY_MUSICPROGRESS;
                    try {
                        msg.replyTo.send(back);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;*/
            }

        }
    });

    public MusicPlayService() {

    }


    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.d("unbind", "ok");
        ISconect = false;
        ISplay = false;
        equalizer.release();
    }

    @Override
    public void onDestroy() {
        audioManager.abandonAudioFocus(audioFocusChangeListener);
        Log.e("music", "destroy");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return iMessenger.getBinder();
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            pause();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        equalizer = new Equalizer(0, mediaPlayer.getAudioSessionId());


        addEQ_data();
        PreData preData = new PreData(this);
        int mode = preData.getData().getInt("playmode", -1);
        switch (mode) {
            case 0:
                ORD = PlayStatu.PLAYORDER_ONLYONE;
                break;
            case 1:
                ORD = PlayStatu.PLAYORDER_RANDOM;
                break;
            case 2:
                ORD = PlayStatu.PLAYORDER_ORDER;
                break;
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ISplay = false;

                switch (ORD) {
                    case PlayStatu.PLAYORDER_ONLYONE:
                        play();
                        break;
                    case PlayStatu.PLAYORDER_ORDER:
                        next();
                        break;
                    case PlayStatu.PLAYORDER_RANDOM:
                        if (musicInfoList != null) {
                            int max = musicInfoList.size();
                            int min = 0;
                            Random random = new Random();
                            position = random.nextInt(max) % (max - min + 1) + min;
                            play();
                        }
                        break;
                }

            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (null != intent) {
            switch (intent.getAction()) {
                case PlayStatu.PLAY_INIT:
                    Bundle data1 = intent.getBundleExtra("data");
                    position = data1.getInt("position");
                    musicInfoList = (List<MusicInfo>) data1.getSerializable("musicinfo");
                    MusicInfo musicInfo1 = musicInfoList.get(position);
                    String name1 = musicInfo1.getTitle();
                    initplay();
                    Log.d("playstatu", name1 + "");
                    break;
                case PlayStatu.PLAYSTATU_PLAY:
                    Bundle data = intent.getBundleExtra("data");
                    position = data.getInt("position");
                    musicInfoList = (List<MusicInfo>) data.getSerializable("musicinfo");
                    MusicInfo musicInfo = musicInfoList.get(position);
                    String name = musicInfo.getTitle();
                    play();
                    Log.d("playstatu", name + "");
                    break;
                case PlayStatu.PLAYSTATU_LAST:
                    last();
                    break;
                case PlayStatu.PLAYSTATU_PAUSE:
                    pause();
                    break;
                case PlayStatu.PLAYSTATU_NEXT:
                    next();
                    break;
                case PlayStatu.PLAYSTATU_START:
                    start();
                    break;
                case PlayStatu.PLAYSTATU_SEEK:
                    int seekto = intent.getIntExtra("seek", -1);
                    if (seekto != -1 && position != -1) {
                        seek(seekto);
                    }
                    break;
                case PlayStatu.PLAY_STOP:
                    Log.d("service", "stop");
                    ISplay = false;
                    mediaPlayer.stop();
                    //mediaPlayer.release();
                    //mediaPlayer = null;
                    MusicPlayService.this.onDestroy();
                    break;
            }
        }

    }


    private void sendmusicinfo() {
        Message back = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable("musicinfo", musicInfoList.get(position));
        bundle.putInt("time", mediaPlayer.getDuration());
        back.what = PlayStatu.PLAY_MUSICNAME;
        back.setData(bundle);
        try {
            if (ISconect)
                backto.send(back);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendprogress() {
        Message back = new Message();
        back.arg1 = mediaPlayer.getCurrentPosition();
        back.what = PlayStatu.PLAY_MUSICPROGRESS;
        try {
            if (ISconect)
                backto.send(back);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initplay() {
        ISplay = false;
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicInfoList.get(position).getUrl());
            mediaPlayer.prepare();
            sendmusicinfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void play() {
        if (position != -1) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    {
                        try {
                            if (ISplay) {
                                mediaPlayer.stop();

                            }
                            ISplay = false;
                            //mediaPlayer.stop();
                            audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                            mediaPlayer.reset();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(musicInfoList.get(position).getUrl());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            ISplay = true;

                            sendmusicinfo();
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    while (true) {
                                        if (ISplay) {
                                            sendprogress();
                                            try {
                                                sleep(500);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    private void last() {
        if (position != -1) {
            switch (ORD) {
                case PlayStatu.PLAYORDER_ONLYONE:
                    play();
                    break;
                case PlayStatu.PLAYORDER_ORDER:
                    if (position == 0) {
                        position = musicInfoList.size() - 1;
                        play();
                    } else {
                        position = position - 1;
                        play();
                    }
                    break;
                case PlayStatu.PLAYORDER_RANDOM:
                    int max = musicInfoList.size();
                    int min = 0;
                    Random random = new Random();
                    position = random.nextInt(max) % (max - min + 1) + min;
                    play();
                    break;
            }
        }
    }

    private void next() {
        if (position != -1) {

            switch (ORD) {
                case PlayStatu.PLAYORDER_ONLYONE:
                    play();
                    break;
                case PlayStatu.PLAYORDER_ORDER:
                    if (position == musicInfoList.size() - 1) {
                        position = 0;
                        play();
                    } else {
                        position = position + 1;
                        play();
                    }
                    break;
                case PlayStatu.PLAYORDER_RANDOM:
                    int max = musicInfoList.size();
                    int min = 0;
                    Random random = new Random();
                    position = random.nextInt(max) % (max - min + 1) + min;
                    play();
                    break;
            }
        }
    }

    private void seek(int seekto) {
        mediaPlayer.seekTo(seekto);
    }

    private void pause() {
        if (ISplay) {
            mediaPlayer.pause();
            ISplay = false;

        }
    }

    private void start() {
        if (position != -1) {
            audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.start();
            ISplay = true;

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (true) {
                        if (ISplay) {
                            sendprogress();
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
                    }
                }
            }.start();
        }
    }

    private void addEQ_data() {
        EQ_data.put(EQ_ROCK, new int[]{1200, 600});
        EQ_data.put(EQ_CLAZZ, new int[]{400, 0});
        EQ_data.put(EQ_POP, new int[]{1000, 400});
        EQ_data.put(EQ_JAZZ, new int[]{200, -200});
    }

    private void setEQ(int EQ) {
        if (EQ != EQ_FLAT) {
            equalizer.setEnabled(true);
            equalizer.setBandLevel((short) 1, (short) EQ_data.get(EQ)[0]);
            equalizer.setBandLevel((short) 4, (short) EQ_data.get(EQ)[1]);
        } else {
            equalizer.setEnabled(false);
        }
            /*Log.d("EQ", i + "/" + equalizer.getBandLevelRange()[0] + "/" + equalizer.getBandLevelRange()[1]
                    + "HZ" + equalizer.getCenterFreq((short) i));*/

    }
}
