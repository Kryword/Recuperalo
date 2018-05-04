package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import kryword.recuperalo.Modelos.Chat;

public class ChatListActivity extends AppCompatActivity {
    Query reference1, reference2;
    ChildEventListener listener1, listener2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        TextView title = findViewById(R.id.chatListTitle);
        title.setText("Lista de chats de " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        final ArrayAdapter<Chat> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_chat, R.id.chatName);
        ListView lw = findViewById(R.id.chatList);
        lw.setAdapter(adapter);
        final String uid = FirebaseAuth.getInstance().getUid();
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Chat chat = adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", chat.getId());
                bundle.putString("senderName", chat.getSenderName());
                bundle.putString("receiverName", chat.getReceiverName());
                bundle.putString("topic", chat.getTopic());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        reference1 = FirebaseDatabase.getInstance().getReference().child("chats").orderByChild("receiver").equalTo(uid);
        reference2 = FirebaseDatabase.getInstance().getReference().child("chats").orderByChild("sender").equalTo(uid);
        listener1 = reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("AÑADIDO", "onChildAdded: Se añadió" + dataSnapshot.getValue());
                adapter.add(dataSnapshot.getValue(Chat.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listener2 = reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("AÑADIDO", "onChildAdded: Se añadió" + dataSnapshot.getValue());
                adapter.add(dataSnapshot.getValue(Chat.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference1.removeEventListener(listener1);
        reference2.removeEventListener(listener2);
    }
}
