package com.reviewer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Review {

	public String reviewId;
	public List<String> commits;
	public List<Comment> comments = new ArrayList<Comment>();
	public List<FileComment> fileComments = new ArrayList<FileComment>();

	public Review(String reviewId, List<String> commits) {
		this.reviewId = reviewId;
		this.commits = commits;
	}

	public void addComment(String comment, String user, Date date) {
		comments.add(new Comment(comment, user, date));
	}

	public void addFileComment(String file, int line, String comment, String user, Date date) {
		fileComments.add(new FileComment(file, line, comment, user, date));
	}

}
