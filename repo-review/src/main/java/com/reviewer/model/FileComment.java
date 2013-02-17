package com.reviewer.model;

import java.util.Date;

public class FileComment {

	private final String file;
	private final int line;
	private final String comment;
	private final String user;
	private final Date date;

	public FileComment(String file, int line, String comment, String user, Date date) {
		this.file = file;
		this.line = line;
		this.comment = comment;
		this.user = user;
		this.date = date;
	}
}
