package org.xpie.platform;

import java.security.BasicPermission;
import java.security.Permission;

public class ResourcePermission extends BasicPermission {

	public ResourcePermission(String name, String actions) {
		super(name, actions);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3270622572099485699L;

	@Override
	public boolean implies(Permission p) {
		return super.implies(p);
	}
	

}
