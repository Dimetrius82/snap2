package com.snapmail.login;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.snapmail.R;
import com.snapmail.util.Account;
import com.snapmail.util.Constants;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";

    private AuthorizationService authorizationService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        authorizationService = new AuthorizationService(this);

        setContentView(R.layout.activity_choose_email_service);

        List<IdentityProvider> providers = IdentityProvider.getEnabledProviders(this);

        for (final IdentityProvider idp : providers)
        {
            final AuthorizationServiceConfiguration.RetrieveConfigurationCallback retrieveCallback =
                    new AuthorizationServiceConfiguration.RetrieveConfigurationCallback()
                    {

                        @Override
                        public void onFetchConfigurationCompleted(
                                @Nullable AuthorizationServiceConfiguration serviceConfiguration,
                                @Nullable AuthorizationException ex)
                        {
                            if (ex != null)
                            {
                                Log.w(TAG, "Failed to retrieve configuration for " + idp.name, ex);
                            }
                            else
                            {
                                Log.d(TAG, "configuration retrieved for " + idp.name
                                        + ", proceeding...");
                                makeAuthRequest(serviceConfiguration, idp);
                            }
                        }
                    };

            if (idp.name.equals(getString(R.string.google_name)))
            {
                LinearLayout emailProviderGoogleLinearLayout = findViewById(R.id.email_provider_google_linear_layout);
                emailProviderGoogleLinearLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.d(TAG, "initiating auth for " + idp.name);
                        idp.retrieveConfig(LoginActivity.this, retrieveCallback);
                    }
                });
            }
        }
    }

    private void makeAuthRequest(
            @NonNull AuthorizationServiceConfiguration serviceConfig,
            @NonNull IdentityProvider idp)
    {

        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                serviceConfig,
                idp.getClientId(),
                ResponseTypeValues.CODE,
                idp.getRedirectUri())
                .setScope(idp.getScope())
                .build();

        authorizationService.performAuthorizationRequest(
                authRequest,
                CompleteAuthorizationActivity.createPostAuthorizationIntent(
                        this,
                        authRequest,
                        serviceConfig.discoveryDoc,
                        idp.getClientSecret()),
                authorizationService.createCustomTabsIntentBuilder()
                        .setToolbarColor(getCustomTabColor())
                        .build());

        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getCustomTabColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return getColor(R.color.color_email_provider);
        }
        else
        {
            return getResources().getColor(R.color.color_email_provider);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        authorizationService.dispose();
    }
}
