package org.xpie.platform;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.Servlet;

import org.eclipse.equinox.jsp.jasper.JspServlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.xpie.platform.util.SimpleLogFormatter;

public class Activator implements BundleActivator {

	private  BundleContext context;
	private ServiceReference sRef=null;
	private String jspAlias="/_sys/*.jsp";
	protected BundleContext getContext() {
		return context;
	}
	protected void setContext(BundleContext ctx){
		this.context=ctx;
	}
	private void configLogger(){
		
		LogManager logManager=LogManager.getLogManager();
		Logger logger=logManager.getLogger("org.xpie");
		Level l=logger.getLevel();
		Handler[] handlers=logger.getHandlers();
		for(Handler h:handlers){
			if(h instanceof java.util.logging.ConsoleHandler){
				h.setFormatter(new SimpleLogFormatter());
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		
		//configLogger();
		setContext(bundleContext);
		Bundle bundle=getContext().getBundle();
		sRef=context.getServiceReference(HttpService.class.getName());
		HttpService s=context.getService(sRef);
		
		Servlet adaptedJspServlet = new JspServlet(context.getBundle(), "/views", "/_sys");
		s.registerServlet(jspAlias, adaptedJspServlet, null, null);
		
		getLogger().log(Level.FINE, "Bundle [" + bundle.getSymbolicName()+"] started.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Bundle bundle=getContext().getBundle();
		HttpService s=context.getService(sRef);
		s.unregister(jspAlias);
		bundleContext.ungetService(sRef);
		getLogger().log(Level.FINE, "Bundle [" + bundle.getSymbolicName()+"] stopped.");
		setContext(null);
	}
	
	protected Logger getLogger(){
		Logger logger=null;
		String logName=this.getClass().getCanonicalName();
		//System.out.println("Logger Name:" + logName);
		logger=Logger.getLogger(logName);	
		return logger;
	}
}
