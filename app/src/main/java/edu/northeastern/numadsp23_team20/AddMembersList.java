package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddMembersList extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private AddMemberAdapter memberAdapter;
    private static ArrayList<User> memberList;
    private String currentuser;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private final String TAG="AddMembersList";

    static User dataStore;
    static FirebaseUser firebaseUser;
    private static List<String> friendsUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_list);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        recyclerView = findViewById(R.id.members_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        memberList = new ArrayList<>();
        memberAdapter = new AddMemberAdapter(memberList, getApplicationContext());
        recyclerView.setAdapter(memberAdapter);

        //User user1 = new User("Rutu");
        //User user2 = new User("Rahul");
        //memberList.add(user1);
        //memberList.add(user2);

        //fetch friends user IDs.
        friendsUI = new ArrayList<String>();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference userFriendsRef = FirebaseDatabase.getInstance().getReference(
                "GeoNotif/Users/" + userId + "/Friends");
        userFriendsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userFriends) {
                if (!userFriends.isSuccessful()) {
                    Log.d("firebase", "Error getting data", userFriends.getException());
                } else {
                    for (DataSnapshot childSnapshot : userFriends.getResult().getChildren()) {
                        //get the user IFD
                        String userID = childSnapshot.getValue(String.class);
                        friendsUI.add(userID);
                        Log.d("friends", String.valueOf(friendsUI.size()));
                    }

                }
            }
        });



        //fetch details of the userIDs in friends array
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("GeoNotif/Users/");

        usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> userFriends) {
                if (!userFriends.isSuccessful()) {
                    Log.d("firebase", "Error getting data", userFriends.getException());
                } else {
                    for (DataSnapshot childSnapshot : userFriends.getResult().getChildren()) {
                        String uid = childSnapshot.child("uid").getValue(String.class);

                        for (int i = 0; i < friendsUI.size(); i++) {
                            if (uid.equals(friendsUI.get(i))) {
                                String emailID = childSnapshot.child("emailId").getValue(String.class);
                                String fullname = childSnapshot.child("fullname").getValue(String.class);
                                String username = childSnapshot.child("username").getValue(String.class);
                                dataStore = new User(fullname, username, emailID, uid);
                                memberList.add(dataStore);
                                Log.d("memberList", String.valueOf(memberList.size()));
                                break;
                            }
                        }
                    }
                    memberAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

                }
            }
        });

        /*
        User user1 = new User("Rutu");
        User user2 = new User("Rahul");
        User user3 = new User("Srijha");
        User user4 = new User("Eshwar");
        memberList.add(user1);
        memberList.add(user2);
        memberList.add(user3);
        memberList.add(user4);
         */


    }

    public void filterList(String text){
        ArrayList<User> filteredList = new ArrayList<>();
        for(User user: memberList){
            if(user.getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                filteredList.add(user);
            }
        }

        if (filteredList.isEmpty()){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }else{
            memberAdapter.setFilteredList(filteredList);
        }
    }

}