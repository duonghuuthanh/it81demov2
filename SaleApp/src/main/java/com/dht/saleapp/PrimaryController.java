package com.dht.saleapp;

import com.dht.pojo.Category;
import com.dht.pojo.Product;
import com.dht.service.CategoryService;
import com.dht.service.JdbcUitls;
import com.dht.service.ProductService;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrimaryController implements Initializable {

    @FXML
    private ComboBox<Category> cbCates;
    @FXML
    private TableView<Product> tbProducts;
    @FXML
    private TextField txtKeywords;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection conn = JdbcUitls.getConn();
            CategoryService s = new CategoryService(conn);

            this.cbCates.setItems(FXCollections.observableList(s.getCates()));

            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadTable();
        loadData("");

        this.txtKeywords.textProperty().addListener((obj) -> {
            loadData(this.txtKeywords.getText());
        });
        
        this.tbProducts.setRowFactory(obj -> {
            TableRow row = new TableRow();
            
            row.setOnMouseClicked(evt -> {
                try {
                    Product p = this.tbProducts.getSelectionModel().getSelectedItem();
                    txtName.setText(p.getName());
                    txtPrice.setText(p.getPrice().toString());
                    
                    
                    Connection conn = JdbcUitls.getConn();
                    CategoryService s = new CategoryService(conn);
                    Category c = s.getCategoryById(p.getCategoryId());
                    
                    this.cbCates.getSelectionModel().select(c);
                    
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            return row;
        });
    }
    
    public void addHandler(ActionEvent evt) {
        try {
            Product p = new Product();
            p.setName(txtName.getText());
            p.setPrice(new BigDecimal(txtPrice.getText()));
            p.setCategoryId(this.cbCates.getSelectionModel().getSelectedItem().getId());
            
            Connection conn = JdbcUitls.getConn();
            
            ProductService s = new ProductService(conn);
            if (s.addProduct(p)) {
                Utils.getBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                loadData("");
            } else {
                Utils.getBox("FAILED", Alert.AlertType.ERROR).show();
            }
            
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadData(String kw) {
        try {
            Connection conn = JdbcUitls.getConn();
            ProductService s = new ProductService(conn);

            tbProducts.setItems(FXCollections.observableList(s.getProducts(kw)));

            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTable() {
        TableColumn colId = new TableColumn("Mã SP");
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = new TableColumn("Tên SP");
        colName.setCellValueFactory(new PropertyValueFactory("name"));

        TableColumn colDes = new TableColumn("Mô tả SP");
        colDes.setCellValueFactory(new PropertyValueFactory("description"));

        TableColumn colPrice = new TableColumn("Gía SP");
        colPrice.setCellValueFactory(new PropertyValueFactory("price"));

        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(obj -> {
            Button btn = new Button("Xóa");

            btn.setOnAction((evt) -> {
                Utils.getBox("Ban chac chan xoa khong?", Alert.AlertType.CONFIRMATION)
                        .showAndWait().ifPresent(bt -> {
                            if (bt == ButtonType.OK) {
                                try {
                                    TableCell cell = (TableCell) ((Button) evt.getSource()).getParent();
                                    Product p = (Product) cell.getTableRow().getItem();

                                    Connection conn = JdbcUitls.getConn();
                                    ProductService s = new ProductService(conn);
                                    if (s.deleteProduct(p.getId()) == true) {
                                        Utils.getBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                                        loadData("");
                                    } else {
                                        Utils.getBox("FAILED", Alert.AlertType.ERROR).show();
                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });

            });

            TableCell cell = new TableCell();
            cell.setGraphic(btn);
            return cell;
        });

        this.tbProducts.getColumns().addAll(colId, colName, colDes, colPrice, colAction);
    }

//    @FXML
//    private void switchToSecondary() throws IOException {
//        App.setRoot("secondary");
//    }
}
