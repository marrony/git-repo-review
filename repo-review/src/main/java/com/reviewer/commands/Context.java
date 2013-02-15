package com.reviewer.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.reviewer.model.Command;

public class Context {
	private static Map<CommandType, Class<? extends Command>> typeToClass;
	private static Map<Class<? extends Command>, CommandType> classToType;
	
	{
		typeToClass = new HashMap<CommandType, Class<? extends Command>>();
		classToType = new HashMap<Class<? extends Command>, CommandType>();
		
		register(CommandType.ADD_REVIEW, NewReview.class);
		register(CommandType.ADD_COMMENT, AddComment.class);
		register(CommandType.ADD_FILE_COMMENT, AddFileComment.class);
	}
	
	private static void register(CommandType type, Class<? extends Command> clazz) {
		typeToClass.put(type, clazz);
		classToType.put(clazz, type);
	}
	
	private byte[] bytes;

	public List<Command> deserialize() throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		DataInput input = new DataInputStream(in);
		
		List<Command> commands = new ArrayList<Command>();
		
		while(in.available() > 0) {
			CommandType type = CommandType.fromValue(input.readInt());
			Class<? extends Command> clazz = typeToClass.get(type);
			
			Command command = clazz.newInstance();
			command.read(input);
			commands.add(command);
		}
		
		return commands;
	}

	public void serialize(List<Command> commands) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataOutput output = new DataOutputStream(out);
		
		for(Command command : commands) {
			CommandType type = classToType.get(command.getClass());
			
			output.writeInt(type.getValue());
			command.write(output);
		}
		
		bytes = out.toByteArray();
	}
}
