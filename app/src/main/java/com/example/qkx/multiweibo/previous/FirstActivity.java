package com.example.qkx.multiweibo.previous;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.activity.MainActivity;


/**
 * Created by qkx on 2015/11/1.
 */
public class FirstActivity extends Activity implements View.OnClickListener{
    private Button btnSendOauth;
    private Button btnAcquireWeibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);

        init();
    }

    private void init() {
        btnSendOauth = (Button) findViewById(R.id.sendOauth);
        btnAcquireWeibo = (Button) findViewById(R.id.acquireWeibo);

        btnSendOauth.setOnClickListener(this);
        btnAcquireWeibo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendOauth : {
//                String anthorize_url = Constans.AUTHORIZE_URL + "?client_id=" + Constans.CLIENT_ID + "&redirect_uri=" + Constans.REDIRECT_URI;
                String anthorize_url = "https://open.weibo.cn/oauth2/authorize?" +
                        "client_id=3702692012&redirect_uri=https://api.weibo.com/oauth2/default.html&display=mobile";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(anthorize_url)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
                startActivity(intent);
                break;
            }
            case R.id.acquireWeibo: {
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            }
            default: {
                break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

}
