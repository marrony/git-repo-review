package com.reviewer.commands;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.reviewer.model.System;

public abstract class Command {
	public abstract void read(DataInput input) throws IOException;

	public abstract void write(DataOutput output) throws IOException;
	
	public abstract void apply(System system);
	
	protected void writeString(DataOutput output, String string) throws IOException {
		output.writeInt(string.length());
		output.writeChars(string);
	}
	
	protected String readString(DataInput input) throws IOException {
		int size = input.readInt();

		String string = "";
		for (int i = 0; i < size; i++)
			string += input.readChar();
		
		return string;
	}
	
	protected void writeBytes(DataOutput output, byte[] bytes) throws IOException {
		output.write(bytes);
	}
	
	protected byte[] readBytes(DataInput input, int size) throws IOException {
		byte[] bytes = new byte[size];
		input.readFully(bytes);
		return bytes;
	}
	
	protected void writeSha1(DataOutput output, String sha1) throws IOException {
		writeBytes(output, sha1.getBytes());
	}
	
	protected String readSha1(DataInput input) throws IOException {
		byte[] sha1 = readBytes(input, 40);
		return new String(sha1);
	}
}
