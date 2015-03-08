package fiu.ssobec.DataAccess;

import android.content.Intent;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maria on 2/5/2015.
 */
public class ExternalDatabaseController {


    List<NameValuePair> parameters;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    String url;
    String response_str;

    /*

    **/
    public ExternalDatabaseController(ArrayList<NameValuePair> parameters, String url)  {

        this.parameters = parameters;
        this.url = url;
        response_str = null;
    }


    public String send() throws InterruptedException {
        Thread mThread = new Thread(new Runnable() {

            public void run() {
                db_post();
            }

        });
        mThread.start(); //start thread
        mThread.join(); //wait for thread to finish

        return response_str;
    }

    public void db_post(){

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(parameters));

            //Execute HTTP Post Request
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response_str = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response_str);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
