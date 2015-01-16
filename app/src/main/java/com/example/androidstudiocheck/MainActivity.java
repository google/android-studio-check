package com.example.androidstudiocheck;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
                registrationText.setText(rot13(in));
                if (!rot13(rot13(in)).equals(in)) throw new RuntimeException ("Invalid rot13 conversion");
            }
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
        emailText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
