package com.ings.gogo.base;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseNet {
    public static final int NET_OK = 1;
    public static final int NET_ERROR = 0;
    public URL url;
    public Map<String,String> nameValuesPairs;
    public BaseParser parser;
    public OnNetCompleteListener listener;
    public static ExecutorService executorService= Executors.newFixedThreadPool(5);
    public Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==NET_OK){
            	Log.e("BaseNet", "NET_OK");
              listener.onOK(msg.obj);
            }

            if (msg.what==NET_ERROR){
                listener.onError();
                Log.e("BaseNet", "NET_ERROR");
            }
        }
    };

    public BaseNet(String url, Map<String, String> nameValuesPairs, BaseParser parser, OnNetCompleteListener listener) {


        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
           handler.sendEmptyMessage(NET_ERROR);
        }
        this.nameValuesPairs = nameValuesPairs;
        this.parser = parser;
        this.listener = listener;
    }

    public BaseNet(String url, BaseParser parser, OnNetCompleteListener listener) {
        try {
            this.url =  new URL(url);
        } catch (MalformedURLException e) {
            handler.sendEmptyMessage(NET_ERROR);
        }
        this.parser = parser;
        this.listener = listener;
    }

    public abstract  void netting();
}
