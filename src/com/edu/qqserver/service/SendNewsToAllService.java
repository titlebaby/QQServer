package com.edu.qqserver.service;

import com.edu.qqcommon.Message;
import com.edu.qqcommon.MessageType;
import com.edu.utils.Utility;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SendNewsToAllService implements Runnable{
    @Override
    public void run() {

        while (true) {
            System.out.println("请输入服务器要推送的新闻、消息[输入 exit 退出推送服务器]");

            String content = Utility.readString(100);

            if (content.equals("exit")) {
                break;
            }
            Message message = new Message();
            message.setSender("服务器");
            message.setSendTime(new Date().toString());
            message.setContent(content);
            message.setMesType(MessageType.MESSAGE_TO_ALL_MESS);


            HashMap<String, ServerConnectClientThread> hm = MangeClientThread.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String userId = iterator.next();
                ServerConnectClientThread serverConnectClientThread = hm.get(userId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
