package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.reviewer.io.IOUtil;
import com.reviewer.model.Command;
import com.reviewer.model.System;

public class NewReview implements Command {
	public String hash = "";
	public List<String> commits = new ArrayList<String>();
	
	public void read(DataInput input) throws IOException {
		hash = IOUtil.readSha1(input);
		
		int size = input.readInt();
		commits = new ArrayList<String>(size);
		
		for(int i = 0; i < size; i++)
			commits.add(IOUtil.readSha1(input));
	}

	public void write(DataOutput output) throws IOException {
		IOUtil.writeSha1(output, hash);
		
		output.writeInt(commits.size());
		
		for(int i = 0; i < commits.size(); i++)
			IOUtil.writeSha1(output, commits.get(i));
	}

	public void apply(System system) {
		system.createReview(hash, commits);
	}
}
