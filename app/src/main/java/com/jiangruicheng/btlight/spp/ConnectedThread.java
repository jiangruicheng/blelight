package com.jiangruicheng.btlight.spp;

import android.bluetooth.BluetoothSocket;

import com.jiangruicheng.btlight.RXbus.RxBus;
import com.jiangruicheng.btlight.eventtype.ConnSucc;
import com.jiangruicheng.btlight.eventtype.ReciveCmd;
import com.jiangruicheng.btlight.eventtype.SendCmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by jiang_ruicheng on 16/12/17.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private ReciveCmd reciveCmd;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        reciveCmd = new ReciveCmd();
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    private Subscription SendCmd;

    @Override
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        RxBus.getDefault().post(new ConnSucc());
        SendCmd = RxBus.getDefault().toObservable(com.jiangruicheng.btlight.eventtype.SendCmd.class).observeOn(Schedulers.io()).subscribe(new Observer<SendCmd>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(com.jiangruicheng.btlight.eventtype.SendCmd sendCmd) {
                write(sendCmd.getCmd());
            }
        });
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                reciveCmd.setCmd(buffer);
                RxBus.getDefault().post(reciveCmd);
                // Send the obtained bytes to the UI activity

            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
        if (SendCmd.isUnsubscribed()) {
            SendCmd.unsubscribe();
        }
    }
}

