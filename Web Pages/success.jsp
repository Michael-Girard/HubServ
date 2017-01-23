<%-- 
    Document   : success
    Created on : May 14, 2015, 3:10:40 PM
    Author     : Michael
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="master.css">
        <style>
            .container{
                position: absolute;
                left: 30%;
                top: 45%;
                left: calc(50% - 200px);
                top: calc(50% - 28px);
            }
        </style>
        <c:if test="${successmessage == null}">
            <c:redirect url="login.jsp"/>
        </c:if>
        <meta http-equiv="Refresh" content="3;url=${successredirect}">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Success</title>
    </head>
    <body onload="<%if (request.getSession().getAttribute("usertype") == null){response.sendRedirect("login.jsp");} %>">
        <div class="container">
            <div class="header">
                <h1>${successmessage}</h1>
            </div>
        </div>
    </body>
</html>
