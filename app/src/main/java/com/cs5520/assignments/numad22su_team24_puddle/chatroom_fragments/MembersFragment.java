package com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.cs5520.assignments.numad22su_team24_puddle.Utils.FirebaseDB;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.Member;
import com.cs5520.assignments.numad22su_team24_puddle.chatroom_fragments.adapters.MembersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersFragment extends Fragment {
    private DatabaseReference membersRef;
    private String puddleID;
    private Handler handler = new Handler();
    private RecyclerView recyclerView;
    private MembersAdapter membersAdapter;
    private Context context;

    public MembersFragment(String puddleID){
        this.puddleID = puddleID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.members_fragment,container,false);
        membersRef = FirebaseDB.getDataReference("Members").child(puddleID);
        recyclerView = view.findViewById(R.id.members_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.hasFixedSize();
        context = getContext();
        initializeRecyclerView();
        return view;
    }

    private void initializeRecyclerView(){
        class getMembersRunnable implements Runnable{
            @Override
            public void run() {
                membersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Member> memberList = new ArrayList<>();
                        for (DataSnapshot snap: snapshot.getChildren()) {
                            String username = snap.child("username").getValue(String.class);
                            String profile_url = snap.child("profile_url").getValue(String.class);
//                            Log.d("here",profile_url);
                            memberList.add(new Member(username,profile_url));
                        }

                        handler.post(() -> {
                            membersAdapter = new MembersAdapter(memberList,context);
                            recyclerView.setAdapter(membersAdapter);
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        Thread worker = new Thread(new getMembersRunnable());
        worker.start();
    }
}