package com.example.chao.smoke;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by chao on 2017/4/6.
 * Http相关的操作建立连接、提交用户名和密码、接收服务器的相应
 */

public class Http {
    public static void sendHttpRequest(final String address, final Map<String, String> contentValuesMap, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("content-type", "text/plain");
                    connection.connect();

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    if (contentValuesMap.get("job").equals("change")) {
                        out.writeBytes("username=" + contentValuesMap.get("username") + "&" + "password=" + contentValuesMap.get("password") + "&job=" + contentValuesMap.get("job") + "&newpassword=" + contentValuesMap.get("newpassword"));
                    } else {
                        out.writeBytes("username=" + contentValuesMap.get("username") + "&" + "password=" + contentValuesMap.get("password") + "&job=" + contentValuesMap.get("job"));
                    }
//                    connection.setDoOutput(true);
//                    connection.setDoInput(true);
//                    PrintWriter out = new PrintWriter(connection.getOutputStream());
//                    out.print("username=jj");
//                    out.flush();

                    if (connection.getResponseCode() == 302) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        System.out.print(response);
                        if (listener != null) {
                            listener.onFinish(response.toString());
                        }

                    } else {
                        listener.onBad("bad");
                    }

                } catch (Exception e) {
                    listener.onError(e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
