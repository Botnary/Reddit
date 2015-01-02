package com.botnari.reddit.async;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by botnari on 15-01-01.
 */
public class LoginTask extends AsyncTask<String,Object,String> {
    @Override
    protected String doInBackground(String... params) {
        String code = params[0];
        Log.d("REDDIT_DEBUG",String.format("Code [%s] ",code));
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://www.reddit.com/api/v1/access_token");
        httpPost.setHeader("User-Agent","Android app by /u/iamfromk");
        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                ("sCn233gDaZPyAg:").getBytes(),
                Base64.NO_WRAP);


        httpPost.setHeader("Authorization", base64EncodedCredentials);
        List<NameValuePair> nameValuePair = new ArrayList<>(3);
        nameValuePair.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePair.add(new BasicNameValuePair("code", code));
        nameValuePair.add(new BasicNameValuePair("redirect_uri", "redditoauthtest://response/"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        StringBuilder builder = new StringBuilder();
        try {
            response = httpClient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            Log.d("REDDIT_DEBUG", builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // write response to log
        return builder.toString();
    }
}
