package com.reviewer.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Context {
	private byte[] bytes;

	public List<Command> deserialize() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInput input = new DataInputStream(in);
		
		List<Command> commands = new ArrayList<Command>();
		
		while(in.available() > 0) {
			Command command = null;
			CommandType type = CommandType.fromValue(input.readInt());
			
			switch(type) {
			case ADD_REVIEW:
				command = new NewReview();
				break;
				
			case ADD_COMMENT:
				command = new AddComment();
				break;
				
			case ADD_FILE_COMMENT:
				command = new AddFileComment();
				break;
			}
			
			command.read(input);
			commands.add(command);
		}
		
		return commands;
	}

	public void serialize(List<Command> commands) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutput output = new DataOutputStream(out);
		
		for(Command command : commands) {
			output.writeInt(command.getType().getValue());
			command.write(output);
		}
		
		bytes = out.toByteArray();
	}
}
