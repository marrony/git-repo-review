package com.reviewer.model;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final String comment;
	private final String user;
	private final Date date;

	public Comment(String comment, String user, Date date) {
		this.comment = comment;
		this.user = user;
		this.date = date;
	}

	@Override
	public String toString() {
		return user + " " + comment;
	}
}
