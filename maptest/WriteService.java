package com.jikexueyuan.maptest;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class WriteService extends Service {
    private boolean running;
    private Location myLocation = new Location();
    private Socket socket = null;
    private BufferedWriter writer = null;
    private BufferedReader reader = null;
    private List<Integer> ids = new ArrayList<>();


    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        System.out.println("start write service");
        AsyncTask<Void, String, Void> writeAsyncTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                    socket = new Socket("10.0.2.2", 8000);
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    writerInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            private void writerInfo() {
                try {
                    while (running) {
                        Thread.sleep(500);
                        String info = myLocation.getId() + "@" + myLocation.getRadius() + "@" + myLocation.getLatitude() + "@" + myLocation.getLongitude() + "\n";
                        writer.write(info);
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        writeAsyncTask.executeOnExecutor(Executors.newCachedThreadPool());
        AsyncTask<Void, String, Void> readAsyncTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(4000);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readInfo();
                return null;
            }


            private void readInfo() {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        Location location = new Location();
                        String splitData[] = line.split("[@]");
                        location.setId(Integer.parseInt(splitData[0]));
                        location.setRadius(Float.parseFloat(splitData[1]));
                        location.setLatitude(Double.parseDouble(splitData[2]));
                        location.setLongitude(Double.parseDouble(splitData[3]));
                            callback.onDataChange(location);
                    }
                    System.out.println("clear");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        readAsyncTask.executeOnExecutor(Executors.newCachedThreadPool());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }

    public static interface Callback {
        void onDataChange(Location locations);
    }

    private Callback callback = null;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    public class Binder extends android.os.Binder {
        public void setRadius(float radius) {
            myLocation.setRadius(radius);
        }

        public void setLatitude(double latitude) {
            myLocation.setLatitude(latitude);
        }

        public void setLongitude(double longitude) {
            myLocation.setLongitude(longitude);
        }

        public void setId(int id) {
            myLocation.setId(id);
        }

        public WriteService getService() {
            return WriteService.this;
        }

    }

}
