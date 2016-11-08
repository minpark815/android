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

    private static final String userPoolId = "us-east-1_TtksO8fQP";
    private static final String clientId = "1vnj95783jtrs0nml1q3df85rv";
    private static final String clientSecret = "1j2mql1gjaj69st6ogjafllh5i4bmu0gdiakc3mv5fodo8app4e8";
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

    public static void setUser(String u) { user = u; }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }
}
