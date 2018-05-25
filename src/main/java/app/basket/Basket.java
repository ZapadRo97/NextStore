package app.basket;

import app.product.Product;

import java.util.HashMap;

import lombok.*;


@AllArgsConstructor
public class Basket {
    int userID;
    @Getter @Setter HashMap<Product, Integer> products;


}
