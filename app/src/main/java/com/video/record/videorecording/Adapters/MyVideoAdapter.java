package com.video.record.videorecording.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.video.record.videorecording.HomeActivity;
import com.video.record.videorecording.Model.VideoPath;
import com.video.record.videorecording.R;

import java.io.File;
import java.util.ArrayList;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.ImageViewHolder> {

    Context context;
    ArrayList<VideoPath> videoPathArrayList = new ArrayList<>();

    public MyVideoAdapter(Context context, ArrayList<VideoPath> videoPathArrayList){
        this.context=context;
        this.videoPathArrayList=videoPathArrayList;
    }

    @NonNull
    @Override
    public MyVideoAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_video_adapter,parent,false);
        return new MyVideoAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoAdapter.ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txt_video_name.setText(videoPathArrayList.get(position).getPath()
                .substring(videoPathArrayList.get(position).getPath().lastIndexOf("/")+1));

        File file = new File(videoPathArrayList.get(position).getPath());
        long length = file.length();
        long kb = length/1024;
        long mb = kb/1024;

        holder.txt_video_size.setText(String.valueOf(mb)+"MB");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri filedata = FileProvider.getUriForFile(context,
                        "com.video.record.videorecording.fileprovider",
                        new File(videoPathArrayList.get(position).getPath()));
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra(Intent.EXTRA_STREAM, filedata);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"Musical Video");
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("video/*");
                context.startActivity(sendIntent);
            }
        });

        holder.img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri filedata = FileProvider.getUriForFile(context,
                        "com.video.record.videorecording.fileprovider",
                        new File(videoPathArrayList.get(position).getPath()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(filedata, "video/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        });

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File fdelete = new File(videoPathArrayList.get(position).getPath());
                if (fdelete.exists()) {
                    fdelete.delete();
                    Toast.makeText(context, "deleted!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoPathArrayList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        TextView txt_video_name,txt_video_size;
        ImageView img_delete,img_view;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_video_name = itemView.findViewById(R.id.txt_video_name);
            txt_video_size = itemView.findViewById(R.id.txt_video_size);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_view = itemView.findViewById(R.id.img_view);

        }
    }
}
