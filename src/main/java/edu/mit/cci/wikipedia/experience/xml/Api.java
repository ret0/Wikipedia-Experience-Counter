package edu.mit.cci.wikipedia.experience.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
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
	
	/**
	 * Returns a map containing the usernames and the number of edits
	 */
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
	
	/**
	 * activity = editCount / daysSinceRegistration
	 * Score = 0.3 * daysSinceRegistration + 0.3 * editCount + 0.4 * activity 
	 */
	public long generateScoreForUser() {
		User user = query.getUsers().get(0);
		Double editCount = Double.valueOf(user.getEditcount());
		DateTime now = new DateTime();
		DateTime userRegisteredAt = new DateTime(user.getRegistration());
		int daysSinceRegistration = Days.daysBetween(userRegisteredAt, now)
				.getDays();

		Double activity = editCount / daysSinceRegistration;
		Double score = 0.3 * daysSinceRegistration + 0.3 * editCount + 0.4 * activity;
		return Math.round(score);
	}
}

@Root(strict = false)
class Query {

	@ElementList(required = false)
	private List<Page> pages;
	
	@ElementList(required = false)
	private List<User> users;

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
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

@Root(strict = false)
class User {
	
	@Attribute
	private String editcount;
	
	@Attribute
	private String registration;
	
	@Attribute
	private String name;

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getEditcount() {
		return editcount;
	}

	public void setEditcount(String editcount) {
		this.editcount = editcount;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
