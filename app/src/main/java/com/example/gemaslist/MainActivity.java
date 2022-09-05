package com.example.gemaslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navDrawer);

        //set action bar and nav drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

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

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //nav drawer click events
        switch (item.getItemId()){
            case R.id.navDashboard: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.dashboard);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_dashboard);
                break;
            }
            case R.id.navGames: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.games);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_games);
                break;
            }
            case R.id.navMovies: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.movies);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_movies);
                break;
            }
            case R.id.navSeries: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.series);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_series);
                break;
            }
            case R.id.navAnime: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.animeList);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_animeList);
                break;
            }
            case R.id.navStats: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.statistics);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_Statistics);
                break;
            }
            case R.id.navProfile: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.profile);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_profile);
                break;
            }
            case R.id.navLogout: {
                Objects.requireNonNull(MainActivity.this.getSupportActionBar())
                        .setSubtitle(R.string.logout);
                Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_logout);
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