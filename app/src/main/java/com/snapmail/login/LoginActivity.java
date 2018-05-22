package com.snapmail.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.snapmail.R;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import java.util.List;

public class LoginActivity extends AppCompatActivity
{
    private AuthorizationService authorizationService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        authorizationService = new AuthorizationService(this);

        setContentView(R.layout.activity_login);

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
                            if (ex == null && serviceConfiguration != null)
                            {
                                makeAuthRequest(serviceConfiguration, idp);
                            }
                            else
                            {
                                // TODO Handle error
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
                        .build());

        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        authorizationService.dispose();
    }
}
