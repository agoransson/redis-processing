redis-processing
================

A very basic implementation of a Redis pub-sub client.

## Connect to a Redis host
``` java
Redis redis;

void setup() {
  redis = new Redis(this);
  redis.connect("hostname_or_ip", 6379);
}

void draw(){
}
```

## Publish a message to a channel
``` java
// Make sure not to publish before you connect!
redis.publish("channel", "message");
```

## Subscribe to a channel
``` java
// Subscribe to a channel name, avoid using special characters or spaces!
redis.subscribe("channel");

// To recieve the message, you need the method as well. The method name corresponds to the channel name!
void channel(String message){
	println( message );
}
```