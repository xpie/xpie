package org.xpie.platform.jaas;

import java.security.Principal;

public class XPiePrincipal implements Principal {

	public static final String ANONYMOUS="anonymous";
	private String name;
	
	public XPiePrincipal(String name){
		this.name=name;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public boolean equals(Object obj) {
		boolean equal=false;
		if(obj instanceof XPiePrincipal){
			equal=name.equals(((XPiePrincipal) obj).getName());
		}
		return equal;
	}
	@Override
	public String toString() {
		return "XPiePrincipal:"+name;
	}
	public boolean isAnonymous(){
		return ANONYMOUS.equals(getName());
	}
}
