package ua.naiksoftware.stompclientexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.BasicAWSCredentials;


import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;

public class S3Services {
    Context myContext;
    private static final String TAG = GroupChatActivity.class.getSimpleName();
    HashMap<String,Integer> transferIds = new HashMap<String, Integer>();

    TransferUtility transferUtility;

    public S3Services(Context context,String KEY,String SECRET) {
        myContext = context;
        myContext.getApplicationContext().startService(new Intent(myContext.getApplicationContext(), TransferService.class));
        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        transferUtility =
                TransferUtility.builder()
                        .context(myContext.getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();
    }



    public void upload(File file) {
        upload(file,"appcon-storage");
    }

    public void downpload(String name, String path) {
        download(name,"appcon-storage",path);
    }

    public void upload(File file, String bucket) {

        TransferObserver uploadObserver = transferUtility.upload(
                            bucket,
                            file.getName()
                            ,file);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Context context = myContext.getApplicationContext();
                    CharSequence text = "Upload Finished!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        //if (TransferState.COMPLETED == uploadObserver.getState()) {
        //    // Handle a completed upload.
        //}

        Log.d(TAG, "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d(TAG, "Bytes Total: " + uploadObserver.getBytesTotal());
        transferIds.put(file.getName(), uploadObserver.getId());
    }

    private void download(String name,String bucket, String filepath) {

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(myContext.getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();
        TransferObserver downloadObserver =
                transferUtility.download(
                        name,
                        bucket, new File(filepath));

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Context context = myContext.getApplicationContext();
                    CharSequence text = "download Finished!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d("Your Activity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == downloadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("Your Activity", "Bytes Transferred: " + downloadObserver.getBytesTransferred());
        Log.d("Your Activity", "Bytes Total: " + downloadObserver.getBytesTotal());
        transferIds.put(name, downloadObserver.getId());
    }


    public void pause(String Name) {
        transferUtility.pause(transferIds.get(Name));
    }

    public void pauseAllDownloads() {
        transferUtility.pauseAllWithType(TransferType.DOWNLOAD);
    }

    public void pauseAllDUploads() {
        transferUtility.pauseAllWithType(TransferType.UPLOAD);
    }

    public void pauseAll() {
        transferUtility.pauseAllWithType(TransferType.ANY);
    }

    public void resume(String name) {
        transferUtility.resume(transferIds.get(name));
    }

    public void resumeAllDownloads() {
        transferUtility.resumeAllWithType(TransferType.DOWNLOAD);
    }

    public void resumeAllUploads() {
        transferUtility.resumeAllWithType(TransferType.UPLOAD);
    }

    public void resumeAll() {
        transferUtility.resumeAllWithType(TransferType.ANY);
    }

    public void cancel(String name)
    {
        transferUtility.cancel(transferIds.get(name));
    }

    public void cancelAllDownloads() {
        transferUtility.cancelAllWithType(TransferType.DOWNLOAD);
    }

    public void cancelAllUploads() {
        transferUtility.cancelAllWithType(TransferType.UPLOAD);
    }
}
