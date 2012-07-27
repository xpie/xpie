package org.xpie.platform;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.equinox.jsp.jasper.JspServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class HttpActivator implements BundleActivator {
	
	private Logger logger=Logger.getLogger(HttpActivator.class.getName());
	
	private List<String> aliasList=new ArrayList<String>();
	
	private List<Filter> filters=new ArrayList<Filter>();
	
	private ServiceReference mHttpServiceReference=null;
	
	private BundleActivator mLauncherActivator=null;
	
	private BundleContext mContext=null;
	
	public HttpActivator(BundleActivator activator){
		mLauncherActivator=activator;
	}

	protected BundleContext getContext(){
		return mContext;
	}
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		logger.entering(this.getClass().getName(), "start");
		mContext=bundleContext;
		mHttpServiceReference=getContext().getServiceReference(HttpService.class.getName());
		register(bundleContext);
		_initializeControllers();
		logger.exiting(this.getClass().getName(), "start");
	}

	private void _initializeControllers() {
		initializeControllers();		
	}

	protected void initializeControllers(){
		try {
			Class clz=Class.forName("org.xpie.platform.sample.OrderController");
			logger.finer(clz.getName()+" class found.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		
		logger.entering(this.getClass().getName(), "stop");
		HttpService service=getContext().getService(mHttpServiceReference);
		logger.fine("HttpService instance: " + service.toString()+".");
		for(int i=0;i<aliasList.size();i++){
			String alias=aliasList.get(i);
			logger.finer("Alias [" + alias +"] will be unregistered...");
			try{
				service.unregister(alias);
				logger.fine("Alias [" +alias+"] has been unregistered now.");
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
		getContext().ungetService(mHttpServiceReference);
		aliasList.clear();
		logger.exiting(this.getClass().getName(), "stop");
	}

	protected void register(BundleContext context){
		String servletAlias=getServletAliasName();
		HttpService service=getContext().getService(mHttpServiceReference);
		try {
			Hashtable initparams=new Hashtable();
			initparams.put(BasicHttpServlet.KEY_LAUNCHACTIVATOR, mLauncherActivator.getClass().getName());
			initparams.put(BasicHttpServlet.KEY_SERVLETALIAS, servletAlias);
			service.registerServlet(servletAlias,new BasicHttpServlet() , initparams, new BasicHttpContext(context.getBundle()));
			aliasList.add(servletAlias);
			logger.fine("HttpBasicServlet registered on alias ["+servletAlias+"].");
			
			
			String resourceAlias=servletAlias+"/public";
			service.registerResources(resourceAlias, "/public", new BasicHttpContext(context.getBundle()));
			aliasList.add(resourceAlias);
			logger.fine("Resource ["+resourceAlias+"] has been registered.");
			
			
			String jspAlias=servletAlias+"/jsp/*.jsp";
			Servlet adaptedJspServlet = new JspServlet(context.getBundle(), "/views", servletAlias+"/jsp");
			service.registerServlet(jspAlias, adaptedJspServlet, null, new BasicHttpContext(context.getBundle()));
			aliasList.add(jspAlias);
			
		} catch (ServletException e) {
			
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}finally{
			logger.finer("alias List size is [" + aliasList.size()+"].");
		}
	}
	protected String getServletAliasName(){
		String className="";
		String servletAlias="";
		className=mLauncherActivator.getClass().getName();
		
		int dotIndex=className.lastIndexOf(".");
		if(dotIndex>=0)
			servletAlias=className.substring(className.lastIndexOf(".")+1);
		
		int activatorIndex=servletAlias.lastIndexOf("Activator");
		if(activatorIndex>=0)
			servletAlias=servletAlias.substring(0, activatorIndex);
		
		servletAlias="/"+servletAlias.toLowerCase();
		logger.fine("return servletAlias is [" + servletAlias +"].");
		return servletAlias;
	}
}
