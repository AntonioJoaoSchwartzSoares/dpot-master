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
public class aboutActivity extends Activity {

	WebView mWebView;
	String SONG_ARTIST = "Song title - Artist name";
	Boolean isPlaying;
	TextView tv = null;
	ImageView play;

	@Override
	protected void onPause() {
		super.onPause();

	};

	public aboutActivity() {

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
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

		String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont; src: url(\"file:///android_asset/fonts/font.otf\")}body {font-family: \'MyFont\'; font-size: 12px;text-align: justify;}</style></head>"
				+ "<body>"
				+ "Móveis notáveis combinam com música boa. Por isso, a <b>dpot</b> - marca que reúne a maior coleçao de mobiliário moderno e contemporâneo de autor do país - criou uma rádio digital com programação exclusiva, 100% dedicada à música e à cultura brasileiras, 24 horas por dia, 7 dias por semana.";
		String pas = "</body></html>";

		String myHtmlString = pish + pas;
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadDataWithBaseURL(null, myHtmlString, "text/html",
				"charset=UTF-8", null);

	}

	public void contactus(View v) {
		Intent i = new Intent(getApplicationContext(), ContactActivity.class);
		i.putExtra("SONG_ARTIST",SONG_ARTIST );
		i.putExtra("isPlaying",isPlaying );
		startActivity(i);
		finish();

	}

	public void radio(View v) {
		finish();

	}
}
