package illinois.nao.nao.Storage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        Glide.with(imageView.getContext()).using(new FirebaseImageLoader()).load(imageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
    }

    public static void pushToFeed(String userName, PostEvent.Type type) {
        FirebaseDatabase.getInstance().getReference("users").push().setValue(new PostEvent(userName, type));
        Log.d("PUSH", "push to newsfeed");
    }
}
