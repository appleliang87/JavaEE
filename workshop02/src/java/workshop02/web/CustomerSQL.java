package workshop02.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

@WebServlet(urlPatterns = ( "/customer-sql" ))
public class CustomerSQL extends HttpServlet {
    
    //Get a reference to the SamplePool jdbc/sample
    @Resource(lookup = "jdbc/sample")
    private DataSource sampleDB;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        //Read the custId from field
        Integer custId = Integer.parseInt(req.getParameter("custId"));
        
        //Get a Connection
        try(Connection conn = sampleDB.getConnection()){
            
            //Create a PrepareStatement
            PreparedStatement ps = conn.prepareStatement("select * from customer where customer_id = ?");
            //Set the parameter
            ps.setInt(1, custId);
            //Execute the query
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                //We found a record
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType(MediaType.TEXT_PLAIN);
                try(PrintWriter pw = resp.getWriter()){
                    pw.printf("id: %d, name: %s, address: %s, phone: %s, email: %s\n",
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("addressline1"),
                            rs.getString("phone"),
                            rs.getString("email"));
                }
            }else{
                //Customer id is not found
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType(MediaType.TEXT_PLAIN);
                try(PrintWriter pw = resp.getWriter()){
                    pw.printf("Customer with Id %d does not exists", custId);
                }
            }
            
        }catch(SQLException ex){
            log(ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType(MediaType.TEXT_PLAIN);
            try(PrintWriter pw = resp.getWriter()){
                pw.println(ex.getMessage());
            }
        }
    }
    
    
}
