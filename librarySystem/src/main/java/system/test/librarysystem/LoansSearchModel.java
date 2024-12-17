package system.test.librarysystem;

public class LoansSearchModel {
    Integer ID, late_fee;
    String item_name, customer, product_type, return_date;


public LoansSearchModel(Integer ID, String item_name, String customer, String product_type, String return_date, Integer late_fee) {
        this.ID = ID;
        this.item_name = item_name;
        this.customer = customer;
        this.product_type = product_type;
        this.return_date = return_date;
        this.late_fee = late_fee;
    }

    public Integer getID() {
        return ID;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getCustomer() {
        return customer;
    }

    public String getProduct_type() {
        return product_type;
    }

    public String getReturn_date() {
        return return_date;
    }

    public Integer getLate_fee() {
        return late_fee;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public void setLate_fee(Integer late_fee) {
        this.late_fee = late_fee;
    }
}
