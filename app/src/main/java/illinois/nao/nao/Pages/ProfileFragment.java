package illinois.nao.nao.Pages;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;


public class ProfileFragment extends Fragment {
    @BindView(R.id.profile_videoplayer) ExposureVideoPlayer videoPlayer;
    @BindView(R.id.profile_button_audio) ImageButton buttonAudio;
    @BindView(R.id.scrollView_profile) ScrollView scrollView;

    private MediaPlayer mp;

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
        mp = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.ifelephantscouldfly);

        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Media Player", "Is Playing: " + mp.isPlaying());

                if(mp.isPlaying()) {
                    mp.pause();
                    //mp.stop();
                    Log.i("Media Player", "Pause");
                    buttonAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    mp.start();
                    Log.i("Media Player", "Play");
                    buttonAudio.setImageResource(R.drawable.ic_pause_black_24dp);
                }
            }
        });


        return view;
    }
}
