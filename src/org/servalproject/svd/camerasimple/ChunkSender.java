package org.servalproject.svd.camerasimple;

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
	
	public static final String TAG = "SPCA";


	/**
	 * Sets up the thread by giving to it the good chunk
	 * 
	 * @param chunkPath
	 *            The path to the chunk
	 */
	public ChunkSender(String chunkPath) {
		Log.v(TAG, )
		this.chunkPath = chunkPath;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}

}
