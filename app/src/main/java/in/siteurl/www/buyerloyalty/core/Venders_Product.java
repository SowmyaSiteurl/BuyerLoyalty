package in.siteurl.www.buyerloyalty.core;

/**
 * Created by siteurl on 26/12/17.
 */

public class Venders_Product {

    String offer_id, vendor_id, offer_name, offer_description,
            offer_image, updated_at, expiry_date, offer_price, terms_and_condtion, status;

    public Venders_Product(String offer_id, String vendor_id, String offer_name,
                           String offer_description, String offer_image, String updated_at,
                           String expiry_date, String offer_price,
                           String terms_and_condtion, String status) {
        this.offer_id = offer_id;
        this.vendor_id = vendor_id;
        this.offer_name = offer_name;
        this.offer_description = offer_description;
        this.offer_image = offer_image;
        this.updated_at = updated_at;
        this.expiry_date = expiry_date;
        this.offer_price = offer_price;
        this.terms_and_condtion = terms_and_condtion;
        this.status = status;


    }

    public String getOffer_id() {
        return offer_id;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public String getOffer_description() {
        return offer_description;
    }

    public String getOffer_image() {
        return offer_image;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public String getTerms_and_condtion() {
        return terms_and_condtion;
    }

    public String getStatus() {
        return status;
    }


    @Override
    public String toString() {
        return "Venders_Product{" +
                "offer_id='" + offer_id + '\'' +
                ", vendor_id='" + vendor_id + '\'' +
                ", offer_name='" + offer_name + '\'' +
                ", offer_description='" + offer_description + '\'' +
                ", offer_image='" + offer_image + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                ", offer_price='" + offer_price + '\'' +
                ", terms_and_condtion='" + terms_and_condtion + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

