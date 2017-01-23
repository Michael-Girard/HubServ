<%-- 
    Document   : login
    Created on : May 13, 2015, 8:45:54 PM
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
                text-align: center;
                padding:0;
            }
            
            button{
                margin: auto auto 5px auto;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>Login</h1>
            </div>
            <form action="HubServlet" method="POST">
                <input type="hidden" name="page" value="login"/>
                <p>Username: <input type="text" name="tbUsername" value="${username}"/></p>
                <p>Password: <input type="password" name="tbPassword"/></p>
                
                <button type="reset">Clear</button>
                <input type="submit" value="Submit"/>
            </form>
        </div>
    </body>
</html>
