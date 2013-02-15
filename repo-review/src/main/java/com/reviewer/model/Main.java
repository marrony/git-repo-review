package com.reviewer.model;

import java.util.ArrayList;
import java.util.List;

import com.reviewer.commands.AddComment;
import com.reviewer.commands.AddFileComment;
import com.reviewer.commands.Command;
import com.reviewer.commands.Context;
import com.reviewer.commands.NewReview;

public class Main {

	public static void main(String[] args) throws Exception {
		List<Command> commands = new ArrayList<Command>();
		
		String sha1Review = "6ef5d47d0132e20d828b54314ec4660851b215d8";
		String sha1Commit = "1aaba95af26c2420752c7c1912ce07c058492eb6";
		
		NewReview newReview = new NewReview();
		newReview.hash = sha1Review;
		newReview.commits.add(sha1Commit);
		commands.add(newReview);
		
		AddComment addComment = new AddComment();
		addComment.review = sha1Review;
		addComment.comment = "new comment";
		commands.add(addComment);
		
		AddFileComment addFileComment = new AddFileComment();
		addFileComment.review = sha1Review;
		addFileComment.file = "Main.java";
		addFileComment.line = 10;
		addFileComment.comment = "I find a bug in this line";
		commands.add(addFileComment);
		
		//test serializatin and deserialization
		Context context = new Context();
		context.serialize(commands);
		commands = context.deserialize();
		
		//test apply commands
		System system = new System();
		system.apply(commands);
	}
}
