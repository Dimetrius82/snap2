package com.snapmail;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.snapmail.database.MailDatabase;
import com.snapmail.login.LoginActivity;
import com.snapmail.util.Constants;

public class MainActivity extends AppCompatActivity
{

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupDatabase();

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.putExtra(Constants.LOGIN_ACTIVITY_STAGE, Constants.LOGIN_ACTIVITY_STAGE_CHOOSE_EMAIL_SERVICE);
        startActivityForResult(loginActivityIntent, Constants.LOGIN_NEW_ACCOUNT_REQUEST_CODE);
    }

    private void setupDatabase()
    {
        Constants.mailDatabase = Room.databaseBuilder(getApplicationContext(),
                MailDatabase.class, "Mail").build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawers();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        if (Constants.mailDatabase != null && Constants.mailDatabase.isOpen())
        {
            Constants.mailDatabase.close();
        }

        super.onDestroy();
    }
}
