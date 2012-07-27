package org.xpie.platform.sample;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletOutputStream out = resp.getOutputStream();
		out.println("<h1>Hello World!!</h1>");
		out.println("<p>URI:" + req.getRequestURI() + ".</p>");
		out.println("<p>QStr:" + req.getQueryString() + ".</p>");
		out.println("<p>ctx path:" + req.getContextPath() + ".</p>");
		out.flush();
		out.close();

	}

}
