package workshop02.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import workshop02.business.CustomerBean;
import workshop02.business.CustomerException;
import workshop02.model.Customer;
import workshop02.model.DiscountCode;

@WebServlet(urlPatterns = ( "/customer" ))
public class CustomerServlet extends HttpServlet {
    
    @EJB private CustomerBean customerBean;
    
    private Customer createCustomer(HttpServletRequest req){
        Customer customer = new Customer();
        customer.setCustomerId(Integer.parseInt(req.getParameter("customerId")));
        customer.setName(req.getParameter("name"));
        customer.setAddressline1(req.getParameter("addressline1"));
        customer.setAddressline2(req.getParameter("addressline2"));
        customer.setCity(req.getParameter("city"));
        customer.setState(req.getParameter("state"));
        customer.setZip(req.getParameter("zip"));
        customer.setPhone(req.getParameter("phone"));
        customer.setFax(req.getParameter("fax"));
        customer.setEmail(req.getParameter("email"));
        
        DiscountCode dc = new DiscountCode();
        dc.setDiscountCode(DiscountCode.Code.valueOf(req.getParameter("discountCode")));
        customer.setDiscountCode(dc);
        
        customer.setCreditLimit(Integer.parseInt(req.getParameter("creditLimit")));
        
        return customer;
    }

    //GET /workshop02/customer
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Integer customerId = Integer.parseInt(req.getParameter("customerId"));
        
        //We get an optional object which indicate that the object may not exists
        Optional<Customer> opt = customerBean.findByCustomerId(customerId);
        
        //Check if we have a result
        if(!opt.isPresent()){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType(MediaType.TEXT_PLAIN);
            try(PrintWriter pw = resp.getWriter()){
                pw.printf("Customer id %d does not exists\n", customerId);
            }
            return;
        }
        
        //Get the result
        Customer customer = opt.get();
        
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(MediaType.APPLICATION_JSON);
        try(PrintWriter pw = resp.getWriter()){
            pw.print(customer.toJson());
        }
        
    }

    //POST /workshop02/customer
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Customer customer = createCustomer(req);
        
        Optional<Customer> opt = customerBean.findByCustomerId(customer.getCustomerId());
        if(opt.isPresent()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType(MediaType.TEXT_PLAIN);
            try(PrintWriter pw = resp.getWriter()){
                pw.printf("Customer id %d exists", customer.getCustomerId());
            }
            return;
        }
        
        try{
            customerBean.addNewCustomer(customer);
        }catch(CustomerException ex){
            ex.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType(MediaType.TEXT_PLAIN);
            try(PrintWriter pw = resp.getWriter()){
                pw.print(ex.getMessage());
            }
            return;
        }
        
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
        resp.setContentType(MediaType.TEXT_PLAIN);
        try(PrintWriter pw = resp.getWriter()){
                pw.print("Customer Created");
        }
    
    }
    
}
