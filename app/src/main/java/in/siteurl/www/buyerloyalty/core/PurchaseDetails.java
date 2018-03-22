package in.siteurl.www.buyerloyalty.core;

/**
 * Created by siteurl on 29/12/17.
 */

public class PurchaseDetails {

    String statement_id; String buyer_id; String points_id;
    String description; String redemption_id; String opening_balance;
    String credit_points; String debit_points; String closing_balance;
    String status; String created_at; String updated_at;
    String vendorName; String vendorEmail; String vendorAddress; String vendorPhone;

    public PurchaseDetails(String statement_id, String buyer_id, String points_id,
                           String description, String redemption_id, String opening_balance,
                           String credit_points, String debit_points,
                           String closing_balance, String status, String created_at, String updated_at,
                           String vendorName, String vendorEmail, String vendorAddress,String vendorPhone) {
        super();
        this.statement_id = statement_id;
        this.buyer_id = buyer_id;
        this.points_id = points_id;
        this.description = description;
        this.redemption_id = redemption_id;
        this.opening_balance = opening_balance;
        this.credit_points = credit_points;
        this.debit_points = debit_points;
        this.closing_balance = closing_balance;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.vendorName = vendorName;
        this.vendorEmail = vendorEmail;
        this.vendorAddress = vendorAddress;
        this.vendorPhone = vendorPhone;

    }

    public String getStatement_id() {
        return statement_id;
    }

    public void setStatement_id(String statement_id) {
        this.statement_id = statement_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getPoints_id() {
        return points_id;
    }

    public void setPoints_id(String points_id) {
        this.points_id = points_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRedemption_id() {
        return redemption_id;
    }

    public void setRedemption_id(String redemption_id) {
        this.redemption_id = redemption_id;
    }

    public String getOpening_balance() {
        return opening_balance;
    }

    public void setOpening_balance(String opening_balance) {
        this.opening_balance = opening_balance;
    }

    public String getCredit_points() {
        return credit_points;
    }

    public void setCredit_points(String credit_points) {
        this.credit_points = credit_points;
    }

    public String getDebit_points() {
        return debit_points;
    }

    public void setDebit_points(String debit_points) {
        this.debit_points = debit_points;
    }

    public String getClosing_balance() {
        return closing_balance;
    }

    public void setClosing_balance(String closing_balance) {
        this.closing_balance = closing_balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getVendorPhone() {
        return vendorPhone;
    }

    public void setVendorPhone(String vendorPhone) {
        this.vendorPhone = vendorPhone;
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
        return "PurchaseDetails{" +
                "statement_id='" + statement_id + '\'' +
                ", buyer_id='" + buyer_id + '\'' +
                ", points_id='" + points_id + '\'' +
                ", description='" + description + '\'' +
                ", redemption_id='" + redemption_id + '\'' +
                ", opening_balance='" + opening_balance + '\'' +
                ", credit_points='" + credit_points + '\'' +
                ", debit_points='" + debit_points + '\'' +
                ", closing_balance='" + closing_balance + '\'' +
                ", status='" + status + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", vendorEmail='" + vendorEmail + '\'' +
                ", vendorAddress='" + vendorAddress + '\'' +
                ", vendorPhone='" + vendorPhone + '\'' +
                '}';
    }
}
