package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.reviewer.model.System;

public class AddComment extends Command {
	public String review = "";
	public String comment = "";

	@Override
	public void read(DataInput input) throws IOException {
		review = readSha1(input);
		comment = readString(input);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		writeSha1(output, review);
		writeString(output, comment);
	}

	@Override
	public void apply(System system) {
		system.addComment(review, comment);
	}
}
