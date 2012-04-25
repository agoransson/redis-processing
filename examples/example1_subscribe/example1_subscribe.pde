import org.json.*;

import se.goransson.redis.processing.*;

Redis r;

void setup() {
  r = new Redis(this);
  r.connect("localhost", 6379);
  r.subscribe("blink");
}

void draw() {
}

void blink(String s) {
  JSONObject obj = new JSONObject(s);

  if ( obj.getInt("state") == 1 )
    background(255);
  else 
    background(0);
}
