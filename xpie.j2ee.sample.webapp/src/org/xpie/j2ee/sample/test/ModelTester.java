package org.xpie.j2ee.sample.test;


import java.security.AccessController;
import java.util.PropertyPermission;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.xpie.j2ee.sample.model.Book;

public class ModelTester {

	public static void main(String[] args){
		ModelTester tester=new ModelTester();
		tester.testSecurity();
	}
	private void testJPA(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("xpie.j2ee.sample.webapp");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Book b=new Book();
        b.setId(1L);
        b.setTitle("test title");
        b.setPrice(20.222);
        em.persist(b);
        em.getTransaction().commit();
	}
	protected void testSecurity(){
		
		String a=System.getProperty("java.version");
		System.out.println("java.home="+a);
		PropertyPermission pp=new PropertyPermission("java.version","read");
		PropertyPermission p1=new PropertyPermission("java.home","read");
		AccessController.checkPermission(pp);
		AccessController.checkPermission(p1);
		System.out.println("Security test passed.");
	}
}
