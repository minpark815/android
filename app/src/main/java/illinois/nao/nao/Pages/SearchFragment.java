package illinois.nao.nao.Pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.net.URI;

import butterknife.BindView;
import illinois.nao.nao.R;

public class SearchFragment extends Fragment {

    @BindView(R.id.recyclerView_users) RecyclerView userList;
    FirebaseRecyclerAdapter<User, UserHolder> mRecyclerViewAdapter;
    private FirebaseDatabase database;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database = FirebaseDatabase.getInstance();
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(User.class, R.layout.user_card, UserHolder.class, database.getReference("users")) {

            @Override
            protected void populateViewHolder(UserHolder viewHolder, User user, int position) {
                viewHolder.name.setText(user.getUserName());
            }
        };
        View view = inflater.inflate(R.layout.fragment_search, container, false);
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
        inflater.inflate(R.menu.search_menu, menu);
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

        public void setName(String n){
            name.setText(n);
        }
    }

}
