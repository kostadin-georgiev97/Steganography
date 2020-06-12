
public interface IDecoder {

	/**
	 * This method decrypts file from image using LSB algorithm.
	 * 
	 * @param imgPath - e.g. ../../img.bmp
	 * 
	 * @return true, if file is encrypted successfully
	 */
	public boolean decode();
	
	/**
	 * This method returns an error message, telling why the decryption failed.
	 */
	public String getError();
	
}
