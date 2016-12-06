package illinois.nao.nao.ViewHolders;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import illinois.nao.nao.Pages.NewsfeedFragment;
import illinois.nao.nao.R;
import illinois.nao.nao.User.PostEvent;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;

/**
 * Created by Orang on 12/6/2016.
 */

public class VideoViewHolder extends RecyclerView.ViewHolder {
    private final static String TAG = "newsfeed";
    private ExposureVideoPlayer videoPlayer;
    private TextView author;

    public VideoViewHolder(View v) {
        super(v);
        videoPlayer = (ExposureVideoPlayer) v.findViewById(R.id.newsfeed_videoplayer);
        author = (TextView) v.findViewById(R.id.username_text);
    }

    public void bind(PostEvent model, StorageReference storageReference) {
        author.setText(model.getAuthor());
        try {
            final File videoFile = File.createTempFile("video", "mp4");
            StorageReference userVideoRef = storageReference.child(model.getAuthor()).child("video");
            userVideoRef.getFile(videoFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    videoPlayer.setVideoSource(Uri.fromFile(videoFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            videoPlayer.setVideoSource(Uri.fromFile(videoFile));
        } catch (IOException e) {
            Log.d(TAG, "exception downloading files");
        }
    }
}