package com.yejia.listviewfreshload;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends Activity implements FreshLoadListView.IReflashListener {

    private ArrayList<PostEntity> post_list;
    private PostListAdapter adapter;
    private FreshLoadListView listview;
    // page取得某一页索引，放在url的参数中
    private int page;
    // pages取得分页总数
    private int pages;
    // 取得Wordpress博客内容的 posts的API，分页获取数据
    private String url = "http://alicewpsae.sinaapp.com/?json=core.get_posts&page=";
    // 缓存数据库
    private PostsDb db;
    // 数据库读写对象
    private SQLiteDatabase dbRead, dbWrite;

    // 处理列表项目点击事件
    private AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 点击列表中一条项目，打开博客详细页面
            Intent i = new Intent(MainActivity.this, PostActivity.class);
            String title = post_list.get(position-1).getTitle();
            String content = post_list.get(position-1).getContent();
            i.putExtra("title", title);
            i.putExtra("content", content);
            startActivity(i);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 默认加载第一页
        page = 1;
        // 创建数据库对象
        db = new PostsDb(this);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        // 使用GET方法获取数据
        getData(page+"");
    }

    private void insertDB(String _title, String _date, String _content){
        ContentValues cv = new ContentValues();
        cv.put("title",_title);
        cv.put("date",_date);
        cv.put("content",_content);

        dbWrite.insert("post",null,cv);
    }

    private void setData(String data){
        try {
            System.out.println("data: "+data);
            // 把GET返回的结果转换成JSONObject
            JSONObject root = new JSONObject(data);
            // 取得posts转换成JSON数组
            JSONArray array = root.getJSONArray("posts");
            System.out.println("array.length = "+array.length());
            // 取得分页总数
            pages = root.getInt("pages");
            post_list = new ArrayList<PostEntity>();

            // 清除旧的数据库
            dbWrite.execSQL("delete from post");

            for(int i=0; i< array.length(); i++){

                // 取得postJSON数据
                JSONObject post = array.getJSONObject(i);
                String title = post.getString("title");
                String date = post.getString("date");
                String content = post.getString("content");

                // 给entity对象赋值
                PostEntity entity = new PostEntity();
                entity.setTitle(title);
                entity.setDate(date);
                entity.setContent(content);
                System.out.println("第" + (i + 1) + "个");
                System.out.println(entity.getTitle());
                // 把entity放到list中
                post_list.add(entity);

                // 把数据存入数据库
                insertDB(title,date,content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRefreshData(String data){
        System.out.println("refresh data: " + data);
        try {
            // 把GET返回的结果转换成JSONObject
            JSONObject root = new JSONObject(data);
            // 取得posts转换成JSON数组
            JSONArray array = root.getJSONArray("posts");
            System.out.println("array.length = "+array.length());
            // 取得分页总数
            pages = root.getInt("pages");

            for(int i=0; i< array.length(); i++){

                // 取得postJSON数据
                JSONObject post = array.getJSONObject(i);
                String title = post.getString("title");
                String date = post.getString("date");
                String content = post.getString("content");

                // 给entity对象赋值
                PostEntity entity = new PostEntity();
                entity.setTitle(title);
                entity.setDate(date);
                entity.setContent(content);
                System.out.println("第" + (post_list.size()+1) + "个");
                System.out.println(entity.getTitle());
                // 把entity放到list中
                post_list.add(entity);

                // 把数据存入数据库
                insertDB(title,date,content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showList(ArrayList<PostEntity> post_list){
        if(adapter == null){
            listview = (FreshLoadListView) findViewById(R.id.lv_posts);
            listview.setInterface(this);
            adapter = new PostListAdapter(this, post_list);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(listViewItemClickListener);
        }else{
            adapter.onDataChanged(post_list);
        }
    }
    // 检测网络是否可用
    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    // 使用GET方法获取数据
    private void getData(String _page){
        // 网络已连接的情况
        if(isNetworkConnected()){
            String _url = url + _page;
            new AsyncTask<String, Void, String>(){

                @Override
                protected String doInBackground(String... params) {
                    try {
                        URL url = new URL(params[0]);
                        URLConnection connection = url.openConnection();
                        InputStream is = connection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is,"utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String line="";
                        String result = "";
                        System.out.println("GET>>>>>>>>>>>>>>>>>>");
                        while((line=br.readLine())!=null){
                            result+=line;
                            System.out.println(result);
                        }
                        br.close();
                        isr.close();
                        is.close();
                        return result;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                // GET请求结束以后的回调
                @Override
                protected void onPostExecute(String s) {
                    // 首页设置数据
                    if(page==1){
                        setData(s);
                    }else{
                        // 不是首页，更新数据
                        setRefreshData(s);
                    }

                    System.out.println("post_list.size = " + post_list.size());
//                // 显示列表
                    showList(post_list);
                    // 列表更新结束
                    listview.reflashComplete();
                    super.onPostExecute(s);
                }
            }.execute(_url);
        }else{
            // 没有网络连接从数据库中读取数据
            post_list = new ArrayList<PostEntity>();

            Cursor c = dbRead.query("post", null, null, null, null, null, null);

            while(c.moveToNext()){
                String title = c.getString(c.getColumnIndex("title"));
                String date = c.getString(c.getColumnIndex("date"));
                String content = c.getString(c.getColumnIndex("content"));

                // 给entity对象赋值
                PostEntity entity = new PostEntity();
                entity.setTitle(title);
                entity.setDate(date);
                entity.setContent(content);
                System.out.println("第" + (post_list.size()+1) + "个");
                System.out.println(entity.getTitle());
                // 把entity放到list中
                post_list.add(entity);
            }

            System.out.println("post_list.size = " + post_list.size());
            // 显示列表
            showList(post_list);
            // 列表更新结束
            listview.reflashComplete();
        }
    }

    // 实现IReflashListener的onReflash方法，用来获取新数据，更新列表
    @Override
    public void onReflash() {

        System.out.println("onReflash");
        // 获取下一页数据
        if(page<pages){
            page += 1;
            getData(page+"");
        }else  if(pages == 0){
            System.out.println("pages = " + pages+", page = "+page);
            getData(page+"");
        }else{
            // 最后一页不获取数据
            listview.reflashComplete();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
