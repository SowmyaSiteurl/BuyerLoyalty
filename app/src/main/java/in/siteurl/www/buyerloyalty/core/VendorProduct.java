package in.siteurl.www.buyerloyalty.core;

import java.io.Serializable;

/**
 * Created by siteurl on 24/1/18.
 */

public class VendorProduct implements Serializable {

    String  offer_name, offer_description,
            offer_image, expiry_date, offer_price, terms_and_condtion, status,vendorName,vendorEmail,vendorAddress;

    public VendorProduct( String offer_name,
                         String offer_description, String offer_image,
                         String expiry_date, String offer_price,
                         String terms_and_condtion, String status,String vendorName, String vendorEmail, String vendorAddress) {

        this.offer_name = offer_name;
        this.offer_description = offer_description;
        this.offer_image = offer_image;
        this.expiry_date = expiry_date;
        this.offer_price = offer_price;
        this.terms_and_condtion = terms_and_condtion;
        this.status = status;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.vendorAddress = vendorAddress;

    }



    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    public String getOffer_description() {
        return offer_description;
    }

    public void setOffer_description(String offer_description) {
        this.offer_description = offer_description;
    }

    public String getOffer_image() {
        return offer_image;
    }

    public void setOffer_image(String offer_image) {
        this.offer_image = offer_image;
    }


    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }

    public String getTerms_and_condtion() {
        return terms_and_condtion;
    }

    public void setTerms_and_condtion(String terms_and_condtion) {
        this.terms_and_condtion = terms_and_condtion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getVendorAddress() {
        return vendorAddress;
    }

    public void setVendorAddress(String vendorAddress) {
        this.vendorAddress = vendorAddress;
    }

    @Override
    public String toString() {
        return "VendorProduct{" +
                "offer_name='" + offer_name + '\'' +
                ", offer_description='" + offer_description + '\'' +
                ", offer_image='" + offer_image + '\'' +
                ", expiry_date='" + expiry_date + '\'' +
                ", offer_price='" + offer_price + '\'' +
                ", terms_and_condtion='" + terms_and_condtion + '\'' +
                ", status='" + status + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", vendorEmail='" + vendorEmail + '\'' +
                ", vendorAddress='" + vendorAddress + '\'' +
                '}';
    }
}


