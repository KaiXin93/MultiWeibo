package com.example.qkx.multiweibo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qkx.multiweibo.R;
import com.example.qkx.multiweibo.previous.*;
import com.example.qkx.multiweibo.previous.Constans;
import com.example.qkx.multiweibo.util.UrlUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 15/11/23.
 */
public class MyFragment extends Fragment {
    private static final String TAG = "MyFragment";

    private MyAdapter adapter;
//    private MyNineGridViewAdapter mNineGridViewAdapter;
    private ListView listView;

    private List<WeiboDetail.Statuse> mList = new ArrayList<>();
    private WeiboDetail weiboDetail[] = {};

    private String access_token;

    private RequestQueue mQueue;

    private ImageLoader imageLoader;
    public static MyFragment newInstance(String access_token) {
        MyFragment myFragment = new MyFragment();
        myFragment.setAccess_token(access_token);
        return myFragment;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView -----> start");

        View view = inflater.inflate(R.layout.layout_test_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate -----> start");
        init();
    }

    private void init() {
        mQueue = Volley.newRequestQueue(getActivity());
        imageLoader = new ImageLoader(mQueue, new MyBitmapCache());
//        mNineGridViewAdapter = new MyNineGridViewAdapter(getActivity());

        //获取AcessToken
        /*SharedPreferences spf = getActivity().getSharedPreferences("Oauth", Context.MODE_PRIVATE);
        access_token = spf.getString("access_token", null);
        Log.d(TAG, "access_token ----->" + access_token);*/
        queryHome();
    }

    private void queryHome() {
        String queryHomeUrl = Constans.HOME_URL + "?access_token=" + access_token ;
        Log.d(TAG, "sendHomeRequst ----->" + queryHomeUrl);
        /**
         * 用Volley发送请求
         */
        StringRequest stringRequest = new StringRequest(queryHomeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("WeiboDisplayActivity", "response ----->" + s);
                        handleHomeRequest(s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "加载失败...", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error ----->" + volleyError.getMessage());
                    }
                });
        mQueue.add(stringRequest);//开始发送请求
    }

    private void    handleHomeRequest(String response) {
        Gson gson = new Gson();
        WeiboDetail weiboDetail2 = gson.fromJson(response, WeiboDetail.class);
        mList = weiboDetail2.statuses;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                Log.d(TAG, "ListView has notified");
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
//                convertView = View.inflate(getActivity(),R.layout.listview_fragment_detail,null);
                convertView = View.inflate(getActivity(), R.layout.weibo_detail_layout, null);

                holder = new ViewHolder();
                holder.ivHeadPic = (ImageView) convertView.findViewById(R.id.iv_head_pic);
                holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
                holder.tvTimeDevice = (TextView) convertView.findViewById(R.id.tv_time_device);
                holder.tvStatusText = (TextView) convertView.findViewById(R.id.tv_status_text);
                holder.imageGroup = (RelativeLayout) convertView.findViewById(R.id.imageGroup);
                holder.retweetedStatus = (LinearLayout) convertView.findViewById(R.id.retweeted_status);
                holder.tvRetweetedText = (TextView) convertView.findViewById(R.id.tv_retweeted_text);
                holder.retweetedImageGroup = (RelativeLayout) convertView.findViewById(R.id.retweeted_imageGroup);
//                holder.ngivPicGroup = (NineGridImageView) convertView.findViewById(R.id.ngiv_picGroup_test);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            /**
             * Volley加载头像
             */
            ImageLoader.ImageListener headListener = ImageLoader.getImageListener(holder.ivHeadPic, R.drawable.defaultimage, R.drawable.errorimage);
            imageLoader.get(mList.get(position).user.profile_image_url, headListener);

            /**
             * 用户名,微博文本
             */
            holder.tvUserName.setText(mList.get(position).user.screen_name);
            holder.tvStatusText.setText(mList.get(position).text);
            /**
             * 在imageGroup中加载微博图片内容
             */
            List<WeiboDetail.Statuse.PicUrl> picUrls = mList.get(position).pic_urls;
            if (picUrls != null) {
                holder.imageGroup.removeAllViews();
                for (int i = 0; i < picUrls.size(); i++) {
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(250,250);
                    //计算ImageView的宽度 间隔5dp
                    int side = (listView.getWidth() - 20 - 10) / 3;
                    //ImageView大小
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(side, side);
                    switch (i / 3) {
                        case 0:
                            params.topMargin = 0;
                            break;
                        case 1:
                            params.topMargin = side + 5;
                            break;
                        case 2:
                            params.topMargin = side * 2 + 10;
                            break;
                    }
                    switch (i % 3) {
                        case 0:
                            params.leftMargin = 0;
                            break;
                        case 1:
                            params.leftMargin = side + 5;
                            break;
                        case 2:
                            params.leftMargin = side * 2 + 10;
                            break;
                    }
//                    params.topMargin = (i / 3) * side;
//                    params.leftMargin = (i % 3) * side;
                    imageView.setLayoutParams(params);
//                    viewHolder.imageGroup.addView(imageView, 250, 250);
                    holder.imageGroup.addView(imageView);

                    ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
                    //缩略图
//                    imageLoader.get(picUrls.get(i).thumbnail_pic, listener);
                    //中等图片
                    imageLoader.get(UrlUtils.fromThumbnail2Bmiddle(picUrls.get(i).thumbnail_pic), listener);
                }

            }
            /**
             * 转发微博
             */
            WeiboDetail.Statuse.RetweetedStatus retweetedStatus = mList.get(position).retweeted_status;
            if (retweetedStatus == null) {
                holder.retweetedStatus.setVisibility(View.GONE);
            } else {
                holder.retweetedStatus.setVisibility(View.VISIBLE);
                holder.tvRetweetedText.setText("@" + retweetedStatus.user.screen_name + ":" +
                        retweetedStatus.text);

                /**
                 * 转发图片内容
                 * 必须先清理原先图片
                 */
                holder.retweetedImageGroup.removeAllViews();
                List<WeiboDetail.Statuse.PicUrl> retweetedPicUrls = retweetedStatus.pic_urls;
                if (!retweetedPicUrls.isEmpty()) {
                    for (int i = 0; i < retweetedPicUrls.size(); i++) {
                        ImageView imageView = new ImageView(getActivity());
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //计算ImageView的宽度 间隔5dp padding左右各10dp
                        int side = (listView.getWidth() - 20 - 10) / 3;
                        //ImageView大小
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(side, side);
                        switch (i / 3) {
                            case 0:
                                params.topMargin = 0;
                                break;
                            case 1:
                                params.topMargin = side + 5;
                                break;
                            case 2:
                                params.topMargin = side * 2 + 10;
                                break;
                        }
                        switch (i % 3) {
                            case 0:
                                params.leftMargin = 0;
                                break;
                            case 1:
                                params.leftMargin = side + 5;
                                break;
                            case 2:
                                params.leftMargin = side * 2 + 10;
                                break;
                        }
//                    params.topMargin = (i / 3) * side;
//                    params.leftMargin = (i % 3) * side;
                        imageView.setLayoutParams(params);
//                    viewHolder.imageGroup.addView(imageView, 250, 250);
                        holder.retweetedImageGroup.addView(imageView);

                        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
                        //缩略图
//                        imageLoader.get(retweetedPicUrls.get(i).thumbnail_pic, listener);
                        //中等图片
                    imageLoader.get(UrlUtils.fromThumbnail2Bmiddle(retweetedPicUrls.get(i).thumbnail_pic), listener);
                    }
                }
            }
//            holder.ngivPicGroup.setAdapter(mNineGridViewAdapter);

//            holder.ngivPicGroup.setImagesData(mList.get(position).pic_urls);

            return convertView;
        }
    }
    class ViewHolder {
        public ImageView ivHeadPic;
        public TextView tvUserName;
        public TextView tvTimeDevice;
        public TextView tvStatusText;
        //原创微博图片集合
        public RelativeLayout imageGroup;
        //转发微博集合
        public LinearLayout retweetedStatus;
        public TextView tvRetweetedText;
        //转发微博图片集合
        public RelativeLayout retweetedImageGroup;

//        public NineGridImageView ngivPicGroup;

    }
}
