package in.siteurl.www.buyerloyalty.core;

/**
 * Created by siteurl on 22/12/17.
 */

public class RedeemProduct {
    String redeemption_prod_id;
    String product_name;
    String user_id;
    String points_value;
    String product_img;
    String updated_at;
    String expiry_date;
    String prod_description,terms_and_condition,status;

    public RedeemProduct(String redeemption_prod_id, String product_name, String user_id,
                         String points_value, String product_img, String updated_at,
                         String expiry_date, String prod_description,
                         String terms_and_condition, String status) {
        super();
        this.redeemption_prod_id = redeemption_prod_id;
        this.product_name = product_name;
        this.user_id = user_id;
        this.points_value = points_value;
        this.product_img = product_img;
        this.updated_at = updated_at;
        this.expiry_date = expiry_date;
        this.prod_description = prod_description;
        this.terms_and_condition = terms_and_condition;
        this.status = status;
    }

    public RedeemProduct() {

    }


    public String getRedeemption_prod_id() {
        return redeemption_prod_id;
    }

    public void setRedeemption_prod_id(String redeemption_prod_id) {
        this.redeemption_prod_id = redeemption_prod_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPoints_value() {
        return points_value;
    }

    public void setPoints_value(String points_value) {
        this.points_value = points_value;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getProd_description() {
        return prod_description;
    }

    public void setProd_description(String prod_description) {
        this.prod_description = prod_description;
    }

    public String getTerms_and_condition() {
        return terms_and_condition;
    }

    public void setTerms_and_condition(String terms_and_condition) {
        this.terms_and_condition = terms_and_condition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return  product_name + " " + points_value + " "
                + expiry_date + " " + terms_and_condition+ " "
                + prod_description;
    }
}

