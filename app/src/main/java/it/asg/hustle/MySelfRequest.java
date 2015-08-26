package it.asg.hustle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MySelfRequest extends AsyncTask<Void,Void,JSONObject> {

    private String LOG_TAG = "HUSTLE";
    private Context ctx;

    public MySelfRequest (Context context) {
        this.ctx = context;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,picture");
        GraphResponse response = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback(){
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                    }
                }
        ).executeAndWait();
        Log.d("HUSTLE", (String) response.toString());
        return response.getJSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        try {
            Log.d(LOG_TAG, jsonObject.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            Log.d("HUSTLE", jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URL imageURL = null;
        Bitmap bmp = null;

        /*try {
            image_path = new URL(jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));
            Log.d("HUSTLE", image_path.toString());
            HttpURLConnection conn = (HttpURLConnection) image_path.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.connect();
            InputStream i = conn.getInputStream();
            bmp = BitmapFactory.decodeStream(i);
            imageURL = new URL("http://graph.facebook.com/" + jsonObject.getLong("id") + "/picture?type=large");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("HUSTLE", imageURL.toString());

        String profilePhoto = "profilePhoto";

        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(profilePhoto, Context.MODE_PRIVATE);
            Log.d("ASG", "immagine file in scrittura");
            fos.write(bmp.getRowBytes());
            Log.d("ASG", "immagine file scritto");
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }*/

    }
}