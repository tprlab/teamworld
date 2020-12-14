package com.leaguetor.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esportplace.android.Tracer;

import java.util.Map;


public class NetClient {

    protected static String getString(HttpResponse response) throws Exception {
        StringBuilder builder = new StringBuilder();
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);

        } else {
            Tracer.err("Invalid http status: " + statusCode, null);
            return null;
        }
        return builder.toString();
    }

    public static String getString(String url) {

        Tracer.log("Requesting " + url);
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            return getString(response);
        } catch (Exception e) {
            Tracer.err("Excetion on " + url, e);
            e.printStackTrace();
            return null;
        }
    }

    protected static JSONObject parseJSON(String str) {
        if (str == null)
            return null;
        try {
            JSONObject js = new JSONObject(str);
            return js;
        } catch (Exception e) {
            Tracer.err("Cannot parse json " + str, e);
        }
        return null;
    }


    public static JSONObject getJSON(String url) {
        String str = getString(url);
        if (str == null || str.length() < 1)
            return null;
        JSONObject js = parseJSON(str);
        Tracer.log("URL [" + url + "] returned " + js.toString());
        return js;
    }

    public static JSONArray getArray(String url) {
        String str = getString(url);
        if (str == null || str.length() < 1)
            return null;
        try {
            JSONArray js = new JSONArray(str);
            Tracer.log("URL [" + url + "] returned " + js.toString());
            return js;
        } catch (Exception e) {
            Tracer.err("Cannot parse json " + str, e);
        }
        return null;
    }

    public static JSONObject post(String url, Map<String, Object> data) {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        Tracer.log("Posting to " + url);
        HttpPost httpost = new HttpPost(url);
        try {

            if (data != null) {
                JSONObject js = new JSONObject(data);
                StringEntity se = new StringEntity(js.toString());
                httpost.setEntity(se);
            }
            httpost.setHeader("Accept", "application/json");            
            httpost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpclient.execute(httpost);
            String str = getString(resp);
            Tracer.log("Post result: " + str);
            return str == null ? null : parseJSON(str);
        } catch(Throwable t) {
            Tracer.err("Cannot post to " + url, t);
            return null;
        }

    }
}