package id.backbonedev.smartprint.App;

import android.app.Activity;
import android.content.Context;
import android.icu.text.UFormat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import id.backbonedev.smartprint.Data.ItemTentang;
import id.backbonedev.smartprint.R;

/**
 * Created by Aziz Nur Ariffianto on 0009, 09 Jan 2018.
 */

public class TentangItemAdapter extends BaseAdapter
{
    private String TAG = "#TentangItemAdapter#";
    private Activity activity;
    private LayoutInflater inflater;
    private List<ItemTentang> itemTentangs;
    private static Context cc;

    public TentangItemAdapter(Activity activity, List<ItemTentang> itemTentangs)
    {
        this.activity = activity;
        this.itemTentangs = itemTentangs;
    }

    @Override
    public int getCount()
    {
        return itemTentangs.size();
    }

    @Override
    public Object getItem(int i)
    {
        return itemTentangs.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        ItemTentang item = itemTentangs.get(i);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.item_tentang, null);

        TextView LTeks = view.findViewById(R.id.LTeks);
        TextView LSubTeks = view.findViewById(R.id.LSubTeks);
        ImageView IVIcon = view.findViewById(R.id.IVIcon);

        if (TextUtils.isEmpty(item.getSubteks()))
            LSubTeks.setVisibility(View.GONE);

        LTeks.setText(item.getTeks());
        LSubTeks.setText(item.getSubteks());
        IVIcon.setImageResource(item.getIcon());

        return view;
    }
}
