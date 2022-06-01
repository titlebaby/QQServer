package com.edu.qqserver.service;

import com.edu.qqcommon.Message;
import com.edu.qqcommon.MessageType;
import com.edu.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 在监听9999端口，等待客服端的链接，并保持通信
 */
public class QQServer {

    private ServerSocket ss = null;

//    private static HashMap<String, User> validUsers = new HashMap<>();
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> offLineDb = new ConcurrentHashMap<>();

    static {
        //初始化 静态代码块
        validUsers.put("100", new User("100","123456"));
        validUsers.put("200", new User("200","123456"));
        validUsers.put("300", new User("300","123456"));
        validUsers.put("小明", new User("小明","123456"));

    }


    public boolean checkUser(String userId , String paw){
        User user = validUsers.get(userId);
        if(user == null) {
            return false;
        }
        if (!user.getPassword().equals(paw)) {
            return false;
        }
        return true;
    }

    public QQServer() {

        try {
            System.out.println("服务器在9999 端口监听。。。。。");
            new Thread(new SendNewsToAllService()).start();
            ss = new ServerSocket(9999);
            // 不是说监听一个客服端一个链接就退出了，是一直监听
            // 会继续监听
            while (true) {
                Socket socket = ss.accept();
                //得到socket中的数据流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                User u = (User) ois.readObject();
                // 创建一个message 对象，准备回复客服端
                Message message = new Message();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//                ServerConnectClientThread isExist = MangeClientThread.getServerConnectClientThread(u.getUserId());

                if (checkUser(u.getUserId(), u.getPassword())) {
                    System.out.println("用户id= "+u.getUserId()+"登录成功~");
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    //创建一个线程，和客服端保持通信，该线程需要持有socket
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动了之后，才会执行run方法
                    serverConnectClientThread.start(); //来一个连接就会启动一个线程，怎么判断是不是同一个客服端来的 ，通过userid
                    // 将线程对象放入集合中，方便管理
                    MangeClientThread.addClientThread(u.getUserId(),serverConnectClientThread );

                } else {
                    System.out.println("登录失败，用户id= "+u.getUserId()+"pwd="+u.getPassword());
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

//            如果服务器端退出了while循环 ，需要关闭serversocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
