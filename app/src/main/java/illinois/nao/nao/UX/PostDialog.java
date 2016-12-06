package illinois.nao.nao.UX;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;

/**
 * Created by harryarakkal on 12/6/16.
 */

public class PostDialog extends Dialog implements View.OnClickListener {

    private MediaRecorder recorder;
    private boolean recording = false;
    private EditText body;
    private DatabaseReference databaseReference;

    public PostDialog(Context context, DatabaseReference databaseReference) {
        super(context);
        this.databaseReference = databaseReference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.post_dialog);
        body = (EditText) this.findViewById(R.id.post_body);
        Button post = (Button) this.findViewById(R.id.submit_post);
        Button cancel = (Button) this.findViewById(R.id.cancel_post);
        post.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.submit_post){
            String postBody = body.getText().toString();
            databaseReference.setValue(postBody);
            dismiss();
        } else if (view.getId() == R.id.cancel_post) {
            dismiss();
        }
    }
}
