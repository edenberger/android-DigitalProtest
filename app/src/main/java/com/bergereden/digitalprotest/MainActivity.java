package com.bergereden.digitalprotest;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    String email, destination, hashtag = "";
    String proto = "http"; // TODO: Allow only http and https
    EditText testDest, testEmail, testHashtag, manualDest, manualEmail, manualHashtag;
    TextView address;
    Switch switchView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webview);
        address = (TextView) findViewById(R.id.address);
        testDest = (EditText) findViewById(R.id.testDest);
        testHashtag = (EditText) findViewById(R.id.hashtag);
        switchView = (Switch) findViewById(R.id.switchView);
        testEmail = (EditText) findViewById(R.id.testEmail);
        manualDest = (EditText) findViewById(R.id.manualDest);
        manualEmail = (EditText) findViewById(R.id.manualEmail);
        manualHashtag = (EditText) findViewById(R.id.manualHashtag);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final Uri data = intent.getData();

        if (data != null) {
            String[] parts = String.valueOf(data).split("/");
            parts[1] = "1";
            if (!Arrays.asList(parts).contains("")) {
                hashtag = parts[6].replaceAll("[# ]", "");
                email = parts[5];
                destination = parts[4];
                proto = parts[3];
            }
        }

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testDest.getVisibility() == View.VISIBLE) {
                    destination = getAddress();
                    email = getEmail();
                    hashtag = getHashtag();

                } else if (manualDest.getVisibility() == View.VISIBLE) {
                    destination = String.valueOf(manualDest.getText());
                    email = String.valueOf(manualEmail.getText());
                    hashtag = String.valueOf(manualHashtag.getText()).replaceAll("[# ]", "");
                }
                if (!destination.equals("") && !email.equals("")) {
                    webView.setWebViewClient(new WebViewClient());
                    webView.getSettings().setCacheMode(webView.getSettings().LOAD_NO_CACHE);
                    webView.getSettings().setAppCacheEnabled(false);
                    webView.clearHistory();
                    webView.clearCache(true);
                    webView.getSettings().setJavaScriptEnabled(true);

                    webView.loadUrl(proto + "://" + destination);
                    Intent mailer = new Intent(Intent.ACTION_SEND);
                    mailer.setType("text/plain");
                    mailer.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    mailer.putExtra(Intent.EXTRA_SUBJECT, "#" + hashtag);
                    startActivity(Intent.createChooser(mailer, "Send email..."));
                } else {
                    showError("Missing destination or e-mail, please make new or manually set one", "OK");
                }
            }
        });

        testDest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateAddress(getAddress(), getEmail(), getHashtag());
            }
        });

        testEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateAddress(getAddress(), getEmail(), getHashtag());
            }
        });

        testHashtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateAddress(getAddress(), getEmail(), getHashtag());
            }
        });

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchView.setText(R.string.https);
                    proto = "https";
                    updateAddress(getAddress(), getEmail(), getHashtag());

                } else {
                    switchView.setText(R.string.http);
                    proto = "http";
                    updateAddress(getAddress(), getEmail(), getHashtag());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_testpage) {
            if (testDest.getVisibility() == View.INVISIBLE) {
                showTest();
            } else {
                hideTest();
            }
            return true;
        } else if (id == R.id.action_manual) {
            if (manualDest.getVisibility() == View.INVISIBLE) {
                showManual();
            } else {
                hideManual();
            }
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError(String message, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(
                button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showTest() {
        hideManual();
        testHashtag.setVisibility(View.VISIBLE);
        webView.setVisibility(View.VISIBLE);
        testDest.setVisibility(View.VISIBLE);
        testDest.requestFocus();
        testEmail.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        switchView.setVisibility(View.VISIBLE);
    }

    private void showManual() {
        hideTest();
        manualDest.setVisibility(View.VISIBLE);
        manualEmail.setVisibility(View.VISIBLE);
        manualHashtag.setVisibility(View.VISIBLE);
    }

    private void hideTest() {
        testHashtag.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);
        testDest.setVisibility(View.INVISIBLE);
        testEmail.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        switchView.setVisibility(View.INVISIBLE);
    }

    private void hideManual() {
        manualDest.setVisibility(View.INVISIBLE);
        manualEmail.setVisibility(View.INVISIBLE);
        manualHashtag.setVisibility(View.INVISIBLE);
    }

    private void updateAddress(String add, String em, String hash) {
        address.setText("http://sudfeld.io/" + proto + "/" + add + "/" + em + "/#" + hash);
    }

    private String getAddress() {
        return String.valueOf(testDest.getText()).toLowerCase().replace(" ", "");
    }

    private String getEmail() {
        return String.valueOf(testEmail.getText()).toLowerCase().replace(" ", "");
    }
    private String getHashtag() {
        return String.valueOf(testHashtag.getText()).toLowerCase().replaceAll("[# ]", "");
    }
}














