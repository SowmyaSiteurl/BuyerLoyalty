package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.core.Points;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 28/12/17.
 */

public class PointsAdapter extends ArrayAdapter<Points> {

    private ArrayList<Points> originalList;
    private ArrayList<Points> countryList;
    private PointsAproved_Filter filter;//filter data from an entire data set based on some pattern(a String )
    int positionoflist;

    //sharedPreferences used to save data
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String userData, sessionId, uId;
    Calendar newDate = Calendar.getInstance();
    int mYear, mMonth, mDay;

    public PointsAdapter(Context context, int textViewResourceId, ArrayList<Points> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<Points>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<Points>();
        this.originalList.addAll(countryList);
    }

    //filter for search
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new PointsAproved_Filter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView userName;
        TextView claimPrice;
        TextView pointsEarned, status, aprovedDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        loginpref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginpref.getString("sessionid", null);
        uId = loginpref.getString("User-id", null);
        userData = loginpref.getString("user_data", null);
        editor = loginpref.edit();
        ViewHolder holder = null;

        final Points currentenquiry = countryList.get(position);
        if (convertView == null) {

            positionoflist = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.points_layout, parent, false);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.pointsId);
            holder.claimPrice = (TextView) convertView.findViewById(R.id.pointsAmount);
            holder.pointsEarned = (TextView) convertView.findViewById(R.id.pointsEarned);
            holder.status = (TextView) convertView.findViewById(R.id.pointsStatus);
            holder.aprovedDate = (TextView) convertView.findViewById(R.id.pointsDate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //setting text in view holder text view from an arraylist
        String vendorname = currentenquiry.getVendorName();
        holder.userName.setText(vendorname);
        holder.claimPrice.setText("₹ " + currentenquiry.getPurchase_amount());
        holder.pointsEarned.setText(currentenquiry.getPoints_earned());
        holder.status.setText(currentenquiry.getStatus());
        String getaprovaldate = currentenquiry.getApproval_date();

        //datepicker
        String calender_date = new SimpleDateFormat("yyyy-MM-dd").format(newDate.getTime());
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date date = null;
        String newclldate = null;
        newDate.set(mYear, mMonth, mDay);
        try {
            date = inputFormat.parse(getaprovaldate);
            newclldate = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.aprovedDate.setText(newclldate);

        return convertView;
    }

    // filter the results according to the constraint
    private class PointsAproved_Filter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<Points> filteredItems = new ArrayList<Points>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Points mycountry = originalList.get(i);
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
            countryList = (ArrayList<Points>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }
}
