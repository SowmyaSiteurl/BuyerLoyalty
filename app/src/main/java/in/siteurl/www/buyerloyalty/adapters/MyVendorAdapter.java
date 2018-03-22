package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.core.Venders_Product;

/**
 * Created by siteurl on 26/12/17.
 */

public class MyVendorAdapter extends ArrayAdapter<Venders_Product> {

    private ArrayList<Venders_Product> originalList;
    private ArrayList<Venders_Product> countryList;
    private Venders_ProductFilter filter; //filter data from an entire data set based on some pattern(a String )
    int positionoflist;


    public MyVendorAdapter(Context context, int textViewResourceId, ArrayList<Venders_Product> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<Venders_Product>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<Venders_Product>();
        this.originalList.addAll(countryList);
    }

    //fliter for search
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Venders_ProductFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView pro_name;
        TextView pro_price;
        TextView pro_exp;
        TextView pro_terms,
                pro_desc, pro_status;
        ImageView imageView, gps_showi, shop_nowi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        final Venders_Product currentenquiry = countryList.get(position);
        if (convertView == null) {
            positionoflist = position;

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.allvendorproduct_list, parent, false);
            holder = new ViewHolder();
            holder.pro_name = (TextView) convertView.findViewById(R.id.head_image_left_text);
            holder.pro_price = (TextView) convertView.findViewById(R.id.head_image_center_text);
            holder.pro_exp = (TextView) convertView.findViewById(R.id.head_image_right_text);
            holder.pro_terms = (TextView) convertView.findViewById(R.id.termsandconditionTV);
            holder.pro_desc = (TextView) convertView.findViewById(R.id.descriptionTV);
            holder.pro_status = (TextView) convertView.findViewById(R.id.statusTV);
            holder.imageView = (ImageView) convertView.findViewById(R.id.head_image);
            holder.shop_nowi = (ImageView) convertView.findViewById(R.id.shop_now);
            holder.gps_showi = (ImageView) convertView.findViewById(R.id.gps_show);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //setting text in view holder text view from an arraylist
        String vendorname = currentenquiry.getOffer_name();
        holder.pro_name.setText(vendorname);
        holder.pro_price.setText("₹ " + currentenquiry.getOffer_price());
        holder.pro_exp.setText(currentenquiry.getExpiry_date());
        holder.pro_terms.setText(currentenquiry.getTerms_and_condtion());
        holder.pro_desc.setText(currentenquiry.getOffer_description());
        holder.pro_status.setText("status: " + currentenquiry.getStatus());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions .placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        //It is a image loading framework
        Glide.with(getContext()).load(currentenquiry.getOffer_image())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.imageView);
        return convertView;
    }

    // filter the results according to the constraint
    private class Venders_ProductFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Venders_Product> filteredItems = new ArrayList<Venders_Product>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Venders_Product mycountry = originalList.get(i);
                    if (mycountry.toString().toLowerCase().contains(constraint))
                        filteredItems.add(mycountry);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }


        // show the result set created by performingFiltering method
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countryList = (ArrayList<Venders_Product>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }
}

