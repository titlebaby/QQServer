package com.edu.qqserver.service;

import com.edu.qqcommon.Message;
import com.edu.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类的对象和某个客服端保持通信
 */
public class ServerConnectClientThread extends Thread {

    private Socket socket = null;

    private String userId; //链接到服务器端的用户id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }


    @Override
    public void run() {
        //线程一直在run，可以一直发送、接收消息

        while (true) {
            System.out.println("服务器和客服端(用户" + userId + ")保持通信，读取数据.....");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                String mesType = message.getMesType();


                if (mesType.equals(MessageType.MESSAGE_GET_ONLINE_USER)) {
                    System.out.println(message.getSender() + "要在线用户列表");
                    String onlineUser = MangeClientThread.getOnlineUser();
                    Message message2 = new Message();
                    message2.setMesType(MessageType.MESSAGE_GET_ONLINE_USER);
                    message2.setContent(onlineUser);
                    message.setGetter(message.getSender());
                    //返回给客服端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message2);

                } else if (mesType.equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + "要退出.");
                    //将客服端对应的线程，从集合中删除
                    MangeClientThread.removeServerConnectClientThread(message.getSender());
                    socket.close();// 这个线程所持有的这个socket。相当于关闭这个通信通道
                    // 退出（当前）线程
                    break;
                } else if (mesType.equals(MessageType.MESSAGE_COMMON_MES)) {
                    System.out.println(message.getSender() + "要和"+ message.getGetter()+"私聊");
                    //将客服端对应的线程
                    ServerConnectClientThread serverConnectClientThread =
                            MangeClientThread.getServerConnectClientThread(message.getGetter());
                    //这里不能用this.socket. 不是同一个socket
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                    break;
                } else if(mesType.equals(MessageType.MESSAGE_TO_ALL_MESS)) {
                    HashMap<String, ServerConnectClientThread> hm = MangeClientThread.getHm();
                    //遍历 取出key
                    Iterator<String> iterator = hm.keySet().iterator();
                    while (iterator.hasNext()) {
                        String onlineUserId = iterator.next().toString();
                        if(!onlineUserId.equals(message.getSender())) {
                            //转发
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }

                    }
                } else if(mesType.equals(MessageType.MESSAGE_FILE_MESS)){
                    ServerConnectClientThread serverConnectClientThread =
                            MangeClientThread.getServerConnectClientThread(message.getGetter());

                    ObjectOutputStream oos = new ObjectOutputStream(
                            serverConnectClientThread.getSocket().getOutputStream()
                    );
                    oos.writeObject(message);
                } else {
                    System.out.println("其他类型");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public Socket getSocket() {
        return this.socket;
    }

}
