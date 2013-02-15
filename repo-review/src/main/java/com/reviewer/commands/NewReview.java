package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.reviewer.model.System;

public class NewReview extends Command {
	public String hash = "";
	public List<String> commits = new ArrayList<String>();
	
	@Override
	public void read(DataInput input) throws IOException {
		hash = readSha1(input);
		
		int size = input.readInt();
		commits = new ArrayList<String>(size);
		
		for(int i = 0; i < size; i++)
			commits.add(readSha1(input));
	}

	@Override
	public void write(DataOutput output) throws IOException {
		writeSha1(output, hash);
		
		output.writeInt(commits.size());
		
		for(int i = 0; i < commits.size(); i++)
			writeSha1(output, commits.get(i));
	}

	@Override
	public void apply(System system) {
		system.createReview(hash, commits);
	}
}
