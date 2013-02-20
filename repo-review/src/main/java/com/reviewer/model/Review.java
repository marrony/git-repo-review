package com.reviewer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public String reviewId;
	public final String message;
	public List<String> commits;
	public List<Comment> comments = new ArrayList<Comment>();
	public List<FileComment> fileComments = new ArrayList<FileComment>();

	public Review(String reviewId, String message, List<String> commits) {
		this.reviewId = reviewId;
		this.message = message;
		this.commits = commits;
	}

	public void addComment(String comment, String user, Date date) {
		comments.add(new Comment(comment, user, date));
	}

	public void addFileComment(String file, int line, String comment, String user, Date date) {
		fileComments.add(new FileComment(file, line, comment, user, date));
	}

}
