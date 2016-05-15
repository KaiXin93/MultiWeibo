package com.example.qkx.multiweibo.activity;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.previous.WeiboDetail;
import com.example.qkx.multiweibo.util.UrlUtils;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

/**
 * Created by qkx on 16/3/8.
 */
public class MyNineGridViewAdapter extends NineGridImageViewAdapter<WeiboDetail.Statuse.PicUrl> {
    private Context mContext;
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    public MyNineGridViewAdapter(Context context) {
        this.mContext = context;
        mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new MyBitmapCache());
    }
    @Override
    protected void onDisplayImage(Context context, ImageView imageView, WeiboDetail.Statuse.PicUrl picUrl) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.defaultimage, R.drawable.errorimage);
        mImageLoader.get(UrlUtils.fromThumbnail2Bmiddle(picUrl.thumbnail_pic), listener);
//        mImageLoader.get(picUrl.thumbnail_pic, listener);
//        mImageLoader.get(picUrl.thumbnail_pic, listener);
    }
}
