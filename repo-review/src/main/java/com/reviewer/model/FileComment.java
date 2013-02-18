package com.reviewer.model;

import java.io.Serializable;
import java.util.Date;

public class FileComment implements Serializable {
	
	private static final long serialVersionUID = 1L;

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
