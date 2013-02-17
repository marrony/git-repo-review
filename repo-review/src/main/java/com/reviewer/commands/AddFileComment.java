package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import com.reviewer.git.GitObject;
import com.reviewer.io.IOUtil;
import com.reviewer.model.Command;
import com.reviewer.model.Reviewer;

public class AddFileComment implements Command, GitObject {
	public String reviewId = "";
	public String file = "";
	public int line;
	public String comment = "";
	public transient String user = "";
	public transient Date date;

	public void read(DataInput input) throws IOException {
		reviewId = IOUtil.readSha1(input);
		file = IOUtil.readString(input);
		line = input.readInt();
		comment = IOUtil.readString(input);
	}

	public void write(DataOutput output) throws IOException {
		IOUtil.writeSha1(output, reviewId);
		IOUtil.writeString(output, file);
		output.writeInt(line);
		IOUtil.writeString(output, comment);
	}

	public void apply(Reviewer system) {
		system.addFileComment(reviewId, file, line, comment, user, date);
	}

	public void gitInfo(String commit, String author, Date date) {
		this.user = author;
		this.date = date;
	}
}
