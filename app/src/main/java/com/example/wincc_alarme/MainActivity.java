package com.example.wincc_alarme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Button connect, get;
    private TextView output,outputGet;
    private OkHttpClient client;
    private Gson gson = new Gson();
    private WebSocket webSocket;
    private EchoWebSocketListener listener = new EchoWebSocketListener();

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;


        public void close(WebSocket webSocket){
            webSocket.close(NORMAL_CLOSURE_STATUS, "disconnect");
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text,false);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex(), false);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason, false);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage(), false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get = (Button) findViewById(R.id.Get);
        outputGet = (TextView) findViewById(R.id.outputGet);
        client = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://194.56.189.167/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolder jsonPlaceHolder = retrofit.create(JsonPlaceHolder.class);
        Call<List<AlarmModel>> call = jsonPlaceHolder.getAlarme();


        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputGet.setText("");
                call.clone().enqueue(new Callback<List<AlarmModel>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<AlarmModel>> call, retrofit2.Response<List<AlarmModel>> response) {
                        if(!response.isSuccessful()) {
                            outputGet.setText("ResponseCode: " + response.code());
                            return;
                        }
                        List<AlarmModel> alarmModels = response.body();

                        for(AlarmModel alarmModel : alarmModels)
                        {
                            String content = "";
                            content += "ConditionName: " + alarmModel.getConditionName() + "\n";
                            content += "Message: " + alarmModel.getMessage() + "\n";
                            outputGet.append(content);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AlarmModel>> call, Throwable t) {
                        outputGet.setText(t.getMessage());
                    }
                });
            }
        });
    }

    private void output(final String txt, final boolean get) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String content = "";
                try{
                    AlarmModel[] alarmModel = gson.fromJson(txt, AlarmModel[].class);
                    for (AlarmModel i : alarmModel) {
                        content += "ConditionName: " + i.getConditionName() + "\n";
                        content += "Message: " + i.getMessage() + "\n";
                        if(get){
                            outputGet.setText(content);
                        }
                        else{
                            output.setText(content);
                        }
                    }
                }
                catch(Exception e){
                    output.setText("Parsing Error: " + txt);
                }
                //content += "ConditionName: " + alarmModel[0].getConditionName() + "\n";
                //content += "Message: " + alarmModel[0].getMessage() + "\n";
                //output.setText(output.getText().toString() + "\n\n" + content);
            }
        });
    }
}