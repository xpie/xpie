package org.xpie.platform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.xpie.platform.impl.ResourceContextImpl;

public abstract class ResourceContext {

	private static Logger logger = Logger.getLogger(ResourceContext.class
			.getName());

	private static Map<String, Map<String, Class>> maps = new HashMap<String, Map<String, Class>>();

	public static final String KEY_RESOURCECONTEXT = "org.xpie.platform.ctx";
	public static final String KEY_ANONYMOUS="anonymous";

	public static ResourceContext createResourceContext(HttpServletRequest req,
			HttpServletResponse resp) {
		ResourceContext ctx = null;
		ctx = new ResourceContextImpl(req, resp);
		req.setAttribute(KEY_RESOURCECONTEXT, ctx);
		logger.finer("set context on attribute ctx.");
		return ctx;
	}

	public String getResourceName() {
		return getNouns();
	}

	public abstract HttpServletRequest getHttpRequest();

	public abstract HttpServletResponse getHttpResponse();

	public abstract Collection<Part> getParts();

	public abstract String getVerb();

	public abstract String getNouns();

	public abstract String getFormat();

	public abstract String getViewId();

	public abstract String getActionId();

	public abstract String[] getQualifiers();

	public abstract Map getData();

	public abstract PlatformException getException();
	
	protected void registerController(String idn, String nouns, Class clz,
			boolean force) {
		synchronized (maps) {
			Map<String, Class> map = maps.get(idn);
			if (map == null) {
				map = new HashMap<String, Class>();
				maps.put(idn, map);
			}
			Class ctrlClass = map.get(nouns);
			if (ctrlClass == null) {
				map.put(nouns, clz);
			} else {
				if (force)
					map.put(nouns, clz);
			}
		}
	}

	protected Class findController(String idn, String nouns) {
		Class ctrlClass = ResourceController.class;
		if (nouns != null && !nouns.equals("default") && !nouns.equals("")) {
			Map<String, Class> map = maps.get(idn);
			if (map != null) {
				Class c = map.get(nouns);
				if (c != null)
					ctrlClass = c;
			}
		}
		logger.finer("idn:" + idn + ",nouns:" + nouns + ",class:"
				+ ctrlClass.getName());
		return ctrlClass;
	}

	public abstract Class getControllerClass();

	public abstract void processRequest();

	public abstract void dispatch(String outcome);
	
	public boolean isAuthenticated(){
		return false;
	}
	public abstract Subject getSubject();
}
