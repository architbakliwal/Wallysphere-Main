package com.wallysphere.app;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

final class MyMediaScannerConnectionClient implements MediaScannerConnectionClient {
	
	private String mFilename;
	private String mMimetype;
	private MediaScannerConnection mConn;
	
	public MyMediaScannerConnectionClient(Context ctx, File file, String mimetype) {
		this.mFilename = file.getAbsolutePath();
		mConn = new MediaScannerConnection(ctx, this);
		mConn.connect();
	}
	
	@Override
	public void onMediaScannerConnected() {
		mConn.scanFile(mFilename, mMimetype);
	}
	
	@Override
	public void onScanCompleted(String path, Uri uri) {
		mConn.disconnect();
		System.out.println("file: " + path + " was scanned successfully: " + uri);
	}
}