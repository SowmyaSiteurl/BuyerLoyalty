package in.siteurl.www.buyerloyalty.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.activities.IndivisualOffer;
import in.siteurl.www.buyerloyalty.activities.IndivisualVendorDataWithOffers;
import in.siteurl.www.buyerloyalty.core.OtherProduct;
import in.siteurl.www.buyerloyalty.core.Points;
import in.siteurl.www.buyerloyalty.core.VendorProduct;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 30/1/18.
 */

public class viewPagerAdapter extends PagerAdapter {

    private ArrayList<OtherProduct> vendorotherProdarrayalllist;
    Context mcontext;
    private LayoutInflater inflater;

    //sharedPreferences used to save data
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String userData, sessionId, uId;


    public viewPagerAdapter(Context context, ArrayList<OtherProduct> vendorProdarrayalllist) {

        this.mcontext = context;
        this.vendorotherProdarrayalllist = vendorProdarrayalllist;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return vendorotherProdarrayalllist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }


    //get UI elements and set text
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        loginpref = mcontext.getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginpref.getString("sessionid", null);
        uId = loginpref.getString("User-id", null);
        userData = loginpref.getString("user_data", null);
        editor = loginpref.edit();

        View itemView = inflater.inflate(R.layout.view_pager, container, false);

        TextView offername = (TextView) itemView.findViewById(R.id.offerName);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.offerImage);
        TextView offerprice = (TextView) itemView.findViewById(R.id.offerPrice);

        offername.setText(vendorotherProdarrayalllist.get(position).getOtherName());
        offerprice.setText(vendorotherProdarrayalllist.get(position).getOtherOffer() + " Rs/-");

        //It is a image loading framework
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();


        Glide.with(mcontext).load(vendorotherProdarrayalllist.get(position).getOtherImage())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(imageView);

        // container.addView(itemView);
        ((ViewPager) container).addView(itemView);


        imageView.setOnClickListener(new View.OnClickListener() {

            //  String vendorId = String.valueOf(vendorotherProdarrayalllist.get(position).getVendorId());
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mcontext, IndivisualVendorDataWithOffers.class);
                intent.putExtra("VendorId", String.valueOf(vendorotherProdarrayalllist.get(position).getVendorId()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((View) object);
        // container.removeView((LinearLayout) object);
    }
}
