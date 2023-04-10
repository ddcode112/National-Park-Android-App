package ds.edu.cmu.nationalparkapp;
/**
 * @author Candice Chiang
 * Andrew id: wantienc
 * Last Modified: Apr 6, 2023
 *
 * This adapter class controlls the contents of ListView.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
// Refer to https://youtu.be/zS8jYzLKirM
public class ParkInfoAdapter extends ArrayAdapter<ParkInfo> {
    private Context mContext;
    private int mResource;
    public ParkInfoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ParkInfo> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.parkImage);
        TextView parkName = convertView.findViewById(R.id.fullName);
        TextView u = convertView.findViewById(R.id.url);
        TextView fee = convertView.findViewById(R.id.entranceFee);
        TextView des = convertView.findViewById(R.id.description);
        imageView.setImageBitmap(getItem(position).getImg());
        imageView.setVisibility(View.VISIBLE);
        parkName.setText(getItem(position).getFullName());
        fee.setText("Entrance Fees: " + getItem(position).getMin() + " - " + getItem(position).getMax());
        u.setText("Link: " + getItem(position).getUrl());
        des.setText(getItem(position).getDescription());
        return convertView;
    }
}
