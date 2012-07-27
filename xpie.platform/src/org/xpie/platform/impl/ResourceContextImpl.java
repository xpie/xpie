package org.xpie.platform.impl;

import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


import org.xpie.platform.BasicCallbackHandler;
import org.xpie.platform.BasicHttpServlet;
import org.xpie.platform.PlatformException;
import org.xpie.platform.ResourceContext;
import org.xpie.platform.ResourceController;
import org.xpie.platform.jaas.XPiePrincipal;

public class ResourceContextImpl extends ResourceContext {

	private static Logger logger = Logger.getLogger(ResourceContextImpl.class
			.getName());

	public static final String KEY_SUBJECT="org.xpie.platform.subject";
	
	private HttpServletRequest mReq = null;
	private HttpServletResponse mResp = null;
	private Map mData = null;
	private String mNouns = null;
	private String mVerb = null;
	private String mFormat = null;
	private String mViewId = null;
	private String mActionId = null;
	private String[] mQualifiers = null;
	private String servletAlias = "";
	private String mQueryString = "";

	private Subject currentUser=null;
	
	private PlatformException mException = null;

	private String idn = "";// idn=information domain name

	private String mContentType = "";

	private Collection<Part> mParts = null;

	
	public ResourceContextImpl(HttpServletRequest req, HttpServletResponse resp) {
		mReq = req;
		mResp = resp;
		servletAlias = (String) mReq
				.getAttribute(BasicHttpServlet.KEY_SERVLETALIAS);
		_parse();
		_initializeSecurityObjects();
	}
	private synchronized void _initializeSecurityObjects(){
		HttpSession session=mReq.getSession();
		Subject s=(Subject)session.getAttribute(KEY_SUBJECT);
		if(s==null){
			s=new Subject();
			s.getPrincipals().add(new XPiePrincipal(XPiePrincipal.ANONYMOUS));
			session.setAttribute(KEY_SUBJECT, s);
		}
		currentUser=s;
	}
	private void _parse() {
		prepareParse();

		mContentType = mReq.getContentType();
		logger.fine("Content-Type: [" + mContentType + "].");
		if (mContentType != null
				&& mContentType.startsWith("multipart/form-data")) {
			try {
				mParts = mReq.getParts();
				logger.fine("Parts size: " + mParts.size());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}

		mQueryString = mReq.getQueryString();

		String uri = mReq.getRequestURI();
		mFormat = parseFormat();
		logger.fine("set mFormat to [" + mFormat + "].");
		mVerb = mReq.getMethod();
		logger.fine("set mVerb to [" + mVerb + "]");
		mData = mReq.getParameterMap();
		logger.fine("mData size is " + mData.size() + ".");
		logger.finest("mData:" + mData);
		mNouns = parseNouns();
		logger.fine("set mNouns to [" + mNouns + "].");

		_registerController();

		postParse();
	}

	private void _registerController() {
		Class clz = _findControllerClass();
		this.registerController(idn, mNouns, clz, false);
	}

	private Class _findControllerClass() {
		Class clz = ResourceController.class;
		String launchActivator = (String) mReq
				.getAttribute(BasicHttpServlet.KEY_LAUNCHACTIVATOR);
		logger.finer("launchActivator: [" + launchActivator + "].");
		String packageName = launchActivator.substring(0,
				launchActivator.lastIndexOf("."));
		logger.finer("Base package name: " + packageName + "].");
		String ctrlClassName = mNouns.substring(0, 1).toUpperCase()
				+ mNouns.substring(1) + "Controller";
		String c1 = packageName + ".controllers." + ctrlClassName;
		String c2 = packageName + "." + ctrlClassName;
		try {
			clz = Class.forName(c1);
		} catch (ClassNotFoundException e) {
			logger.finer("no class named [" + c1 + "].");
			try {
				clz = Class.forName(c2);
			} catch (ClassNotFoundException e1) {
				logger.finer("no class named [" + c2 + "].");
			}
		}
		logger.finer("clz:" + clz.getName());
		return clz;
	}

	private String parseNouns() {
		String nouns = "default";
		String uri = getHttpRequest().getRequestURI();
		logger.finer("parse target uri: " + uri);
		int partIndex = 0;
		if (uri != null && !uri.equals("")) {
			if (uri.startsWith("/")) {
				String aUri = uri.substring(1);
				String[] parts = aUri.split("/");
				if (parts.length > 0) {
					idn = parts[0];
					logger.finer("parsed idn is [" + idn + "].");
					if (parts.length > 1) {
						// http://localhost/sample/1.html?a=bc&b=cd, in this
						// case nouns will be "default"
						if (parts[1].indexOf(".") < 0
								&& !isViewIdReserved(parts[1])) {
							nouns = parts[1];
							mQualifiers = new String[parts.length - 2];
							for (int i = 2; i < parts.length; i++) {
								mQualifiers[i - 2] = parts[i];
							}
						} else {
							mQualifiers = new String[1];
							mQualifiers[0] = parts[1];
						}
					} else {
						mQualifiers = new String[0];
					}
					parseQualifiers();
				}
			}
		}
		logger.finer("idn is [" + idn + "], nouns is [" + nouns
				+ "], qualifiers size is " + mQualifiers.length + ".");
		return nouns;
	}

	private boolean parseQualifiers() {
		String viewId = "";
		String actionId = "";
		boolean modified = false;
		if (mQualifiers.length > 0) {
			String lastQualifier = mQualifiers[mQualifiers.length - 1];
			if (lastQualifier.equals("new") || lastQualifier.equals("add")) {
				viewId = "new_form";
			} else if (lastQualifier.equals("search")
					|| lastQualifier.equals("query")) {
				viewId = "search_form";
			} else {
				int i = lastQualifier.lastIndexOf(".action");
				if (i > 0) {
					actionId = lastQualifier.substring(0, i);
				}
			}
		}
		mViewId = viewId;
		mActionId = actionId;
		return modified;
	}

	protected boolean isViewIdReserved(String name) {
		boolean reserved = false;
		if (name != null) {
			if (name.equals("new") || name.equals("add"))
				reserved = true;
		}
		return reserved;
	}

	protected String parseFormat() {
		String format = "html";
		String uri = getHttpRequest().getRequestURI();
		logger.finer("parse target uri: " + uri);
		int partIndex = 0;
		partIndex = uri.lastIndexOf("/");
		if (partIndex >= 0) {
			String a = uri.substring(partIndex + 1);
			logger.finer("Last part path: " + a);
			partIndex = 0;
			partIndex = a.lastIndexOf(".");
			if (partIndex >= 0) {
				format = a.substring(partIndex + 1);
			}
		}
		logger.finer("parsed format: " + format);
		return format;
	}

	public HttpServletRequest getHttpRequest() {
		return mReq;
	}

	public HttpServletResponse getHttpResponse() {
		return mResp;
	}

	protected void prepareParse() {
	}

	protected void postParse() {
	}

	@Override
	public String getVerb() {
		return mVerb;
	}

	@Override
	public String getNouns() {
		return mNouns;
	}

	@Override
	public String getFormat() {
		return mFormat;
	}

	@Override
	public Map getData() {
		return mData;
	}

	@Override
	public String[] getQualifiers() {
		return mQualifiers;
	}

	@Override
	public Class getControllerClass() {
		return this.findController(idn, mNouns);
	}

	@Override
	public void processRequest() {
		
		if (requireLogin()) {
			dispatchLogin();
		} else {
			if (!processSysRequest()) {
				Class c = getControllerClass();
				try {
					ResourceController ctrl;
					try {
						ctrl = (ResourceController) c.newInstance();
					} catch (Throwable e) {
						throw new PlatformException(e);
					}
					ctrl.process(this);
				} catch (SecurityException e) {
					e.printStackTrace();
					dispatchDenied();
				} catch (PlatformException e) {
					e.printStackTrace();
					_handlePlatformException(e);
				}
			}
		}
	}

	private boolean requireLogin() {
		boolean require = true;
		//Subject s=Subject.getSubject(AccessController.getContext());
		//if(s!=null)require=false;
		if(isAuthenticated()){
			logger.fine("currentUser [" + currentUser+"] is authenticated.");
			return false;
		}
		String path=mReq.getPathInfo();
		if(path.endsWith(BasicHttpServlet.KEY_SECURITY_CHECK))require=false;
		
		logger.exiting(this.getClass().getName(),"requireLogin", require);
		return require;
	}

	private boolean processSysRequest() {
		boolean processed = false;
		String path = mReq.getPathInfo();
		if (path.endsWith(BasicHttpServlet.KEY_SECURITY_CHECK)) {
			processSysLogin();
			processed = true;
		} else {
			// is not System request...
		}
		return processed;
	}

	private void dispatchLogin() {
		_dispatch("/_sys/login.jsp",true);
	}

	private void dispatchDenied() {
		_dispatch("/_sys/deny.jsp",true);
	}

	private void processSysLogin() {

		Object[] objs = (Object[]) mData.get("x_user_name");
		String userName = objs.length > 0 ? (String) objs[0] : "";
		objs = (Object[]) mData.get("x_password");
		String password = objs.length > 0 ? (String) objs[0] : "";
		objs = (Object[]) mData.get("x_return_url");
		final String returnView = objs.length > 0 ? (String) objs[0] : "";
		objs = (Object[]) mData.get("x_return_url_q");
		final String returnViewQuery = objs.length > 0 ? (String) objs[0] : "";

		BasicCallbackHandler basicHandler = new BasicCallbackHandler(userName,
				password.toCharArray());

		boolean loginSucceeded = false;

		try {
			LoginContext lc = new LoginContext("XPie", basicHandler);
			lc.login();
			loginSucceeded = true;
			currentUser = lc.getSubject();
			mReq.getSession().setAttribute(KEY_SUBJECT, currentUser);
		} catch (LoginException e1) {
			String msg = e1.getMessage();
			e1.printStackTrace();
		}
		
		if (loginSucceeded) {
			if (isAuthenticated()) {
				String viewId = returnView + "?" + returnViewQuery;
				try {
					logger.fine("returnView:" + viewId);
					mResp.sendRedirect(viewId);
				} catch (IOException e) {
					throw new PlatformException(e);
				}
			} else {
				logger.fine("isAuthenticated return false.");
			}
		} else {
			// login failed for some exception.
			logger.fine("loginSucceeded is false.");
		}
	}

	@Override
	public void dispatch(String outcome) {
		_dispatch(outcome, false);
	}

	private void _handlePlatformException(PlatformException e) {
		try {
			this.mException = e;
			_dispatch("/_sys/error.jsp", true);
		} catch (PlatformException e1) {
			e1.printStackTrace();
		}
	}

	private void _dispatch(String outcome, boolean sys) {
		String name = this.getNouns() + "/" + outcome;
		String resPath = servletAlias + "/jsp/" + name + ".jsp";
		if (sys)
			resPath = outcome;
		logger.finer("view name: [" + resPath + "].");
		RequestDispatcher rd = mReq.getRequestDispatcher(resPath);

		ResourceContext ctx = (ResourceContext) mReq
				.getAttribute(KEY_RESOURCECONTEXT);
		try {
			rd.include(mReq, mResp);
		} catch (Throwable e) {
			throw new PlatformException(e);
		}
	}

	@Override
	public Collection<Part> getParts() {
		// TODO Auto-generated method stub
		return mParts;
	}

	@Override
	public String getViewId() {
		// TODO Auto-generated method stub
		return mViewId;
	}

	@Override
	public String getActionId() {
		// TODO Auto-generated method stub
		return mActionId;
	}

	@Override
	public PlatformException getException() {
		// TODO Auto-generated method stub
		return mException;
	}

	@Override
	public boolean isAuthenticated() {
		boolean is=true;
		if(currentUser!=null){
			Set<Principal> s=currentUser.getPrincipals();
			Iterator<Principal> iter=s.iterator();
			while(iter.hasNext()){
				Principal p=iter.next();
				if(p instanceof XPiePrincipal){
					XPiePrincipal xp=(XPiePrincipal)p;
					if(xp.isAnonymous()){
						is=false;
						break;
					}
				}
			}
		}
		return is;
	}

	@Override
	public javax.security.auth.Subject getSubject() {
		Subject s=currentUser;
		return s;
	}

}
