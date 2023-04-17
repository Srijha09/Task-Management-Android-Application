package edu.northeastern.numadsp23_team20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private final ArrayList<Group> groupsList;
    private ItemClickListener listener;
    private final Context context;
    //String currentGroup;

    //Creating the constructor
    public GroupsAdapter(ArrayList<Group> groupsList,Context context) {
        this.groupsList = groupsList;
        this.context = context;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groups_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupsList.get(position);
        holder.groupname.setText(group.getGroupName());
        if(group.getGroupParticipantsNo()!=null) {
            holder.participants_no.setText(String.valueOf(group.getGroupParticipantsNo()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupsList.size()<2){
                    Intent intent = new Intent(context, StickItToEm.class);
                    intent.putExtra("groupName", group.getGroupName());
                    //intent.putExtra("loggedInUsername", GroupsAdapter.this.currentUser);
                    context.startActivity(intent);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("groupName", group.getGroupName());
                    GroupTasksFragment grouptasksFragment = new GroupTasksFragment();
                    grouptasksFragment.setArguments(bundle);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.FrameLayout, grouptasksFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupname;
        public TextView participants_no;
        public ViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);

            groupname = itemView.findViewById(R.id.groupName);
            participants_no = itemView.findViewById(R.id.no_of_participants);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getLayoutPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
