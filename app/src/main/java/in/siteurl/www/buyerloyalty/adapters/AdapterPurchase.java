package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.core.PurchaseDetails;

/**
 * Created by siteurl on 13/1/18.
 */

public class AdapterPurchase extends RecyclerView.Adapter<AdapterPurchase.ViewHolder> implements Filterable {

    private ArrayList<PurchaseDetails> purchaseDetails = new ArrayList<>();
    private ArrayList<PurchaseDetails> purchaseDetails1 = new ArrayList<>();
    private Context mcontext;

    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;

    public AdapterPurchase(Context context, ArrayList<PurchaseDetails> countryList) {
        this.purchaseDetails = countryList;
        this.mcontext = context;
        this.purchaseDetails1 = countryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_transcation, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterPurchase.ViewHolder ViewHolder, int position) {

        String vendorname = purchaseDetails.get(position).getVendorName();
        //To convert first letter to uppercase
        vendorname = vendorname.substring(0, 1).toUpperCase() + vendorname.substring(1);
        ViewHolder.pro_name.setText("Points awarded to " + vendorname);

        String getaprovaldate = purchaseDetails.get(position).getCreated_at();
        String calender_date = new SimpleDateFormat("yyyy-MM-dd").format(newDate.getTime());
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String newclldate = null;

        //date picker dialog
        newDate.set(mYear, mMonth, mDay);
        try {
            date = inputFormat.parse(getaprovaldate);
            newclldate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ViewHolder.pro_date.setText("" + newclldate);
        ViewHolder.openingBalance.setText(purchaseDetails.get(position).getOpening_balance() + " Pts");
        ViewHolder.closingBalance.setText(purchaseDetails.get(position).getClosing_balance() + " Pts");

    }


    @Override
    public int getItemCount() {
        return (null != purchaseDetails ? purchaseDetails.size() : 0);
    }

    //view holder to initialize and get the ID
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pro_name;
        TextView pro_des, pro_date;
        ImageView imageView;
        TextView openingBalance, creditPoints, closingBalance, debitPoints;

        public ViewHolder(View view) {
            super(view);

            pro_name = view.findViewById(R.id.purchaseVendorName);
            pro_date = view.findViewById(R.id.purchaseDate);
            openingBalance = view.findViewById(R.id.purchaseOpeningBalance);
            closingBalance = view.findViewById(R.id.purchaseClosingBalance);
        }
    }

    //search functionality
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {

                    purchaseDetails = purchaseDetails1;
                } else {
                    ArrayList<PurchaseDetails> filteredList = new ArrayList<>();
                    for (PurchaseDetails row : purchaseDetails1) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getOpening_balance().toLowerCase().contains(charString.toLowerCase()) || row.getClosing_balance().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    purchaseDetails = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = purchaseDetails;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                purchaseDetails = (ArrayList<PurchaseDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
