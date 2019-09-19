package oren.battleships;

import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class APIConsumer {
    private static final String TAG = APIConsumer.class.getSimpleName();
    private String protocol = "http";
    private String address = "10.0.2.2";
    private String port = "8080";

    public APIConsumer() {
    }

    public String makeServiceCall(String reqUrl) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    public Map<String, Object> getLobbyData(String user) throws Exception {
        // Making a request to url and getting response
        String url = protocol + "://"+ address + ":" + port + "/getLobbyData";
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("username", user);
        Map<String, Object> result = this.send_post(url, parameters);

        return result;
    }

    public Map<String, Object> send_post(String url, HashMap<String, String> parameters) throws Exception {
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyGson.toJson(parameters);
        Gson gson = new Gson();
        String jsonString = this.SendPost(url, prettyJson);
        Type ResultMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> parsedJson = gson.fromJson(jsonString, ResultMap);
        return parsedJson;
    }

    public String SendPost(String url, HashMap<String, String> parameters) throws Exception {
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyGson.toJson(parameters);
        return this.SendPost(url, prettyJson);
    }

    public String SendPost(String reqUrl, String message) throws Exception {
        URL obj = new URL(reqUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        // Send post request
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        wr.writeBytes(message);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'POST' request to URL : " + obj);
        System.out.println("Post parameters : " + message);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
