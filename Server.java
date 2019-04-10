package com.jack.scut;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final String msg = "name:jack;password:123456;IP:127.0.0.1";
	public static void main(String[] args) throws IOException {
		// ����������Socket����
		ServerSocket ss = new ServerSocket(8000);
		// �����ͻ��˵�����
		Socket s = ss.accept(); // ����
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
			// ��ȡ������
			InputStream is = s.getInputStream();
			byte[] bys = new byte[1024];
			int len = is.read(bys); // ����
			String server = null;
			if(len != -1) {
				server = new String(bys, 0, len);
				System.out.println("server:" + server);
			}
			

//			// ��ȡ�����
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
