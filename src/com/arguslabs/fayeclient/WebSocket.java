package com.arguslabs.fayeclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class WebSocket implements Runnable {

	// --------------------------------------------------
	// Constants
	// --------------------------------------------------

	/**
	 * Socket string encoding
	 */
	public static final String ENCODING = "UTF-8";

	/**
	 * Byte representing CR (\r)
	 */
	public static final byte CR = (byte) 0x0D;

	/**
	 * Byte representing LF (\n)
	 */
	public static final byte LF = (byte) 0x0A;

	/**
	 * Byte used to start text (UTF-8) frame
	 */
	public static final byte START_TEXT_FRAME = (byte) 0x00;

	/**
	 * Byte used to end frame
	 */
	public static final byte END_FRAME = (byte) 0xFF;

	/**
	 * First byte in challenge header
	 */
	public static final byte CHALLENGE_HEADER1 = (byte) 0x0D;

	/**
	 * Second byte in challenge header
	 */
	public static final byte CHALLENGE_HEADER2 = (byte) 0x0A;

	/**
	 * Array of random characters for WebSocket key
	 * (http://trac.webkit.org/browser
	 * /trunk/WebCore/websockets/WebSocketHandshake.cpp)
	 */
	public static final char[] RANDOM_KEY_CHARACTERS = new String("!\"#$%&'()*+,-./:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~").toCharArray();

	// --------------------------------------------------
	// Enums
	// --------------------------------------------------

	/**
	 * Connection state enumeration
	 * 
	 */
	public enum ReadyState {
		CONNECTING, OPEN, CLOSING, CLOSED
	};

	// --------------------------------------------------
	// Private Fields
	// --------------------------------------------------

	/**
	 * WebSocket unique identifier
	 */
	private String id;

	/**
	 * URI to connect to
	 */
	private URI uri;

	/**
	 * Sub-protocol that the server must support
	 */
	private String subProtocol;

	/**
	 * State of the connection
	 */
	private ReadyState readyState = ReadyState.CLOSED;

	/**
	 * Optional headers to include when connecting
	 */
	private HashMap<String, String> headers;

	/**
	 * SocketChannel instance used to read and write data
	 */
	private SocketChannel socketChannel;

	/**
	 * 1 byte buffer for reading
	 */
	private ByteBuffer readBuffer;

	/**
	 * Buffer containing the current frame
	 */
	private ByteBuffer frameBuffer;

	/**
	 * Use WebSocket challenge (draft 76)
	 */
	private Boolean useChallenge = false;

	/**
	 * Flag noting whether or not a connection has been established
	 */
	private Boolean hasConnected = false;

	/**
	 * Object to be notified of open, close and message events
	 */
	
	//private WebSocketListener listener = null;
	
	
	private Handler messageHandler =null;

	// --------------------------------------------------
	// Public Properties
	// --------------------------------------------------

	/**
	 * Expose the connection state of the socket
	 */
	public ReadyState getReadyState() {
		return this.readyState;
	}

	/**
	 * Return the object listening for WebSocket events
	 * 
	 * @return WebSocketListener instance
	 */
//	public WebSocketListener getListener() {
//		return this.listener;
//	}

	/**
	 * Set the object listening for WebSocket events
	 * 
	 * @param listener
	 *            WebSocketListener instance
	 */
//	public void setListener(WebSocketListener listener) {
//		this.listener = listener;
//	}

	/**
	 * Expose the WebSocket unique ID
	 * 
	 * @return ID as String
	 */
	public String getId() {
		return this.id;
	}

	// --------------------------------------------------
	// Constructor
	// --------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param url
	 *            URL to connect to
	 */
	public WebSocket(String url, Handler h) {
		this(url, "", h);
	}

	/**
	 * Constructor
	 * 
	 * @param url
	 *            URL to connect to
	 * @param protocol
	 *            Sub-protocol that the server must support
	 */
	public WebSocket(String url, String protocol, Handler h) {
		try {
			this.initialize(new URI(url), protocol);
			this.messageHandler = h;
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException("Invalid url: " + url);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param uri
	 *            URI to connect to
	 */
	public WebSocket(URI uri) {
		this(uri, "");
	}

	/**
	 * Constructor
	 * 
	 * @param uri
	 *            URI to connect to
	 * @param protocol
	 *            Sub-protocol that the server must support
	 */
	public WebSocket(URI uri, String protocol) {
		this.initialize(uri, protocol);
	}

	// --------------------------------------------------
	// Public Methods
	// --------------------------------------------------

	/**
	 * Add a custom HTTP header
	 * 
	 * @param key
	 *            Header key
	 * @param value
	 *            Header value
	 */
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	/**
	 * Remove a custom HTTP header
	 * 
	 * @param key
	 *            Header key
	 */
	public void removeHeader(String key) {
		this.headers.remove(key);
	}

	/**
	 * Open the WebSocket connection
	 */
	public void open() {
		if (this.readyState == ReadyState.CLOSED) {
			// Open connection on another thread
			(new Thread(this)).start();
		}
	}

	/**
	 * Close the WebSocket connection
	 */
	public void close() {
		if (this.readyState != ReadyState.CLOSED) {
			if (this.socketChannel instanceof SocketChannel) {
				this.readyState = ReadyState.CLOSING;
				try {
					// Close the SocketChannel, which should kill the thread
					this.socketChannel.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	/**
	 * Send a message through the WebSocket
	 * 
	 * @param data
	 *            Data to send
	 */
	public void send(String data) throws IllegalArgumentException,
			NotYetConnectedException {
		if (this.readyState == ReadyState.OPEN) {
			if (data == null || data.equals("")) {
				throw new IllegalArgumentException(
						"WebSocket cannot send an empty string");
			}
			try {
				// Create the socket message with framing bytes
				byte[] messageBuffer = data.getBytes(WebSocket.ENCODING);
				ByteBuffer buffer = ByteBuffer
						.allocate(messageBuffer.length + 2);
				buffer.put(WebSocket.START_TEXT_FRAME);
				buffer.put(messageBuffer);
				buffer.put(WebSocket.END_FRAME);
				// Write the buffer the socket
				buffer.rewind();
				this.socketChannel.write(buffer);
			} 
			catch (IOException ex) { }
		} 
		else { throw new NotYetConnectedException(); } //deze catchen !!!!
	}
	
	
	/**
	 * Runnable interface implementation. Initializes socket connection
	 */
	@Override
	public void run() {
		
		if (this.readyState == ReadyState.CLOSED) {
			this.hasConnected = false;
			try {
				// Flag the connection as connecting
				this.readyState = ReadyState.CONNECTING;

				// Get the socket port
				int port = this.uri.getPort();
				if (port == -1) {
					port = 80;
				}

				// Android 2.2 prefers IPv6, but throws
				// "java.net.SocketException: Address family not supported by protocol"
				System.setProperty("java.net.preferIPv6Addresses", "false");
				// Create a non-blocking SocketChannel
				
				SocketAddress socketAddress = new InetSocketAddress(uri
						.getHost(), port);
				this.socketChannel = SocketChannel.open();
				this.socketChannel.configureBlocking(false);
				this.socketChannel.connect(socketAddress);

				// Create a Selector and attach to SocketChannel
				Selector selector = Selector.open();
				this.socketChannel.register(selector, this.socketChannel
						.validOps());

				// Loop until SocketChannel closes (10 minute timeout)
				while (selector.select(10*60*1000) > 0 && selector.isOpen()) { 
					// Iterate over the selection keys with pending events
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = keys.iterator();
					while (iterator.hasNext()) {
						// Get the selection key and remove it from the set
						SelectionKey key = iterator.next();
						iterator.remove();

						// Check if connection is established
						if (key.isValid() && key.isConnectable()) {
							this.handleConnect(key);
						}

						// Check if the connection can read
						if (key.isValid() && key.isReadable()) {
							this.handleRead();
						}
					}
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (IOException ex) {
				
				Log.e("websocket", ex.getLocalizedMessage());
			}
			// Set the ready state to closed
			this.readyState = ReadyState.CLOSED;
			Boolean notifyClosed = this.hasConnected;
			this.hasConnected = false;
			// Check if need to notify of close
			if (notifyClosed) {
				System.out.println("*** WebSocket CLOSED ***");
				// Check if a listener is defined and notify
//				if (this.listener != null && this.listener instanceof WebSocketListener) {
//					this.listener.onClose(this);
//				}
				messageHandler.sendMessage(Message.obtain(messageHandler, MESSAGE_ONCLOSE));
			}
		}
	}

	// --------------------------------------------------
	// Private Methods
	// --------------------------------------------------

	/**
	 * Initialize the WebSocket with URI and sub-protocol
	 * 
	 * @param uri
	 *            URI to connect to
	 * @param protocol
	 *            Sub-protocol that the server must support
	 */
	private void initialize(URI uri, String protocol) {
		// Set the unique ID
		this.id = UUID.randomUUID().toString();

		// Check if the scheme is valid and store URI and protocol
		String scheme = uri.getScheme();
		if (scheme.equals("ws") || scheme.equals("wss")) {
			this.uri = uri;
			this.subProtocol = protocol;
		} else {
			throw new IllegalArgumentException("Invalid scheme: " + uri);
		}
		// Initialize headers collection and buffers
		this.headers = new HashMap<String, String>();
		this.readBuffer = ByteBuffer.allocate(1);
		this.frameBuffer = null;
	}

	/**
	 * Handle connection to host
	 * 
	 * @param key
	 *            SelectionKey instance to connect
	 * @throws IOException
	 */
	private void handleConnect(SelectionKey key) throws IOException,
			UnsupportedEncodingException {
		// Ensure connection is finished
		if (this.socketChannel.isConnectionPending()) {
			this.socketChannel.finishConnect();
		}
		// Send WebSocket client handshake
		this.socketChannel.write(ByteBuffer.wrap(this.createHandshake()));
	}

	/**
	 * Handle reading from SocketChannel
	 * 
	 * @throws IOException
	 */
	private void handleRead() throws IOException {
		// Read from the SocketChannel
 		this.readBuffer.rewind();
		int bytesRead = -1;
		try {
			bytesRead = this.socketChannel.read(this.readBuffer);
		} catch (Exception ex) {
			@SuppressWarnings("unused")
			String test = ex.getMessage();
		}
		if (bytesRead == -1) {
			// Bytes could not be read, close the connection
			this.close();
		} else if (bytesRead > 0) {
			this.readBuffer.rewind();
			// Bytes were read, handle the data
			if (this.readyState == ReadyState.CONNECTING) {
				this.receiveHandshake();
			} else if (this.readyState == ReadyState.OPEN) {
				this.receiveFrame();
			} else {
				// Invalid state, close the connection
				this.close();
			}
		}
	}

	/**
	 * Create the handshake request
	 * 
	 * @return
	 */
	private byte[] createHandshake() throws UnsupportedEncodingException {
		StringBuilder request = new StringBuilder();
		// Create the WebSocket handshake request
		// (http://www.whatwg.org/specs/web-socket-protocol/)
		request.append("GET ");
		String path = this.uri.getPath();
		if (path == null || path.equals("")) {
			request.append("/");
		} else {
			request.append(path);
		}
		String query = this.uri.getQuery();
		if (query != null && !query.equals("")) {
			request.append("?" + query);
		}
		String host = this.uri.getHost();
		request
				.append(" HTTP/1.1\r\nHost: "
						+ host
						+ "\r\nConnection: Upgrade\r\nUpgrade: WebSocket\r\nOrigin: http://"
						+ host);
		int port = this.uri.getPort();
		if (port != 80) {
			request.append(":" + port);
		}
		request.append("\r\n");
		if (!this.subProtocol.equals("")) {
			request.append("Sec-WebSocket-Protocol: " + this.subProtocol
					+ "\r\n");
		}
		for (Entry<String, String> header : this.headers.entrySet()) {
			request.append(header.getKey() + ": " + header.getValue() + "\r\n");
		}
		byte[] key3 = new byte[0];
		if (this.useChallenge) {
			// Create the random header keys
			String key1 = this.generateKey();
			request.append("Sec-WebSocket-Key1: " + key1 + "\r\n");
			String key2 = this.generateKey();
			request.append("Sec-WebSocket-Key2: " + key2 + "\r\n");
			// Generate random key for body
			key3 = new byte[3];
			Random random = new Random();
			random.nextBytes(key3);
			// TODO generate expected challenge response
		}
		request.append("\r\n");
		// Return the request as bytes
		byte[] buffer = request.toString().getBytes(WebSocket.ENCODING);
		if (key3.length > 0) {
			buffer = this.appendBytes(buffer, key3);
		}
		return buffer;
	}

	/**
	 * Receive handshake data from WebSocket server
	 * 
	 * @see http://www.whatwg.org/specs/web-socket-protocol/
	 */
	private void receiveHandshake() {
		// Append the read buffer to the frame buffer
		this.frameBuffer = this.appendBuffer(this.frameBuffer, this.readBuffer);
		// Check if the frame buffer ends with two CRLFs (0x0D 0x0A 0x0D 0x0A)
		byte[] buffer = this.frameBuffer.array();
		int length = buffer.length;
		if (length >= 4 && buffer[length - 4] == WebSocket.CR
				&& buffer[length - 3] == WebSocket.LF
				&& buffer[length - 2] == WebSocket.CR
				&& buffer[length - 1] == WebSocket.LF) {
			if (this.validateHandshakeHeaders()) {
				this.readyState = ReadyState.OPEN;
				this.hasConnected = true;
				frameBuffer = null;
				// Check if a listener is defined and notify
				System.out.println("*** WebSocket OPEN ***");
				//if (this.listener != null && this.listener instanceof WebSocketListener) {
					//this.listener.onOpen(this);
				//}
				messageHandler.sendMessage(Message.obtain(messageHandler, MESSAGE_ONOPEN));
			}
		}
	}

	/**
	 * Validate the WebSocket handshake headers stored in the frame buffer
	 * 
	 * @return Boolean for valid handshake
	 * @see http://www.whatwg.org/specs/web-socket-protocol/
	 */
	private Boolean validateHandshakeHeaders() {
		try {
			// Get an array of lines in the handshake header
			String[] handshake = new String(this.frameBuffer.array(),
					WebSocket.ENCODING).split("\r\n");
			int length = handshake.length;
			if (length > 1) {
				// Validate the leading line
				if (handshake[0].trim().equals(
						"HTTP/1.1 101 Web Socket Protocol Handshake")) {
					Boolean hasUpgrade = false;
					Boolean hasConnection = false;
					// Iterate over the lines and check for the Upgrade and
					// Connection fields
					for (int i = 1; i < length; i++) {
						String[] fields = handshake[i].split(":");
						if (fields.length == 2) {
							String key = fields[0].trim();
							String value = fields[1].trim();
							if (key.equals("Upgrade")
									&& value.equals("WebSocket")) {
								hasUpgrade = true;
							} else if (key.equals("Connection")
									&& value.equals("Upgrade")) {
								hasConnection = true;
							}
						}
						if (hasUpgrade && hasConnection) {
							return true;
						}
					}
				}
			}
		} catch (UnsupportedEncodingException ex) {
		}
		return false;
	}

	/**
	 * Receive frame data from WebSocket
	 * 
	 * @see http://www.whatwg.org/specs/web-socket-protocol/
	 */
	
	final int MESSAGE_ONOPEN = 1;
	final int MESSAGE_ONCLOSE = 2;
	final int MESSAGE_ONMESSAGE = 3;
	
	private void receiveFrame() {
		
		byte current = this.readBuffer.get();
		if (current == WebSocket.START_TEXT_FRAME) {
			// Start of text frame encountered, clear frame buffer
			this.frameBuffer = null;
		} else if (current == WebSocket.END_FRAME) {
			try {
				if (this.frameBuffer != null) {
					// Currently, only text frames are supported, so convert to
					// string
					String frame = new String(this.frameBuffer.array(),
							WebSocket.ENCODING);
					// Check if a listener is defined and notify
					Log.d("WebSocket", "*** WebSocket MESSAGE (" + frame + ") ***");
					//if (this.listener != null && this.listener instanceof WebSocketListener) {
						
						
						
						//this.listener.onMessage(this, frame);
						
						messageHandler.sendMessage(Message.obtain(messageHandler, MESSAGE_ONMESSAGE, frame));
						
						
					//}
				}
			} catch (UnsupportedEncodingException ex) {
			}
		} else {
			// Append the read buffer to the frame buffer
			this.frameBuffer = this.appendBuffer(this.frameBuffer,
					this.readBuffer);
		}
	}

	/**
	 * Return the length of a ByteBuffer instance
	 * 
	 * @param buffer
	 *            ByteBuffer instance to return length of
	 * @return 0 if ByteBuffer instance is null; otherwise buffer capacity
	 */
	private int bufferLength(ByteBuffer buffer) {
		return buffer != null ? buffer.capacity() : 0;
	}

	/**
	 * Append a ByteBuffer to another ByteBuffer
	 * 
	 * @param appendTo
	 *            Buffer to be appended to
	 * @param appending
	 *            Buffer to append to the end of appendTo
	 * @return New ByteBuffer with appended data
	 */
	private ByteBuffer appendBuffer(ByteBuffer appendTo, ByteBuffer appending) {
		// Allocate a new buffer
		ByteBuffer temp = ByteBuffer.allocate(this.bufferLength(appendTo)
				+ this.bufferLength(appending));
		// Append the first buffer
		if (appendTo != null) {
			appendTo.rewind();
			temp.put(appendTo);
		}
		// Append the second buffer
		if (appending != null) {
			appending.rewind();
			temp.put(appending);
		}
		return temp;
	}

	/**
	 * Append a byte array to another byte array
	 * 
	 * @param appendTo
	 *            Byte array to append to
	 * @param appending
	 *            Byte array to append to the end of appendTo
	 * @return New byte array with appended data
	 */
	private byte[] appendBytes(byte[] appendTo, byte[] appending) {
		// Allocate a new byte array
		byte[] temp = new byte[appendTo.length + appending.length];
		System.arraycopy(appendTo, 0, temp, 0, appendTo.length);
		System.arraycopy(appending, 0, temp, appendTo.length, appending.length);
		return temp;
	}

	/**
	 * Generate the challenge (draft 76) header key
	 * 
	 * @return Key as string
	 * @see http://www.whatwg.org/specs/web-socket-protocol/
	 * @see http
	 *      ://trac.webkit.org/browser/trunk/WebCore/websockets/WebSocketHandshake
	 *      .cpp
	 */
	private String generateKey() {
		Random random = new Random();
		// Generate the random number value
		int max = -1;
		int spaces = 0;
		do {
			spaces = random.nextInt(12) + 1;
			max = Integer.MAX_VALUE / spaces; // Use maximum integer, not
												// 4294967295
		} while (max < -1);
		int number = random.nextInt(max + 1);
		int product = number * spaces;
		StringBuilder key = new StringBuilder(Integer.toString(product));
		// Add 1-12 random characters to key in random positions
		int count = random.nextInt(12) + 1;
		for (int i = 0; i < count; i++) {
			int position = random.nextInt(key.length() + 1);
			int charPosition = random
					.nextInt(WebSocket.RANDOM_KEY_CHARACTERS.length);
			key.insert(position, WebSocket.RANDOM_KEY_CHARACTERS[charPosition]);
		}
		// Add spaces to key in random positions (not first or last)
		for (int i = 0; i < spaces; i++) {
			int position = random.nextInt(key.length() - 1) + 1;
			key.insert(position, " ");
		}
		return key.toString();
	}

}
