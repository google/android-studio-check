package com.example.androidstudiocheck;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public EditText emailText;
    public TextView registrationText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle to update the registration code as the user enters their email address
        emailText = (EditText)findViewById(R.id.emailText);
        registrationText = (TextView)findViewById(R.id.registrationText);
        emailText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                String in = emailText.getText().toString();
                registrationText.setText(rot13(in).toUpperCase());
                if (!rot13(rot13(in)).equals(in)) throw new RuntimeException ("Invalid rot13 conversion");
            }
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
        emailText.setText("");
    }
}
