package com.fivrr.radio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dpot.radio.R;

@SuppressLint({ "SetJavaScriptEnabled" })
public class ContactActivity extends Activity {

	WebView mWebView;
	String SONG_ARTIST = "Song title - Artist name";
	Boolean isPlaying;
	TextView tv = null;
	ImageView play;

	@Override
	protected void onPause() {
		super.onPause();

	};

	public ContactActivity() {

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
		Bundle extras = getIntent().getExtras();
		tv = (TextView) findViewById(R.id.mainRadioLocation);
		play = (ImageView) findViewById(R.id.radio_image);
		if (extras != null) {
			SONG_ARTIST = extras.getString("SONG_ARTIST");
			isPlaying = extras.getBoolean("isPlaying");
			tv.setText(SONG_ARTIST);
			if (isPlaying) {
				play.setImageResource(R.drawable.play_image);
			} else
				play.setImageResource(R.drawable.pause_image);
		}
		Typeface font2 = Typeface
				.createFromAsset(getAssets(), "fonts/font.otf");
		tv.setTypeface(font2);
		tv.setText(SONG_ARTIST);
		mWebView = (WebView) findViewById(R.id.webview);
		String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/font.otf\")}body {font-family: MyFont;font-size: 12px;text-align: justify;}</style></head>"
				+ "<body>"
				+ "<b>DPOT Gabriel</b>"
				+ "<br>"
				+ "Al Gabriel Monteiro da Silva, 479<br>"
				+ "01441-000 Jardim Paulistano|São Paulo-SP <br>"
				+ "T:+55 11 3082-9513 | 3063-0113<br>"
				+ "dpotgabriel@dpot.com.br";
		
		String pish1 = 
				 "<b>DPOT D&D</b>"
				+ "<br>"
				+ "Shopping D&D, piso térreo<br>"
				+ "Avenida das Nações Unidas, 12.551 - Loja 232 <br>"
				+ "04578-903 Brooklin Novo | São Paulo - SP<br>"
				+ "T:+55 11 3043-9159 | 3043-9087<br>"
				+ "dpotded@dpot.com.br<br>";
		String pas1 = "</body></html>";

		String myHtmlString = pish + "<br><br>" + pish1 + pas1;
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadDataWithBaseURL(myHtmlString, myHtmlString,
				"text/html; charset=UTF-8", null, null);
			}

	public void aboutus(View v) {
		Intent i = new Intent(getApplicationContext(), aboutActivity.class);
		i.putExtra("SONG_ARTIST",SONG_ARTIST );
		i.putExtra("isPlaying",isPlaying );
		startActivity(i);
		finish();

	}

	public void radio(View v) {
		finish();
	}
}
