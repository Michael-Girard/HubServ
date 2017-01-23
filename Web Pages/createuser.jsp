<%-- 
    Document   : createuser
    Created on : May 13, 2015, 10:40:36 PM
    Author     : Michael
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="master.css">
        <style>
            .container{
                position: absolute;
                left: 36%;
                top: 38%;
                left: calc(50% - 200px);
                top: calc(50% - 100px);
            }
            form{
                display:block;
                overflow:hidden;
                min-width: 400px;
                max-width: 400px;
                background-color: rgb(211, 227, 229);
                border:double;
                text-align:center;
                padding:0;
            }
            label{
                margin: auto auto auto 10px;
            }
            p.error{
                margin: -10px auto 5px auto;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create User</title>
    </head>
    <body onload="<%String usertype = request.getSession().getAttribute("usertype").toString(); 
                    if (usertype == null || !usertype.equals("admin")){response.sendRedirect("login.jsp");} %>">
        <div class="container">
            <div class="header">
                <h1>Create User</h1>
            </div>
            <form action="HubServlet" method="POST">
                <input type="hidden" name="page" value="createuser"/>
                <p>
                    Username: <input type="text" name="tbUsername" value="${sentusername}"/>
                    <label for="cbAdmin">Admin?</label> <input type="checkbox" id="cbAdmin" name="cbAdmin"/>
                </p>
                <p>Password: <input type="password" name="tbPassword" size="35" value="${sentpassword}"/></p>
                <p>
                    <button type="reset">Clear</button>
                    <input type="submit" value="Submit"/>
                </p>
                <p class="error">${createusermessage}</p>
            </form>
        </div>
    </body>
</html>
