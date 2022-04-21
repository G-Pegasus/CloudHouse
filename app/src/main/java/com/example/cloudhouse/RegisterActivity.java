package com.example.cloudhouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import util.CodeUtils;
import util.OperateDate;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText editUsername;
    private EditText editPhoneNum;
    private EditText editPassword;
    private EditText editPasswordAgain;
    private EditText editCode;
    private String[] str = null;
    private static final String URL_Register = "http://106.52.165.70:8080/register";
    private Bitmap bitmap;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        //获取需要展示图片验证码的ImageView
        final ImageView image = findViewById(R.id.verification_code_image);
        //获取工具类生成的图片验证码对象
        bitmap = CodeUtils.getInstance().createBitmap();
        //获取当前图片验证码的对应内容用于校验
        code = CodeUtils.getInstance().getCode();

        image.setImageBitmap(bitmap);
        image.setOnClickListener(view -> {
            bitmap = CodeUtils.getInstance().createBitmap();
            code = CodeUtils.getInstance().getCode();
            image.setImageBitmap(bitmap);
        });

        btnRegister.setOnClickListener(view -> {
            String inputUsername = editUsername.getText().toString();
            String inputPhoneNum = editPhoneNum.getText().toString();
            String inputPassword = editPassword.getText().toString();
            String inputPasswordAgain = editPasswordAgain.getText().toString();
            String inputCode = editCode.getText().toString();

            if (TextUtils.isEmpty(inputUsername)) {
                Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(inputPhoneNum)) {
                Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(inputPassword)) {
                Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(inputPasswordAgain)) {
                Toast.makeText(RegisterActivity.this, "请确认密码", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(inputCode)) {
                Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            } else if (!inputPassword.equals(inputPasswordAgain)) {
                Toast.makeText(RegisterActivity.this, "两次密码不同，请验证", Toast.LENGTH_SHORT).show();
            } else if (!inputCode.equals(code)) {
                Toast.makeText(RegisterActivity.this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                str = new String[] {inputUsername, inputPassword, inputPhoneNum};

                Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);

                        switch (msg.what) {
                            case 0:
                                Toast.makeText(RegisterActivity.this, "用户存在", Toast.LENGTH_SHORT).show();
                                break;

                            case 1:
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                RegisterActivity.this.finish();
                                break;

                            case 2:
                                Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                                break;

                            case 3:
                                Log.e("Input error", "url为空");
                                break;

                            case 4:
                                Toast.makeText(RegisterActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                        }
                    }
                };

                OperateDate operateDate = new OperateDate();
                String jsonString = operateDate.stringToJson(str);
                URL url = null;
                try {
                    url = new URL(URL_Register);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                operateDate.setDate(jsonString, handler, url, this);
            }
        });
    }

    private void init() {
        btnRegister = findViewById(R.id.btn_register);
        editUsername = findViewById(R.id.user_name_edit);
        editPhoneNum = findViewById(R.id.phone_num_edit);
        editPassword = findViewById(R.id.password_edit);
        editPasswordAgain = findViewById(R.id.password_edit_again);
        editCode = findViewById(R.id.code_edit);
    }
}