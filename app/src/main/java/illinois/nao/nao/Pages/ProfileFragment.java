package illinois.nao.nao.Pages;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;
import illinois.nao.nao.User.User;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int REQUEST_RECORD_VIDEO = 1002;
    private static final int PICK_MEDIA_FILE = 1003;

    @BindView(R.id.profile_videoplayer) ExposureVideoPlayer videoPlayer;
    @BindView(R.id.profile_button_audio) ImageButton buttonAudio;
    @BindView(R.id.scrollView_profile) ScrollView scrollView;
    @BindView(R.id.textView_textContent) TextView textContent;
    @BindView(R.id.textView_name) TextView name;
    @BindView(R.id.imageView2) ImageView imageContent;
    @BindView(R.id.imageView) ImageView profilePicture;

    private MediaPlayer mp;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private StorageReference mUserStorageRef;
    private StorageReference mAllUserStorageRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("users");
        mStorage = FirebaseStorage.getInstance();
        mStorageRef  = mStorage.getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com");
        mAllUserStorageRef = mStorageRef.child("users");
        mUserStorageRef = mStorageRef.child("users").child(mUser.getDisplayName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);


        // videoPlayer.setVideoSource(Uri.parse("android.resource://illinois.nao.nao/" + R.raw.naovideo));
        name.setText(mUser.getDisplayName());
        populateProfilePicture();
        populateText();
        populateImage();
        populateVideo();
        populateAudio();

        FloatingActionButton photo = (FloatingActionButton) view.findViewById(R.id.add_photo);
        FloatingActionButton video = (FloatingActionButton) view.findViewById(R.id.record_video);
        FloatingActionButton text  = (FloatingActionButton) view.findViewById(R.id.write_post);
        photo.setOnClickListener(this);
        video.setOnClickListener(this);
        text.setOnClickListener(this);

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

    public void populateText() {
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

        mUsersRef.child(mUser.getDisplayName()).addListenerForSingleValueEvent(userTextListener);
    }

    public void populateImage() {
        StorageReference imageReference = mUserStorageRef.child("image.png");
        StorageHelper.populateImage(imageReference, imageContent);
    }

    public void populateVideo() {
        try {
            final File file = File.createTempFile("video", "mp4");
            StorageReference userVideoRef = mAllUserStorageRef.child(mUser.getDisplayName()).child("video.mp4");
            userVideoRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    videoPlayer.setVideoSource(Uri.fromFile(file));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            videoPlayer.setVideoSource(Uri.fromFile(file));
        } catch (IOException e) {
            Log.d(TAG, "exception downloading files");
        }
    }

    public void populateAudio() {
        try {
            final File file = File.createTempFile("audio", "mp3");
            StorageReference userVideoRef = mAllUserStorageRef.child(mUser.getDisplayName()).child("audio.mp3");
            userVideoRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    mp = MediaPlayer.create(getActivity().getApplicationContext(), Uri.fromFile(file));
                    Log.d(TAG, "media player created");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            videoPlayer.setVideoSource(Uri.fromFile(file));
        } catch (IOException e) {
            Log.d(TAG, "exception downloading files");
        }
    }

    public void populateProfilePicture() {
        StorageReference profilePicReference = mUserStorageRef.child("profile.png");
        StorageHelper.populateImage(profilePicReference, profilePicture);
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
        StorageHelper.uploadFile(file, userVideoRef);
        mUsersRef.child(mUser.getDisplayName()).child("videoPath").setValue(file.getLastPathSegment());
    }

    public void uploadImage(Uri file) {
        StorageReference userPhotoRef = mUserStorageRef.child("image/" + file.getLastPathSegment());
        StorageHelper.uploadFile(file, userPhotoRef);
        mUsersRef.child(mUser.getDisplayName()).child("imagePath").setValue(file.getLastPathSegment());
    }

    public void uploadSound(Uri file) {
        StorageReference userSoundRef = mUserStorageRef.child("sound/" + file.getLastPathSegment());
        StorageHelper.uploadFile(file, userSoundRef);
        mUsersRef.child(mUser.getDisplayName()).child("soundPath").setValue(file.getLastPathSegment());
    }

    @Override
    public void onClick(View view) {
        int button = view.getId();
        if(button == R.id.add_photo){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(view.getContext(),
                            "illinois.nao.nao.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }else if(button == R.id.record_video){
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_RECORD_VIDEO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }
}
