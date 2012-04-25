package se.goransson.redis.processing;

import java.util.EventObject;

public class RedisEvent extends EventObject {
	String[] arguments;

	public RedisEvent(Object source, String[] arguments) {
		super(source);
		this.arguments = arguments;
	}

	public String[] getArguments() {
		return arguments;
	}
}
