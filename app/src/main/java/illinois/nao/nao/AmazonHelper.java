package illinois.nao.nao;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

/**
 * Created by Eric on 10/22/2016.
 */

public class AmazonHelper {
    private static CognitoUserPool userPool;
    private static String user;
    private static CognitoDevice newDevice;
    private static AmazonHelper instance;

    private static final String userPoolId = "us-east-1_mPda1wHoN";
    private static final String clientId = "4vdvvt8prlfqtomf31n83vf4nf";
    private static final String clientSecret = "d79576ja4op5tsge1qm9k6o5m6t5leei0gj8mfr06ltfehsieg";
    private static final Regions cognitoRegion = Regions.US_EAST_1;

    public static void init(Context c) {

        // We only want to initialize our instance and our user pool once.
        if (instance != null && userPool != null) {
            return;
        }

        if (instance == null) {
            instance = new AmazonHelper();
        }

        if (userPool == null) {
            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(c, userPoolId, clientId, clientSecret, cognitoRegion);
        }

        newDevice = null;
    }


    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static String getCurrUser() { return user; }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }
}
