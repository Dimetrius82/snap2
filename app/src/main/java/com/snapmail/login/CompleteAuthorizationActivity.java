package com.snapmail.login;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import net.openid.appauth.AuthorizationServiceDiscovery;
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

public class CompleteAuthorizationActivity extends AppCompatActivity
{
    private static final String TAG = "CompleteAuthActivity";

    private static final String EXTRA_AUTH_SERVICE_DISCOVERY = "authServiceDiscovery";
    private static final String EXTRA_CLIENT_SECRET = "clientSecret";

    private AuthState authState;
    private AuthorizationService authorizationService;

    private ProgressBar completeAuthorizationProgressBar;
    private ImageView authorizationCompleteImageView;

    private static final int BUFFER_SIZE = 1024;
    private JSONObject userInfoJson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_authorization);

        completeAuthorizationProgressBar = findViewById(R.id.complete_authorization_progress_bar);
        completeAuthorizationProgressBar.setVisibility(View.VISIBLE);

        authorizationCompleteImageView = findViewById(R.id.authorization_complete_image_view);
        authorizationCompleteImageView.setVisibility(View.GONE);

        authorizationService = new AuthorizationService(this);

        if (authState == null)
        {
            AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
            authState = new AuthState(response, ex);

            if (response != null)
            {
                Log.d(TAG, "Received AuthorizationResponse.");
                exchangeAuthorizationCode(response);
            }
            else
            {
                Log.i(TAG, "Authorization failed: " + ex);
                Toast.makeText(this, R.string.authorization_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    static PendingIntent createPostAuthorizationIntent(
            @NonNull Context context,
            @NonNull AuthorizationRequest request,
            @Nullable AuthorizationServiceDiscovery discoveryDoc,
            @Nullable String clientSecret)
    {
        Intent intent = new Intent(context, CompleteAuthorizationActivity.class);

        if (discoveryDoc != null)
        {
            intent.putExtra(EXTRA_AUTH_SERVICE_DISCOVERY, discoveryDoc.docJson.toString());
        }

        if (clientSecret != null)
        {
            intent.putExtra(EXTRA_CLIENT_SECRET, clientSecret);
        }

        return PendingIntent.getActivity(context, request.hashCode(), intent, 0);
    }

    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse)
    {
        HashMap<String, String> additionalParams = new HashMap<>();
        if (getClientSecretFromIntent(getIntent()) != null)
        {
            additionalParams.put("client_secret", getClientSecretFromIntent(getIntent()));
        }
        performTokenRequest(authorizationResponse.createTokenExchangeRequest(additionalParams));
    }

    static String getClientSecretFromIntent(Intent intent)
    {
        if (!intent.hasExtra(EXTRA_CLIENT_SECRET))
        {
            return null;
        }
        return intent.getStringExtra(EXTRA_CLIENT_SECRET);
    }

    private void performTokenRequest(TokenRequest request)
    {
        authorizationService.performTokenRequest(
                request,
                new AuthorizationService.TokenResponseCallback()
                {
                    @Override
                    public void onTokenRequestCompleted(
                            @Nullable TokenResponse tokenResponse,
                            @Nullable AuthorizationException ex)
                    {
                        receivedTokenResponse(tokenResponse, ex);
                    }
                });
    }

    private void receivedTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException)
    {
        authState.update(tokenResponse, authException);

        getAccountInfo();
    }

    private void getAccountInfo()
    {
        AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getIntent());

        if (!authState.isAuthorized()
                || discoveryDoc == null
                || discoveryDoc.getUserinfoEndpoint() == null)
        {
            //TODO Error occurred
        }
        else
        {
            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    fetchUserInfo();
                    return null;
                }
            }.execute();
        }
    }

    static AuthorizationServiceDiscovery getDiscoveryDocFromIntent(Intent intent)
    {
        if (!intent.hasExtra(EXTRA_AUTH_SERVICE_DISCOVERY))
        {
            return null;
        }
        String discoveryJson = intent.getStringExtra(EXTRA_AUTH_SERVICE_DISCOVERY);
        try
        {
            return new AuthorizationServiceDiscovery(new JSONObject(discoveryJson));
        }
        catch (JSONException | AuthorizationServiceDiscovery.MissingArgumentException ex)
        {
            throw new IllegalStateException("Malformed JSON in discovery doc");
        }
    }

    private void fetchUserInfo()
    {
        if (authState.getAuthorizationServiceConfiguration() == null)
        {
            Log.e(TAG, "Cannot make userInfo request without service configuration");
        }

        authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction()
        {
            @Override
            public void execute(String accessToken, String idToken, AuthorizationException ex)
            {
                if (ex != null)
                {
                    Log.e(TAG, "Token refresh failed when fetching user info");
                    return;
                }

                AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getIntent());
                if (discoveryDoc == null)
                {
                    throw new IllegalStateException("no available discovery doc");
                }

                URL userInfoEndpoint;
                try
                {
                    userInfoEndpoint = new URL(discoveryDoc.getUserinfoEndpoint().toString());
                }
                catch (MalformedURLException urlEx)
                {
                    Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
                    return;
                }

                InputStream userInfoResponse = null;
                try
                {
                    HttpURLConnection conn = (HttpURLConnection) userInfoEndpoint.openConnection();
                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    conn.setInstanceFollowRedirects(false);
                    userInfoResponse = conn.getInputStream();
                    String response = readStream(userInfoResponse);
                    updateUserInfo(new JSONObject(response));
                }
                catch (IOException ioEx)
                {
                    Log.e(TAG, "Network error when querying userinfo endpoint", ioEx);
                }
                catch (JSONException jsonEx)
                {
                    Log.e(TAG, "Failed to parse userinfo response");
                }
                finally
                {
                    if (userInfoResponse != null)
                    {
                        try
                        {
                            userInfoResponse.close();
                        }
                        catch (IOException ioEx)
                        {
                            Log.e(TAG, "Failed to close userinfo response stream", ioEx);
                        }
                    }
                }
            }
        });
    }

    private static String readStream(InputStream stream) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        char[] buffer = new char[BUFFER_SIZE];
        StringBuilder sb = new StringBuilder();
        int readCount;
        while ((readCount = br.read(buffer)) != -1)
        {
            sb.append(buffer, 0, readCount);
        }
        return sb.toString();
    }

    private void updateUserInfo(final JSONObject jsonObject)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                userInfoJson = jsonObject;
                onAccountInfoGotten();
            }
        });
    }

    private void onAccountInfoGotten()
    {
        if (userInfoJson != null)
        {
            try
            {
                String name = null;
                if (userInfoJson.has("name"))
                {
                    name = userInfoJson.getString("name");
                }

                String emailAddress = null;
                if (userInfoJson.has("email"))
                {
                    emailAddress = userInfoJson.getString("email");
                }

                Account account = new Account();
                account.setName(name);
                account.setEmailAddress(emailAddress);
                account.setAuthToken(authState.getAccessToken());
                account.setRefreshToken(authState.getRefreshToken());

                onAccountSetupSuccessful(account);
            }
            catch (JSONException ex)
            {
                Log.e(TAG, "Failed to read userinfo JSON", ex);
            }
        }
        else
        {
            //TODO Error occurred
        }
    }

    private void onAccountSetupSuccessful(final Account account)
    {
        completeAuthorizationProgressBar.setVisibility(View.GONE);
        authorizationCompleteImageView.setVisibility(View.VISIBLE);

        new CountDownTimer(2000, 2000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                // Do nothing
            }

            @Override
            public void onFinish()
            {
                Constants.LOGIN_NEW_ACCOUNT_CALLBACK.onAuthorizationComplete(account);
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        authorizationService.dispose();
    }
}
