
public interface IEncoder {
	
	/**
	 * This method encodes file into image using LSB algorithm.
	 * 
	 * @param filePath - e.g. ../../file.txt
	 * @param imgPath - e.g. ../../img.bmp
	 * 
	 * @return true, if file is encoded successfully
	 */
	public boolean encode();
	
	/**
	 * This method returns an error message, telling why the encoding failed.
	 */
	public String getError();
	
}
