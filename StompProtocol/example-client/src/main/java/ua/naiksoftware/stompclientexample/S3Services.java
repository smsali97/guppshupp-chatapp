package ua.naiksoftware.stompclientexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;


import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.URL;
import java.security.acl.Group;
import java.util.HashMap;

public class S3Services {
    Context myContext;
    private static final String TAG = GroupChatActivity.class.getSimpleName();
    HashMap<String,Integer> transferIds = new HashMap<String, Integer>();
    AmazonS3Client s3Client;
    TransferUtility transferUtility;

    public S3Services(Context context,String KEY,String SECRET) {
        myContext = context;
        myContext.getApplicationContext().startService(new Intent(myContext.getApplicationContext(), TransferService.class));
        BasicAWSCredentials credentials = new BasicAWSCredentials(KEY, SECRET);
        s3Client = new AmazonS3Client(credentials);
        transferUtility =
                TransferUtility.builder()
                        .context(myContext.getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();
    }



    public void upload(File file, Fragment fragment) {
        upload(file,"appcon-storage", fragment);
    }

    public void downpload(String name, String path) {
        download(name,"appcon-storage",path);
    }

    public String[] upload(File file, String bucket, Fragment fragment) {
        TransferObserver uploadObserver = transferUtility.upload(
                            bucket,
                            file.getName()
                            ,file);
        Log.d(TAG, "upload: " + file.getName());
        String result[] = new String[2];
        result[0] = file.getName();
        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Context context = myContext.getApplicationContext();
                    CharSequence text = "Upload Finished! This will last one day in our servers.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    Regions clientRegion = Regions.DEFAULT_REGION;

                    try {/*
                        AmazonS3Client s3Client_link = AmazonS3ClientBuilder.standard()
                                .withRegion(clientRegion)
                                .withCredentials(s3Client)
                                .build();*/

                        // Set the presigned URL to expire after one hour.
                        java.util.Date expiration = new java.util.Date();
                        long expTimeMillis = expiration.getTime();
                        expTimeMillis += 1000 * 60 * 60 * 24;
                        expiration.setTime(expTimeMillis);

                        // Generate the presigned URL.
                        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                                new GeneratePresignedUrlRequest(bucket, file.getName())
                                        .withMethod(HttpMethod.GET)
                                        .withExpiration(expiration);
                        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
                        //adding link to return array
                        result[1] = url.toString();

                        if (fragment instanceof GroupChatFragment)
                            ((GroupChatFragment)fragment).addLink(result[1],result[0]);
                        else if (fragment instanceof SingleChatFragment)
                            ((SingleChatFragment)fragment).addLink(result[1],result[0]);


                        //Log.d("Pre-Signed URL: ", url.toString());

                    } catch (AmazonServiceException e) {
                        // The call was transmitted successfully, but Amazon S3 couldn't process
                        // it, so it returned an error response.
                        e.printStackTrace();
                    }
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
        Log.d(TAG, "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d(TAG, "Bytes Total: " + uploadObserver.getBytesTotal());
        transferIds.put(file.getName(), uploadObserver.getId());

        Log.d("Pre-Signed URL: ", result[1] + "");
        return result;

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        //if (TransferState.COMPLETED == uploadObserver.getState()) {
        //    // Handle a completed upload.
        //}


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
