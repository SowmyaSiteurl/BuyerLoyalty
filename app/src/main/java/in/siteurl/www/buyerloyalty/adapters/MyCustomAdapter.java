package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.content.Intent;
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
import in.siteurl.www.buyerloyalty.activities.CalltoVendor;
import in.siteurl.www.buyerloyalty.core.Vendors;

/**
 * Created by siteurl on 26/12/17.
 */

public class MyCustomAdapter extends ArrayAdapter<Vendors> {

    private ArrayList<Vendors> originalList;
    private ArrayList<Vendors> countryList;
    private CountryFilter filter; //filter data from an entire data set based on some pattern(a String )
    ArrayList<String> phonenumberlist = new ArrayList<>();
    ArrayList<String> namelist = new ArrayList<>();
    ArrayList<String> emailidlist = new ArrayList<>();
    ArrayList<String> enquiryidlist = new ArrayList<>();


    String phonenumber, name, emailid, enquiryid;
    int positionoflist;
    String str_phn;

    public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Vendors> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<Vendors>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<Vendors>();
        this.originalList.addAll(countryList);
    }

    //filter for search
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }


    private class ViewHolder {
        TextView vndr_name;
        TextView vndr_email;
        TextView vnder_address;
        TextView vndr_id;
        ImageView nextpage, callvnder, vnderlogo;

    }

    //get UI elements and Set text
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        final Vendors currentenquiry = countryList.get(position);
        if (convertView == null) {
            positionoflist = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.country_info, parent, false);
            phonenumber = currentenquiry.getPhone();
            phonenumberlist.add(phonenumber);

            emailid = currentenquiry.getEmail();
            emailidlist.add(emailid);

            name = currentenquiry.getName();
            namelist.add(name);

            enquiryid = currentenquiry.getVnderuser_id();
            enquiryidlist.add(enquiryid);

            holder = new ViewHolder();
            holder.vndr_name = (TextView) convertView.findViewById(R.id.content_name_view);
            holder.vndr_email = (TextView) convertView.findViewById(R.id.textView);
            holder.vnder_address = (TextView) convertView.findViewById(R.id.textView1);
            holder.vndr_id = (TextView) convertView.findViewById(R.id.content_avatar_title1);
            holder.callvnder = (ImageView) convertView.findViewById(R.id.overflow1);

            holder.callvnder.setOnClickListener(new View.OnClickListener() {
                String getnumber = String.valueOf(currentenquiry.getPhone());

                @Override
                public void onClick(View v) {

                    //callToVendor();
                    Intent intent = new Intent(getContext(), CalltoVendor.class);
                    intent.putExtra("phonenumber", getnumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            // holder.nextpage=(ImageView)convertView.findViewById(R.id.overflow);
            holder.vnderlogo = (ImageView) convertView.findViewById(R.id.vndr_logoview);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //setting text in view holder text view from an arraylist
        String vendorname = currentenquiry.getName();
        holder.vndr_name.setText("Name: " + vendorname.substring(0, 1).toUpperCase() + vendorname.substring(1));
        holder.vndr_email.setText("Email: " + currentenquiry.getEmail());
        holder.vnder_address.setText("Address: " + currentenquiry.getAddress());
        holder.vndr_id.setText("Phone: " + currentenquiry.getPhone());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions .placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        //It is a image loading framework
        Glide.with(getContext()).load(currentenquiry.getStore_image())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.vnderlogo);
        convertView.setTag(holder);
        return convertView;

    }

    private void callToVendor() {
        if (!str_phn.isEmpty()) {
            Intent intent = new Intent(getContext(), CalltoVendor.class);
            intent.putExtra("phonenumber", str_phn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    // filter the results according to the constraint
    private class CountryFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Vendors> filteredItems = new ArrayList<Vendors>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Vendors country = originalList.get(i);
                    if (country.toString().toLowerCase().contains(constraint))
                        filteredItems.add(country);
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
            countryList = (ArrayList<Vendors>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }

}
