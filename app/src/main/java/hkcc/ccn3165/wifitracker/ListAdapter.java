package hkcc.ccn3165.wifitracker;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<ScanResult> wifiList;


    public ListAdapter(Context context, List<ScanResult> wifiList) {
        this.context = context;
        this.wifiList = wifiList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;
        System.out.println("viewpos" + position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.dataset, null);
            holder = new Holder();
            holder.ltwifiDetails = (TextView) view.findViewById(R.id.ltwifiDetails);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.ltwifiDetails.setText("SSID :: " + wifiList.get(position).SSID
                + "\nBSSID :: " + wifiList.get(position).BSSID
               );
        return view;
    }

    class Holder {
        TextView ltwifiDetails;

    }
}
