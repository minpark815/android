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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;

import butterknife.BindView;
import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;

public class PeopleFragment extends Fragment {

    @BindView(R.id.recyclerView_users) RecyclerView userList;
    FirebaseRecyclerAdapter<User, UserHolder> mRecyclerViewAdapter;
    private FirebaseDatabase database;
    private StorageReference storageReference;

    public PeopleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com").child("users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(User.class, R.layout.user_card, UserHolder.class, database.getReference("users")) {

            @Override
            protected void populateViewHolder(UserHolder viewHolder, final User user, int position) {
                viewHolder.bind(user, storageReference);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!user.getUserName().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            ProfileFragment fragment = new ProfileFragment();
                            Bundle args = new Bundle();
                            args.putString("userName", user.getUserName());
                            fragment.setArguments(args);
                            manager.beginTransaction().replace(R.id.content_holder, fragment).commit();
                        } else {
                            Toast.makeText(getContext(), "This is you!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        userList = (RecyclerView) view.findViewById(R.id.recyclerView_users);
        userList.setAdapter(mRecyclerViewAdapter);
        userList.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.people_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static class User {
        private URI image;
        private String userName;

        public User(){
        }

        public User(URI i, String n){
            image = i;
            userName = n;
        }

        public void setUserName(String n){
            userName = n;
        }

        public String getUserName(){
            return userName;
        }

        public URI getImage() {
            return image;
        }

        public void setImage(URI image) {
            this.image = image;
        }
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView profile;
        public TextView name;

        public UserHolder(View itemView) {
            super(itemView);
            view = itemView;
            profile = (ImageView) view.findViewById(R.id.profile_pic);
            name = (TextView) view.findViewById(R.id.search_name);
        }

        public void bind(User user, StorageReference storageRef) {
            name.setText(user.getUserName());
            StorageReference imageRef = storageRef.child(user.getUserName()).child("profile");
            StorageHelper.populateImage(imageRef, profile);
        }

    }

}
