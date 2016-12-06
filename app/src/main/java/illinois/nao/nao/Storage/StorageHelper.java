package illinois.nao.nao.Storage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import illinois.nao.nao.User.PostEvent;

/**
 * Created by franklinye on 12/6/16.
 */

public class StorageHelper {

    class Test extends FirebaseImageLoader {

    }

    public static void uploadFile(Uri file, StorageReference userVideoRef, OnFailureListener fail, OnSuccessListener success) {
        UploadTask uploadTask = userVideoRef.putFile(file);
        if(fail != null) {
            uploadTask.addOnFailureListener(fail);
        }
        if(success != null) {
            uploadTask.addOnSuccessListener(success);
        }

    }

    public static void populateImage(final StorageReference imageReference, final ImageView imageView) {
        final ProgressBar progressBar = new ProgressBar(imageView.getContext());
        progressBar.setIndeterminate(true);
        imageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String md5 = storageMetadata.getMd5Hash();
                Glide.with(imageView.getContext()).using(new FirebaseImageLoader())
                        .load(imageReference).signature(new StringSignature(md5))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
            }
        });
    }

    public static void pushToFeed(final String userName, final PostEvent.Type type) {
        ValueEventListener duplicateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postEvent : dataSnapshot.getChildren()) {
                    PostEvent event = postEvent.getValue(PostEvent.class);
                    if (event.getType().equals(type) && event.getAuthor().equals(userName)) {
                        FirebaseDatabase.getInstance().getReference("newsfeed").child(postEvent.getKey()).setValue(null);
                    }
                }
                FirebaseDatabase.getInstance().getReference("newsfeed").push().setValue(new PostEvent(userName, type));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase.getInstance().getReference("newsfeed").addListenerForSingleValueEvent(duplicateListener);

    }
}
