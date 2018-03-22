package in.siteurl.www.buyerloyalty.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.activities.DatabaseHandler;
import in.siteurl.www.buyerloyalty.activities.HomePageActivity;
import in.siteurl.www.buyerloyalty.activities.Loyalty_Singlton;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.RedeemProduct;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 4/1/18.
 */

public class CartingAdapter extends ArrayAdapter<RedeemProduct> {
    private ArrayList<RedeemProduct> originalList;
    private ArrayList<RedeemProduct> countryList;
    private CartingFilter filter;
    Context mcontext;
    int positionOfList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId, loginIdentity;
    DatabaseHandler databaseHandler;
    RedeemProduct currentEnquiry;
    int getproductioIndex;


    public CartingAdapter(Context context, int textViewResourceId, ArrayList<RedeemProduct> countryList) {
        super(context, textViewResourceId, countryList);
        this.originalList = new ArrayList<RedeemProduct>();
        this.originalList.addAll(countryList);
        this.countryList = new ArrayList<RedeemProduct>();
        this.countryList.addAll(countryList);
        mcontext = context;
    }

    //filter function for Search
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CartingFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView pro_name;
        TextView pro_price;
        ImageView imageView, shopImageView, deleteImageView;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        getproductioIndex = position;

        sharedPreferences = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        loginIdentity = sharedPreferences.getString("loginname", null);
        editor = sharedPreferences.edit();

        //initializing database
        databaseHandler = new DatabaseHandler(getContext());

        ViewHolder holder = null;
        currentEnquiry = countryList.get(position);

        if (convertView == null) {
            positionOfList = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.carting_products, parent, false);
            holder = new ViewHolder();

            holder.pro_name = (TextView) convertView.findViewById(R.id.nameofproduct);
            holder.pro_price = (TextView) convertView.findViewById(R.id.priceofproduct);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.shopImageView = (ImageView) convertView.findViewById(R.id.shopimage);
            holder.deleteImageView = (ImageView) convertView.findViewById(R.id.removeshop);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String vendorName = currentEnquiry.getProduct_name();
        holder.pro_name.setText(vendorName);
        holder.pro_price.setText(currentEnquiry.getPoints_value() + " points");

        final View finalConvertView = convertView;

        //this is the function to delete item From list
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            String removeId = String.valueOf(currentEnquiry.getRedeemption_prod_id());

            @Override
            public void onClick(View v) {

                databaseHandler.deleteSingleProduct(currentEnquiry);

                Toast.makeText(mcontext, "" + CartingAdapter.this.getItem(position), Toast.LENGTH_SHORT).show();
                RedeemProduct toRemove = CartingAdapter.this.getItem(position);
                CartingAdapter.this.remove(originalList.get(position));
                CartingAdapter.this.notifyDataSetChanged();
            }
        });


        //this is the function to shop items
        holder.shopImageView.setOnClickListener(new View.OnClickListener() {

            String prodId = currentEnquiry.getRedeemption_prod_id();
            String prodDesc = currentEnquiry.getProduct_name();
            String prodPoints = currentEnquiry.getPoints_value();
            String noOfProd = String.valueOf(1);

            @Override
            public void onClick(View v) {

                //shopNow(prodId,prodDesc,prodPoints,noOfProd);
                AlertDialog alertDialogs = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                alertDialogs.setTitle("Shop now");
                alertDialogs.setMessage("Would you like to shop now ?");
                alertDialogs.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shopNow(prodId, prodDesc, prodPoints, noOfProd, finalConvertView);
                                dialog.dismiss();
                            }
                        });
                alertDialogs.show();
            }
        });

        //Image loading framework
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(getContext()).load(currentEnquiry.getProduct_img())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(holder.imageView);
        return convertView;
    }

    //search function to search products
    private class CartingFilter extends Filter {
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

    //final View finalConvertViews = view;
    //this is the method for shopping Cart Products
    public void shopNow(final String prodId, final String prodDesc, final String prodPoints, final String noOfProd, final View finalConvertView) {
        StringRequest shopNow = new StringRequest(Request.Method.POST, Constants.shopNow,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");
                            //  String orderId = jsonObject.getString("Order Id");

                            if (Error.equals("false")) {

                               // Toast.makeText(getContext(), Message, Toast.LENGTH_LONG).show();

                                AlertDialog alertDialog = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                                alertDialog.setIcon(R.mipmap.ic_launcher);
                                alertDialog.setTitle("Loyalty Program application");
                                alertDialog.setMessage(Message);
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();


                                databaseHandler.deleteSingleProduct(currentEnquiry);

                                Toast.makeText(mcontext, "" + CartingAdapter.this.getItem(getproductioIndex), Toast.LENGTH_SHORT).show();
                                RedeemProduct toRemove = CartingAdapter.this.getItem(getproductioIndex);
                                CartingAdapter.this.remove(originalList.get(getproductioIndex));
                                CartingAdapter.this.notifyDataSetChanged();

                                Intent intent = new Intent(getContext(), HomePageActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);

                            }

                            if (Error.equals("true")) {

                              //  Toast.makeText(getContext(), Message, Toast.LENGTH_LONG).show();

                                AlertDialog alertDialog = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                                alertDialog.setIcon(R.mipmap.ic_launcher);
                                alertDialog.setTitle("Loyalty Program application");
                                alertDialog.setMessage(Message);
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(getContext(), "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(getContext(), "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(getContext(), "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(getContext(), "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(getContext(), " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uId);
                params.put("sid", sessionId);
                params.put("product_id", prodId);
                params.put("no_of_products", noOfProd);
                params.put("total_points_used", prodPoints);
                params.put("description", prodDesc);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        shopNow.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getContext()).addtorequestqueue(shopNow);


    }
}

