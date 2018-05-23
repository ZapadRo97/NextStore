package app.product;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Category {

    @Getter @Setter int id;
    @Getter @Setter private String name;
}
