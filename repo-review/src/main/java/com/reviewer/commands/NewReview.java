package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.reviewer.git.GitObject;
import com.reviewer.io.IOUtil;
import com.reviewer.model.Command;
import com.reviewer.model.Reviewer;

public class NewReview implements Command, GitObject {
	public String reviewId = "";
	public String message = "";
	public List<String> commits = new ArrayList<String>();
	public transient String user = "";
	public transient Date date;
	
	public void read(DataInput input) throws IOException {
		reviewId = IOUtil.readSha1(input);
		message = IOUtil.readString(input);
		
		int size = input.readInt();
		commits = new ArrayList<String>(size);
		
		for(int i = 0; i < size; i++)
			commits.add(IOUtil.readSha1(input));
	}

	public void write(DataOutput output) throws IOException {
		IOUtil.writeSha1(output, reviewId);
		IOUtil.writeString(output, message);
		
		output.writeInt(commits.size());
		
		for(int i = 0; i < commits.size(); i++)
			IOUtil.writeSha1(output, commits.get(i));
	}

	public void apply(Reviewer system) {
		system.createReview(reviewId, message, commits);
	}

	public void gitInfo(String commit, String author, Date date) {
		this.user = author;
		this.date = date;
	}
}
