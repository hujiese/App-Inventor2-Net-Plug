package com.jack.scut;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final String msg = "name:jack;password:123456;IP:127.0.0.1";
	public static void main(String[] args) throws IOException {
		// 创建服务器Socket对象
		ServerSocket ss = new ServerSocket(8000);
		// 监听客户端的连接
		Socket s = ss.accept(); // 阻塞
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						OutputStream os = s.getOutputStream();
						os.write(msg.getBytes());
						Thread.sleep(5000);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		while(true) {
			// 获取输入流
			InputStream is = s.getInputStream();
			byte[] bys = new byte[1024];
			int len = is.read(bys); // 阻塞
			String server = null;
			if(len != -1) {
				server = new String(bys, 0, len);
				System.out.println("server:" + server);
			}
			

//			// 获取输出流
//			OutputStream os = s.getOutputStream();
//			os.write(server.toUpperCase().concat("\n").getBytes());


			
//			os.write("hello\n".getBytes());
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
}
