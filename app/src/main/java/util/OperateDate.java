package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OperateDate {

    // 将String数组转换成json数据格式
    public String stringToJson(String[] stringArray) {
        JSONObject jsonObject;

        if (stringArray == null)
            return "";

        jsonObject = new JSONObject();

        try {
            if (stringArray.length == 2) {
                jsonObject.put("phone_num", stringArray[0]);
                jsonObject.put("password", stringArray[1]);
            } else {
                jsonObject.put("user_name", stringArray[0]);
                jsonObject.put("password", stringArray[1]);
                jsonObject.put("phone_num", stringArray[2]);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf(jsonObject);
    }

    // Json 数据转相应信息
    public int jsonToInt(String jsonString) {
        int type = 1;

        try {
            JSONObject responseJson = new JSONObject(jsonString);
            type = responseJson.getInt("type");
            Log.i("type", " " + type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return type;
    }

    // 获取用户的token值
    public static String getToken(String param) {
        Map<String, Object> map = new HashMap<>();

        //去除{}
        String s1 = param.replace("{", "");
        String s2 = s1.replace("}", "");
        String s3 = s2.replace("\"data\":", "");
        String s4 = s3.replace("\"", "");
        String s5 = s4.trim();

        //1.根据逗号分隔
        String[] split = s5.split(",");

        for (int i = split.length - 1; i >= 0; i--) {
            String trim = split[i].trim();
            String[] split1 = trim.split(":");

            map.put(split1[0],split1[1]);
        }

        return Objects.requireNonNull(map.get("token")).toString();
    }

    /*  发送jsonString到服务器并解析回应
     *  msg.what:
     *  0：服务器连接失败
     *  1：注册 /登录成功 跳转页面
     *  2：用户已存在 /登录失败
     *  3：地址为空
     *  4：连接超时
     */

    public void setDate(final String jsonString, final Handler mHandler, final URL url, Context context) {
        if (url == null)
            mHandler.sendEmptyMessage(3);

        else {
            new Thread(() -> {
                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;

                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setInstanceFollowRedirects(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();

                    // 发送数据
                    Log.i("JSONString", jsonString);
                    DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                    os.writeBytes(jsonString);
                    os.flush();
                    os.close();
                    Log.i("状态码","" + httpURLConnection.getResponseCode());

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String temp;

                        while ((temp = bufferedReader.readLine()) != null) {
                            response.append(temp);
                            String token = getToken(response.toString());
                            SharedPreferences sharedPreferences = context.getSharedPreferences("user_token", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                            editor.putString("token", token);
                            editor.apply();//提交修改

                            Log.i("Main", response.toString());
                        }

                        int type = jsonToInt(response.toString());

                        switch (type) {
                            case 1:
                                mHandler.sendEmptyMessage(1);
                                break;
                            case 2:
                                mHandler.sendEmptyMessage(2);
                                break;
                            default:
                        }
                    } else {
                        mHandler.sendEmptyMessage(0);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(4);
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }).start();
        }
    }
}
