package VTTP.projectB.Models;

public class Premium {
    private Long amount;
    private String name;
    private String currency;
    private Long quantity;

    public Premium(){
        this.amount = 1000L;
        this.name = "Premium Membership";
        this.currency="SGD";
        this.quantity= 1L;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    
}
