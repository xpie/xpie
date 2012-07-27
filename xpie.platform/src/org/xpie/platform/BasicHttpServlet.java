package org.xpie.platform;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.PropertyPermission;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BasicHttpServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 838066637075913628L;
	
	public static final String KEY_LAUNCHACTIVATOR = "org.xpie.platform.launchactivator";
	public static final String KEY_RESOURCECONTEXT = "org.xpie.platform.resourcecontext";
	public static final String KEY_SERVLETALIAS = "org.xpie.platform.servletalias";
	public static final String KEY_SECURITY_CHECK = "x_security_check";

	private static Logger logger = Logger.getLogger(BasicHttpServlet.class
			.getName());

	private ServletConfig mConfig = null;

	private class ResourceServletAction implements PrivilegedAction {

		private ResourceContext ctx = null;

		public ResourceServletAction(ResourceContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public Object run() {
			ctx.processRequest();
			return null;
		}

	}


	private void process(HttpServletRequest req, HttpServletResponse resp) {
		ResourceContext ctx = (ResourceContext) req
				.getAttribute(KEY_RESOURCECONTEXT);

		PropertyPermission pp = new PropertyPermission("java.home", "read");
		AccessController.checkPermission(pp);
		
		Subject s = ctx.getSubject();
		logger.fine("subject is " + s);
		if (s != null) {
			s.doAs(s, new ResourceServletAction(ctx));
		} else {
			ctx.processRequest();
		}
	}
	
	/*
	 * private void process(HttpServletRequest req, HttpServletResponse resp){
		ResourceContext ctx = (ResourceContext) req
				.getAttribute(KEY_RESOURCECONTEXT);
		ctx.processRequest();
	}
	*/
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.process(req, resp);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute(KEY_LAUNCHACTIVATOR,
				mConfig.getInitParameter(KEY_LAUNCHACTIVATOR));
		req.setAttribute(KEY_SERVLETALIAS,
				mConfig.getInitParameter(KEY_SERVLETALIAS));
		ResourceContext ctx = ResourceContext.createResourceContext(req, resp);
		req.setAttribute(KEY_RESOURCECONTEXT, ctx);
		super.service(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.entering(this.getClass().getName(), "init(config)");
		mConfig = config;
		String activatorName = mConfig.getInitParameter(KEY_LAUNCHACTIVATOR);
		logger.fine("org.xpie.platform.launchactivator:" + activatorName);
		
		super.init(config);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		logger.exiting(this.getClass().getName(), "destroy");
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		logger.entering(this.getClass().getName(), "init");
		super.init();
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.entering(this.getClass().getName(), "doPost");

		this.process(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logger.entering(this.getClass().getName(), "doPut");
		this.process(req, resp);
	}

}
