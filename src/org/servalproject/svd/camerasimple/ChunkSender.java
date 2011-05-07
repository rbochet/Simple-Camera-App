package org.servalproject.svd.camerasimple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * Send the chunks over the network to the Receiver project
 * 
 * @author rbochet
 */
public class ChunkSender extends Thread {

	/**
	 * The path to the chunk file
	 */
	private String chunkPath;

	/**
	 * Remote port used
	 */
	public static final int REMOTE_PORT = 1234;

	/**
	 * Server where the stream will be send
	 */
	private static final String SERVER = "192.168.1.7";

	/**
	 * Size of the buffer for the transfer
	 */
	private static final int BUFFER_SIZE = 1024;

	public static final String TAG = "SPCA";

	/**
	 * Sets up the thread by giving to it the good chunk
	 * 
	 * @param chunkPath
	 *            The path to the chunk
	 */
	public ChunkSender(String chunkPath) {
		Log.v(TAG, "ChunkSender created for the chunk " + chunkPath);
		this.chunkPath = chunkPath;
	}

	@Override
	public void run() {
		BufferedOutputStream outputStream;
		BufferedInputStream inputStream;
		try {
			// Connect to the server
			Socket socket = new Socket(ChunkSender.SERVER,
					ChunkSender.REMOTE_PORT);
			Log.i(TAG, "Remote socket connected " + socket);

			// Read the chunk in binary mode
			inputStream = new BufferedInputStream(
					new FileInputStream(chunkPath));

			// Write the socket in binary mode
			outputStream = new BufferedOutputStream(socket.getOutputStream(),
					ChunkSender.BUFFER_SIZE);

			Log.v(TAG, "I/O streams set up.");

			int i = 0;
			byte[] buffer = new byte[BUFFER_SIZE];

			while (true) {
				try {
					if (inputStream.available() > BUFFER_SIZE) {
						inputStream.read(buffer);
						outputStream.write(buffer);
						Log.v(TAG, "Write " + ChunkSender.BUFFER_SIZE * i
								+ "bytes (" + i + "packets)");
						i++;
					} else {
						outputStream.write(inputStream.read());
					}
				} finally {
					// Close the streams
					socket.close();
					inputStream.close();
					outputStream.close();
					Log.v(TAG, "Streams & socket closed.");
				}
			}
		} catch (UnknownHostException e) {
			Log.e(TAG, "The server " + ChunkSender.SERVER + ":"
					+ ChunkSender.REMOTE_PORT + " is not up.");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "The chunk (" + chunkPath + ") does not exist.");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "I/O exception in the ChunkSender");
			e.printStackTrace();
		}
	}
}
