package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.Locale;

public class StickItToEm extends AppCompatActivity implements SelectStickerDialog.SelectStickerListener {

    SelectStickerDialog selectStickerDialog;
    ScrollView scrollableChatContainer;
    LinearLayout linearChatLayout;
    List<Message> history;
    private FirebaseDatabase mDatabase;

    Intent intent;
    TextView chosenUsername;
    String chosenUser = "user1";


    String username = "user3";
    private String stickerName;
    private String timeStamp;
    private String sentBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_stick_it_to_em);

        this.linearChatLayout = findViewById(R.id.LinearChatLayout);
        this.scrollableChatContainer = findViewById(R.id.ScrollableChatContainer);
        this.history = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();

        mDatabase.getReference().child("Users/" + username + "/messages/" + chosenUser).addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext()
                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        mDatabase.getReference().child("Users/" + username + "/messages/" + chosenUser).addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Message values = dataSnapshot.getValue(Message.class); //get values inside  nest
                        stickerName = values.getStickerName();
                        timeStamp = values.getTimestamp();
                        sentBy = values.getUserId();

                        if (!sentBy.equals(username)) {
                            sendNotification();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                }
        );
    }

    public void onSendStickerButtonClick(View view) {
        this.selectStickerDialog = new SelectStickerDialog();
        this.selectStickerDialog.show(getSupportFragmentManager(), "SelectStickerDialog");
    }

    @Override
    public void applyInformation(String selectedStickerName, int selectedStickerId) {
        this.selectStickerDialog.dismiss();
        this.onSendSticker(mDatabase.getReference(), selectedStickerName);
    }

    private void onSendSticker(DatabaseReference postRef, String stickerName) {
        postRef
                .child("Users")
                .child(this.username)
                .child("messages").child(this.chosenUser).push().setValue(new Message(StickItToEm.this.username, stickerName, getTime()));
        postRef
                .child("Users")
                .child(this.chosenUser)
                .child("messages").child(this.username).push().setValue(new Message(StickItToEm.this.username, stickerName, getTime()));
        postRef
                .child("Users")
                .child(this.username)
                .child("stickerCount")
                .child(stickerName).setValue(ServerValue.increment(1));
    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        String time = String.format("%02d", Integer.valueOf(hour12hrs))
                .concat(":")
                .concat(String.format("%02d", Integer.valueOf(minutes)));
        if (calendar.get(Calendar.AM_PM) == Calendar.AM)
            time += " AM";
        else {
            time += " PM";
        }
        return time;
    }

    private void addToChatWindow(Message message) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setPadding(10, 10, 10, 10);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(this.getDrawable(message.getStickerName()));
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                300, 300
        );
        imageView.setPadding(20, 20, 20, 20);
        imageView.setLayoutParams(imageViewParams);
        linearLayout.addView(imageView);
        TextView textView = new TextView(this);
        textView.setPadding(20, 0, 20, 0);
        textView.setText(getTime());
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(10);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                300, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(textViewParams);
        linearLayout.addView(textView);
        if (message.getUserId().equalsIgnoreCase(username)) {
            linearLayout.setGravity(Gravity.END);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            linearLayout.setGravity(Gravity.START);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        linearLayout.setLayoutParams(linearLayoutParams);
        this.linearChatLayout.addView(linearLayout);
        this.scrollableChatContainer.post(() -> scrollableChatContainer.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private int getDrawable(String sticker) {
        switch (sticker) {
            case "pikachu":
                return R.drawable.pikachu;
            case "mewtwo":
                return R.drawable.mewtwo;
            case "charmander":
                return R.drawable.charmander;
            case "squirtle":
                return R.drawable.squirtle;
            case "groudon":
                return R.drawable.groudon;
            case "jolteon":
                return R.drawable.jolteon;
        }
        return 0;
    }

    private void showMessage(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);

        if (dataSnapshot.getKey() != null) {
            this.addToChatWindow(message);
            System.out.println("Hooray");
        }

    }


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void sendNotification() {
        int id = getResources().getIdentifier("edu.northeastern.numadsp23_team20:drawable/" + stickerName, null, null);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), id);

        String channelId = getString(R.string.channel_id);
        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("New sticker from " + sentBy + " at: " + timeStamp )
                .setContentText("You received a new " + stickerName + " sticker!")
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                .setSmallIcon(R.drawable.placeholder_image)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int idUnique = createID();
        notificationManager.notify(idUnique, notifyBuild.build());
    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}