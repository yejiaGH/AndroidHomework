package com.yejia.listviewfreshload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by yejiapc on 15/12/8.
 */
public class PostActivity extends Activity {
    private TextView tv_posttitle;
    private TextView tv_postcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layout);
        tv_posttitle = (TextView) findViewById(R.id.tv_posttitle);
        tv_postcontent = (TextView) findViewById(R.id.tv_postcontent);

        Intent i = getIntent();
        tv_posttitle.setText(i.getStringExtra("title"));
        // 显示含有html标签的文本内容
        tv_postcontent.setText(Html.fromHtml(i.getStringExtra("content")));
    }
}
