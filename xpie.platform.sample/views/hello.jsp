<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Hello JSP</title>
</head>
<body>
Add content here.
<br/>
<hr/>
<%
long abc=System.currentTimeMillis();
out.println("aaaa:"+abc);
%>
</body>
</html>