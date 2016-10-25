package illinois.nao.nao.Pages;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;


public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_videoplayer) ExposureVideoPlayer videoPlayer;
    @BindView(R.id.profile_button_audio) Button buttonAudio;
    @BindView(R.id.scrollView_profile) ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        videoPlayer.setVideoSource(Uri.parse("android.resource://illinois.nao.nao/" + R.raw.naovideo));

        return view;
    }
}
