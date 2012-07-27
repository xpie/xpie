<?xml version="1.0" encoding="ISO-8859-1" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
	<jsp:directive.page contentType="text/html; charset=ISO-8859-1" 
		pageEncoding="ISO-8859-1" session="false"/>
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Platform Login</title>
</head>
<body>
<form action="x_security_check" method="post">
<input type="hidden" name="x_return_url" value="${pageContext.request.pathInfo}"/>
<input type="hidden" name="x_return_url_q" value="${pageContext.request.queryString }"/>
<LABEL>Name:<input type="text" name="x_user_name" /></LABEL>
<LABEL>Password:<input type="password" name="x_password"/></LABEL>
<INPUT  type="submit" name="login_btn" value="Login"/>
</form>
</body>
</html>
</jsp:root>