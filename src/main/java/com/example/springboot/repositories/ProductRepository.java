package com.example.springboot.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springboot.modelo.ProductModel;

@Repository /*Usado para dizer q esse é um bean, gerenciado por ele com a anotation Repositorye*/
public interface ProductRepository extends JpaRepository<ProductModel, UUID>{

}
