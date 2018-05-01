package kryword.recuperalo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import kryword.recuperalo.Modelos.ObjetoEncontrado;

public class MyAdapter extends BaseAdapter {
    Context context;
    List<ObjetoEncontrado> data;
    private static LayoutInflater inflater = null;

    public MyAdapter(Context context, List<ObjetoEncontrado> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null){
            vi = inflater.inflate(R.layout.item_objeto, null);
        }
        TextView title = (TextView) vi.findViewById(R.id.title);
        TextView description = (TextView) vi.findViewById(R.id.description);
        TextView user = (TextView) vi.findViewById(R.id.user);
        ObjetoEncontrado element = data.get(position);
        title.setText(element.getTitle() + ":");
        description.setText(element.getDescription());
        user.setText(element.getName());
        return vi;
    }
    public void remove(ObjetoEncontrado object){
        data.remove(object);
    }
}
