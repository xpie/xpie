package org.xpie.platform.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.Servlet;

import org.eclipse.equinox.jsp.jasper.JspServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.xpie.platform.HttpActivator;
import org.xpie.platform.BasicHttpServlet;

public class SampleActivator implements BundleActivator {

	private HttpActivator mActivator=null;
	private static Logger logger=Logger.getLogger(SampleActivator.class.getName());
	
	private ServiceReference sRef=null;
	
	private ServiceTracker httpServiceTracker;
	
	public SampleActivator(){
		mActivator=new HttpActivator(this);
	}
	@Override
	public void start(BundleContext context) throws Exception {

		sRef=context.getServiceReference(HttpService.class.getName());
		HttpService s=context.getService(sRef);
		s.registerServlet("/jsp/sample/*.sample", new SampleServlet(), null, null);
		mActivator.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mActivator.stop(context);	
		context.ungetService(sRef);
	}
	
	private void testJPAProvider(BundleContext context){
		
		ServiceReference emfRef=null;
		emfRef=context.getServiceReference(EntityManagerFactory.class.getName());
		logger.fine("#######emfRef="+emfRef);
		EntityManagerFactory emf=(EntityManagerFactory)context.getService(emfRef);
		EntityManager em=emf.createEntityManager();
		em.getTransaction().begin();
		Order order=new Order();
		order.setId(12);
		order.setCustomerId(20);
		order.setDate(new Date());
		order.setComment("just a test comment.");
		em.persist(order);
		em.getTransaction().commit();
		System.out.println("an order created.");
		//this.testJPAProvider();
	}
	
	private void testJDBCDriver(){
		try
	    {
	      Class.forName("com.mysql.jdbc.Driver").newInstance();
	      String url = "jdbc:mysql://192.168.1.121/test";
	      Connection conn = DriverManager.getConnection(url, "test", "passw0rd");
	      conn.close();
	      System.out.println("MYSQL driver passed.");
	    }
	    catch (ClassNotFoundException ex) {System.err.println(ex.getMessage());}
	    catch (IllegalAccessException ex) {System.err.println(ex.getMessage());}
	    catch (InstantiationException ex) {System.err.println(ex.getMessage());}
	    catch (SQLException ex)           {System.err.println(ex.getMessage());}
	}
	private void testJPAProvider(){
		EntityManagerFactory emf=Persistence.createEntityManagerFactory("aaa");
		EntityManager em=emf.createEntityManager();
		em.getTransaction().begin();
		Order order=new Order();
		order.setId(10);
		order.setCustomerId(10);
		order.setDate(new Date());
		order.setComment("test comment.");
		em.persist(order);
		em.getTransaction().commit();
		System.out.println("JPA Provider passed.");
	}
	public static void main(String[] args){
		SampleActivator a=new SampleActivator();
		a.testJDBCDriver();
		a.testJPAProvider();
	}
}
