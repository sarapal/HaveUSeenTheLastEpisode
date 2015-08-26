package it.asg.hustle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class FacebookActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    Button share,details;
    ShareDialog shareDialog;
    LoginButton login;
    ProfilePictureView profile;
    Dialog details_dialog;
    TextView details_txt;
    static String id = null;
    de.hdodenhof.circleimageview.CircleImageView circleProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        callbackManager = CallbackManager.Factory.create();
        login = (LoginButton)findViewById(R.id.login_button);
        profile = (ProfilePictureView)findViewById(R.id.picture);
        shareDialog = new ShareDialog(this);
        share = (Button)findViewById(R.id.share);
        //details = (Button)findViewById(R.id.details);
        //login.setReadPermissions("public_profile email");
        share.setVisibility(View.INVISIBLE);
        //details.setVisibility(View.INVISIBLE);
        //details_dialog = new Dialog(this);
        //details_dialog.setContentView(R.layout.dialog_details);
        //details_dialog.setTitle("Details");
        //details_txt = (TextView)details_dialog.findViewById(R.id.details);
        /*details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details_dialog.show();
            }
        });*/

        //prova pulsante
        circleProfile = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.circleViewProfile);
        circleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = null;
                ImageView profileImageView = ((ImageView)profile.getChildAt(0));
                bitmap  = ((BitmapDrawable)profileImageView.getDrawable()).getBitmap();
                circleProfile.setImageBitmap(bitmap);
            }
        });
        //fine pulsante

        if(AccessToken.getCurrentAccessToken() != null){
            RequestData();
            share.setVisibility(View.VISIBLE);
            //details.setVisibility(View.VISIBLE);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AccessToken.getCurrentAccessToken() != null) {
                    Log.d("HUSTLE", "Setting id_facebook to NULL");
                    SharedPreferences options = getSharedPreferences("id_facebook", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = options.edit();
                    editor.putString("id_facebook" ,null);
                    editor.commit();
                    share.setVisibility(View.INVISIBLE);
                    //details.setVisibility(View.INVISIBLE);
                    profile.setProfileId(null);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent content = new ShareLinkContent.Builder().build();
                shareDialog.show(content);

            }
        });
        login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if(AccessToken.getCurrentAccessToken() != null){
                    RequestData();
                    share.setVisibility(View.VISIBLE);
                    //details.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

    }
    public void RequestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object,GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if(json != null){
                        //String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        //Log.d("HUSTLE", text);;
                        //details_txt.setText(Html.fromHtml(text));
                        Log.d("HUSTLE", "SAVING id_facebook :" +json.getString("id"));
                        SharedPreferences options = getSharedPreferences("id_facebook", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = options.edit();
                        editor.putString("id_facebook" ,json.getString("id"));
                        editor.commit();
                        profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}