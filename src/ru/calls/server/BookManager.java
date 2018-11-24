package ru.calls.server;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import ru.calls.shared.Book;

public class BookManager {

	Logger logger = Logger.getLogger("MyLogger");

	protected SessionFactory sessionFactory;

	protected void setup() {
		logger.info("BookManager: setup()");
		
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
//			sessionFactory = HibernateUtil.getSessionFactory();
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			logger.info("========================> BookManager: setup() - OK");
		} catch (Exception ex) {
			logger.info("------------------------> BookManager: setup() ERROR: " + ex.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	private void stackTraceToString(Throwable e) {
		StringBuffer sb = new StringBuffer();
	    for (StackTraceElement element : e.getStackTrace()) {
	    	sb.append(element.toString() + "\r\n");			
	    }
		logger.info("------------------------> BookManager: setup() ERROR2: " + sb.toString());
	}	
	
	protected void exit() {
		sessionFactory.close();
	}

	protected void create() {
		logger.info("BookManager: create()");
		if (sessionFactory == null) {
			logger.info("BookManager: sessionFactory == null");
			return;
		}
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		session.save(new Book("Effective Java p1", "Joshua Bloch", 32.59f));
		session.save(new Book("Effective Java p2", "Joshua Bloch", 32.59f));
		session.save(new Book("Effective Java p3", "Joshua Bloch", 32.59f));

		session.getTransaction().commit();
		session.close();
	}

	protected void read() {
		Session session = sessionFactory.openSession();

		long bookId = 20;
		Book book = session.get(Book.class, bookId);

		System.out.println("Title: " + book.getTitle());
		System.out.println("Author: " + book.getAuthor());
		System.out.println("Price: " + book.getPrice());

		session.close();
	}

	protected void update() {
		Book book = new Book();
		book.setId(20);
		book.setTitle("Ultimate Java Programming");
		book.setAuthor("Nam Ha Minh");
		book.setPrice(19.99f);

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		session.update(book);

		session.getTransaction().commit();
		session.close();
	}

	protected void delete() {
		Book book = new Book();
		book.setId(20);

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		session.delete(book);

		session.getTransaction().commit();
		session.close();
	}

	public List<Book> listBooks() {
		Session session = this.sessionFactory.openSession();
		Transaction transaction = null;

		transaction = session.beginTransaction();
		List<Book> books = session.createQuery("FROM Book").list();

		transaction.commit();
		session.close();
		return books;
	}	

	public String displayBooks() {
		List<Book> books = (List<Book>) listBooks();
		for (Book book : books) {
			logger.info(book.toString());
		}
		return "books.toString()";
	}

	public static void main(String[] args) {
		BookManager manager = new BookManager();
		manager.setup();

		//manager.create();
		manager.displayBooks();

		manager.exit();		
	}

}
