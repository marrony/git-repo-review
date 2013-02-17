package com.reviewer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Reviewer {
	
	private List<Review> reviews = new ArrayList<Review>();
	
	public void apply(List<? extends Command> commands) {
		for(Command command : commands)
			command.apply(this);
	}

	public Review createReview(String hash, List<String> commits) {
		Review review = new Review(hash, commits);
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
	
	private Review findReview(String hash) {
		for(Review review : reviews)
			if(review.reviewId.equals(hash))
				return review;
		return null;
	}
}
