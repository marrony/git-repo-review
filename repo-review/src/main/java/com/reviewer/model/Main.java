package com.reviewer.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.reviewer.commands.AddComment;
import com.reviewer.commands.AddFileComment;
import com.reviewer.commands.NewReview;
import com.reviewer.git.Git;
import com.reviewer.git.Git.GitFile;
import com.reviewer.git.Git.Tree;
import com.reviewer.git.GitObject;
import com.reviewer.io.Context;

public class Main {
	
	private static String getLastCommit() throws IOException {
		return Git.rev_parse("refs/heads/review");
	}

	private static void updateCommands(String commit, List<? extends Command> commands) throws IOException {
		String[] commit_info = Git.author_name_and_date(commit).split("[\t]");
		String author = commit_info[0];
		Date date = new Date(Long.parseLong(commit_info[1]) * 1000);
		
		for(Command command : commands) {
			if(command instanceof GitObject)
				((GitObject) command).gitInfo(commit, author, date);
		}
	}
	
	private static List<Command> getCommands() throws IOException {
		List<Command> commands = new ArrayList<Command>();
		
		String commit = getLastCommit();
		
		Context context = new Context();
		
		for(GitFile file : Git.ls_tree(commit)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Git.cat_file(file.sha1, out);
			
			try {
				context.setBytes(out.toByteArray());
				
				List<Command> deserialized = context.deserialize();
				
				updateCommands(commit, deserialized);
				commands.addAll(deserialized);
			} catch(Exception e) {
			}
		}
		
		return commands;
	}
	
	private static String commitCommands(List<? extends Command> commands) throws Exception {
		Context context = new Context();
		context.serialize(commands);
		
		String sha1 = Git.hash_object(context.getBytes());
		
		Tree tree = Git.mktree();
		tree.addBlob(Git.FileMode.REGULAR_NON_EXECUTABLE, sha1, "review.bin");
		tree.end();
		
		String commit = Git.commit_tree("new review", tree.sha1, null);
		Git.update_ref("refs/heads/review", commit);
		
		updateCommands(commit, commands);
		
		return commit;
	}
	
	private static Command crateReview(String... commits) throws Exception {
		NewReview newReview = new NewReview();
		newReview.commits.addAll(Arrays.asList(commits));
		return newReview;
	}
	
	private static Command createFileComment(String reviewId, String file, int line, String comment) {
		AddFileComment addFileComment = new AddFileComment();
		addFileComment.comment = comment;
		addFileComment.file = file;
		addFileComment.line = 123;
		addFileComment.reviewId = reviewId;
		return addFileComment;
	}

	private static Command createComment(String reviewId, String comment) {
		AddComment addComment = new AddComment();
		addComment.comment = comment;
		addComment.reviewId = reviewId;
		return addComment;
	}
	
	public static void main(String[] args) throws Exception {
		Git.delete_branch("review", true);
		
		Reviewer reviewer = new Reviewer();
		
		List<Command> commands = new ArrayList<Command>();
		commands.add(crateReview("c719433a83d749d572b9ca8875b5f4009a4bc537"));
		commitCommands(commands);
		
		reviewer.apply(commands);
		
		String reviewId = ((NewReview)commands.get(0)).reviewId;
		
		commands.clear();
		
		commands.add(createComment(reviewId, "new comment"));
		commands.add(createFileComment(reviewId, "file.txt", 10, "new comment"));
		commitCommands(commands);
		
		reviewer.apply(commands);
	}
}
