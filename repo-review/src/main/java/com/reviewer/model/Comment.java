package com.reviewer.model;

import java.util.Date;

public class Comment {

	private final String comment;
	private final String user;
	private final Date date;

	public Comment(String comment, String user, Date date) {
		this.comment = comment;
		this.user = user;
		this.date = date;
	}

}
