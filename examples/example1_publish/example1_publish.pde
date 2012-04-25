import org.json.*;

import se.goransson.redis.processing.*;

Redis r;

void setup() {
  r = new Redis(this);
  r.connect("localhost", 6379);
}

void draw() {
}

void test(String s){
}

void mousePressed() {
  JSONObject obj = new JSONObject();
  obj.put("state", 1);
  
  if ( mouseButton == LEFT ) {
    r.publish("blink", obj.toString());
  }
}

void mouseReleased() {
  JSONObject obj = new JSONObject();
  obj.put("state", 0);
  
  if ( mouseButton == LEFT ) {
    r.publish("blink", obj.toString());
  }
}