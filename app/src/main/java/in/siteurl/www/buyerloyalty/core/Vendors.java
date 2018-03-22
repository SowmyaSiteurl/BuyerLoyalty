package in.siteurl.www.buyerloyalty.core;

/**
 * Created by siteurl on 26/12/17.
 */

public class Vendors {

    String vnderuser_id;
    String name;
    String email;
    String phone;
    String address, hash, store_image;


    public Vendors(String store_image, String vnderuser_id, String name, String email, String phone, String address, String hash) {
        super();
        this.vnderuser_id = vnderuser_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.hash = hash;
        this.store_image = store_image;

    }

    public String getStore_image() {
        return store_image;
    }

    public String getHash() {
        return hash;
    }

    public String getVnderuser_id() {
        return vnderuser_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return vnderuser_id + " " + name + " "
                + email + " " + phone;
    }
}
