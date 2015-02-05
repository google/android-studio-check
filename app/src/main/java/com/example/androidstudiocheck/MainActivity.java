package com.example.androidstudiocheck;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    public EditText emailText;
    public TextView registrationText;
    public boolean foundWearDevice;
    public boolean foundWearCompanion;
    public boolean foundAndroidEmulator;

    public String rot13 (String in) {
        String out = "";
        for (int i = 0; i < in.length(); ++i) {
            char c = in.charAt(i);
            if      ((c >= 'A') && (c <= 'M')) c += 13;
            else if ((c >= 'a') && (c <= 'm')) c += 13;
            else if ((c >= 'N') && (c <= 'Z')) c -= 13;
            else if ((c >= 'n') && (c <= 'z')) c -= 13;
            out = out + c;
        }
        return out;
    }

    class DetectWearTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... unused) {
            foundAndroidEmulator = Build.HARDWARE.contains("goldfish");
            if (foundAndroidEmulator) {
                Log.d("AndroidStudioCheck", "Discovered that device is an emulator");
            } else {
                Log.d("AndroidStudioCheck", "Discovered that device is a real phone/tablet");
            }

            try {
                getPackageManager().getPackageInfo("com.google.android.wearable.app", PackageManager.GET_META_DATA);
                Log.d("AndroidStudioCheck", "Detected Android Wear companion app");
                foundWearCompanion = true;
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("AndroidStudioCheck", "Failed to detect Android Wear companion app");
                foundWearCompanion = false;
            }

            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addApi(Wearable.API)
                    .build();
            ConnectionResult result = mGoogleApiClient.blockingConnect();
            if (!result.isSuccess()) {
                Log.d("AndroidStudioCheck", "Failed to connect to Play Services for Wearable.API");
                foundWearDevice = false;
            }
            List<Node> connectedNodes =
                    Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();
            if (connectedNodes.size() >= 1) {
                Log.d("AndroidStudioCheck", "Found " + (connectedNodes.size() - 1) + " wearables nodes");
                foundWearDevice = true;
            } else {
                Log.d("AndroidStudioCheck", "Did not find any wearable nodes");
                foundWearDevice = false;
            }
            // Do not update the UI here, it must be done in the UI thread from onPostExecute()
            return null;
        }

        protected void onPostExecute() {
            refreshOutput();
        }
    }

    public String getAttributeString() {
        String out = "";
        if (foundWearCompanion)
            out = out + "C";
        if (foundWearDevice)
            out = out + "W";
        if (foundAndroidEmulator)
            out = out + "E";
        if (out.length() > 0)
            out = "=" + out;
        return out;
    }

    public void refreshOutput() {
        String in = emailText.getText().toString();
        String out = rot13(in).toUpperCase();
        // Do not output anything until we have what might be an email address
        if (((out.length()) > 3) && (out.contains("@"))) {
            out += getAttributeString();
        } else {
            out = "";
        }
        registrationText.setText(out);
        if (!rot13(rot13(in)).equals(in)) throw new RuntimeException ("Invalid rot13 conversion");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle to update the registration code as the user enters their email address
        emailText = (EditText)findViewById(R.id.emailText);
        registrationText = (TextView)findViewById(R.id.registrationText);
        emailText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                refreshOutput();
            }
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
        emailText.setText("");

        // Schedule a background check to see if we have a wear device
        new DetectWearTask().execute();
    }
}
