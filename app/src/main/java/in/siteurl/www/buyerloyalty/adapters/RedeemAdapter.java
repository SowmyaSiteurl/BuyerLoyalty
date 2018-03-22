package in.siteurl.www.buyerloyalty.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.activities.DatabaseHandler;
import in.siteurl.www.buyerloyalty.core.RedeemProduct;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 22/12/17.
 */

public class RedeemAdapter extends ArrayAdapter<RedeemProduct> {

    private ArrayList<RedeemProduct> originalList;
    private ArrayList<RedeemProduct> countryList;
    private RedeemFilter filter;//filter data from an entire data set based on some pattern(a String )
    int positionoflist;
    ArrayList<String> prodIdList = new ArrayList<>();
    ArrayList<RedeemProduct> redeemIdList = new ArrayList<>();
    Context mcontext;
    SharedPreferences cartPref;
    SharedPreferences.Editor cartEditor;
    DatabaseHandler databaseHandler;
    String TotalPoints;


    public RedeemAdapter(Context context, int textViewResourceId, ArrayList<RedeemProduct> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<RedeemProduct>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<RedeemProduct>();
        this.originalList.addAll(countryList);

        cartPref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        cartEditor = cartPref.edit();

        mcontext = context;

    }

    //filter for search
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new RedeemFilter();
        }
        return filter;
    }


    private class ViewHolder {
        TextView pro_name;
        TextView pro_price;
        TextView pro_exp;
        TextView pro_terms,
                pro_desc;
        ImageView imageView, imageNew, imageEndingSoon, imageExpired;
        CheckBox addToCart;
    }

    //get UI elements and set Text
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        redeemIdList.clear();
        ViewHolder holder = null;

        databaseHandler = new DatabaseHandler(getContext());

        final RedeemProduct currentEnquiry = countryList.get(position);
        if (convertView == null) {
            positionoflist = position;

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.redeemproduct_list, parent, false);
            holder = new ViewHolder();
            holder.pro_name = (TextView) convertView.findViewById(R.id.redeem_product_name);
            holder.pro_price = (TextView) convertView.findViewById(R.id.redeemPoints);
            holder.pro_exp = (TextView) convertView.findViewById(R.id.redeem_date_details);
            holder.pro_terms = (TextView) convertView.findViewById(R.id.termsandconditionTVRedeem);
            holder.pro_desc = (TextView) convertView.findViewById(R.id.redeem_description);
            holder.imageView = (ImageView) convertView.findViewById(R.id.redeem_image);
            holder.addToCart = convertView.findViewById(R.id.shop_now_redeem);
            holder.imageNew = convertView.findViewById(R.id.newImage1);
            holder.imageEndingSoon = convertView.findViewById(R.id.endingSoon1);
            holder.imageExpired = convertView.findViewById(R.id.expired1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String vendorname = currentEnquiry.getProduct_name();
        holder.pro_name.setText(vendorname);
        holder.pro_price.setText(currentEnquiry.getPoints_value() + " points");
        holder.pro_exp.setText(currentEnquiry.getExpiry_date());
        holder.pro_terms.setText(currentEnquiry.getTerms_and_condition());
        holder.pro_desc.setText(currentEnquiry.getProd_description());


        //this is o check system date with userEntered date and display new, endingSoon and Expired Products
        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formatDate = df.format(c.getTime());

            Date newDateStr = df.parse(formatDate);
            Date userEnteredDate = df.parse(originalList.get(position).getExpiry_date());

            long NoOfDays = userEnteredDate.getTime() - newDateStr.getTime();
            NoOfDays = TimeUnit.DAYS.convert(NoOfDays, TimeUnit.MILLISECONDS);

            if (NoOfDays >= 5) {
                holder.imageNew.setVisibility(View.VISIBLE);
                holder.imageEndingSoon.setVisibility(View.GONE);
                holder.imageExpired.setVisibility(View.GONE);
                Glide.with(mcontext).load(R.drawable.newoffer)
                        .thumbnail(0.5f)
                        .into(holder.imageNew);

            }

            if (NoOfDays < 5) {
                holder.imageEndingSoon.setVisibility(View.VISIBLE);
                holder.imageNew.setVisibility(View.GONE);
                holder.imageExpired.setVisibility(View.GONE);
                Glide.with(mcontext).load(R.drawable.ending)
                        .thumbnail(0.5f)
                        .into(holder.imageEndingSoon);

            }

            if (NoOfDays <= 0) {
                holder.imageExpired.setVisibility(View.VISIBLE);
                holder.imageEndingSoon.setVisibility(View.GONE);
                holder.imageNew.setVisibility(View.GONE);
                Glide.with(mcontext).load(R.drawable.expiry)
                        .thumbnail(0.5f)
                        .into(holder.imageExpired);

            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        final View finalConvertView = convertView;

        //this is the Add to cart function
        holder.addToCart.setOnClickListener(new View.OnClickListener() {

            String id = String.valueOf(currentEnquiry.getRedeemption_prod_id());
            String name = String.valueOf(currentEnquiry.getProduct_name());
            String points = String.valueOf(currentEnquiry.getPoints_value());


            @Override
            public void onClick(View v) {

                TotalPoints = cartPref.getString("TotalPoints", null);

                if (prodIdList.equals(null) || prodIdList.equals("")) {

                } else {

                    if (Integer.parseInt(TotalPoints) > Integer.parseInt(points)) {

                        if (!prodIdList.contains(id)) {
                            prodIdList.add(id);

                            AlertDialog alertDialog = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                            alertDialog.setTitle("Cart");
                            alertDialog.setMessage("your product " + name + " added to cart successfully");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            databaseHandler.addToCart(currentEnquiry);
                                            //  cartEditor.putString("cartListName", String.valueOf(prodIdList));
                                            //  cartEditor.commit();
                                            //  Toast.makeText(getContext(), String.valueOf(prodIdList), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                        } else {

                            AlertDialog alertDialog = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                            alertDialog.setMessage("Already Added");
                            alertDialog.setIcon(R.mipmap.ic_launcher);
                            alertDialog.setTitle("Loyalty Program application");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                            // Toast.makeText(getContext(), "Already Added", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        //  Toast.makeText(getContext(), "You do not have sufficient Points", Toast.LENGTH_LONG).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                        alertDialog.setMessage("You do not have sufficient Points");
                        alertDialog.setIcon(R.mipmap.ic_launcher);
                        alertDialog.setTitle("Loyalty Program application");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }
                }
            }
        });

        //It is a image loading framework
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        //It is a image loading framework (that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface)
        Glide.with(getContext()).load(currentEnquiry.getProduct_img())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.imageView);
        return convertView;


    }

    // filter the results according to the constraint
    private class RedeemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<RedeemProduct> filteredItems = new ArrayList<RedeemProduct>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    RedeemProduct mycountry = originalList.get(i);
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

            countryList = (ArrayList<RedeemProduct>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }

}
