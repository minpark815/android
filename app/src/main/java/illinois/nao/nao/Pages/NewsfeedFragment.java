package illinois.nao.nao.Pages;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;
import illinois.nao.nao.User.PostEvent;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;

public class NewsfeedFragment extends Fragment {
    private final static String TAG = "newsfeed";

    @BindView(R.id.recyclerView_posts) RecyclerView postsView;
    private FirebaseRecyclerAdapter<PostEvent, DefaultViewHolder> newsfeedAdapter;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference newsfeedRef;
    private StorageReference storageRef;


    public NewsfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        newsfeedRef = database.getReference("newsfeed");
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com").child("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        postsView = (RecyclerView) rootView.findViewById(R.id.recyclerView_posts);
        postsView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        postsView.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        newsfeedAdapter = new FirebaseRecyclerAdapter<PostEvent, DefaultViewHolder>
                (PostEvent.class, R.layout.default_feed_item, DefaultViewHolder.class, newsfeedRef) {
            @Override
            protected void populateViewHolder(DefaultViewHolder viewHolder, PostEvent model, int position) {
                switch (model.getType()) {
                    case TEXT:
                        if (viewHolder instanceof TextViewHolder) {
                            ((TextViewHolder) viewHolder).bind(model, usersRef);
                        }
                        break;
                    case IMAGE:
                        if (viewHolder instanceof ImageViewHolder) {
                            ((ImageViewHolder) viewHolder).bind(model, storageRef);
                        }
                        break;
                    case VIDEO:
                        if (viewHolder instanceof  VideoViewHolder) {
                            ((VideoViewHolder) viewHolder).bind(model, storageRef);
                        }
                        break;
                    case AUDIO:
                        if (viewHolder instanceof  AudioViewHolder) {
                            ((AudioViewHolder) viewHolder).bind(model, storageRef);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public int getItemViewType(int position) {
                PostEvent post = getItem(position);
                switch(post.getType()) {
                    case TEXT:
                        return 0;
                    case IMAGE:
                        return 1;
                    case AUDIO:
                        return 2;
                    case VIDEO:
                        return 3;
                }

                return super.getItemViewType(position);
            }

            @Override
            public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        View textView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_text, parent, false);
                        return new TextViewHolder(textView);
                    case 1:
                        View imageView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_image, parent, false);
                        return new ImageViewHolder(imageView);
                    case 3:
                        View videoView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_video, parent, false);
                        return new VideoViewHolder(videoView);
                    case 2:
                        View audioView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_audio, parent, false);
                        return new AudioViewHolder(audioView);
                    default:
                        break;
                }
                return super.onCreateViewHolder(parent, viewType);
            }
        };

        postsView.setAdapter(newsfeedAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.newsfeed_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static class DefaultViewHolder extends RecyclerView.ViewHolder {

        public DefaultViewHolder(View v) {
            super(v);
        }
    }

    public static class ImageViewHolder extends DefaultViewHolder {
        private ImageView image;
        private TextView author;
        public ImageViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.newsfeed_image);
            author = (TextView) v.findViewById(R.id.username_text);
        }

        public void bind(PostEvent model, StorageReference storageReference) {
            author.setText(model.getAuthor());
            StorageReference imageRef = storageReference.child(model.getAuthor()).child("image");
            StorageHelper.populateImage(imageRef, image);
        }
    }

    public static class TextViewHolder extends DefaultViewHolder {
        private TextView message;
        private TextView author;

        public TextViewHolder(View v) {
            super(v);
            message = (TextView) v.findViewById(R.id.message_text);
            author = (TextView) v.findViewById(R.id.username_text);
        }

        public void bind(PostEvent model, DatabaseReference databaseReference) {
            ValueEventListener messageListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userMessage = dataSnapshot.getValue(String.class);
                    message.setText(userMessage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            author.setText(model.getAuthor());
            databaseReference.child(model.getAuthor()).child("profileDescription").addListenerForSingleValueEvent(messageListener);
        }
    }

    public static class VideoViewHolder extends DefaultViewHolder {
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

    public static class AudioViewHolder extends DefaultViewHolder {
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
}
