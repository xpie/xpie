package org.xpie.platform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.PropertyPermission;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourceController {

	private static Logger logger=Logger.getLogger(ResourceController.class.getName());
	public void process(ResourceContext ctx) {
		String format=ctx.getFormat();
		String verb=ctx.getVerb().toLowerCase();
		String viewId=ctx.getViewId();
		String outcome="";
		
		SecurityManager sm=System.getSecurityManager();
		ResourcePermission perm=new ResourcePermission(ctx.getNouns(),ctx.getVerb());
		if(sm!=null)
			sm.checkPermission(perm);
		
		if(verb.equals("get")){
			if(viewId!=null&&!viewId.equals("")){
				//if viewId has some value, override the outcome.
				outcome=viewId;
			}else{
				if(format.equals("search")){
					outcome="search_form";
				}else if(format.equals("form")){
					outcome=doShow(ctx);
					outcome="edit_form";
				}else if(format.equals("newform")){
					outcome="new_form";
				}else if(format.equals("show")){
					outcome=doShow(ctx);
				}else{
					String[] qualifiers=ctx.getQualifiers();
					outcome=getOutcomeFromQualifiers(qualifiers);
				}
			}
		}else if(verb.equals("post")){
			if(format.equals("search")){
				outcome=doSearch(ctx);
			}else if(format.equals("action")){
				outcome=invokeControllerMethod(ctx);
			}else{
				outcome=doUpdate(ctx);
			}
		}else if(verb.equals("put")){
			outcome=doNew(ctx);
		}else if(verb.equals("delete")){
			outcome=doDelete(ctx);
		}else{
			hello(ctx);
			outcome="";
		}
		ctx.dispatch(outcome);
	}
	private String invokeControllerMethod(ResourceContext ctx) {
		String outcome="";
		String actionId=ctx.getActionId();
		if(actionId!=null&&!actionId.equals("")){
			Class c=this.getClass();
			try {
				Method m=c.getMethod(actionId, ResourceContext.class);
				Object obj=m.invoke(this,ctx);
				outcome=(String)obj;
			} catch (Throwable e) {
				throw new PlatformException(e);
			} 
		}
		return outcome;
	}
	public void hello(ResourceContext ctx){
		HttpServletResponse resp=ctx.getHttpResponse();
		HttpServletRequest req=ctx.getHttpRequest();
		try {
			ServletOutputStream out=resp.getOutputStream();
			out.println("<h1>Hello World!!</h1>");
			out.println("<p>URI:" + req.getRequestURI()+"</p>");
			out.println("<p>QStr:"+req.getQueryString()+"</p>");
			out.println("<p>ctrl:"+ctx.getControllerClass().getName()+"</p>");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String doUpdate(ResourceContext ctx){
		return "show";
	}
	protected String doNew(ResourceContext ctx){
		return "index";
	}
	protected String doDelete(ResourceContext ctx){
		return "index";
	}
	protected String doSearch(ResourceContext ctx){
		return "search";
	}
	protected String doShow(ResourceContext ctx){
		return "show";
	}
	protected String getOutcomeFromQualifiers(String[] qualifiers){
		String outcome="index";
		if(qualifiers.length>0){
			String q=qualifiers[qualifiers.length-1];
			int posIndex=q.lastIndexOf(".");
			String id="";
			if(posIndex>=0){
				id=q.substring(0, posIndex);
			}else{
				id=q;
			}
			try{
				new Long(id);
				outcome="show";
			}catch(NumberFormatException e){
				logger.finer("["+ id +"] is not a Numberic string, so outcome will not be show.");
			}
		}
		return outcome;
	}
}
