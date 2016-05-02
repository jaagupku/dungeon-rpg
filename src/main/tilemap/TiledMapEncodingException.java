package main.tilemap;

public class TiledMapEncodingException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 370095460941291454L;
	private String encoding;

	public TiledMapEncodingException(String encoding){
		super("Unsupported encoding: " + (encoding == null || encoding.equals("") ? "xml" : encoding) + ", please use CSV.");
		this.encoding = (encoding == null ? "xml" : encoding);
	}
	
	public String getEncoding(){
		return encoding;
	}
}
