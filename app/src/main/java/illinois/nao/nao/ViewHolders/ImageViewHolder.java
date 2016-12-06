package illinois.nao.nao.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import illinois.nao.nao.Pages.NewsfeedFragment;
import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;
import illinois.nao.nao.User.PostEvent;

/**
 * Created by Orang on 12/6/2016.
 */
public class ImageViewHolder extends RecyclerView.ViewHolder {
    private final static String TAG = "newsfeed";
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