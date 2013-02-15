package com.reviewer.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Serializable {
	void read(DataInput input) throws IOException;

	void write(DataOutput output) throws IOException;
}
