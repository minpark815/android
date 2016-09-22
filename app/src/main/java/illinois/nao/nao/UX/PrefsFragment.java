package illinois.nao.nao.UX;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import illinois.nao.nao.R;

/**
 * Created by franklinye on 9/21/16.
 */
public class PrefsFragment extends PreferenceFragment{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load preferences from XML
        addPreferencesFromResource(R.xml.preferences);
    }
}
