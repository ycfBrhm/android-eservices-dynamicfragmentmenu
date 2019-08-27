package android.eservices.dynamicfragmentmenu;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationInterface {

    private final static String FRAGMENT_NUMBER_KEY = "Fragment_Number";
    private final static String FRAGMENT_STORED_KEY = "Fragment_Stored";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private SelectableNavigationView navigationView;
    private SparseArray<Fragment> fragmentArray;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigationElements();

        int menuItemNumberToSelect;
        if (savedInstanceState != null) {
            //State had been saved so we need to restore the right fragment
            //No need to check the menu item because savedInstance restores automatically the view states
            //We need to store the fragment inside our array so it won't be recreated
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_STORED_KEY);
            replaceFragment(currentFragment);
            fragmentArray.append(savedInstanceState.getInt(FRAGMENT_NUMBER_KEY), currentFragment);
        } else {
            //Set default screen to "My Selection", i.e. the first menu element
            navigationView.setSelectedItem(navigationView.getMenu().getItem(0));
        }

        //Let's imagine we retrieve the stored counter state, before creating the favorite Fragment
        //and then be able to update and manage its state.
        updateFavoriteCounter(3);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setupNavigationElements() {
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        fragmentArray = new SparseArray<>(3);

        navigationView = findViewById(R.id.navigation);
        navigationView.inflateHeaderView(R.layout.navigation_header);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getOrder() == 2) {
                    //Log off and then
                    logoff();
                    //We don't want to set the selected item to selected state
                    return false;
                }
                //If selection is not logoff, display the right screen
                if (navigationView.getCheckedItem() != menuItem) {
                    System.out.println(menuItem.getOrder());
                    replaceFragment(getSelectedMenuFragment(menuItem.getOrder()));
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private Fragment getSelectedMenuFragment(int position) {
        Fragment selectedFragment = fragmentArray.get(position);
        if (selectedFragment == null) {
            switch (position) {
                //List screen
                case 0:
                    selectedFragment = SelectedFragment.newInstance();
                    break;
                //Favorites screen
                case 1:
                    selectedFragment = FavoritesFragment.newInstance();
                    break;
                //Default, let's go back to list screen
                default:
                    selectedFragment = SelectedFragment.newInstance();
                    break;
            }
            fragmentArray.append(position, selectedFragment);
        }
        currentFragment = selectedFragment;
        return selectedFragment;
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logoff() {
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void updateFavoriteCounter(int counter) {
        String counterContent = counter > 9 ? getString(R.string.counter_full) : Integer.toString(counter);
        View counterView = navigationView.getMenu().getItem(1).getActionView();
        counterView.setVisibility(counter > 0 ? View.VISIBLE : View.GONE);
        ((TextView) counterView.findViewById(R.id.counter_view)).setText(counterContent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //we need to save the state of the current selected fragment,
        //so in case of orientation change, the right fragment will be displayed
        savedInstanceState.putInt(FRAGMENT_NUMBER_KEY, navigationView.getCheckedItem().getOrder());
        getSupportFragmentManager().putFragment(savedInstanceState, FRAGMENT_STORED_KEY, currentFragment);

    }
}
