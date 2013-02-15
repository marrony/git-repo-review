package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.reviewer.io.IOUtil;
import com.reviewer.model.Command;
import com.reviewer.model.System;

public class AddComment implements Command {
	public String review = "";
	public String comment = "";

	public void read(DataInput input) throws IOException {
		review = IOUtil.readSha1(input);
		comment = IOUtil.readString(input);
	}

	public void write(DataOutput output) throws IOException {
		IOUtil.writeSha1(output, review);
		IOUtil.writeString(output, comment);
	}

	public void apply(System system) {
		system.addComment(review, comment);
	}
}
