package com.reviewer.model;

import java.util.ArrayList;
import java.util.List;

import com.reviewer.commands.Command;

public class System {
	
	private List<Review> reviews = new ArrayList<Review>();
	
	public void apply(List<Command> commands) {
		for(Command command : commands)
			command.apply(this);
	}

	public Review createReview(String hash, List<String> commits) {
		Review review = new Review(hash, commits);
		reviews.add(review);
		return review;
	}

	public void addComment(String hash, String comment) {
		Review review = findReview(hash);
		review.addComment(comment);
	}

	public void addFileComment(String hash, String file, int line, String comment) {
		Review review = findReview(hash);
		review.addFileComment(file, line, comment);
	}
	
	private Review findReview(String hash) {
		for(Review review : reviews)
			if(review.hash.equals(hash))
				return review;
		return null;
	}
}
