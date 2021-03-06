package app.product;

import java.math.BigDecimal;
import java.sql.Blob;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Product {
    @Getter @Setter private int id;
    @Getter @Setter private String name;
    @Getter @Setter private int categoryID;
    @Getter @Setter private BigDecimal price;
    @Getter @Setter private String description;
    @Getter @Setter private Blob image;
    @Getter @Setter private int quantity;
    @Getter @Setter private String imagePath;

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {

        if (this.getClass() != obj.getClass()) return false;
        Product p = (Product)obj;
        if(this.id != p.getId())
            return false;

        return true;

    }
}
