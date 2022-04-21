package task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cloudhouse.MyApplication;

import util.HttpUtil;

public class UploadTask extends AsyncTask<String, Void, String> {
    private final static String TAG = "UploadTask";

    @Override
    protected String doInBackground(String... strings) {
        SharedPreferences sharedPreferences = MyApplication.getAppContext()
                .getSharedPreferences("user_token", Context.MODE_PRIVATE);
        String mToken = sharedPreferences.getString("token", "");
        String filePath = strings[0];
        Log.d(TAG, "filePath=" + filePath);
        return HttpUtil.upload("http://106.52.165.70:8080/user/profile", filePath, mToken);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mListener.finishUpload(s);
    }

    private OnUploadListener mListener; // 声明一个文件上传的监听器对象
    // 设置文件上传的监听器
    public void setOnUploadListener(OnUploadListener listener) {
        mListener = listener;
    }

    // 定义一个文件上传的监听器接口
    public interface OnUploadListener {
        void finishUpload(String result);
    }
}
