/**
 * A basic implementation of a Redis client for Processing.
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

package se.goransson.redis.processing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import processing.core.PApplet;

/**
 * A basic implementation of a Redis client for Processing.
 * 
 * @author Andreas Goransson
 * 
 */
public class Redis implements RedisListener {

	// myParent is a reference to the parent sketch
	PApplet myParent;

	RedisComm mRedisComm;

	public final static String VERSION = "##version##";

	/**
	 * 
	 * @param theParent
	 */
	public Redis(PApplet theParent) {
		myParent = theParent;
		welcome();

		mRedisComm = new RedisComm();
		mRedisComm.addRedisListener(this);
	}

	public void connect(String host, int port) {
		try {
			mRedisComm.connect(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Publish a message to the connected Redis host.
	 * 
	 * @param channel
	 *          the channel to publish to
	 * @param message
	 *          the message to publish
	 */
	public void publish(String channel, String message) {
		StringBuffer sb = new StringBuffer();

		// Number of arguments in this message is (3)
		sb.append('*').append('3').append("\r\n");
		// Bytes in argument 1 ("publish") is "publish".length
		sb.append('$').append("publish".length()).append("\r\n");
		// Argument 1 data
		sb.append("PUBLISH").append("\r\n");
		// Bytes in argument 2 (channel) is (channel.length)
		sb.append('$').append(channel.length()).append("\r\n");
		// Argument 2 data
		sb.append(channel).append("\r\n");
		// Bytes in argument 3 (message) is (message.length)
		sb.append('$').append(message.length()).append("\r\n");
		// Argument 3 data
		sb.append(message).append("\r\n");

		try {
			mRedisComm.writePublishSocket(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Subscribe to a channel.
	 * 
	 * @param channel
	 *          the channel to subscribe to.
	 */
	public void subscribe(String channel) {
		// Add the callback method
		Method m = null;
		try {
			m = myParent.getClass().getMethod(channel, new Class[] { String.class });
		} catch (Exception e) {
			System.out.println("Ohnoes! Error creating method... " + e.getMessage());
		}

		// Assemble the message
		StringBuffer sb = new StringBuffer();
		// Number of arguments in this message is (2)
		sb.append('*').append('2').append("\r\n");
		// Bytes in argument 1 ("subscribe") is "subscribe".length
		sb.append('$').append("subscribe".length()).append("\r\n");
		// Argument 1 data
		sb.append("SUBSCRIBE").append("\r\n");
		// Bytes in argument 2 (channel) is (channel.length)
		sb.append('$').append(channel.length()).append("\r\n");
		// Argument 2 data
		sb.append(channel).append("\r\n");

		try {
			mRedisComm.writeSubscribeSocket(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Un-subscribe from a channel.
	 * 
	 * @param channel
	 *          the channel to un-subscribe from.
	 */
	public void unsubscribe(String channel) {
		StringBuffer sb = new StringBuffer();

		// Number of arguments in this message is (2)
		sb.append('*').append('2').append("\r\n");
		// Bytes in argument 1 ("unsubscribe") is "subscribe".length
		sb.append('$').append("unsubscribe".length()).append("\r\n");
		// Argument 1 data
		sb.append("UNSUBSCRIBE").append("\r\n");
		// Bytes in argument 2 (channel) is (channel.length)
		sb.append('$').append(channel.length()).append("\r\n");
		// Argument 2 data
		sb.append(channel).append("\r\n");

		try {
			mRedisComm.writeSubscribeSocket(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void welcome() {
		System.out.println("##name## ##version## by ##author##");
	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

	@Override
	public void onMessage(String[] arguments) {
		// Get the callback method
		Method eventMethod = null;
		try {
			eventMethod = myParent.getClass().getMethod(arguments[1], String.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}

		if (eventMethod != null) {
			try {
				eventMethod.invoke(myParent, arguments[2]);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onSubscribe(String[] arguments) {
		myParent.println("Subscribed to " + arguments[1]);
	}

	@Override
	public void onPublish(String[] arguments) {
		myParent.println("Published to " + arguments[0] + " clients");
	}
}
