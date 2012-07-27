package org.xpie.platform;

import java.io.IOException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Principal;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

public class BasicHttpContext implements HttpContext {

	private static Logger logger = Logger.getLogger(BasicHttpContext.class
			.getName());

	private Bundle bundle = null;

	public BasicHttpContext(Bundle bundle) {
		this.bundle = bundle;
	}

	
	private void dispatchDenied(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("/_sys/deny.jsp");
		rd.include(request, response);
	}
	private boolean isLoginRequest(String path)  {
		boolean yes = false;
		
		if (path!=null&&path.endsWith("x_security_check"))
			yes = true;
		return yes;
	}
	private boolean isAnonymousAllowed(HttpServletRequest request){
		boolean is=false;
		return is;
	}
	@Override
	public boolean handleSecurity(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		logger.entering(this.getClass().getName(), "handleSecurity");
		boolean checked = true;
		/*
		if(isAnonymousAllowed(request))return true;
		
		
		boolean authenticated = false;
		

		AccessControlContext acc=AccessController.getContext();
		Subject subject=Subject.getSubject(acc);
		
		if(subject!=null){
			
			Set<Principal> principals=subject.getPrincipals();
			Iterator iter=principals.iterator();
			while(iter.hasNext()){
				Principal p=(Principal)iter.next();
				logger.fine("subject name:" + p.getName());
			}
		}else{
			logger.fine("subject is null.");
		}
		HttpSession sess = request.getSession();
		if (sess != null) {
			Object o = sess.getAttribute("x_authenticated");
			if (o != null)
				authenticated = true;
		}
		
		logger.fine("authenticated:[" + authenticated+"].");
		
		try {
			String pathInfo=request.getPathInfo();
			if (!authenticated && !isLoginRequest(pathInfo)) {
				checked = false;
				dispatchLogin(request, response);
			}else{
				if(!isLoginRequest(pathInfo)){
					
					ResourcePermission perm=new ResourcePermission(pathInfo,request.getMethod());
					try{
						AccessController.getContext().checkPermission(perm);
						checked=true;
					}catch(AccessControlException e){
						e.printStackTrace();
						checked=false;
						dispatchDenied(request,response);
					}
					
				}else{
					checked=true;
				}
			}
		} catch (ServletException e) {
			e.printStackTrace();
			checked=false;
		}
		*/
		logger.exiting(this.getClass().getName(), "handleSecurity", checked);
		return checked;
	}

	@Override
	public URL getResource(String name) {
		logger.entering(this.getClass().getName(), "getResource");
		return bundle.getResource(name);
	}

	@Override
	public String getMimeType(String name) {
		logger.entering(this.getClass().getName(), "getMineType");
		return null;
	}

}
