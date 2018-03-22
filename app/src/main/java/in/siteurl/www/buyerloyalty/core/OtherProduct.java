package in.siteurl.www.buyerloyalty.core;

/**
 * Created by siteurl on 2/2/18.
 */

public class OtherProduct {

    String otherName, otherImage, otherOffer,vendorId;

    public OtherProduct(String otherName, String otherImage, String otherOffer ,String vendorId) {
        this.otherName = otherName;
        this.otherImage = otherImage;
        this.otherOffer = otherOffer;
        this.vendorId = vendorId;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getOtherImage() {
        return otherImage;
    }

    public void setOtherImage(String otherImage) {
        this.otherImage = otherImage;
    }

    public String getOtherOffer() {
        return otherOffer;
    }

    public void setOtherOffer(String otherOffer) {
        this.otherOffer = otherOffer;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        vendorId = vendorId;
    }
}
