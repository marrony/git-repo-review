package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.reviewer.model.System;

public class AddFileComment extends Command {
	public String review = "";
	public String file = "";
	public int line;
	public String comment = "";

	@Override
	public CommandType getType() {
		return CommandType.ADD_FILE_COMMENT;
	}

	@Override
	public void read(DataInput input) throws IOException {
		review = readSha1(input);
		file = readString(input);
		line = input.readInt();
		comment = readString(input);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		writeSha1(output, review);
		writeString(output, file);
		output.writeInt(line);
		writeString(output, comment);
	}

	@Override
	public void apply(System system) {
		system.addFileComment(review, file, line, comment);
	}

}
