package com.jiangruicheng.btlight.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jiangruicheng.btlight.R;
import com.jiangruicheng.btlight.activity.MusicPlayService;
import com.jiangruicheng.btlight.adapter.MusicInfo;
import com.jiangruicheng.btlight.adapter.PlayStatu;
import com.jiangruicheng.btlight.adapter.SetMusicInFo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("ValidFragment")
public class MusicList extends Fragment {

    private ListView musiclist;
    private List<MusicInfo> list;
    private List<String> namelist;
    private String Mode;
    public static final String ARTIST = "ARTIST";
    public static final String ABLUM = "ABLUM";
    public static final String MUSIC = "MUSIC";
    public static final String ARTIST_MUSIC = "ARTISTMUSIC";
    public static final String ALBUM_MUSIC = "ABLUMMUSIC";

    public MusicList() {
    }

    public MusicList(List list, List namelist, String Mode) {
        // Required empty public constructor
        this.list = list;
        this.Mode = Mode;
        this.namelist = namelist;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_music_list, container, false);
        musiclist = (ListView) v.findViewById(R.id.musci_list);
        if (list != null) {
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_musiclist,
                    list);
            musiclist.setAdapter(adapter);
        }

        musiclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), MusicPlayService.class);
                Bundle data = new Bundle();
                musiclist.setItemChecked(position, true);
                switch (Mode) {
                    case ARTIST:
                        HashMap<String, ArrayList<MusicInfo>> artist = (HashMap)
                                SetMusicInFo.getMusicInFo(getActivity(), SetMusicInFo
                                        .ARTIST);
                        ArrayList<MusicInfo> musicinfo = artist.get(list.get(position));
                        ArrayList<String> musicname = new ArrayList<>();
                        for (MusicInfo title : musicinfo) {
                            musicname.add(title.getTitle());
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R
                                .id.music_screen, new MusicList(musicname, musicinfo,
                                MusicList.ARTIST_MUSIC)).addToBackStack("").commit();
                        break;
                    case MUSIC:
                        intent.setAction(PlayStatu.PLAYSTATU_PLAY);
                        data.putInt("position", position);
                        data.putSerializable("musicinfo", SetMusicInFo.getMedioList(getActivity(), SetMusicInFo.ALLMUSIC));
                        intent.putExtra("data", data);
                        getActivity().startService(intent);
                        Log.d("play", "play");
                        break;
                    case ABLUM:
                        HashMap<String, ArrayList<MusicInfo>> album =
                                (HashMap) SetMusicInFo.getMusicInFo(getActivity(),
                                        SetMusicInFo.ALBUMS);
                        ArrayList<MusicInfo> mmusicinfo = album.get(list.get(position));
                        ArrayList<String> mmusicname = new ArrayList<>();
                        for (MusicInfo title : mmusicinfo) {
                            mmusicname.add(title.getTitle());
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R
                                .id.music_screen, new MusicList(mmusicname, mmusicinfo, MusicList
                                .ALBUM_MUSIC)).addToBackStack("").commit();
                        break;
                    case ARTIST_MUSIC:
                        intent.setAction(PlayStatu.PLAYSTATU_PLAY);
                        data.putInt("position", position);
                        data.putSerializable("musicinfo", (Serializable) namelist);
                        intent.putExtra("data", data);
                        getActivity().startService(intent);
                        Log.d("play", "play");
                        break;
                    case ALBUM_MUSIC:
                        intent.setAction(PlayStatu.PLAYSTATU_PLAY);
                        data.putInt("position", position);
                        data.putSerializable("musicinfo", (Serializable) namelist);
                        intent.putExtra("data", data);
                        getActivity().startService(intent);
                        Log.d("play", "play");
                        break;
                }
            }
        });
        return v;
    }


}
