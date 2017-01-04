package com.jiangruicheng.btlight.data;

import android.graphics.Color;

import java.util.Calendar;

public class Command {
    public static final byte SOP_HIGH_BYTE = 0x41;
    public static final byte SOP_LOW_BYTE = 0x54;

    public static class CommandId {
        public static final byte Handshake = 0x00;
        public static final byte GetStatus = 0x01;
        public static final byte SetColor = 0x10;
        public static final byte SetBright = 0x11;
        public static final byte SetMode = 0x12;
        public static final byte SetBackLight = 0x13;
        public static final byte SetClock = 0x14;
        public static final byte SetAlarm = 0x15;
        public static final byte SetSeelp = 0x16;
        public static final byte SetPower = 0x17;
        public static final byte SetAudioStatus = 0x18;
        public static final byte SetLightRate = 0x19;
        public static final byte Setupgradestatus = (byte) 0xAA;
        public static final byte Getindex = (byte) 0xAB;
        public static final byte SendUpData = (byte) 0xAC;
        public static final byte Upg_Finish = (byte) 0xAD;
    }

    public static class Event {
        public static final byte Handshake_Ack = 0x30;
        public static final byte Command_Ack = 0x31;
        public static final byte GetStatus_Event = 0x32;
        public static final byte SetBright_Event = 0x41;
        public static final byte SetMode_Event = 0x42;
        public static final byte SetBackLight_Event = 0x43;
        public static final byte SetAlarm_Event = 0x45;
        public static final byte SetSleep_Event = 0x46;
        public static final byte SetPower_Event = 0x47;
        public static final byte SetPlayStatu_Event = 0x48;
        public static final byte SetRate_Event = 0x49;
        public static final byte SetMcuVersion_Event = 0x50;
        public static final byte SetBluetoothVersion_Event = 0x51;
        public static final byte SetSleepTime = (byte) 0x80;


        public static final byte Upgradestatus_Ack = (byte) 0xCA;
        public static final byte UserIndex = (byte) 0xCB;
        public static final byte Upg_Ack = (byte) 0xCC;
        public static final byte Upg_FinishAck = (byte) 0xCD;
    }

    public static class Event_Lenght {
        public static final byte Handshake_Ack = 5;
        public static final byte CommandAck = 6;
        public static final byte SetBright_Event = 6;
        public static final byte SetMode_Event = 6;
        public static final byte SetBackLight_Event = 6;
        public static final byte SetAlarm_Event = 13;
    }

    public static class DataByte {
        public static final byte Mode_RGB = 0X02;
        public static final byte Mode_LED = 0X01;
        public static final byte Mode_ROMANTIC = 0X03;
        public static final byte Mode_DISC = 0X04;
        public static final byte Mode_SEELP = 0X05;
        public static final byte Mode_PARTY = 0X06;
        public static final byte Mode_RATE = 0X07;
        public static final byte BackLight_ON = 0X01;
        public static final byte BackLight_OFF = 0X00;
        public static final byte Power_ON = 0X01;
        public static final byte Power_OFF = 0X00;
        public static final byte Seelp_time_OFF = 0;
        public static final byte Seelp_time_15 = (byte) 15;
        public static final byte Seelp_time_30 = (byte) 30;
        public static final byte Seelp_time_45 = (byte) 45;
        public static final byte Seelp_time_60 = (byte) 60;
        public static final byte Seelp_time_90 = (byte) 90;
        public static final byte Seelp_time_120 = (byte) 120;


    }


    public static final byte[] Handshake = {SOP_HIGH_BYTE, SOP_LOW_BYTE, 0x00,
            CommandId.Handshake,
            (byte) ((0x100 - (SOP_LOW_BYTE + SOP_HIGH_BYTE + 0X00 + CommandId
                    .Handshake)) & 0xff)};

    public static byte[] SetLightRate(byte rate) {
        byte[] buffer = new byte[6];
        buffer[0] = Command.SOP_HIGH_BYTE;
        buffer[1] = Command.SOP_LOW_BYTE;
        buffer[2] = 2;
        buffer[3] = CommandId.SetLightRate;
        buffer[4] = rate;
        SetCheckSum(buffer, 5);
        return buffer;
    }


    public static byte[] SetUpGradeStatus() {
        byte[] buffer = new byte[5];
        buffer[0] = Command.SOP_HIGH_BYTE;
        buffer[1] = Command.SOP_LOW_BYTE;
        buffer[2] = 0;
        buffer[3] = CommandId.Setupgradestatus;
        SetCheckSum(buffer, 4);
        return buffer;
    }

    public static byte[] GetIndex() {
        byte[] buffer = new byte[5];
        buffer[0] = Command.SOP_HIGH_BYTE;
        buffer[1] = Command.SOP_LOW_BYTE;
        buffer[2] = 0;
        buffer[3] = CommandId.Getindex;
        SetCheckSum(buffer, 4);
        return buffer;

    }

    public static byte[] SendUpData(byte[] bytes, short address) {
        byte[] buffer = new byte[bytes.length + 7];
        buffer[0] = Command.SOP_HIGH_BYTE;
        buffer[1] = Command.SOP_LOW_BYTE;
        buffer[2] = (byte) (bytes.length + 2);
        buffer[3] = CommandId.SendUpData;
        buffer[4] = (byte) (address >> 8);
        buffer[5] = (byte) address;

        for (int i = 0; i < bytes.length; i++) {
            buffer[6 + i] = bytes[i];
        }
        SetCheckSum(buffer, bytes.length + 6);
        return buffer;

    }

    public static byte[] Upg_Finish(boolean statu) {

        byte[] buffer = new byte[6];
        buffer[0] = Command.SOP_HIGH_BYTE;
        buffer[1] = Command.SOP_LOW_BYTE;
        buffer[2] = 0;
        buffer[3] = CommandId.Upg_Finish;
        if (statu) {
            buffer[4] = 0x01;
        } else {
            buffer[4] = 0x00;
        }
        SetCheckSum(buffer, 5);
        return buffer;

    }


    public static byte[] SetBright(byte etBright) {
        byte[] setBright = new byte[6];
        setBright[0] = Command.SOP_HIGH_BYTE;
        setBright[1] = Command.SOP_LOW_BYTE;
        setBright[2] = 2;
        setBright[3] = Command.CommandId.SetBright;
        setBright[4] = etBright;
        SetCheckSum(setBright, 5);
        return setBright;
    }

    public static byte[] SetRGB(int color) {
        byte[] colorPWM = new byte[8];

        colorPWM[0] = Command.SOP_HIGH_BYTE;
        colorPWM[1] = Command.SOP_LOW_BYTE;
        colorPWM[2] = 4;
        colorPWM[3] = Command.CommandId.SetColor;
        colorPWM[4] = (byte) Color.red(color);
        colorPWM[5] = (byte) Color.green(color);
        colorPWM[6] = (byte) Color.blue(color);
        SetCheckSum(colorPWM, 7);
        return colorPWM;
    }

    public static byte[] SetMode(byte mode) {
        byte[] Mode = new byte[6];
        Mode[0] = Command.SOP_HIGH_BYTE;
        Mode[1] = Command.SOP_LOW_BYTE;
        Mode[2] = 2;
        Mode[3] = CommandId.SetMode;
        Mode[4] = mode;
        SetCheckSum(Mode, 5);
        return Mode;
    }

    public static byte[] SetStatuGet() {
        byte[] GetStatus = new byte[14];
        GetStatus[0] = Command.SOP_HIGH_BYTE;
        GetStatus[1] = Command.SOP_LOW_BYTE;
        GetStatus[2] = 9;
        GetStatus[3] = CommandId.GetStatus;
        GetStatus[4] = Event.SetMode_Event;
        GetStatus[5] = Event.SetBright_Event;
        GetStatus[6] = Event.SetBackLight_Event;
        GetStatus[7] = Event.SetAlarm_Event;
        GetStatus[8] = Event.SetPower_Event;
        GetStatus[9] = Event.SetSleep_Event;
        GetStatus[10] = Event.SetBluetoothVersion_Event;
        GetStatus[11] = Event.SetMcuVersion_Event;
        GetStatus[12] = Event.SetRate_Event;

        SetCheckSum(GetStatus, 13);
        return GetStatus;
    }

    public static byte[] SetStatuGetSign(byte comma) {
        byte[] GetStatus = new byte[6];
        GetStatus[0] = Command.SOP_HIGH_BYTE;
        GetStatus[1] = Command.SOP_LOW_BYTE;
        GetStatus[2] = 1;
        GetStatus[3] = CommandId.GetStatus;
        GetStatus[4] = comma;

        SetCheckSum(GetStatus, 5);
        return GetStatus;
    }

    public static byte[] SetBackLight(byte statu) {
        byte[] Statu = new byte[6];
        Statu[0] = Command.SOP_HIGH_BYTE;
        Statu[1] = Command.SOP_LOW_BYTE;
        Statu[2] = 1;
        Statu[3] = CommandId.SetBackLight;
        Statu[4] = statu;
        SetCheckSum(Statu, 5);

        return Statu;
    }

    public static byte[] SetClock() {

        Calendar c = Calendar.getInstance();
        byte year = (byte) (c.get(Calendar.YEAR) - 2000);
        byte month = (byte) (c.get(Calendar.MONTH) + 1);
        byte date = (byte) c.get(Calendar.DAY_OF_WEEK);
        byte hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        byte minute = (byte) c.get(Calendar.MINUTE);
        byte second = (byte) c.get(Calendar.SECOND);

        byte[] Clock = new byte[11];
        Clock[0] = Command.SOP_HIGH_BYTE;
        Clock[1] = Command.SOP_LOW_BYTE;
        Clock[2] = 6;
        Clock[3] = CommandId.SetClock;
        Clock[4] = hour;
        Clock[5] = minute;
        Clock[6] = second;
        Clock[7] = year;
        Clock[8] = month;
        Clock[9] = date;
        SetCheckSum(Clock, 10);

        return Clock;
    }

    public static byte[] SetAlarm(AlarmData alarmTime) {
        byte[] alarm = new byte[13];
        alarm[0] = Command.SOP_HIGH_BYTE;
        alarm[1] = Command.SOP_LOW_BYTE;
        alarm[2] = 8;
        alarm[3] = CommandId.SetAlarm;
        alarm[4] = 0;
        alarm[5] = alarmTime.power;
        alarm[6] = alarmTime.hour;
        alarm[7] = alarmTime.minute;
        alarm[8] = alarmTime.repeat;
        alarm[9] = alarmTime.snooze;
        alarm[10] = alarmTime.volume;
        alarm[11] = alarmTime.mode;
        SetCheckSum(alarm, 12);

        return alarm;
    }

    public static byte[] SetSleep(byte time) {
        byte[] Sleep = new byte[6];
        Sleep[0] = Command.SOP_HIGH_BYTE;
        Sleep[1] = Command.SOP_LOW_BYTE;
        Sleep[2] = 1;
        Sleep[3] = CommandId.SetSeelp;
        Sleep[4] = time;
        SetCheckSum(Sleep, 5);

        return Sleep;
    }

    public static byte[] SetPower(byte statu) {
        byte[] Statu = new byte[6];
        Statu[0] = Command.SOP_HIGH_BYTE;
        Statu[1] = Command.SOP_LOW_BYTE;
        Statu[2] = 2;
        Statu[3] = CommandId.SetPower;
        Statu[4] = statu;
        SetCheckSum(Statu, 5);

        return Statu;
    }

    private static void SetCheckSum(byte[] bytes, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += bytes[i];
        }
        bytes[bytes.length - 1] = (byte) ((0x100 - sum) & 0xFF);

    }
}
