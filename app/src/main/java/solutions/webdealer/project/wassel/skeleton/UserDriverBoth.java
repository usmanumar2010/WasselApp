package solutions.webdealer.project.wassel.skeleton;

public class UserDriverBoth {

    String name, number, category, price;
    float ratingStar;

    public UserDriverBoth(String name, String number, String category, String price, float ratingStar) {
        this.name = name;
        this.number = number;
        this.category = category;
        this.price = price;
        this.ratingStar = ratingStar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRatingStar() {
        return ratingStar;
    }

    public void setRatingStar(float ratingStar) {
        this.ratingStar = ratingStar;
    }

}
