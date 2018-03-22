package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.activities.CalltoVendor;
import in.siteurl.www.buyerloyalty.activities.IndivisualVendorDataWithOffers;
import in.siteurl.www.buyerloyalty.core.Vendors;

/**
 * Created by siteurl on 1/2/18.
 */

public class MyCustomAdapter1 extends RecyclerView.Adapter<MyCustomAdapter1.ViewHolder>
        implements Filterable {

    private ArrayList<Vendors> vendorProdarrayalllist;
    private ArrayList<Vendors> vendorProdarrayalllist1;
    private Context mcontext;


    public MyCustomAdapter1(Context context, ArrayList<Vendors> countryList) {
        this.vendorProdarrayalllist = countryList;
        this.mcontext = context;
        this.vendorProdarrayalllist1 = countryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCustomAdapter1.ViewHolder holder, int position) {

        String vendorname = vendorProdarrayalllist.get(position).getName();
        holder.vndr_name.setText(vendorname.substring(0, 1).toUpperCase() + vendorname.substring(1));
        holder.vndr_email.setText(vendorProdarrayalllist.get(position).getEmail());
        holder.vnder_address.setText(vendorProdarrayalllist.get(position).getAddress());
        holder.vndr_id.setText(vendorProdarrayalllist.get(position).getPhone());

        //It is a image loading framework
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(mcontext).load(vendorProdarrayalllist.get(position).getStore_image())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.vnderlogo);


        //this is the function to call to vendor
        holder.callvnder.setOnClickListener(new View.OnClickListener() {
            String getnumber = String.valueOf(vendorProdarrayalllist.get(position).getPhone());

            @Override
            public void onClick(View v) {

                //callToVendor();
                Intent intent = new Intent(mcontext, CalltoVendor.class);
                intent.putExtra("phonenumber", getnumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);
            }
        });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callnow = new Intent(mcontext, IndivisualVendorDataWithOffers.class);
                callnow.putExtra("individialvenderid", String.valueOf(vendorProdarrayalllist.get(position).getVnderuser_id()));
                callnow.putExtra("HashValue", String.valueOf(vendorProdarrayalllist.get(position).getHash()));
                callnow.putExtra("vendorName", String.valueOf(vendorProdarrayalllist.get(position).getName()));
                callnow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(callnow);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != vendorProdarrayalllist ? vendorProdarrayalllist.size() : 0);
    }


    //search functionality
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {

                    vendorProdarrayalllist = vendorProdarrayalllist1;
                } else {
                    ArrayList<Vendors> filteredList = new ArrayList<>();
                    for (Vendors row : vendorProdarrayalllist1) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    vendorProdarrayalllist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = vendorProdarrayalllist;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                vendorProdarrayalllist = (ArrayList<Vendors>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

   //UI elements to get the ID
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView vndr_name;
        TextView vndr_email;
        TextView vnder_address;
        TextView vndr_id;
        ImageView callvnder, vnderlogo;
        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            vndr_name = (TextView) view.findViewById(R.id.content_name_view);
            vndr_email = (TextView) view.findViewById(R.id.textView);
            vnder_address = (TextView) view.findViewById(R.id.textView1);
            vndr_id = (TextView) view.findViewById(R.id.content_avatar_title1);
            callvnder = (ImageView) view.findViewById(R.id.overflow1);
            vnderlogo = (ImageView) view.findViewById(R.id.vndr_logoview);
            callvnder = (ImageView) view.findViewById(R.id.overflow1);
            relativeLayout = view.findViewById(R.id.relative);
        }
    }
}


