package com.edu.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类用于管理金额客服端通信的线程
 */
public class MangeClientThread {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);

    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }
    public static ServerConnectClientThread removeServerConnectClientThread(String userId) {
        return hm.remove(userId);
    }

    public static String getOnlineUser() {
        //遍历hashMap 集合
        Iterator<String> iterator = hm.keySet().iterator();
        String onlineUserList = "";
        while (iterator.hasNext()) {
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }
    public static HashMap<String, ServerConnectClientThread> getHm() {
       return hm;
    }
}
