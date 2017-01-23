<%-- 
    Document   : documentation
    Created on : May 15, 2015, 4:21:12 PM
    Author     : Michael
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false" %>
<!DOCTYPE html>
<html>
    <%if (request.getSession().getAttribute("usertype") == null){response.sendRedirect("login.jsp");}%>
    ${pagecontent}
</html>
