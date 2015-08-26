package it.asg.hustle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;


public class RequestFriendsList extends AsyncTask<Void,Void,JSONObject> {
    private String LOG_TAG = "ActivityFacebook";
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
        try {
            Log.d(LOG_TAG, jsonObject.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

