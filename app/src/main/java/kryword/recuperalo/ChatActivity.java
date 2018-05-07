package kryword.recuperalo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import kryword.recuperalo.Modelos.Message;

public class ChatActivity extends AppCompatActivity {
    private MessageAdapter adapter;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
        String senderName = bundle.getString("senderName");
        String receiverName = bundle.getString("receiverName");
        String topic = bundle.getString("topic");
        TextView chatTitle = findViewById(R.id.chatTitle);
        chatTitle.setText("Chat entre " + senderName + " y " + receiverName + " sobre " + topic);

        final ListView chatList = findViewById(R.id.messageList);
        adapter = new MessageAdapter(this);
        chatList.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child("messages").child(this.id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    adapter.add(message);
                    adapter.notifyDataSetChanged();
                    chatList.setSelection(adapter.getCount() - 1);
                }
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

    public void sendMessage(View view){
        MultiAutoCompleteTextView editText = findViewById(R.id.sendText);
        if (!TextUtils.isEmpty(editText.getText())) {
            Message message = new Message(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), this.id, editText.getText().toString(), ServerValue.TIMESTAMP);
            FirebaseDatabase.getInstance().getReference().child("messages").child(this.id).push().setValue(message);
            editText.setText("");
        }
    }
}
