package workshop04.rest;

import java.sql.SQLException;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import workshop04.model.Customer;
import workshop04.business.CustomerBean;


public class FindByCustomerIdRunnable implements Runnable{

    private Integer custId;
    private CustomerBean customerBean;
    private AsyncResponse asyncResp;
    
    public FindByCustomerIdRunnable(Integer cid, CustomerBean cb, AsyncResponse ar) {
        custId = cid;
        customerBean = cb;
        asyncResp = ar;
    }
    
    @Override
    public void run() {
       
        Optional<Customer> opt = null;
        
        try{
            opt = customerBean.findByCustomerId(custId);
        }catch (SQLException ex){
            //("error": "error message")
            JsonObject error = Json.createObjectBuilder()
                    .add("error", ex.getMessage())
                    .build();
            //server error
            asyncResp.resume(Response.serverError().entity(error).build());
            return;
        }
        
        //return 404 Not Found if the customer does not exist
        if(!opt.isPresent()){
            asyncResp.resume(Response.status(Response.Status.NOT_FOUND).build());
            return;
        }
        
        //return the data as Json
        asyncResp.resume(Response.ok(opt.get().toJson()).build());
        System.out.println(">>> resuming request");
        
    }
    
}
