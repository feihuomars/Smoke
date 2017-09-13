package com.example.chao.smoke;

import com.example.chao.smoke.Constant;

/**
 * Created by chao on 2017/4/6.
 */

public class UserNameAndPassword {
    /*
     检测用户名的格式的正确性
 */
    public static  boolean Usernamecheck(String username) {


        if (username.length() == Constant.Phone_Number)//不为空 并且 位数符合要求
        {
            return true;
        } else {
            return false;
        }
    }

    /*
        检测密码格式的正确性
     */
    public static Boolean Passwordcheck(String password) {

        if (password.length() >= Constant.Password_Min_Number)//不为空 并且 位数符合要求
        {
            return true;
        } else {
            return false;
        }
    }
}
