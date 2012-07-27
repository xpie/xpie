<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@page import="org.xpie.platform.PlatformException"%>
<%@page import="org.xpie.platform.ResourceContext"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>System Error!</title>
</head>
<body>
<h1>System Error!!</h1>
<hr></hr>
<p>
<%out.println(request.getRequestURI());%>
<hr/>
${header["user-agent"]}
</p>
<p>
<% 
ResourceContext ctx=(ResourceContext)request.getAttribute(ResourceContext.KEY_RESOURCECONTEXT);
PlatformException e=ctx.getException();
if(e!=null){
	out.println(e.getMessage());
}
%>
</p>
</body>
</html>