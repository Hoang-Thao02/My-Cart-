package hanu.a2_2001040188.models.Order;

public class Order {
    Integer Id;
    String name;
    String Thumbnail;
    Integer quantity;
    Double price;


    public Order(Integer id, String thumbnail, String name, Double price, Integer quantity) {
        Id = id;
        this.name = name;
        Thumbnail = thumbnail;
        this.quantity = quantity;
        this.price = price;

    }

    @Override
    public String toString() {
        return "Order{" +
                "Id=" + Id +
                ", Thumbnail='" + Thumbnail + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}
