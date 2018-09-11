package com.fivrr.radio;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dpot.radio.R;

public class MainActivity extends FragmentActivity {
	private static DataManager dataManager;
	private static TableLayout UIRadioList;
	private static ArrayList<String> userRadios = new ArrayList<String>();
	private static ViewPager viewPager;
	private static ImageView bufferingIndicator, speaker;
	private static LoadingAnimation bufferingAnimation;
	private static AudioManager audioManager;
	private static TextView radioListLocation;
	private static TextView radioListName;

	private static boolean first = true;
	private static NotificationPanel nPanel;
	private ImageView screenChaneButton;
	private Button aboutbtn, contactbtn, radiobtn;

	private boolean runOnce = true;
	private LinearLayout volumeLayout, volumeButton;
	private int volumeStore;

	private Typeface fontRegular;
	private WebView twitter;
	protected static boolean notificationWhile = true;
	private boolean active = true;
	private String last = "";

	public static String title = "";
	private static boolean exit = false;
	public static int page = 1;
	public static int pos = 0;
	private boolean reStartAnimation = false;

	protected static BitmapDrawable ob;
	private static TextView pauseTextLocation, stopText;

	static String R_IP = "", URL = "";
	static Context _context;
	String time = "";
	int h, m;
	String SONG_ARTIST = "Song title - Artist name";
	boolean isRunning = false;
	
	@Override
	protected void onStart() {

		super.onStart();
		active = true;
		notificationWhile = true;
		if (exit) {
			MusicPlayer.setIsWorking(false);
			notificationWhile = true;
			trackDetection();
			last = "";
		}

	}

	@Override
	protected void onPause() {
		super.onPause();

	};

	@Override
	protected void onResume() {
		super.onResume();

	};

	@Override
	protected void onStop() {
		super.onStop();
		active = false;
		if (notificationWhile) {
			MainActivity.newNotification(
					getResources().getString(R.string.radio_name), active);
		}
	}

	@Override
	protected void onDestroy() {
		NotificationPanel.notificationCancel();
		exit = true;

		super.onDestroy();

	}

	public static void newNotification(String notText, boolean status) {
		try {
			nPanel.showNotification(notText, status);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public static void radioListRefresh() {
		dataManager.createRadioListForRadioScreen(UIRadioList, userRadios,
				radioListName, radioListLocation);
	}

	public static void setViewPagerSwitch() {
		viewPager.setCurrentItem(0, true);
	}

	public static void startBufferingAnimation() {
		bufferingIndicator = MainScreen.getLoadingImage();
		bufferingAnimation = new LoadingAnimation(bufferingIndicator);
		bufferingAnimation.startAnimation();
	}

	public static void stopBufferingAnimation() {
		bufferingIndicator = MainScreen.getLoadingImage();
		bufferingAnimation.clearAnimation();
	}

	public static TextView getRadioListName() {
		return radioListName;
	}

	public static TextView getRadioListLocation() {
		return radioListLocation;
	}

	public static void nextPage() {
		viewPager.setCurrentItem(1, true);
	}

	public static void setExit(boolean b) {

		exit = b;
	}

	public static void play(String location) {
		try {
			exit = false;
			PhoneCallListener.message = false;
			if (first) {
				radioListLocation.setText(location);
				pauseTextLocation.setText("Pause");
				MainScreen.setPauseImage();
				title = location;
				RadioList.nextOrPreviousRadioStation(1, radioListLocation,
						radioListName);
				if (!MusicPlayer.isStarted()) {
					RadioList.nextOrPreviousRadioStation(0, radioListLocation,
							radioListName);
				}
				first = false;
			} else {
				first = true;
				if (MusicPlayer.isStarted()) {
					MusicPlayer.stopMediaPlayer();
					pauseTextLocation.setText("Play");
					MainScreen.setPlayImage();
				} else if (LoadingAnimation.hasEnded()) {
					try {
						pauseTextLocation.setText("Pause");
						MainScreen.setPauseImage();
						RadioList.nextOrPreviousRadioStation(0,
								radioListLocation, radioListName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String getURL() {

		return URL;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		_context = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		fontRegular = Typeface.createFromAsset(getAssets(), "fonts/font.otf");
		dataManager = new DataManager(this);

		ServiceConnection mConnection = new ServiceConnection() {
			@SuppressWarnings("deprecation")
			public void onServiceConnected(ComponentName className,
					IBinder binder) {
				((KillNotificationsService.KillBinder) binder).service
						.startService(new Intent(MainActivity.this,
								KillNotificationsService.class));
				Notification notification = new Notification(
						R.drawable.ic_launcher, getResources().getString(
								R.string.radio_name),
						System.currentTimeMillis());
				Intent notificationIntent = new Intent(MainActivity.this,
						this.getClass());
				PendingIntent contentIntent = PendingIntent.getActivity(
						MainActivity.this, 0, notificationIntent, 0);
				notification.setLatestEventInfo(getApplicationContext(),
						getResources().getString(R.string.radio_name), "",
						contentIntent);
				NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mNM.notify(KillNotificationsService.NOTIFICATION_ID,
						notification);
			}

			public void onServiceDisconnected(ComponentName className) {
			}

		};
		bindService(new Intent(MainActivity.this,
				KillNotificationsService.class), mConnection,
				Context.BIND_AUTO_CREATE);

		radiobtn = (Button) findViewById(R.id.radio);

		radiobtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			}
		});

		aboutbtn = (Button) findViewById(R.id.about);

		aboutbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						aboutActivity.class);
				i.putExtra("SONG_ARTIST", SONG_ARTIST);
				i.putExtra("isPlaying", first);
				startActivity(i);
			}
		});

		contactbtn = (Button) findViewById(R.id.contact);

		contactbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						ContactActivity.class);
				i.putExtra("SONG_ARTIST", SONG_ARTIST);
				i.putExtra("isPlaying", first);
				startActivity(i);
			}
		});

		TabPagerAdapter tabPageAdapter = new TabPagerAdapter(
				getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(tabPageAdapter);

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {
				pos = i2;
			}

			@Override
			public void onPageSelected(int i) {
				if (viewPager.getCurrentItem() == 0) {
					page = 1;
					if (reStartAnimation) {
						reStartAnimation = false;
						if (title.length() == 0) {
							MainScreen.setRadioListName(getResources()
									.getString(R.string.welcome_small));
						} else {
							MainScreen.setRadioListName(title);
						}
					}
				} else if (viewPager.getCurrentItem() == 1) {
					page = 2;
					screenChaneButton.setImageResource(R.drawable.back);

				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});

		speaker = (ImageView) findViewById(R.id.speaker);
		speaker.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							volumeStore, volumeStore);
					defaultVolumeBarPosition(audioManager, volumeLayout,
							volumeButton);
				} else {
					volumeStore = audioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
							0);
					defaultVolumeBarPosition(audioManager, volumeLayout,
							volumeButton);
				}
				return false;
			}
		});
		volumeLayout = (LinearLayout) findViewById(R.id.linearLayout_t);
		volumeButton = (LinearLayout) findViewById(R.id.button_t);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		registerReceiver(new HeadsetReceiver(getApplicationContext()),
				new IntentFilter(Intent.ACTION_HEADSET_PLUG));

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneCallListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
		else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		if (runOnce) {

			UIRadioList = (TableLayout) findViewById(R.id.radioListUi);
			radioListName = (TextView) findViewById(R.id.mainRadioName);
			radioListLocation = (TextView) findViewById(R.id.mainRadioLocation);
			pauseTextLocation = (TextView) findViewById(R.id.pause);
			pauseTextLocation.setTypeface(this.fontRegular);
			stopText = (TextView) findViewById(R.id.stoptext);
			stopText.setTypeface(this.fontRegular);
			if (title.length() == 0) {
				MainScreen.setRadioListName(getResources().getString(
						R.string.welcome_small));
			} else {
				MainScreen.setRadioListName(title);
			}

			radioListRefresh();
			volumeBarReaction(volumeLayout, volumeButton, audioManager);
			connectionDialog(isOnline());
			trackDetection();
			nPanel = new NotificationPanel(this);
			nPanel.showNotification(
					this.getResources().getString(R.string.radio_name), true);
			twitter = (WebView) findViewById(R.id.webView);
			twitter.setWebViewClient(new WebViewClient());
			twitter.getSettings().setJavaScriptEnabled(true);
			twitter.loadUrl(getResources().getString(R.string.web_url));
			twitter.refreshDrawableState();
			runOnce = false;

			if (!MusicPlayer.isStarted()) {
				if (Boolean.parseBoolean(this.getResources().getString(
						R.string.autostart_true_or_false))) {
					play(this.getResources().getString(R.string.radio_location));
				}
			}
		}
		defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
	}

	public void defaultVolumeBarPosition(AudioManager audioManager,
			LinearLayout volumeLayout, LinearLayout volumeButton) {
		float actual = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float endPoint = volumeLayout.getWidth() - volumeButton.getWidth();
		volumeButton.setX((endPoint / max * actual));
		if (volumeButton.getX() == 0)
			speaker.setImageResource(R.drawable.volume_muted_light);
		else
			speaker.setImageResource(R.drawable.volume_on);
	}

	public void volumeBarReaction(final LinearLayout volumeLayout,
			final LinearLayout volumeButton, final AudioManager audioManager) {

		volumeLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				float max = audioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				float endPoint = volumeLayout.getWidth()
						- volumeButton.getWidth();
				volumeButton.setX(motionEvent.getX() - volumeButton.getWidth()
						/ 2);

				if (volumeButton.getX() >= 0) {
					float pos = volumeButton.getX() / (endPoint / max);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							(int) pos, 0);
				}
				if (volumeButton.getX() >= endPoint) {
					volumeButton.setX(endPoint);
				}
				if (volumeButton.getX() <= 0) {
					volumeButton.setX(0);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
							0);
					speaker.setImageResource(R.drawable.volume_muted_light);
				} else
					speaker.setImageResource(R.drawable.volume_on);
				return true;
			}
		});
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		} else
			return false;
	}

	public void connectionDialog(boolean isOnline) {
		if (!isOnline) {
			final Dialog dialog = new Dialog(MainActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.without_internet);
			Typeface fontRegular = Typeface.createFromAsset(this.getAssets(),
					"fonts/font.otf");
			TextView dialogTitle = (TextView) dialog
					.findViewById(R.id.dialogTitle);
			TextView dialogText = (TextView) dialog
					.findViewById(R.id.dialogQuestion);
			TextView dialogButton = (TextView) dialog.findViewById(R.id.retry);
			dialogTitle.setTypeface(fontRegular);
			dialogText.setTypeface(fontRegular);
			dialogButton.setTypeface(fontRegular);

			FrameLayout mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
			dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.8),
					(int) (mainLayout.getHeight() * 0.35));
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			Button retryBtn = (Button) dialog.getWindow().findViewById(
					R.id.retry);
			retryBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!isOnline()) {
						dialog.dismiss();
						dialog.show();
					} else {
						dialog.dismiss();
						twitter.loadUrl(getResources().getString(
								R.string.web_url));
					}
				}
			});
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialogInterface) {
					if (!isOnline()) {
						dialog.dismiss();
						dialog.show();
					} else
						twitter.loadUrl(getResources().getString(
								R.string.web_url));
				}
			});
			dialog.show();
		}
	}

	public void trackDetection() {
		new Thread() {

			class C00581 implements Runnable {
				C00581() {
				}

				public void run() {
					if (PhoneCallListener.message) {
						MainScreen.setRadioListName(MainActivity.this
								.getResources().getString(R.string.resume));

					} else if (!MusicPlayer.isWorking()) {

						MainScreen.setRadioListName(MainActivity.this
								.getResources().getString(R.string.resume));
						if (MainActivity.radioListLocation.getText().toString()
								.equals(MainActivity.title)
								|| MainActivity.exit) {
							play(getResources().getString(
									R.string.radio_location));
							MainActivity.newNotification(
									MainActivity.this.getResources().getString(
											R.string.radio_name),
									MainActivity.this.active);
							return;
						}
						MainActivity.title = MainActivity.this.getResources()
								.getString(R.string.radio_offline);
						MainScreen.setRadioListName(MainActivity.this
								.getResources().getString(
										R.string.radio_offline));
						MainScreen.setPlayImage();
						pauseTextLocation.setText("Retry...");
						MainActivity.newNotification(MainActivity.this
								.getResources().getString(R.string.radio_name),
								MainActivity.this.active);
					} else if (!MusicPlayer.getTrackTitle().trim().toString()
							.equals("")) {
						if (!MainActivity.title.equals(MusicPlayer
								.getTrackTitle().trim().toString())) {
							MainActivity.title = MusicPlayer.getTrackTitle()
									.trim().toString();
							SONG_ARTIST = MainActivity.title;
							if (MainActivity.title.contains("~+")
									|| MainActivity.title.contains("ad|")) {
								MainActivity.title = "COMMERCIAL BREAK";
							}
							if (MainActivity.title.length() > 0
									&& MainActivity.title
											.charAt(MainActivity.title.length() - 1) == '-') {
								MainActivity.title = MainActivity.title
										.substring(0,
												MainActivity.title.length() - 1)
										.trim();
							}
							String[] parts = MainActivity.title.split("\\(");
							if (parts.length == 2) {
								MainScreen.setRadioListName(new StringBuilder(
										String.valueOf(parts[0].toString()))
										.append("\n(").append(parts[1])
										.toString());
								MainActivity.title = new StringBuilder(
										String.valueOf(parts[0].toString()))
										.append("\n(").append(parts[1])
										.toString();
								SONG_ARTIST = MainActivity.title;
							} else {
								MainScreen.setRadioListName(MainActivity.title);
								SONG_ARTIST = MainActivity.title;
							}
							if (!MainActivity.this.last
									.equals(MainActivity.title)) {
								MainActivity
										.newNotification(
												MainActivity.this
														.getResources()
														.getString(
																R.string.radio_name),
												MainActivity.this.active);
								MainActivity.this.last = MainActivity.title;
							}
						}
					} else if (MusicPlayer.getTrackTitle().trim().toString()
							.equals("")
							&& MusicPlayer.isStarted()
							&& !MainActivity.radioListLocation
									.getText()
									.toString()
									.equals(MainActivity.this.getResources()
											.getString(R.string.radio_location))) {
						MainActivity.title = MainActivity.this.getResources()
								.getString(R.string.radio_location);
						MainScreen.setRadioListName(MainActivity.title);
						MainActivity.newNotification(MainActivity.this
								.getResources().getString(R.string.radio_name),
								MainActivity.this.active);
					}
				}
			}

			public void run() {
				while (MainActivity.notificationWhile) {
					MainActivity.this.runOnUiThread(new C00581());
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (MainActivity.exit) {
					try {
						sleep(1000);
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					System.exit(0);
				}
			}
		}.start();
	}

}
