/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dht.service;

import com.dht.pojo.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Admin
 */
public class ProductTester {
    private static Connection conn;
    
    @BeforeAll
    public static void setUpClass() {
        try {
            conn = JdbcUitls.getConn();
        } catch (SQLException ex) {
            Logger.getLogger(ProductTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @AfterAll
    public static void tearDownClass() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(ProductTester.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    @Test
    public void testWithKeyWord() {
        try {
            ProductService s = new ProductService(conn);
            List<Product> products = s.getProducts("iphone");
            
            products.forEach(p -> {
                Assertions.assertTrue(p.getName().toLowerCase().contains("iphone"));
            });
        } catch (SQLException ex) {
            Logger.getLogger(ProductTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    @Tag("critical")
    public void testWithUnknownKeyWord() {
        try {
            ProductService s = new ProductService(conn);
            List<Product> products = s.getProducts("jhwgejhqsagdjhsgdjhsa232432");
            
            Assertions.assertEquals(0, products.size());
        } catch (SQLException ex) {
            Logger.getLogger(ProductTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testException() {
        Assertions.assertThrows(SQLDataException.class, () -> {
            ProductService s = new ProductService(conn);
            List<Product> products = s.getProducts(null);
        });
    }
    
    @Test
    public void testTimeout() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            ProductService s = new ProductService(conn);
            List<Product> products = s.getProducts("");
        });
    }
    
    @Test
    @DisplayName("Kiem thu them sp voi name = null")
    @Tag("critical")
    public void tesAddProductWithNameNull() throws SQLException {
        ProductService s = new ProductService(conn);
        Product p = new Product();
        p.setName(null);
        p.setPrice(new BigDecimal(999));
        p.setCategoryId(1);
        
        Assertions.assertFalse(s.addProduct(p));
    }
    
    @Test
    @Tag("critical")
    public void tesAddProductWithInvalidCate() throws SQLException {
        ProductService s = new ProductService(conn);
        Product p = new Product();
        p.setName("TEST PHONE");
        p.setPrice(new BigDecimal(999));
        p.setCategoryId(9999);
        
        Assertions.assertFalse(s.addProduct(p));
    }
    
    @Test
    @Tag("critical")
    public void tesAddProduct() throws SQLException {
        ProductService s = new ProductService(conn);
        Product p = new Product();
        p.setName("TEST PHONE");
        p.setPrice(new BigDecimal(999));
        p.setCategoryId(1);
        
        Assertions.assertTrue(s.addProduct(p));
        
        //
    }
    
    @ParameterizedTest
    @CsvSource({"P1,999,1,true", "P2,999,99,false", ",99,1,false"})
    public void tesAddProductParams(String name, BigDecimal price, int cateId, boolean expected) throws SQLException {
        ProductService s = new ProductService(conn);
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setCategoryId(cateId);
        
        Assertions.assertEquals(expected, s.addProduct(p));
//        Assertions.assertTrue(s.addProduct(p));
        
        //
    }
}
