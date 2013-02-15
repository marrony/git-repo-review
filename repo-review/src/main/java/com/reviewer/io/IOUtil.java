package com.reviewer.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IOUtil {

	public static void writeString(DataOutput output, String string) throws IOException {
		output.writeInt(string.length());
		output.writeChars(string);
	}
	
	public static String readString(DataInput input) throws IOException {
		int size = input.readInt();

		String string = "";
		for (int i = 0; i < size; i++)
			string += input.readChar();
		
		return string;
	}
	
	public static void writeBytes(DataOutput output, byte[] bytes) throws IOException {
		output.write(bytes);
	}
	
	public static byte[] readBytes(DataInput input, int size) throws IOException {
		byte[] bytes = new byte[size];
		input.readFully(bytes);
		return bytes;
	}
	
	public static void writeSha1(DataOutput output, String sha1) throws IOException {
		byte[] bytes = sha1.getBytes();
		
		if(bytes.length != 40)
			throw new IllegalArgumentException("SHA-1 must contain 40 bytes");
		
		writeBytes(output, bytes);
	}
	
	public static String readSha1(DataInput input) throws IOException {
		byte[] sha1 = readBytes(input, 40);
		return new String(sha1);
	}
	
}
