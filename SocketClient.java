package cn.roger.Socket;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.*;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.View;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;



@DesignerComponent(version = 1,
    description = "by SCUT Jack",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")

@SimpleObject(external = true)

public class SocketClient extends AndroidNonvisibleComponent {
    Socket socket = null;
    OutputStream ou = null;
	InputStream is = null;
	BufferedReader br = null;
    String buffer = "";
    String geted1;
    MyThread mt;
	boolean closed = false;
    final int CONNECT = 100001;
    final int SENDMESSAGE = 100002;
    final int CLOSE = 100003;
	final int RECVMESSAGE = 100004;
	final int RECVBKMESSAGE = 100005;
	
    public SocketClient(ComponentContainer container) {
        super(container.$form());
    }
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
			if(msg.what == RECVBKMESSAGE){
				GetServerMessage(msg.obj.toString());
		    }else{
				GetMessage(msg.obj.toString());
			}
        }
 
    };
	
    @SimpleFunction(description = "close connection")
    public void closeConnect(){
        if(socket != null){
            mt = new MyThread(CLOSE);
            mt.start();
        }else{
            GetMessage("连接未创建！");
        }
    }
    @SimpleFunction(description = "send message")
    public void sendMessage(String s){
        if(socket != null){
            mt = new MyThread(SENDMESSAGE);
            mt.setText(s);
            mt.start();
        }else{
            GetMessage("连接未创建！");
        }
    }
	@SimpleFunction(description = "receive one message")
    public void recvMessage(){
        if(socket != null){
            mt = new MyThread(RECVMESSAGE);
            mt.start();
        }else{
            GetMessage("连接未创建！");
        }
    }
    @SimpleFunction(description = "connect server")
    public void connect(String ip){
        if(socket == null){
            mt = new MyThread(CONNECT);
            mt.setIP(ip);
            mt.start();
        }else{
            GetMessage("连接已创建！");
        }
    }
	
    @SimpleFunction(description = "get connected devices IP")
    public String getConnectedDevIP(){
		ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		if(connectedIP.get(0) != null){
			return connectedIP.get(1);
		}else{
			return "No Device Connect";
		}
    }
	
	public HashMap<String, String> parseString(String message){
		HashMap<String, String> map = new HashMap<String, String>();
		String lists[] = message.split(";");
		for(String str : lists) {
			String key = str.split(":")[0];
			String value = str.split(":")[1];
			map.put(key, value);
		}
		
		return map;
	}
	
	
	@SimpleEvent
    public void GetServerMessage(String s){
		HashMap<String, String> map = parseString(s);
		String temp = "";
		for (Entry<String, String> entry : map.entrySet()) {
			temp += entry.getKey() + "-" + entry.getValue() + "\n";
		}
		EventDispatcher.dispatchEvent(this, "GetServerMessage", "\n"+temp);
    }
	
    @SimpleEvent
    public void GetMessage(String s){
        EventDispatcher.dispatchEvent(this, "GetMessage", "\n"+s);
    }

	
    class MyThread extends Thread {
 
        public String txt1;
        public String IP;
        Message msg;
        public int flag;
        public MyThread(int flag) {
            this.flag = flag;
        }
        public void setText(String s){
            txt1 = s;
        }
        public void setIP(String ip){
            IP = ip;
        }
        @Override
        public void run() {
            switch(flag){
                case CONNECT:
                    try {
                        socket = new Socket();
                        msg = myHandler.obtainMessage();
                        msg.obj = "开始连接";
                        myHandler.sendMessage(msg);
                        socket.connect(new InetSocketAddress(IP, 8000), 1000);
                        ou = socket.getOutputStream();
						is = socket.getInputStream();
                        msg = myHandler.obtainMessage();
                        msg.obj = "连接成功";
                        myHandler.sendMessage(msg);
						while(true && !closed){
							byte[] bys = new byte[1024];
							int len = is.read(bys); // 阻塞
							String temp = new String(bys, 0, len);
			
							if(temp != null){
								msg = myHandler.obtainMessage();
								msg.what = RECVBKMESSAGE;
								msg.obj = temp;
								myHandler.sendMessage(msg);
						   }
						}
                    } catch (SocketTimeoutException aa) {
                        msg = myHandler.obtainMessage();
                        msg.obj = "连接超时";
                        myHandler.sendMessage(msg);
                    } catch (IOException e) {
                        msg = myHandler.obtainMessage();
                        msg.obj = "连接错误";
                        myHandler.sendMessage(msg);
                    }
                break;
                case SENDMESSAGE:
                    try {
                        ou.write(txt1.getBytes("utf-8"));
                        ou.write("\n".getBytes("utf-8"));
                        ou.flush();
                        msg = myHandler.obtainMessage();
                        msg.obj = "发送完毕";
                        myHandler.sendMessage(msg);
						

                    }catch (IOException e) {
                        msg = myHandler.obtainMessage();
                        msg.obj = "发送信息错误";
                        myHandler.sendMessage(msg);
                    }
                break;
                case RECVMESSAGE:
                    try {
						/*br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String tmep = null;
						tmep = br.readLine();
					    if(tmep != null){
							msg = myHandler.obtainMessage();
							msg.obj = socket.getInetAddress().getHostAddress()+":"+tmep;
							myHandler.sendMessage(msg);
                       }*/
					   	byte[] bys = new byte[1024];
						int len = is.read(bys); // 阻塞
						String temp = new String(bys, 0, len);
		
					    if(temp != null){
							msg = myHandler.obtainMessage();
							msg.obj = socket.getInetAddress().getHostAddress()+":"+temp;
							myHandler.sendMessage(msg);
                       }
                    }catch (IOException e) {
                        msg = myHandler.obtainMessage();
                        msg.obj = "接受消息错误";
                        myHandler.sendMessage(msg);
                    }
                break;
                case CLOSE:
                    try {
                        ou.close();
                        socket.close();
                        socket = null;
                        msg = myHandler.obtainMessage();
                        msg.obj = "关闭";
                        myHandler.sendMessage(msg);
                    }catch (IOException e) {
                        msg = myHandler.obtainMessage();
                        msg.obj = "关闭连接错误";
                        myHandler.sendMessage(msg);
                    }
                break;
            }
        }
    }
}
