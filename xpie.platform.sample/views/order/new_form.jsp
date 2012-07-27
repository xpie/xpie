<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
	<jsp:directive.page contentType="text/html; charset=utf-8" 
		pageEncoding="UTF-8" session="false"/>
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Order Form</title>
</head>
<body>
<h1>表单</h1>
<hr/>
<form method="post" action="/sample/order" enctype="multipart/form-data">
<ul>
<li>Name:<input name="order[name]" type="text"/></li>
<li>File:<input name="order[file]" type="file"/></li>
</ul>
<br/>
<input type="submit" name="submit" value="Submit"/>
</form>
</body>
</html>
</jsp:root>