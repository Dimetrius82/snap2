package com.snapmail;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.snapmail.database.AccountDBCallback;
import com.snapmail.database.AccountDBHelper;
import com.snapmail.database.MailDatabase;
import com.snapmail.login.AuthorizationCompleteCallback;
import com.snapmail.login.LoginActivity;
import com.snapmail.settings.SettingsActivity;
import com.snapmail.util.Account;
import com.snapmail.util.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AuthorizationCompleteCallback, AccountDBCallback
{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Account currentlySelectedAccount;
    private String savedEmailAddress;
    private SharedPreferences prefs;

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

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs != null)
        {
            savedEmailAddress = prefs.getString(Constants.SAVE_EMAIL_ADDRESS_KEY, null);
        }

        setupDatabase();

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.navigation_view);
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

        Constants.LOGIN_NEW_ACCOUNT_CALLBACK = this;

        AccountDBHelper.getAccountsFromDatabase(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDatabase()
    {
        Constants.mailDatabase = Room.databaseBuilder(getApplicationContext(),
                MailDatabase.class, "Mail").build();
    }

    @Override
    public void onAuthorizationComplete(final Account account)
    {
        AccountDBHelper.addAccountToDatabase(account, this);
    }

    @Override
    public void onAccountsAddedToDatabase()
    {
        AccountDBHelper.getAccountsFromDatabase(this);
    }

    @Override
    public void onAccountsGottenFromDatabase(final ArrayList<Account> accounts)
    {
        if (accounts.size() == 0)
        {
            Intent loginActivityIntent = new Intent(this, LoginActivity.class);
            startActivity(loginActivityIntent);
        }
        else
        {
            if (currentlySelectedAccount == null)
            {
                if (savedEmailAddress != null && !savedEmailAddress.equals(""))
                {
                    for (Account account : accounts)
                    {
                        if (account.getEmailAddress().equals(savedEmailAddress))
                        {
                            currentlySelectedAccount = account;
                            break;
                        }
                    }
                }
                else
                {
                    currentlySelectedAccount = accounts.get(0);
                }
            }

            updateAccountInfo();

            handleAccountSwitch(accounts);
        }
    }

    private void updateAccountInfo()
    {
        TextView accountUserNameTextView = navigationView.getHeaderView(0).findViewById(R.id.account_user_name_text_view);
        TextView accountEmailAddressTextView = navigationView.getHeaderView(0).findViewById(R.id.account_email_address_text_view);

        accountUserNameTextView.setText(currentlySelectedAccount.getName());
        accountEmailAddressTextView.setText(currentlySelectedAccount.getEmailAddress());
    }

    private void handleAccountSwitch(final ArrayList<Account> accounts)
    {
        // Listen for clicks on the switch account button
        navigationView.getHeaderView(0).findViewById(R.id.switch_account_image_view).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int currentlySelectedAccountId = 0;
                for (Account account : accounts)
                {
                    if (account.getEmailAddress().equals(currentlySelectedAccount.getEmailAddress()))
                    {
                        break;
                    }
                    else
                    {
                        currentlySelectedAccountId++;
                    }
                }

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
                alt_bld.setTitle("Accounts");

                final CharSequence[] emailAddresses = new CharSequence[accounts.size()];
                for (int i = 0; i < emailAddresses.length; i++)
                {
                    emailAddresses[i] = accounts.get(i).getEmailAddress();
                }

                alt_bld.setSingleChoiceItems(emailAddresses, currentlySelectedAccountId, new DialogInterface
                        .OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int item)
                    {
                        for (Account account : accounts)
                        {
                            if (account.getEmailAddress() == emailAddresses[item])
                            {
                                currentlySelectedAccount = account;
                            }
                        }

                        updateAccountInfo();
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });
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
    protected void onPause()
    {
        if (currentlySelectedAccount != null)
        {
            SharedPreferences.Editor editPrefs = prefs.edit();
            editPrefs.putString(Constants.SAVE_EMAIL_ADDRESS_KEY, currentlySelectedAccount.getEmailAddress());
            editPrefs.apply();
        }

        super.onPause();
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
