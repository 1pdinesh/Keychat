package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<SendReceiveMessage> mChat;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference reference;
    //private List<Upload>imageList;

    FirebaseUser fuser;


    public MessageAdapter(Context mContext, List<SendReceiveMessage> mChat) //List<Upload>imageList)
    {
        this.mChat = mChat;
        this.mContext = mContext;
        //this.imageList = imageList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final SendReceiveMessage chat = mChat.get(position);
        final String type = mChat.get(position).getType();
        String message = mChat.get(position).getMessage();

        if(type.equals("text"))
        {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            holder.show_message.setText(message);
            holder.play.setVisibility(View.GONE);
            holder.mapz.setVisibility(View.GONE);

        }
        else if(type.equals("voice"))

        {

            holder.play.setVisibility(View.VISIBLE);
            holder.messageIv.setImageDrawable(null);
            holder.messageIv.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);
            holder.mapz.setVisibility(View.GONE);

            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final StorageReference audioStorage = FirebaseStorage.getInstance().getReference().child(chat.getContentLocation());

                    audioStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            playSound(uri);

                        }
                    });
                }
            });

        }
        else if(type.equals("Location"))

        {
            holder.mapz.setVisibility(View.VISIBLE);
            holder.play.setVisibility(View.GONE);
            holder.messageIv.setImageDrawable(null);
            holder.messageIv.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);

            holder.mapz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri i = Uri.parse(String.valueOf("geo:" +chat.getLo()+ chat.getLa()));
                    Intent map = new Intent(Intent.ACTION_VIEW, i);

                    mContext.startActivity(map);
                }
            });

        }

        else
        {
            holder.play.setVisibility(View.GONE);
            holder.mapz.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);
            holder.messageIv.setVisibility(View.VISIBLE);
            Picasso.get().load(message).placeholder(R.drawable.ic_image_black).into(holder.messageIv);
        }

    }

    private void playSound(Uri uri){

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(uri.toString());
        }catch(Exception e){

        }
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView show_message;
        public ImageButton play,mapz;


        public ImageView messageIv;

        public ViewHolder(View itemView)
        {
            super(itemView);

            play=itemView.findViewById(R.id.voiceMessageButton);
            show_message = itemView.findViewById(R.id.show_message);
              mapz=itemView.findViewById(R.id.map);
            messageIv = itemView.findViewById(R.id.show_Image);
        }
    }
    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}

