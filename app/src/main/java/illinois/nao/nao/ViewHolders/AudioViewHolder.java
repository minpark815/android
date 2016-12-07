package illinois.nao.nao.ViewHolders;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

/**
 * Created by Orang on 12/6/2016.
 */

public class AudioViewHolder extends RecyclerView.ViewHolder {
    private final static String TAG = "newsfeed";
    private ImageButton audioButton;
    private TextView author;
    private MediaPlayer mp;

    public AudioViewHolder(View v) {
        super(v);
        audioButton = (ImageButton) v.findViewById(R.id.newsfeed_button_audio);
        author = (TextView) v.findViewById(R.id.username_text);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Media Player", "Is Playing: " + mp.isPlaying());
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.pause();
                        //mp.stop();
                        Log.i("Media Player", "Pause");
                        audioButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else {
                        mp.start();
                        Log.i("Media Player", "Play");
                        audioButton.setImageResource(R.drawable.ic_pause_black_24dp);
                    }
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                audioButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });
    }

    public void bind(PostEvent model, StorageReference storageReference) {
        author.setText(model.getAuthor());
        try {
            final File audioFile = File.createTempFile("audio", "mp3");
            StorageReference userAudioRef = storageReference.child(model.getAuthor()).child("audio");
            userAudioRef.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    mp = MediaPlayer.create(audioButton.getContext(), Uri.fromFile(audioFile));
                    Log.d(TAG, "media player created");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "exception downloading files");
        }
    }
}