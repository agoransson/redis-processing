import org.json.*;

import se.goransson.redis.processing.*;

Redis r;

void setup() {
  r = new Redis(this);
  r.connect("localhost", 6379);
}

void draw() {
}

void mouseDragged() {
  JSONObject obj = new JSONObject();
  obj.put("x", mouseX);
  obj.put("y", mouseY);

  r.publish("drawing", obj.toString());
}
