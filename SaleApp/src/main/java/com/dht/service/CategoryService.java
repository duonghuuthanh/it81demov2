/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dht.service;

import com.dht.pojo.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class CategoryService {
    private Connection conn;
    
    public CategoryService(Connection conn) {
        this.conn = conn;
    }
    
    public List<Category> getCates() throws SQLException {
        Statement stm = this.conn.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM category");
        
        List<Category> cates = new ArrayList<>();
        while (rs.next()) {
            Category c = new Category();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            c.setDescription(rs.getString("description"));
            
            cates.add(c);
        }
        
        return cates;
    }
    
    public Category getCategoryById(int productId) throws SQLException {
        String q = "SELECT * FROM category WHERE id=?";
        PreparedStatement stm = this.conn.prepareStatement(q);
        stm.setInt(1, productId);
        
        ResultSet rs = stm.executeQuery();
        
        Category c = null;
        
        while (rs.next()) {
            c = new Category();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            break;
        }
        
        return c;
    }
}
