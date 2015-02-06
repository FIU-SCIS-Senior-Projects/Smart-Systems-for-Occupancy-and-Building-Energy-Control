package fiu.ssobec;

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
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maria on 2/5/2015.
 */
public class Database {


    List<NameValuePair> parameters;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    String url;

    /*

    **/
    public Database(ArrayList<NameValuePair> parameters, String url)  {

        this.parameters = parameters;
        this.url = url;
    }


    public String send() throws InterruptedException {
        Thread mThread = new Thread(new Runnable() {

            public void run() {
                db_post();
            }

        });
        mThread.start();
        mThread.join();
        return response.toString();
    }


    public void db_post(){

        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(parameters));

            //Execute HTTP Post Request
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);

            /*
            runOnUiThread(new Runnable() {

                public void run() {
                    System.out.println("Response from PHP : " + response);
                    //dialog.dismiss();
                }

            });*/


          /*
            if (response.equalsIgnoreCase("User Found")) {
                runOnUiThread(new Runnable() {

                    public void run() {
                        //
                    }

                });
                //Intent intent = new Intent(this, MyZonesActivity.class);
                startActivity(intent);
            }
            else {
                System.out.println("Something happened");
            }*/


            //response.toString();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
