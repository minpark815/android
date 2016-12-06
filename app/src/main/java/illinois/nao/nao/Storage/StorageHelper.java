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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import illinois.nao.nao.User.PostEvent;

/**
 * Created by franklinye on 12/6/16.
 */

public class StorageHelper {
    public static void uploadFile(Uri file, StorageReference userVideoRef) {
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

    public static void pushToFeed(String userName, PostEvent.Type type) {
        FirebaseDatabase.getInstance().getReference("newsfeed").push().setValue(new PostEvent(userName, type));
    }
}
