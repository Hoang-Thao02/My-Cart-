package hanu.a2_2001040188.models.Product;

public class Product {
    Integer Id;
    String name;
    Double Price;
    String Thumbnail;
    public Product(Integer id, String thumbnail, String name, Double price) {
        this.Id = id;
        this.name = name;
        this.Price = price;
        this.Thumbnail = thumbnail;
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
    public Double getPrice() {
        return Price;
    }
    public void setPrice(Double price) {
        Price = price;
    }
    public String getThumbnail() {
        return Thumbnail;
    }
    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Product: " +
                "Id=" + Id +
                ", Thumbnail='" + Thumbnail + '\'' +
                ", name='" + name + '\'' +
                ", Price=" + Price +
                '.';
    }
}
