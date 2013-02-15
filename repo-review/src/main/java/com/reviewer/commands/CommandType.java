package com.reviewer.commands;

public enum CommandType {

	ADD_REVIEW(1),
	ADD_COMMENT(2),
	ADD_FILE_COMMENT(3)
	;
	
	private int value;
	
	CommandType(int type) {
		this.value = type;
	}
	
	public int getValue() {
		return value;
	}
	
	public static CommandType fromValue(int value) {
		for(CommandType v : values()) {
			if(v.getValue() == value)
				return v;
		}
		return null;
	}
}
