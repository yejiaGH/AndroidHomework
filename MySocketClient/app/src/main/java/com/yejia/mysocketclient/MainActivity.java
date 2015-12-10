package com.yejia.mysocketclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText ip;
    EditText editText;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) findViewById(R.id.et_host);
        editText = (EditText) findViewById(R.id.et_message);
        text = (TextView) findViewById(R.id.tv_output);

        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }

    // -------------------------------------

    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    String _ip;
    public void connect(){
           _ip = ip.getText().toString();
           AsyncTask<Void,String,Void> read = new AsyncTask<Void, String, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        socket = new Socket(_ip,12345);
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        publishProgress("@success");
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "连接失败",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    try {
                        String line;
                        while((line=reader.readLine())!=null){
                            publishProgress(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(String... values) {
                    System.out.println("别人说："+values[0]);
                    if(values[0].equals("@success")){
                        Toast.makeText(MainActivity.this, "连接成功",Toast.LENGTH_SHORT).show();
                    }
                    text.append("别人说：" + values[0]+"\n");
                    super.onProgressUpdate(values);
                }
            };

            read.execute();

    }

    public void send(){
        try {
            text.append("我说："+editText.getText().toString()+"\n");
            writer.write(editText.getText().toString() +"\n");
            writer.flush();
            editText.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
