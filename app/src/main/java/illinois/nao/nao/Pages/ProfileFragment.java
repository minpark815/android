package illinois.nao.nao.Pages;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;


public class ProfileFragment extends Fragment {

    @BindView(R.id.videoView) VideoView videoView;
    @BindView(R.id.scrollView_profile)
    ScrollView scrollView;

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

        videoView.setVideoURI(Uri.parse("android.resource://illinois.nao.nao/" + R.raw.naovideo));
        videoView.start();

        return view;
    }
}
