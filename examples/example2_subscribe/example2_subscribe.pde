import org.json.*;

import se.goransson.redis.processing.*;

Redis r;

void setup() {
  r = new Redis(this);
  r.connect("localhost", 6379);
  r.subscribe("drawing");
}

void draw() {
}

void drawing(String s){
  JSONObject obj = new JSONObject(s);
  
  if( obj != null ){
    int x = obj.getInt("x");
    int y = obj.getInt("y");
    
    fill( 0 );
    ellipse( x, y, 5, 5 );
  }
}