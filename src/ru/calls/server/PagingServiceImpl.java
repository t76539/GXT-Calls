package ru.calls.server;

/*
 * На заметку : к проекту GXTTable не подключена user library "GXT".
 * Библиотека компонентов gxt-3.1.1.jar добавлена в поддиректорию lib и включена в classpath.
 * При другом подключении на этапе запуска проекта выскакивает 
 *   ошибка : ClassNotFoundException : PagingLoadResult
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import ru.calls.client.services.PagingService;
import ru.calls.shared.Post;

// Класс сортировки по имени пользователя
class SortedByName implements Comparator<Post>
{
	public int compare(Post obj1, Post obj2)
	{
		return obj1.getUsername().toLowerCase().compareTo(obj2.getUsername().toLowerCase());
	}
}

public class PagingServiceImpl extends RemoteServiceServlet implements PagingService
{
	Logger logger = Logger.getLogger("MyLogger");
	private static final long serialVersionUID = 1L;
	
	private  List<Post>  posts     = null;
	private  String      sort_fld  = null;
	private  Post[]      data      = null;

	@Override
	public PagingLoadResult<Post> getPosts(PagingLoadConfig config)
	{
		logger.warning("PagingServiceImpl: PagingLoadResult");
		
		if (posts == null)
			loadPosts();

//		System.out.println ("PagingServiceImpl : config.getSortInfo().size() = " + config.getSortInfo().size());
		if (config.getSortInfo().size() > 0) {
			SortInfo sort = config.getSortInfo().get(0);

			String sortField = sort.getSortField();
			if ((sortField != null) && sortField.equalsIgnoreCase("username") && 
				!sortField.equalsIgnoreCase(sort_fld)) {
				config.setOffset(0);
				data = new Post[posts.size()];
				for (int i = 0; i < posts.size(); i++)
					data[i] = posts.get(i);
				Arrays.sort(data, new SortedByName());
				sort_fld = sortField;
			} else if ((sortField != null) && (sort_fld != null) && !sortField.equalsIgnoreCase(sort_fld)) {
				data = null;
				System.gc();
				sortData(sort);
			}
		}
		ArrayList<Post> sublist = new ArrayList<Post>();
		int start = config.getOffset();
		int limit = posts.size();
		if (config.getLimit() > 0)
			limit = Math.min(start + config.getLimit(), limit);

	    for (int i = config.getOffset(); i < limit; i++) {
	    	if (data != null)
	    		sublist.add((Post) data[i]);
	    	else
	    		sublist.add(posts.get(i));
	    }
	    return new PagingLoadResultBean<Post>(sublist, posts.size(), config.getOffset());
	}

	private void sortData(final SortInfo sort)
	{
		if (sort.getSortField() != null) {
			final String sortField = sort.getSortField();
			if ((sortField != null) && !sortField.equalsIgnoreCase(sort_fld)) {
				sort_fld = sortField;
				Collections.sort(posts, sort.getSortDir().comparator(new Comparator<Post>() {
					public int compare(Post p1, Post p2) {
						if (sortField.equals("forum")) {
							return p1.getForum().compareTo(p2.getForum());
//						} else if (sortField.equals("username")) {
//							return p1.getUsername().compareTo(p2.getUsername());
						} else if (sortField.equals("subject")) {
							return p1.getSubject().compareTo(p2.getSubject());
						} else if (sortField.equals("date")) {
							return p1.getDate().compareTo(p2.getDate());
						}
						return 0;
					}
				}));
			}
		}
	}
	
	private String getValue(NodeList fields, int index)
	{
		NodeList list = fields.item(index).getChildNodes();
		if (list.getLength() > 0) {
			return list.item(0).getNodeValue();
		} else {
			return "";
		}
	}
	
	private void loadPosts()
	{
		logger.warning("PagingServiceImpl: loadPosts");
		
		posts = new ArrayList<Post>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(getClass().getResourceAsStream("posts.xml"));
			doc.getDocumentElement().normalize();

			NodeList nodeList = doc.getElementsByTagName("row");

			for (int s = 0; s < nodeList.getLength(); s++) {
				Node fstNode = nodeList.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					NodeList fields = fstElmnt.getElementsByTagName("field");
					Post p = new Post();
					p.setForum   (getValue(fields, 0));
					p.setDate    (sdf.parse(getValue(fields, 1)));
					p.setSubject (getValue(fields, 2));
					p.setUsername(getValue(fields, 4));
					posts.add(p);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
