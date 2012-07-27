package cn.eiap.portal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xpie.platform.HttpActivator;

import cn.eiap.portal.service.impl.PortalServiceImpl;

public class PortalActivator implements BundleActivator {

	private static BundleContext context;
	private HttpActivator mActivator=null;
	
	
	public PortalActivator(){
		mActivator=new HttpActivator(this);
	}
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		PortalActivator.context = bundleContext;
		mActivator.start(bundleContext);
		context.registerService(PortalService.class.getName(), new PortalServiceImpl(), null);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		PortalActivator.context = null;
		mActivator.stop(bundleContext);
	}

}
