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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.activities.IndivisualOffer;
import in.siteurl.www.buyerloyalty.core.VendorProduct;


/**
 * Created by siteurl on 18/1/18.
 */

public class VendorOffersAdapter extends RecyclerView.Adapter<VendorOffersAdapter.ViewHolder>
        implements Filterable {

    private ArrayList<VendorProduct> vendorProdarrayalllist;
    private ArrayList<VendorProduct> vendorProdarrayalllist1;
    private Context mcontext;

    public VendorOffersAdapter(Context context, ArrayList<VendorProduct> countryList) {

        this.vendorProdarrayalllist = countryList;
        this.mcontext = context;
        this.vendorProdarrayalllist1 = countryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vendor_offers, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VendorOffersAdapter.ViewHolder viewHolder, int position) {


        String vendorname = vendorProdarrayalllist.get(position).getOffer_name();
        viewHolder.pro_name.setText(vendorname);
        viewHolder.pro_price.setText("₹" + vendorProdarrayalllist.get(position).getOffer_price());
        viewHolder.pro_exp.setText(vendorProdarrayalllist.get(position).getExpiry_date());
        viewHolder.pro_terms.setText(vendorProdarrayalllist.get(position).getTerms_and_condtion());
        viewHolder.pro_desc.setText(vendorProdarrayalllist.get(position).getOffer_description());

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mcontext, IndivisualOffer.class);
                intent.putExtra("key", vendorProdarrayalllist.get(position));

                mcontext.startActivity(intent);

            }
        });

        //It is a image loading framework
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(mcontext).load(vendorProdarrayalllist.get(position).getOffer_image())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(viewHolder.imageView);

        //this is o check system date with userEntered date and display new, endingSoon and Expired Products
        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formatDate = df.format(c.getTime());

            Date newDateStr = df.parse(formatDate);
            Date userEnteredDate = df.parse(vendorProdarrayalllist.get(position).getExpiry_date());

            long NoOfDays = userEnteredDate.getTime() - newDateStr.getTime();
            NoOfDays = TimeUnit.DAYS.convert(NoOfDays, TimeUnit.MILLISECONDS);

            if (NoOfDays >= 5) {
                viewHolder.imageNew.setVisibility(View.VISIBLE);
                viewHolder.imageEndingSoon.setVisibility(View.GONE);
                viewHolder.imageExpired.setVisibility(View.GONE);
                Glide.with(mcontext).load(R.drawable.newoffer)
                        .thumbnail(0.5f)
                        .into(viewHolder.imageNew);

            }

            if (NoOfDays < 5) {
                viewHolder.imageEndingSoon.setVisibility(View.VISIBLE);
                viewHolder.imageNew.setVisibility(View.GONE);
                viewHolder.imageExpired.setVisibility(View.GONE);
                Glide.with(mcontext).load(R.drawable.ending)
                        .thumbnail(0.5f)
                        .into(viewHolder.imageEndingSoon);

            }

            if (NoOfDays <= 0) {
                viewHolder.imageExpired.setVisibility(View.VISIBLE);
                viewHolder.imageEndingSoon.setVisibility(View.GONE);
                viewHolder.imageNew.setVisibility(View.GONE);
                Glide.with(mcontext).load(R.drawable.expiry)
                        .thumbnail(0.5f)
                        .into(viewHolder.imageExpired);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return (null != vendorProdarrayalllist ? vendorProdarrayalllist.size() : 0);
    }


    //search Function
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {

                    vendorProdarrayalllist = vendorProdarrayalllist1;
                } else {
                    ArrayList<VendorProduct> filteredList = new ArrayList<>();
                    for (VendorProduct row : vendorProdarrayalllist1) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getOffer_name().toLowerCase().contains(charString.toLowerCase()) || row.getOffer_price().contains(charSequence)) {
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

                vendorProdarrayalllist = (ArrayList<VendorProduct>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //UI elements to get ID
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pro_name;
        TextView pro_price;
        TextView pro_exp;
        TextView pro_terms,
                pro_desc, pro_status;
        ImageView imageView, gps_showi, shop_nowi, imageNew, imageEndingSoon, imageExpired;
        LinearLayout linearLayout;


        public ViewHolder(View view) {
            super(view);

            pro_name = view.findViewById(R.id.head_image_left_text1);
            pro_price = view.findViewById(R.id.head_image_center_text1);
            pro_exp = view.findViewById(R.id.head_image_right_text1);
            pro_terms = view.findViewById(R.id.termsandconditionTV1);
            pro_desc = view.findViewById(R.id.descriptionTV1);
            // pro_status = view.findViewById(R.id.statusTV1);
            imageView = view.findViewById(R.id.head_image1);
            shop_nowi = view.findViewById(R.id.shop_now1);
            gps_showi = view.findViewById(R.id.gps_show1);
            imageNew = view.findViewById(R.id.newImage);
            imageEndingSoon = view.findViewById(R.id.endingSoon);
            imageExpired = view.findViewById(R.id.expired);
            linearLayout = view.findViewById(R.id.IndivisualVendorOffers);
        }
    }
}


