package org.xpie.j2ee.sample.model;

import java.io.Serializable;
import java.lang.Double;
import java.lang.Long;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Book
 *
 */
@Entity
@Table(name="Demo_Book")

public class Book implements Serializable {

	   
	@Id
	private Long Id;
	private String Title;
	private Double Price;
	private static final long serialVersionUID = 1L;

	public Book() {
		super();
	}   
	public Long getId() {
		return this.Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}   
	public String getTitle() {
		return this.Title;
	}

	public void setTitle(String Title) {
		this.Title = Title;
	}   
	public Double getPrice() {
		return this.Price;
	}

	public void setPrice(Double Price) {
		this.Price = Price;
	}
   
}
