<%-- 
    Document   : error
    Created on : 14 Mar, 2021, 12:48:43 PM
    Author     : sourabh
--%>

<%@page  isErrorPage="true" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quiz App - Error Page</title>
    </head>
    <body>
        <h1>Sorry!!! Some Exception Raised in the App</h1>
        <hr/>
        Details are: <br/>
        <%--Using JSP Expression tag to display execption--%>
        <%=exception.toString()%>
        <hr/>
        Stack Trace are <br/>
        <% exception.printStackTrace();%>  <%--Scriplet--%>
    </body>
</html>
