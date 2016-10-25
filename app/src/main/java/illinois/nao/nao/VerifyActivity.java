package illinois.nao.nao;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VerifyActivity extends AppCompatActivity {
    @BindView(R.id.editText_verify) EditText verifyText;
    @BindView(R.id.button_verify) Button verifyButton;
    @BindView(R.id.button_resendverification) Button buttonResend;

    private String verificationCode;
    private GenericHandler verHandler;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ButterKnife.bind(this);
        context = this;

        verHandler = new GenericHandler() {
            @Override
            public void onSuccess() {
                // Refresh the screen
                exitActivity(true);
            }

            @Override
            public void onFailure(Exception exception) {
                // Show error
                Log.i("Verification", "FAIL");
            }
        };

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyUser();
            }
        });
    }

    private void exitActivity(boolean exit) {
        if(exit) {
            finish();
        }
    }

    private void verifyUser() {
        verificationCode = verifyText.getText().toString();

        if (verificationCode == null || verificationCode.isEmpty()) {
            // Handle Error
        } else {
            Toast.makeText(this, "The future is Nao!", Toast.LENGTH_LONG).show();
            go();
//            AmazonHelper.getPool().getUser(AmazonHelper.getCurrUser())
//                    .verifyAttributeInBackground("phone_number", verificationCode, verHandler);
        }
    }

    private void go() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }
}
