package com.fivrr.radio;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpot.radio.R;


public class MainScreen extends Fragment {
    private static ImageView loading;
 
    private static TextView radioListLocation;
    protected static FrameLayout mainView;
    private static ImageView playbtn,radio_image;
	private ImageView stopbtn;

    
    public static TextView getRadioListName() {
        return radioListName;
    }

    public static void setRadioListNameColor(int c) {
        radioListName.setTextColor(c);
    }

    private static TextView radioListName;


    public static ImageView getLoadingImage() {
        return loading;
    }

    public static void setRadioListName(String test) {
        radioListName.setText(test);
    }

    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View android = inflater.inflate(R.layout.main_main, container, false);
        mainView = (FrameLayout) android.findViewById(R.id.mainView);
        mainView.setBackgroundDrawable(MainActivity.ob);
        loading = (ImageView) android.findViewById(R.id.loading);
        radioListLocation = (TextView) android.findViewById(R.id.mainRadioName);
        radioListName = (TextView) android.findViewById(R.id.mainRadioLocation);
        Typeface fontRegular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/font.otf");
        radioListLocation.setTypeface(fontRegular);
        radioListName.setTypeface(fontRegular);
        radio_image = (ImageView) android.findViewById(R.id.radio_image);
        
        
        playbtn = (ImageView) android.findViewById(R.id.playImage);
        
        //playbtn.bringToFront();
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//            	Toast.makeText(getActivity(), "play button", Toast.LENGTH_SHORT).show();
            	 MainActivity.play(getResources().getString(R.string.radio_location));
                 
            }
        });
        
        stopbtn  = (ImageView) android.findViewById(R.id.stopImage);
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
                MainActivity.notificationWhile = false;
                try {
                    MusicPlayer.stopMediaPlayer();
                } catch (Exception e) {
                    e.getMessage();
                }
                NotificationPanel.notificationCancel();
                MainActivity.setExit(true);
                     }
        });
        if (MusicPlayer.isStarted()) {
            radioListLocation.setText(getString(R.string.radio_name));
        }
        return android;
    }
    public static void setPlayImage(){
    	playbtn.setImageResource(R.drawable.play);
    	radio_image.setImageResource(R.drawable.play_image);
    }
    public static void setPauseImage(){
    	playbtn.setImageResource(R.drawable.pause);
    	radio_image.setImageResource(R.drawable.pause_image);
    	
    }
}
