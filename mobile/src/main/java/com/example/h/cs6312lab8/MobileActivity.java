package com.example.h.cs6312lab8;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MobileActivity extends AppCompatActivity implements
GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    GoogleApiClient googleApiClient=null;
    EditText ed;
    Button bt;
    public static final String TAG="MyDataMAP.....";
    public static final String WEARABLE_DATA_PATH="/wearable/data/path";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        ed=(EditText)findViewById(R.id.text);
        bt=(Button)findViewById(R.id.button);
        GoogleApiClient.Builder builder=new GoogleApiClient.Builder(this);
        builder.addApi(Wearable.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        googleApiClient=builder.build();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(googleApiClient!=null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendMessage();
    }

    public void sendMessage(){
        if(googleApiClient.isConnected()){
            String message=(ed).getText().toString();
            if(message == null || message.equalsIgnoreCase("")){
                message="Happy Days!!!";
            }
            new SendMessageToDataLayer(WEARABLE_DATA_PATH,message).start();
        }
        else{

        }
    }
    /**public void sendMessageOnClick(View view){
            sendMessage();
        }**/

    public class SendMessageToDataLayer extends Thread{
        String path;
        String message;
        public SendMessageToDataLayer(String path, String message){
            this.path=path;
            this.message=message;
        }

        @Override
        public void run() {
            NodeApi.GetConnectedNodesResult nodesList= Wearable.NodeApi
                    .getConnectedNodes(googleApiClient).await();
            for(Node node: nodesList.getNodes()){
                MessageApi.SendMessageResult messageResult=
                        Wearable.MessageApi.sendMessage(googleApiClient,node.getId(),path,message.getBytes()).await();
                if(messageResult.getStatus().isSuccess()){
                    Log.v(TAG,"Message: successfully sent to"+ node.getDisplayName());
                    Log.v(TAG,"Message: Node Id is"+node.getId());
                    Log.v(TAG,"Message: Node size is"+nodesList.getNodes().size());
                }
                else{
                    Log.v(TAG,"Error while sending message");
                }
            }
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
