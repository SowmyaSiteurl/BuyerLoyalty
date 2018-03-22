package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
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
 * Created by siteurl on 29/12/17.
 */

public class PurchaseHistoryAdapter extends ArrayAdapter<PurchaseDetails> {

    private ArrayList<PurchaseDetails> originalList;
    private TransactionUserFilter filter;//filter data from an entire data set based on some pattern(a String )
    Context mcontext;
    int positionoflist;

    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;

    public PurchaseHistoryAdapter(Context context, int textViewResourceId, ArrayList<PurchaseDetails> countryList) {
        super(context, textViewResourceId, countryList);
        this.originalList = new ArrayList<PurchaseDetails>();
        this.originalList.addAll(countryList);
        mcontext = context;
    }

    //filter for search
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new TransactionUserFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView pro_name;
        TextView pro_des, pro_date;
        ImageView imageView;
        TextView openingBalance,creditPoints,closingBalance,debitPoints;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final PurchaseDetails currentenquiry = originalList.get(position);
        if (convertView == null) {
            positionoflist = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_transcation, parent, false);

            holder = new ViewHolder();
            holder.pro_name = (TextView) convertView.findViewById(R.id.purchaseVendorName);
            holder.pro_date = (TextView) convertView.findViewById(R.id.purchaseDate);
            holder.openingBalance = convertView.findViewById(R.id.purchaseOpeningBalance);
            holder.closingBalance = convertView.findViewById(R.id.purchaseClosingBalance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String vendorname = currentenquiry.getVendorName();
        holder.pro_name.setText("" + vendorname);
        String getaprovaldate = currentenquiry.getCreated_at();
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
        holder.pro_date.setText("" + newclldate);

        holder.openingBalance.setText("op.bal "+currentenquiry.getOpening_balance());
        holder.closingBalance.setText("cl.bal "+currentenquiry.getClosing_balance());
        return convertView;
    }

    // filter the results according to the constraint
    private class TransactionUserFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<PurchaseDetails> filteredItems = new ArrayList<PurchaseDetails>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    PurchaseDetails mycountry = originalList.get(i);
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
            originalList = (ArrayList<PurchaseDetails>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = originalList.size(); i < l; i++)
                add(originalList.get(i));
            notifyDataSetInvalidated();
        }
    }

}
