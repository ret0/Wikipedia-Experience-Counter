package edu.mit.cci.wikipedia.experience.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Api {

	@Element
	private Query query;

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
	
	public Map<String, Integer> generateRankingOfAllNonAnonymousUsers() {
		Map<String, Integer> ranking = new HashMap<String, Integer>();
		List<Rev> allRevisions = query.getPages().get(0).getRevisions();
		for (Rev rev : allRevisions) {
			if (rev.getAnon() == null) {
				String userName = rev.getUser();
				if (ranking.containsKey(userName)) {
					ranking.put(userName, ranking.get(userName) + 1);
				} else {
					ranking.put(userName, 1);
				}
			}
		}
		return ranking;
	}

}

@Root(strict = false)
class Query {

	@ElementList
	private List<Page> pages;

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

}

@Root(strict = false)
class Page {

	@ElementList
	private List<Rev> revisions;

	public void setRevisions(List<Rev> revisions) {
		this.revisions = revisions;
	}

	public List<Rev> getRevisions() {
		return revisions;
	}

}

class Rev {

	@Attribute
	private String user;

	@Attribute(required = false)
	private String anon;

	public String getAnon() {
		return anon;
	}

	public void setAnon(String anon) {
		this.anon = anon;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

}
