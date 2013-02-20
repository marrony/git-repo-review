package com.reviewer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Reviewer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<Review> reviews = new ArrayList<Review>();
	public String lastCommit;
	
	public void apply(List<? extends Command> commands) {
		for(Command command : commands)
			command.apply(this);
	}

	public Review createReview(String hash, String message, List<String> commits) {
		Review review = new Review(hash, message, commits);
		reviews.add(review);
		return review;
	}

	public void addComment(String hash, String comment, String user, Date date) {
		Review review = findReview(hash);
		review.addComment(comment, user, date);
	}

	public void addFileComment(String hash, String file, int line, String comment, String user, Date date) {
		Review review = findReview(hash);
		review.addFileComment(file, line, comment, user, date);
	}
	
	public Review findReview(String hash) {
		for(Review review : reviews)
			if(review.reviewId.equals(hash))
				return review;
		
		throw new RuntimeException(String.format("Review %s not found", hash));
	}

	public List<Review> getReviews() {
		return Collections.unmodifiableList(reviews);
	}
}
