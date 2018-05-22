package com.snapmail.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.snapmail.R;
import com.snapmail.login.LoginActivity;
import com.snapmail.util.Constants;

public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add_account:
                Intent loginActivityIntent = new Intent(this, LoginActivity.class);
                startActivity(loginActivityIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
