package workshop02.model;

import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "purchase_order")
@NamedQueries({
    @NamedQuery(name = "PurchaseOrder.findByCustomerId",
            query = "select po from PurchaseOrder po join po.customer c where c.customerId = :custId"
    ),
    @NamedQuery(name = "PurchaseOrder.findByOrderNum",
            query = "select po from PurchaseOrder po where po.orderNum = :orderNum"
    ),
    @NamedQuery(
        name = "ObjectArray.countPurchaseOrderByCustomers",       
        query = "select c.customerId, COUNT(po) from PurchaseOrder po join po.customer c group by c.customerId"
    )
})
public class PurchaseOrder {
    
    @Id @Column(name = "order_num")
    private Integer orderNum;
    
    @Column(name = "product_id")
    private Integer productId;
    
    private Integer quantity;
    
    @Column(name = "shipping_cost")
    private Float shippingCost;
    
    @Column(name = "shipping_date")
    @Temporal(TemporalType.DATE)
    private Date shippingDate;
    
    @Column(name = "sales_date")
    @Temporal(TemporalType.DATE)
    private Date salesDate;
    
    @Column(name = "freight_company")
    private String freightCompany;
    
    @ManyToOne
    @JoinColumn(name = "customer_id",
            referencedColumnName = "customer_id")
    private Customer customer;

    public Integer getOrderNum() {
        return orderNum;
    }
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getShippingCost() {
        return shippingCost;
    }
    public void setShippingCost(Float shippingCost) {
        this.shippingCost = shippingCost;
    }

    public Date getShippingDate() {
        return shippingDate;
    }
    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public Date getSalesDate() {
        return salesDate;
    }
    public void setSalesDate(Date salesDate) {
        this.salesDate = salesDate;
    }

    public String getFreightCompany() {
        return freightCompany;
    }
    public void setFreightCompany(String freightCompany) {
        this.freightCompany = freightCompany;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    } 
    
    public JsonObject toJson(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("orderNum", orderNum)
                .add("productId", productId)
                .add("quantity", quantity)
                .add("shippingCost",shippingCost)
                .add("shippingDate", shippingDate.toString())
                .add("salesDate", salesDate.toString())
                .add("freightCompany", freightCompany);     
        return (builder.build());
    }
}
