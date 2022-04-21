package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudhouse.R;

import java.util.ArrayList;
import java.util.List;

import util.CustomEditTextDialog;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private OnItemClickListener onItemClickListener;
    private final Context mContext;
    private List<String> dataSource;
    private List<Integer> imageSource;

    public MainAdapter(Context context) {
        this.mContext = context;
        this.dataSource = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataSource(List<String> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setImageSource(List<Integer> imageSource) {
        this.imageSource = imageSource;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_layout_main, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mIv.setImageResource(imageSource.get(position));
        holder.mTv.setText(dataSource.get(position));

        // 如果 arg == 1 则添加新的家具， 否则跳转到家具细节页面
        holder.mIv.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                if (position == dataSource.size() - 1)
                    onItemClickListener.onItemClick(position, 1, dataSource.size(), dataSource);
                else
                    onItemClickListener.onItemClick(position, 2, dataSource.size(), null);
            }
        });

        holder.mTv.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, 3, dataSource.size(), dataSource);
                notifyItemChanged(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showDialog(Context context, List<String> list, int position) {
        final CustomEditTextDialog customDialog = new CustomEditTextDialog(context);
        final EditText editText = (EditText) customDialog.getEditText();

        customDialog.setOnSureListener(view -> {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                Toast.makeText(context, "内容不能为空哦", Toast.LENGTH_SHORT).show();
            } else {
                list.set(position, editText.getText().toString());
                customDialog.dismiss();
                notifyDataSetChanged();
                notifyItemChanged(position);
            }
        });
        customDialog.setOnCancelListener((View.OnClickListener) view -> customDialog.dismiss());

        customDialog.setTile("请输入家具名称");
        customDialog.show();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton mIv;
        TextView mTv;
        View mItemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIv = itemView.findViewById(R.id.iv);
            mTv = itemView.findViewById(R.id.tv);
            mItemView = itemView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, int arg, int size, List<String> dataSource);
    }
}


