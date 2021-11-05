package ync.ysc3232.pictionary_sabotage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/** Room Main Menu where player can choose to either create a new room or join
 * an existing room.
 * When user creates a new room (createRoom), a new instance of RoomData is created that includes the
 * current user. The following data is pushed into the database.
 * When a user joins an existing room (joinRoom), they will have the enter the room ID (eneteredRoomId)
 * before joining.
 *
 * Room.java leads to WaitingRoom.java
 */

public class Room extends AppCompatActivity {

    private Button createRoom;
    private Button joinRoom;
    private EditText eneteredRoomId;
    private RoomData roomData;

    DatabaseReference room_database = FirebaseDatabase.getInstance("https://pictionary-sabotage-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference().child("Rooms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        createRoom = findViewById(R.id.createRoom);
        joinRoom = findViewById(R.id.joinRoom);
        eneteredRoomId = findViewById(R.id.enterRoomId);

        //At createRoom, immediately create a new room with a new code and go to Waiting Room
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a random room number
                String roomId = String.valueOf((int)(Math.random() * 10000));

                //TODO: If a room id is taken, we cannot use it again
                DatabaseReference newRoom = room_database.child(roomId);
                roomData = new RoomData(roomId);
                roomData.addPlayer(getCurrentUser(), "Undecided");
                newRoom.setValue(roomData);

                //Passing room Id using intent
                //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application#:~:text=The%20easiest%20way%20to%20do,sessionId)%3B%20startActivity(intent)%3B
                Intent intent = new Intent(Room.this, WaitingRoom.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });

        joinRoom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String roomId = eneteredRoomId.getText().toString().trim();

                //Make sure room number is entered
                if (roomId.isEmpty()){
                    eneteredRoomId.setError("Room number is empty");
                    return;
                }

                //Fetch current room data
                room_database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        roomData = snapshot.child(roomId).getValue(RoomData.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("firebase", "Error getting existing room.");
                        Toast.makeText(Room.this, "Invalid Room Number", Toast.LENGTH_SHORT).show();
                    }
                });

                if (roomData == null) {
                    Toast.makeText(Room.this, "Invalid Room Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                roomData.addPlayer(getCurrentUser(), "Undecided");
                room_database.child(roomId).setValue(roomData);

                Intent intent = new Intent(Room.this, WaitingRoom.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }

        });

        ImageView img = (ImageView)findViewById(R.id.backg);
        img.setBackgroundResource(R.drawable.bg_animation);
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
        frameAnimation.start();
    }

    public String getCurrentUser(){
        //Get current user
        //Remove the email @
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String cUsrEmail = mAuth.getCurrentUser().getEmail();
        int userAt = cUsrEmail.lastIndexOf("@");
        String userId = cUsrEmail.substring(0, userAt);
        return userId;
    }
}