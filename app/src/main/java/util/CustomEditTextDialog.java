package util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cloudhouse.R;

public class CustomEditTextDialog extends Dialog {
    Context mContext;
    private Button btnSure;
    private Button btnCancel;
    private TextView title;
    private EditText editText;

    public CustomEditTextDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        this.mContext = context;
        initView();
    }

    //初始化
    public void initView() {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(mContext).inflate(R.layout.update_dialog, null);
        title = view.findViewById(R.id.title);
        editText = view.findViewById(R.id.edittext);
        btnSure = view.findViewById(R.id.dialog_confirm_sure);
        btnCancel = view.findViewById(R.id.dialog_confirm_cancle);
        super.setContentView(view);
    }

    public void setTile(String s) {
        title.setText(s);
    }

    //获取当前输入框对象
    public View getEditText() {
        return editText;
    }

    //确定键监听器
    public void setOnSureListener(View.OnClickListener listener) {
        btnSure.setOnClickListener(listener);
    }

    //取消键监听器
    public void setOnCancelListener(View.OnClickListener listener) {
        btnCancel.setOnClickListener(listener);
    }
}

