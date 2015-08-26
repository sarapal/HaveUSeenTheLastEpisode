package it.asg.hustle;

import android.content.Context;
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
import java.net.MalformedURLException;
import java.net.URL;


public class RequestFriendsList extends AsyncTask<Void,Void,JSONObject> {
    private String LOG_TAG = "ActivityFacebook";
    private Context ctx;

    public RequestFriendsList (Context context) {
        this.ctx = context;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        Bundle bundle = new Bundle();
        bundle.putString("fields", "id, name, picture");
        GraphResponse response = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                bundle,
                HttpMethod.GET,
                new GraphRequest.Callback(){
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                    }
                }
        ).executeAndWait();
        return response.getJSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        Log.d("HUSTLE", jsonObject.toString());
    }
}

