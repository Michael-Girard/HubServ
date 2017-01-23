<%-- 
    Document   : main
    Created on : May 13, 2015, 9:45:16 PM
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
                left: 30%;
                top: 24%;
                left: calc(50% - 200px);
                top: calc(50% - 103px);
            }
            
            div.header{
                min-width: calc(100% - 6px);
            }
            
            form.section{
                border: double;
                max-width: 400px;
                min-width: 400px;
                height: 150px;
                background-color: rgb(211, 227, 229);
            }
            form img{
                margin: 10px 0 0 10px;
            }
            form img.singleimage{
                margin: 10px 0 0 45px;
            }
            form input{
                float: right;
                height: 50px;
                min-width: 100px;
                max-width: 100px;
                margin: 50px 25px auto auto;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Main Menu</title>
    </head>
    <body onload="<%if (request.getSession().getAttribute("usertype") == null){response.sendRedirect("login.jsp");} %>">
        <div class="container">
            <div class="header">
                <h1>Main Menu</h1>
            </div>
            <form class="section" action="HubServlet" method="POST">
                <input type="hidden" name="page" value="main"/>
                <input type="hidden" name="selection" value="document"/>
                <img src="Images/TwoDevices.png" class="singleimage" width="175" height="130"/>
                <input type="submit" value="Document"/>
            </form>
            <p>${loginmessage}</p>
        </div>
    </body>
</html>
