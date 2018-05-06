package kryword.recuperalo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import kryword.recuperalo.Modelos.Message;

public class MessageAdapter extends BaseAdapter {

    private List<Message> data;
    private LayoutInflater inflater;
    private Activity a;

    public MessageAdapter(Activity a){
        this.a = a;
        this.data = new ArrayList<>();
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public MessageAdapter(Activity a, List<Message> data) {
        this.a = a;
        this.data = data;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null){
            vi = inflater.inflate(R.layout.item_message, null);
        }

        TextView senderName = vi.findViewById(R.id.senderName);
        TextView messageText = vi.findViewById(R.id.message);
        Message message = data.get(i);
        senderName.setText(message.getSender());
        messageText.setText(message.getMessage());

        // Esto se encarga de cambiar el color de los mensajes dependiendo de quién lo está enviando
        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(message.getSender())){
            vi.setBackgroundColor(a.getResources().getColor(R.color.colorPrimaryDark, null));
        }else{
            vi.setBackgroundColor(a.getResources().getColor(R.color.colorPrimary, null));
        }
        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }
    public void add(Message message){
        data.add(message);
    }
}
