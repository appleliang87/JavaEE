package workshop02.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/purchaseOrder/count")
public class CountPurchaseOrderServlet extends HttpServlet {
    
    //Get a reference to Entity Manager
    @PersistenceContext private EntityManager em;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        TypedQuery<Object[]> query = em.createNamedQuery(
                "ObjectArray.countPurchaseOrderByCustomers", Object[].class);
        
        List<Object[]> result = query.getResultList();
        
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/csv");
        
        try (PrintWriter pw = resp.getWriter()) {
            pw.println("customerId, orderCount");
            for (Object[] row: result)
                pw.println(row[0] + "," + row[1]);
        }
    }
    
}
