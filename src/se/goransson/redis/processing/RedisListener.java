package se.goransson.redis.processing;

public interface RedisListener {

	void onSubscribe(String[] arguments);

	void onPublish(String[] arguments);

	void onMessage(String[] arguments);
}
