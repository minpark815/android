package illinois.nao.nao.Pages;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;
import illinois.nao.nao.User.User;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @BindView(R.id.profile_videoplayer) ExposureVideoPlayer videoPlayer;
    @BindView(R.id.profile_button_audio) ImageButton buttonAudio;
    @BindView(R.id.scrollView_profile) ScrollView scrollView;
    @BindView(R.id.textView_textContent) TextView textContent;
    @BindView(R.id.imageView2) ImageView imageContent;

    private MediaPlayer mp;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersRef = mDatabase.getReference("users");
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef = mStorage.getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com");
    private StorageReference mUserStorageRef = mStorageRef.child(mUser.getDisplayName());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        videoPlayer.setVideoSource(Uri.parse("android.resource://illinois.nao.nao/" + R.raw.naovideo));
        mp = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.ifelephantscouldfly);

        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Media Player", "Is Playing: " + mp.isPlaying());

                if(mp.isPlaying()) {
                    mp.pause();
                    //mp.stop();
                    Log.i("Media Player", "Pause");
                    buttonAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    mp.start();
                    Log.i("Media Player", "Play");
                    buttonAudio.setImageResource(R.drawable.ic_pause_black_24dp);
                }
            }
        });


        return view;
    }

    public void populateText(String userName) {
        ValueEventListener userTextListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profileDescription").getValue() != null) {
                    String text = dataSnapshot.child("profileDescription").getValue(String.class);
                    textContent.setText(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "populate text failed");
            }
        };

        mUsersRef.child(userName).addListenerForSingleValueEvent(userTextListener);
    }

    public void populateVideo(File file) {
        // TODO: given the video file, populate the video player
    }

    private void setDownloadVideo(final String userName) {
        ValueEventListener userVideoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("videoPath").getValue() != null) {
                    try {
                        final File file = File.createTempFile("video", "mp4");
                        String videoPath = dataSnapshot.child("videoPath").getValue(String.class);
                        StorageReference userVideoRef = mStorageRef.child(userName).child("video/" + videoPath);
                        userVideoRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                populateVideo(file);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUsersRef.child(userName).child(userName).addListenerForSingleValueEvent(userVideoListener);
    }

    public void populateImage(final String userName) {
        ValueEventListener userImageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("imagePath").getValue() != null) {
                    String imagePath = dataSnapshot.child("imagePath").getValue(String.class);
                    StorageReference userImageRef = mStorageRef.child(userName).child("image/" + imagePath);
                    Glide.with(getContext()).using(new FirebaseImageLoader()).load(userImageRef).into(imageContent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void uploadText(String text) {
        DatabaseReference textRef = mUsersRef.child(mUser.getDisplayName());
        textRef.setValue(text);
    }

    /**
     * This method takes a file as a Uri and uploads it to the storage reference /username/video/file
     * @param file Uri file
     */
    public void uploadVideo(Uri file) {
        StorageReference userVideoRef = mUserStorageRef.child("video/" + file.getLastPathSegment());
        uploadFile(file, userVideoRef);
        mUsersRef.child(mUser.getDisplayName()).child("videoPath").setValue(file.getLastPathSegment());
    }

    public void uploadImage(Uri file) {
        StorageReference userPhotoRef = mUserStorageRef.child("image/" + file.getLastPathSegment());
        uploadFile(file, userPhotoRef);
        mUsersRef.child(mUser.getDisplayName()).child("imagePath").setValue(file.getLastPathSegment());
    }

    public void uploadSound(Uri file) {
        StorageReference userSoundRef = mUserStorageRef.child("sound/" + file.getLastPathSegment());
        uploadFile(file, userSoundRef);
        mUsersRef.child(mUser.getDisplayName()).child("soundPath").setValue(file.getLastPathSegment());
    }

    private void uploadFile(Uri file, StorageReference userVideoRef) {
        UploadTask uploadTask = userVideoRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
}
