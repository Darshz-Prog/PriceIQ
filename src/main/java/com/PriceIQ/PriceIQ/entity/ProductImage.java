package com.PriceIQ.PriceIQ.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; //? in current version we are storing the images in diiffrent DB, so we are sotring the image link only in Current DB.
//? Now issue is that How will user update/upload the image in current DB?
//? first user upload image in image DB then we will get the image link from image DB(How ?) and store it in current DB.


    @Column(nullable = false)
    private Boolean primaryImage = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 1;

    @Column(nullable = false)
    private String altText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}