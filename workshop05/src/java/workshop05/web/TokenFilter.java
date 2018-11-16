package workshop05.web;

import workshop05.KeyManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

@WebFilter(urlPatterns = { "/api/*" }, 
        dispatcherTypes = DispatcherType.REQUEST)
public class TokenFilter implements Filter {
    
    @Inject private KeyManager keyMgr;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;
        
        //Check if the request has Authorization header
        String authorization = req.getHeader("Authorization");
        if (null == authorization) {
            authorization = req.getParameter("token");
            if ((null == authorization) || (authorization.length() <= 0)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType(MediaType.TEXT_PLAIN);
                try (PrintWriter pw = resp.getWriter()) {
                    pw.print("Not authorized");
                }
                return;
            }
            authorization = "Bearer " + authorization;
        }
        
        //Extract and verify token
        String token = authorization.substring("Bearer ".length());     
        Jws<Claims> claims;
        try {
            JwtParser parser = Jwts.parser().setSigningKey(keyMgr.getKey());
            claims = parser.parseClaimsJws(token);
        } catch (ExpiredJwtException | IllegalArgumentException 
                | MalformedJwtException | SignatureException
                | UnsupportedJwtException ex) {
            System.out.println("Parse error: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType(MediaType.TEXT_PLAIN);
            try (PrintWriter pw = resp.getWriter()) {
                pw.print("Not authorized");
            }
            return;            
        }
        
        System.out.println("Header: " + claims.getHeader());
        System.out.println("Claims: " + claims.getBody());
        
        //Allow the request to pass
        chain.doFilter(request, response);
    }
    
}