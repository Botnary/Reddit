package com.botnari.reddit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.botnari.reddit.async.LoginTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class StartActivity extends Activity {
    private boolean auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
            //Toast.makeText(this, "Lets login!!", Toast.LENGTH_LONG).show();
            //loginDialog();
            webViewDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loginDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.login_dialog);
        dialog.setTitle("Use your Reddit account to login.");
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        final EditText txtLogin = (EditText) dialog.findViewById(R.id.loginInput);
        final EditText txtPassword = (EditText) dialog.findViewById(R.id.passwordInput);

        //set up button
        Button button = (Button) dialog.findViewById(R.id.loginBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                //loginAction(txtLogin.getText().toString(), txtPassword.getText().toString());
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }

    private void webViewDialog(){
        auth = false;
        String url = String.format("https://www.reddit.com/api/v1/authorize?client_id=sCn233gDaZPyAg&response_type=code&\n" +
                "state=vcxzzcxvxzcv&redirect_uri=redditoauthtest://response/&duration=permanent&scope=read");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alert = builder.create();
        alert.setTitle("Title here");

        WebView wv = new WebView(this);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                if(url.contains("redditoauthtest://response/?state=")){
                    Log.d("REDDIT_DEBUG", url);
                    loginAction(url);
                    alert.cancel();
                }
            }
        });

        alert.setView(wv);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void loginAction(String url) {
        if(auth) return;
        List<NameValuePair> params = null;
        try {
            params = URLEncodedUtils.parse(new URI(url), "UTF-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String code = "";
        for (NameValuePair param : params) {
            if(param.getName().contains("code")){
                code = param.getValue();
            }
        }
        LoginTask task = new LoginTask();
        task.execute(code);
        auth = true;
    }
}
