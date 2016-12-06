package illinois.nao.nao.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import illinois.nao.nao.Pages.NewsfeedFragment;
import illinois.nao.nao.R;
import illinois.nao.nao.User.PostEvent;

/**
 * Created by Orang on 12/6/2016.
 */

public class TextViewHolder extends RecyclerView.ViewHolder {
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