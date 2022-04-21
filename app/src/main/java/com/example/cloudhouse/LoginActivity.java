package com.example.cloudhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import util.OperateDate;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;
    private EditText editUsername;
    private EditText editPassword;
    private CheckBox checkRemPassword;
    private boolean isRemember = false;
    private SharedPreferences mShared;
    private static final String URL_LOGIN = "https://redbook.bazinga.team/login";
    //http://106.52.165.70:8080/login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化控件
        init();

        // 记住密码
        checkRemPassword.setOnCheckedChangeListener(new CheckListener());
        mShared = getSharedPreferences("share_login", MODE_PRIVATE);
        String userName = mShared.getString("userName", "");
        String password = mShared.getString("password", "");
        boolean bRemember = mShared.getBoolean("isRemember", false);
        editUsername.setText(userName);
        editPassword.setText(password);
        checkRemPassword.setChecked(bRemember);

        // 跳转到注册界面
        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            String[] data;
            String inputUsername = editUsername.getText().toString();
            String inputPassword = editPassword.getText().toString();

            if (TextUtils.isEmpty(inputUsername)) {
                Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(inputPassword)) {
                Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            } else {
                data = new String[] {inputUsername, inputPassword};
                @SuppressLint("HandlerLeak")
                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        switch (msg.what) {
                            case 0:
                                Toast.makeText(LoginActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                // 提交上次记录的数据
                                if (isRemember) {
                                    SharedPreferences.Editor editor = mShared.edit();
                                    editor.putString("userName", editUsername.getText().toString());
                                    editor.putString("password", editPassword.getText().toString());
                                    editor.putBoolean("isRemember", checkRemPassword.isChecked());
                                    editor.apply();
                                }

                                LoginActivity.this.finish();
                                break;

                            case 2:
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                break;

                            case 3:
                                Log.e("input error", "url为空");
                                break;

                            case 4:
                                Toast.makeText(LoginActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                        }
                    }
                };

                OperateDate operateDate = new OperateDate();
                String jsonString = operateDate.stringToJson(data);
                URL url = null;
                try {
                    url = new URL(URL_LOGIN);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                operateDate.setDate(jsonString, handler, url, this);
            }
        });
    }

    // 定义是否记住密码的勾选监听器
    private class CheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.remember_password) {
                isRemember = isChecked;
            }
        }
    }

    private void init() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        editUsername = findViewById(R.id.login_edit_username);
        editPassword = findViewById(R.id.login_edit_password);
        checkRemPassword = findViewById(R.id.remember_password);
    }
}