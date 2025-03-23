package VTTP.projectB.Models;

public class Meals {
    private String food;
    private String serving;
    private String type;
    private int amount;
    public String getFood() {
        return food;
    }
    public void setFood(String food) {
        this.food = food;
    }
    public String getServing() {
        return serving;
    }
    public void setServing(String serving) {
        this.serving = serving;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    @Override
    public String toString() {
        return "Meals [food=" + food + ", serving=" + serving + ", type=" + type + ", amount=" + amount + "]";
    }
    

    
}
