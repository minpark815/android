package illinois.nao.nao.Pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;
import illinois.nao.nao.User.PostEvent;
import illinois.nao.nao.ViewHolders.AudioViewHolder;
import illinois.nao.nao.ViewHolders.ImageViewHolder;
import illinois.nao.nao.ViewHolders.TextViewHolder;
import illinois.nao.nao.ViewHolders.VideoViewHolder;

public class NewsfeedFragment extends Fragment {
    private final static String TAG = "newsfeed";

    @BindView(R.id.recyclerView_posts) RecyclerView postsView;
    private FirebaseRecyclerAdapter<PostEvent, RecyclerView.ViewHolder> newsfeedAdapter;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference newsfeedRef;
    private StorageReference storageRef;


    public NewsfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        newsfeedRef = database.getReference("newsfeed");
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com").child("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        ButterKnife.bind(this, rootView);

        postsView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        postsView.setLayoutManager(llm);
        postsView.smoothScrollToPosition(0);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        newsfeedAdapter = new FirebaseRecyclerAdapter<PostEvent, RecyclerView.ViewHolder>
                (PostEvent.class, R.layout.default_feed_item, RecyclerView.ViewHolder.class, newsfeedRef) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, PostEvent model, int position) {
                switch (model.getType()) {
                    case TEXT:
                        if (viewHolder instanceof TextViewHolder) {
                            ((TextViewHolder) viewHolder).bind(model, usersRef);
                        }
                        break;
                    case IMAGE:
                        if (viewHolder instanceof ImageViewHolder) {
                            ((ImageViewHolder) viewHolder).bind(model, storageRef);
                        }
                        break;
                    case VIDEO:
                        if (viewHolder instanceof  VideoViewHolder) {
                            ((VideoViewHolder) viewHolder).bind(model, storageRef);
                        }
                        break;
                    case AUDIO:
                        if (viewHolder instanceof  AudioViewHolder) {
                            ((AudioViewHolder) viewHolder).bind(model, storageRef);
                        }
                        break;
                    default:
                        break;
                }

                final TextView usernameView = (TextView) viewHolder.itemView.findViewById(R.id.username_text);
                usernameView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        ProfileFragment fragment = new ProfileFragment();
                        Bundle args = new Bundle();
                        args.putString("userName", usernameView.getText().toString());
                        fragment.setArguments(args);
                        manager.beginTransaction().replace(R.id.content_holder, fragment).commit();
                    }
                });
            }


            @Override
            public int getItemViewType(int position) {
                PostEvent post = getItem(position);
                switch(post.getType()) {
                    case TEXT:
                        return 0;
                    case IMAGE:
                        return 1;
                    case AUDIO:
                        return 2;
                    case VIDEO:
                        return 3;
                }

                return super.getItemViewType(position);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        View textView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_text, parent, false);
                        return new TextViewHolder(textView);
                    case 1:
                        View imageView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_image, parent, false);
                        return new ImageViewHolder(imageView);
                    case 3:
                        View videoView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_video, parent, false);
                        return new VideoViewHolder(videoView);
                    case 2:
                        View audioView = LayoutInflater.from(getContext()).inflate(R.layout.newsfeed_audio, parent, false);
                        return new AudioViewHolder(audioView);
                    default:
                        break;
                }
                return super.onCreateViewHolder(parent, viewType);
            }
        };

        postsView.setAdapter(newsfeedAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.newsfeed_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
