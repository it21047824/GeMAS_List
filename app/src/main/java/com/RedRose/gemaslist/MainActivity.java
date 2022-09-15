package com.RedRose.gemaslist;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean searchBtnHidden;
    private DrawerLayout drawerLayout;
    public Stack<String> appBarSubtitleHistory = new Stack<>();
    protected SharedPreferences sp;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get shared preferences
        sp = getSharedPreferences(getString(R.string.login), MODE_PRIVATE);
        userID = sp.getInt(getString(R.string.user_id), 0);

        //actionbar and nav drawer setup
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navDrawer);
        View navDrawerHeader = navigationView.getHeaderView(0);
        MaterialTextView navDrawerUsername = navDrawerHeader.findViewById(R.id.nav_profile_name);
        navDrawerUsername.setText(sp.getString(getString(R.string.username), "Username"));

        //set action bar and nav drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        MaterialButton searchButton = findViewById(R.id.appbar_search_button);
        searchButton.setOnClickListener(view -> {
            String subtitle = (String) Objects.requireNonNull
                    (MainActivity.this.getSupportActionBar()).getSubtitle();
            if(appBarSubtitleHistory.empty()){
                appBarSubtitleHistory.push(subtitle);
            } else {
                if(!appBarSubtitleHistory.peek().equals(subtitle)){
                    appBarSubtitleHistory.push(subtitle);
                }
            }

            searchButton.setVisibility(View.GONE);
            searchBtnHidden = true;

            MainActivity.this.getSupportActionBar()
                    .setSubtitle(R.string.search);
            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.action_global_search);
        });

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(view -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        );

        //set custom back button navigation
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!appBarSubtitleHistory.empty()){
                    Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                            .setSubtitle(appBarSubtitleHistory.pop());
                    Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment)
                            .navigateUp();
                    if(searchBtnHidden){
                        searchButton.setVisibility(View.VISIBLE);
                        searchBtnHidden = false;
                    }
                } else {
                    finishAffinity();
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, backPressedCallback);

        //Monitor network status
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Azure.getConnection();
                if(!AnimeUserData.dataInitialized()){
                    try {
                        Azure.Validity result = Azure.getAnimeUserData(userID);
                        if(result == Azure.Validity.QUERY_FAILED){
                            runOnUiThread(()-> Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.user_data_error),
                                    Toast.LENGTH_SHORT).show());
                        } else if (result == Azure.Validity.QUERY_SUCCESSFUL){
                            runOnUiThread(()-> Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.network_connected),
                                    Toast.LENGTH_SHORT).show());
                        }
                    } catch (NullPointerException e) {/*ignore*/}
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                AnimeUserData.clearData();
                runOnUiThread(()-> Toast.makeText(MainActivity.this,
                        getResources().getString(R.string.network_disconnected),
                        Toast.LENGTH_SHORT).show());
            }
        };

        Thread networkCheck = new Thread(() -> {
            ConnectivityManager connectivityManager =
                    getSystemService(ConnectivityManager.class);
            connectivityManager.requestNetwork(networkRequest, networkCallback);
        });
        networkCheck.start();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //nav drawer click events
        switch (item.getItemId()){
            case R.id.navDashboard: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.dashboard);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_dashboard);
                break;
            }
            case R.id.navGames: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.games);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_games);
                break;
            }
            case R.id.navMovies: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.movies);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_movies);
                break;
            }
            case R.id.navSeries: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.series);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_series);
                break;
            }
            case R.id.navAnime: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.animeList);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_animeList);
                break;
            }
            case R.id.navAddTitle: {
                startActivity(new Intent(MainActivity.this, AddNewTitle.class));
                break;
            }
            case R.id.navStats: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.statistics);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_Statistics);
                break;
            }
            case R.id.navProfile: {
                String subtitle = (String) Objects.requireNonNull
                        (MainActivity.this.getSupportActionBar()).getSubtitle();
                if(appBarSubtitleHistory.empty()){
                    appBarSubtitleHistory.push(subtitle);
                } else {
                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
                        appBarSubtitleHistory.push(subtitle);
                    }
                }

                MainActivity.this.getSupportActionBar()
                        .setSubtitle(R.string.profile);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_profile);
                break;
            }
            case R.id.navLogout: {
//                String subtitle = (String) Objects.requireNonNull
//                        (MainActivity.this.getSupportActionBar()).getSubtitle();
//                if(appBarSubtitleHistory.empty()){
//                    appBarSubtitleHistory.push(subtitle);
//                } else {
//                    if(!appBarSubtitleHistory.peek().equals(subtitle)){
//                        appBarSubtitleHistory.push(subtitle);
//                    }
//                }
//
//                MainActivity.this.getSupportActionBar()
//                        .setSubtitle(R.string.logout);
//                Navigation.findNavController(this, R.id.nav_host_fragment)
//                        .navigate(R.id.action_global_logout);

                //logout
                SharedPreferences sp = getSharedPreferences(getString(R.string.login), MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean(getString(R.string.login), false);
                editor.remove(getString(R.string.user_id));
                editor.remove(getString(R.string.username));
                editor.remove(getString(R.string.email));
                editor.apply();

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Azure.closeConnection();
    }
}