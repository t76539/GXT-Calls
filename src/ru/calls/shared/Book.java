package ru.calls.shared;

import javax.persistence.*;

/**
 * Book.java
 * This class maps to a table in database.
 * @author www.codejava.net
 *
 */
 
@Entity
@Table(name = "book")
public class Book {
    private long id;
    private String title;
    private String author;
    private float price;
 
    public Book() {
    }
 
    /**
	 * @param id
	 * @param title
	 * @param author
	 * @param price
	 */
	public Book(long id, String title, String author, float price) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.price = price;
	}

	public Book(String title, String author, float price) {
		super();
		this.title = title;
		this.author = author;
		this.price = price;
	}

	@Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }
 
    public void setId(long id) {
        this.id = id;
    }
 
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }
 
    public String getAuthor() {
        return author;
    }
 
    public void setAuthor(String author) {
        this.author = author;
    }
 
    public float getPrice() {
        return price;
    }
 
    public void setPrice(float price) {
        this.price = price;
    }

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + ", price=" + price + "]";
	}
 
}