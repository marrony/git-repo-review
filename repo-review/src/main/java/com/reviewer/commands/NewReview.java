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
	public transient String reviewId = "";
	public transient Date date;
	public List<String> commits = new ArrayList<String>();
	
	public void read(DataInput input) throws IOException {
		int size = input.readInt();
		commits = new ArrayList<String>(size);
		
		for(int i = 0; i < size; i++)
			commits.add(IOUtil.readSha1(input));
	}

	public void write(DataOutput output) throws IOException {
		output.writeInt(commits.size());
		
		for(int i = 0; i < commits.size(); i++)
			IOUtil.writeSha1(output, commits.get(i));
	}

	public void apply(Reviewer system) {
		system.createReview(reviewId, commits);
	}

	public void gitInfo(String commit, String author, Date date) {
		this.reviewId = commit;
		this.date = date;
	}
}
