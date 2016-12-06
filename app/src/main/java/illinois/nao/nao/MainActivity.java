package illinois.nao.nao;

import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.Pages.NewsfeedFragment;
import illinois.nao.nao.Pages.ProfileFragment;
import illinois.nao.nao.Pages.SearchFragment;
import illinois.nao.nao.Pages.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation) AHBottomNavigation bottomNavigation;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private FragmentManager fragmentManager;
    private NewsfeedFragment newsfeedFragment;
    private ProfileFragment profileFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        newsfeedFragment = new NewsfeedFragment();
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();

        // toolbar_content.setNavigationIcon(R.drawable.icon);
        toolbar.setTitle("Nao");

        setSupportActionBar(toolbar);

        // ********* SET UP BOTTOM NAVIGATION ********
        // Create items
        AHBottomNavigationItem newsfeed = new AHBottomNavigationItem(R.string.newsfeed,
                R.drawable.ic_event_note_black_24dp, R.color.cardview_light_background);

        AHBottomNavigationItem profile = new AHBottomNavigationItem(R.string.profile,
                R.drawable.ic_person_black_24dp, R.color.cardview_light_background);

        AHBottomNavigationItem search = new AHBottomNavigationItem(R.string.search,
                R.drawable.ic_search_black_24dp, R.color.cardview_light_background);

        bottomNavigation.addItem(search);
        bottomNavigation.addItem(profile);
        bottomNavigation.addItem(newsfeed);

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setCurrentItem(1);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch(position) {
                    case 0:
                        // Search
                        goToSearch();
                        break;
                    case 1:
                        // Profile
                        goToProfile();
                        break;
                    case 2:
                        // Newsfeed
                        goToNewsfeed();
                        break;
                }
                return true;
            }
        });

        // *******************************************

        fragmentManager = getSupportFragmentManager();

        // Our starting page
        goToProfile();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_account_settings:
                goToSettings();
                break;
            case R.id.menu_settings_back:
                goToProfile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSettings() {
        toolbar.setTitle("Settings");
        fragmentManager.beginTransaction()
                .replace(R.id.content_holder, new SettingsFragment()).commit();
    }

    private void goToSearch() {
        toolbar.setTitle("Search");
        fragmentManager.beginTransaction()
                .replace(R.id.content_holder, searchFragment).commit();
    }

    private void goToProfile() {
        toolbar.setTitle("Nao");
        fragmentManager.beginTransaction()
                .replace(R.id.content_holder, profileFragment).commit();
    }

    private void goToNewsfeed() {
        toolbar.setTitle("Newsfeed");
        fragmentManager.beginTransaction()
                .replace(R.id.content_holder, newsfeedFragment).commit();
    }

    @Override
    public void onBackPressed(){
        // Nothing
    }
}
