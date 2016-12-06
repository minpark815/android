package illinois.nao.nao.UX;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;
import illinois.nao.nao.User.PostEvent;

/**
 * Created by harryarakkal on 12/6/16.
 */

public class AudioDialog extends Dialog implements View.OnClickListener {

    private MediaRecorder recorder;
    private boolean recording = false;
    private StorageReference storage;
    private File image;
    private String userName;

    public AudioDialog(Context context, StorageReference storage, String userName) {
        super(context);
        this.storage = storage;
        this.userName = userName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_dialog);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.println(storageDir);
        image = null;
        try {
            image = File.createTempFile(
                    "recording",
                    ".mp4",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(image);
        recorder.setOutputFile(image.toString());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        Button record = (Button) this.findViewById(R.id.record_button);
        Button cancel = (Button) this.findViewById(R.id.cancel_recording);
        record.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.record_button) {
            if (!recording) {
                System.out.println("Recording");
                try {
                    recorder.prepare();
                    recorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recording = true;
            } else {
                try {
                    recorder.stop();
                    recorder.release();
                }catch(RuntimeException stopException){
                    stopException.printStackTrace();
                }
                recording = false;
                System.out.println("Done Recording");
                StorageHelper.uploadFile(Uri.fromFile(image), storage.child("audio"), null, null);
                StorageHelper.pushToFeed(userName, PostEvent.Type.AUDIO);
                this.dismiss();
            }
        } else {
            System.out.println("Cancel");
            StorageHelper.uploadFile(Uri.fromFile(image), storage.child("audio"), null, null); //TODO: SHOULD THIS BE HERE?
            this.dismiss();
        }
    }
}
