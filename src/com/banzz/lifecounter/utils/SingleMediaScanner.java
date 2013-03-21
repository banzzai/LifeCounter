package com.banzz.lifecounter.utils;

import java.io.File;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public class SingleMediaScanner implements MediaScannerConnectionClient {

private MediaScannerConnection mMs;
private String mPath;

public SingleMediaScanner(Context context, File imageFile) {
    mPath = imageFile.getAbsolutePath();
//    mMs = new MediaScannerConnection(context, this);
//    mMs.connect();

}

public SingleMediaScanner(Context context, String path) {
    mPath = path;
    mMs = new MediaScannerConnection(context, this);
    mMs.connect();
}

@Override
public void onMediaScannerConnected() {
    mMs.scanFile(mPath, null);
}

@Override
public void onScanCompleted(String path, Uri uri) {
    mMs.disconnect();
}

}