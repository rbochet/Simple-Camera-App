package org.servalproject.svd.camerasimple;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class VideoCaptureActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback {

	MediaRecorder recorder;
	SurfaceHolder holder;

	boolean recording = false;
	/** TAG for the application Logcat */
	public static final String TAG = "SPCA";

	/** Host that will be targeted */
	private static final String HOST = "192.168.1.8";

	/** Port that will be targeted */
	private static final int PORT = 1234;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// No title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Full screen landscape
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// Init recorder
		recorder = new MediaRecorder();
		initRecorder();

		setContentView(R.layout.main);

		// Set up the surface
		SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
		holder = cameraView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Clickable and loopback to the class listener
		cameraView.setClickable(true);
		cameraView.setOnClickListener(this);
	}

	/**
	 * Set up the recorder with default input and a highest definition available
	 */
	private void initRecorder() {
		// Sources
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

		// HD
		CamcorderProfile highProfile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		recorder.setProfile(highProfile);

		try {
			Socket socket = new Socket(InetAddress.getByName(HOST), PORT);
			ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
			recorder.setOutputFile(pfd.getFileDescriptor());
		} catch (UnknownHostException e) {
			Log.e(TAG, "Unknown host description: " + HOST);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IO failure.");
			e.printStackTrace();
		}
	}

	private void prepareRecorder() {
		recorder.setPreviewDisplay(holder.getSurface());
		try {
			recorder.prepare();
			Log.v(TAG, "Recording Started");
		} catch (IllegalStateException e) {
			e.printStackTrace();
			yellInPain();
		} catch (IOException e) {
			e.printStackTrace();
			yellInPain();
		}

	}

	/**
	 * Manage the on / off switch
	 */
	@Override
	public void onClick(View v) {
		if (recording) { // Switch off
			recorder.stop();
			recorder.release();
			recording = false;
			Log.v(TAG, "Recording Stopped");
			initRecorder();
			prepareRecorder();
		} else { // Switch on
			recording = true;
			recorder.start();
			Log.v(TAG, "Recording Started");
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "The surface has been created");
		prepareRecorder();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "surface Destroyed Called -- stop recording");
		if (recording) {
			recorder.stop();
			recording = false;
		}
		recorder.release();
		finish();
	}

	private void yellInPain() {
		Log.e(TAG, "Quitting cause something is WRONG!");
		finish();
	}

}