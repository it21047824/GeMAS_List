package com.RedRose.gemaslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    public Stack<String> appBarSubtitleHistory = new Stack<>();
    private String userName, userEmail;
    private FirebaseUser user;
    private Uri profileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null){
            if(user.getPhotoUrl() != null){
                String uriString = user.getPhotoUrl().toString();
                profileUri = Uri.parse(uriString.replace("=s96-c", "=s400-c"));
            }
            userName = user.getDisplayName();
            userEmail = user.getEmail();
        }

        //actionbar and nav drawer setup
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navDrawer);
        MaterialButton searchButton = findViewById(R.id.appbar_search_button);
        View navDrawerHeader = navigationView.getHeaderView(0);
        MaterialTextView navDrawerUsername = navDrawerHeader.findViewById(R.id.nav_profile_name);
        ImageView navDrawerProfile = navDrawerHeader.findViewById(R.id.nav_profile_image);

        if(userName != null) {
            navDrawerUsername.setText(userName);
        } else {
            navDrawerUsername.setText(userEmail);
        }
        if(profileUri != null) {
            Picasso.get().load(profileUri).into(navDrawerProfile);
        } else {
            navDrawerProfile.setImageResource(R.drawable.placeholder_image);
        }

        //set action bar and nav drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        searchButton.setOnClickListener(view -> Navigation
                .findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_global_search));

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

        //Monitor network status
        monitorNetwork();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user == null){
            finish();
        }
    }

    private void monitorNetwork() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);

                if(!AnimeUserData.dataInitialized()){
                    try {
                         boolean result = FirebaseUtil.getAnimeUserData();
                        if(result){
                            runOnUiThread(()-> Snackbar.make(drawerLayout,
                                    getResources().getString(R.string.network_connected),
                                    Toast.LENGTH_SHORT).show());
                        } else {
                            runOnUiThread(()-> Snackbar.make(drawerLayout,
                                    getResources().getString(R.string.user_data_error),
                                    Toast.LENGTH_SHORT).show());
                        }
                    } catch (NullPointerException e) {/*ignore*/}
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                AnimeUserData.clearData();

                runOnUiThread(()-> Snackbar.make(drawerLayout,
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
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_dashboard);
                break;
            }
            case R.id.navGames: {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_games);
                break;
            }
            case R.id.navMovies: {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_movies);
                break;
            }
            case R.id.navSeries: {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_series);
                break;
            }
            case R.id.navAnime: {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_animeList);
                break;
            }
            case R.id.navAddTitle: {
                startActivity(new Intent(MainActivity.this, AddNewTitle.class));
                break;
            }
            case R.id.navStats: {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_Statistics);
                break;
            }
            case R.id.navProfile: {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_profile);
                break;
            }
            case R.id.navLogout: {

                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.logout))
                        .setMessage("sign out and redirect to login page")
                        .setPositiveButton("Confirm", (dialogInterface, i) -> {
                            //logout
                            AuthUI.getInstance().signOut(MainActivity.this)
                                    .addOnCompleteListener(task-> {
                                        if (task.isSuccessful()){
                                            startActivity(new Intent(MainActivity.this, Login.class));
                                            finish();
                                        } else {
                                            Log.e("Main", "Failed to sign out", task.getException());
                                        }

                                    });
                        })
                        .setNeutralButton("Cancel", (dialogInterface, i) -> {/*do nothing*/})
                        .show();
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
}