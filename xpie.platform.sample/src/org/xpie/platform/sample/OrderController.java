package org.xpie.platform.sample;

import java.util.PropertyPermission;
import java.util.logging.Logger;

import org.xpie.platform.ResourceContext;
import org.xpie.platform.ResourceController;

public class OrderController extends ResourceController {

	private static Logger logger=Logger.getLogger(OrderController.class.getName());
	
	@Override
	protected String doUpdate(ResourceContext ctx) {
		
		logger.fine("doUpdate called.");
		
		return super.doUpdate(ctx);
	}

	@Override
	protected String doShow(ResourceContext ctx) {
		
		String outcome="";
		String javaHome=System.getProperty("java.home");
		System.out.println("java.home="+javaHome);
		PropertyPermission pp=new PropertyPermission("java.home","read");
		
		
		return super.doShow(ctx);
	}

}
