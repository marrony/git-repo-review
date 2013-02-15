package com.reviewer.model;

import java.util.ArrayList;
import java.util.List;

public class Review {

	public String hash;
	public List<String> commits;
	public List<String> comments = new ArrayList<String>();

	public Review(String hash, List<String> commits) {
		this.hash = hash;
		this.commits = commits;
	}

	public void addComment(String comment) {
		comments.add(comment);
	}

	public void addFileComment(String file, int line, String comment) {
	}

}
