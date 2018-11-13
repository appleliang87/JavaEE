package workshop02.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "discount_code")
public class DiscountCode {
    
    public enum Code { H, L, M, N }
    
    @Id @Column(name = "discount_code")
    @Enumerated(EnumType.STRING)
    private Code discountCode;
    
    private Float rate;
    
    @OneToMany(mappedBy = "discountCode")
    private List<Customer> customers;

    public Code getDiscountCode() {
        return discountCode;
    }
    public void setDiscountCode(Code discountCode) {
        this.discountCode = discountCode;
    }

    public Float getRate() {
        return rate;
    }
    public void setRate(Float rate) {
        this.rate = rate;
    }

    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
    
}
