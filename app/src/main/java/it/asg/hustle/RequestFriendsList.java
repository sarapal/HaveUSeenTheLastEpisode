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
    String logtag = "Activity Facebook";
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
        Log.d(logtag, response.toString());
        return response.getJSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        Log.d(logtag, jsonObject.toString());
    }
}

