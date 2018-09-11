package com.fivrr.radio;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class PhoneCallListener extends PhoneStateListener {

    public static boolean message = false;
   
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        try {
//            if (notRunWhenStart)
//                notRunWhenStart = false;
//            else 
        	{
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (!MusicPlayer.isStarted()) {
                                message = false;
                                MainActivity.play("Dhol Beats Radio");
                                
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (MusicPlayer.isStarted()) {
                            message = true;
                            MusicPlayer.stopMediaPlayer();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (MusicPlayer.isStarted()) {
                            message = true;
                            MusicPlayer.stopMediaPlayer();
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}