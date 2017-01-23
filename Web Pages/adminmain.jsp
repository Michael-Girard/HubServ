<%-- 
    Document   : adminmain
    Created on : May 13, 2015, 9:45:25 PM
    Author     : Michael
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="master.css">
        <style>
            div.container{
                position: absolute;
                left: 30%;
                top: 24%;
                left: calc(50% - 300px);
                top: calc(50% - 103px);
            }
            div.header{
                min-width: calc(100% - 6px);
            }
            form.section{
                border: double;
                max-width: 300px;
                min-width: 300px;
                height: 150px;
                background-color: rgb(211, 227, 229);
                float:left;
            }
            form img{
                margin: 10px 0 0 10px;
            }
            form img.singleimage{
                margin: 10px 0 0 55px;
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
    <body onload="<%String usertype = request.getSession().getAttribute("usertype").toString(); 
                    if (usertype == null || !usertype.equals("admin")){response.sendRedirect("login.jsp");} %>">
        <div class="container">
            <div class="header">
                <h1>Main Menu</h1>
            </div>
            <form class="section" action="HubServlet" method="POST">
                <input type="hidden" name="page" value="main"/>
                <input type="hidden" name="selection" value="document"/>
                <img src="Images/OneDevice.png" class="singleimage" width="100" height="130"/>
                <input type="submit" value="Documentation"/>
            </form>
            <form class="section" action="HubServlet" method="POST">
                <input type="hidden" name="page" value="adminmain"/>
                <input type="hidden" name="selection" value="createuser"/>
                <img src="Images/AddUser.png" class="singleimage" width="100" height="130"/>
                <input type="submit" value="Add User"/>
            </form>
        </div>
    </body>
</html>
