package com.edu.qqcommon;

public interface MessageType {

    String MESSAGE_LOGIN_SUCCEED = "1";
    String MESSAGE_LOGIN_FAIL = "2";

    String MESSAGE_COMMON_MES = "3"; //普通信息包

    String MESSAGE_GET_ONLINE_USER = "4"; //在线用户列表
    String MESSAGE_RET_ONLINE_USER = "5"; //返回在线用户列表

    String MESSAGE_CLIENT_EXIT = "6"; //客服端请求退出

    String MESSAGE_TO_ALL_MESS = "7";

    String MESSAGE_FILE_MESS = "8";

    String MESSAGE_PUT_MESS = "9";

}
