package in.siteurl.www.buyerloyalty.core;

/**
 * Created by siteurl on 28/12/17.
 */

public class Points {

    String purchase_amount;
    String points_earned;
    String approval_date;
    String status;
    String vendorName;
    String user_id;



    public Points(String purchase_amount,String points_earned,String approval_date, String status,
                 String vendorName, String user_id) {

        this.purchase_amount = purchase_amount;
        this.points_earned = points_earned;
        this.approval_date = approval_date;
        this.status = status;
        this.vendorName = vendorName;
        this.user_id = user_id;
    }

    public String getPurchase_amount() {
        return purchase_amount;
    }

    public void setPurchase_amount(String purchase_amount) {
        this.purchase_amount = purchase_amount;
    }

    public String getPoints_earned() {
        return points_earned;
    }

    public void setPoints_earned(String points_earned) {
        this.points_earned = points_earned;
    }

    public String getApproval_date() {
        return approval_date;
    }

    public void setApproval_date(String approval_date) {
        this.approval_date = approval_date;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Points{" +
                "purchase_amount='" + purchase_amount + '\'' +
                ", points_earned='" + points_earned + '\'' +
                ", approval_date='" + approval_date + '\'' +
                ", status='" + status + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
