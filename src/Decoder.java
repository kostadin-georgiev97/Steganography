import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Decoder implements IDecoder {

	private static final int HEADER_SIZE_OFFSET = 54;
	private static final int DATA_SIZE_OFFSET = 32;
	private static final int EXTENSION_OFFSET = 64;
	private static final int TOTAL_OFFSET = HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET + EXTENSION_OFFSET;
	private String errorMsg;
	private File imageFile;
	private File newFile;

	/**
	 * Initializes the Decoder object and all its fields
	 * @param imgPath
	 * @param newFilePath
	 */
	public Decoder(String imgPath, String newFilePath){
    	errorMsg = "";
    	imageFile = new File(imgPath);
		newFile = new File(newFilePath);
    }

	@Override
	public boolean decode() {
		try {
			FileInputStream imageStream = new FileInputStream(imageFile);
			ArrayList<Integer> extensionBits = new ArrayList<>();
			ArrayList<Integer> dataBits = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			StringBuilder extension = new StringBuilder();
			int size = 0;

			if(!imageFile.exists()){
				this.setError("Image file does not exist!");
				return false;
			}
			if(newFile.exists()){
				this.setError("File with the same name as the new file already exists.");
				return false;
			}
			//Check image format
			if(!getFileExtension(imageFile).equals("bmp")){
				this.setError("Invalid image format - only .bmp allowed!");
				return false;
			}

			//This skips the header of the .bmp file
			imageStream.skip(HEADER_SIZE_OFFSET);

			//The following for statement reads the data size bits and saves the size in size variable.
			for(int sizeBitPos = 0; sizeBitPos < DATA_SIZE_OFFSET ; sizeBitPos++){
				int imageByte = imageStream.read();
				int currentBit = getBit(imageByte, 0);
				if(currentBit == 0x1){
					currentBit = currentBit << (DATA_SIZE_OFFSET - sizeBitPos - 1);
					size |= currentBit;
				}
			}

			//The following decodes the extension bits from the image and alters the file name
			for (int extBytePos = 0; extBytePos < EXTENSION_OFFSET; extBytePos++){
				int currentBit = getBit(imageStream.read(),0);
				extensionBits.add(currentBit);
				sb.append(currentBit);
			}

			for (int sbPos = 0; sbPos < EXTENSION_OFFSET; sbPos += 8) {
				if (Integer.parseInt(sb.substring(sbPos,sbPos+8)) != 0)
					extension.append((char)Integer.parseInt(sb.substring(sbPos,sbPos+8),2));
			}

			//the following reads the least significant bit of each byte of the encoded image
			for(int imageBytePos = 0; imageBytePos < (size * 8); imageBytePos++){
				int imageByte = imageStream.read();
				int currentDataBit = getBit(imageByte, 0);
				dataBits.add(currentDataBit);
			}

			String newFilePath =  newFile.getName() + "." + extension;
			newFile = new File(newFilePath);
			FileOutputStream newStream = new FileOutputStream(newFile);
			//the following code extracts the encoded information and stores it in the new file
			for (int bytePos = 0; bytePos < dataBits.size(); bytePos += 8) {
				int currentByte = 0;
				List<Integer> subList = dataBits.subList(bytePos, bytePos+8);
				for(int bitPos = 0; bitPos< subList.size();bitPos++){
					if(subList.get(bitPos) == 0x1){
						int currentBit =  subList.get(bitPos);
						currentBit = currentBit << (7 - bitPos );
						currentByte |= currentBit;
					}
				}
				newStream.write(currentByte);
			}

			imageStream.close();
			newStream.close();
		}
		catch (IOException e) {
			this.setError(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Returns the value of the field "error"
	 * @return
	 */
	@Override
	public String getError() {
		return errorMsg;
	}

	/**
	 *  Returns a String representing the file extension of 'file'
	 * @param file
	 * @return
	 */
	private String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");

		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf+1);
	}

	/**
	 * Sets the value of the field "error"
	 * @param err
	 */
	private void setError(String err) {
		this.errorMsg = err;
	}
	/**
	 * Returns the value of the kth bit of 'number' - either 0 or 1
	 * @param number
	 * @param k
	 * @return
	 */
	private int getBit(int number, int k) {
		return (number >> k) & 0x1;
	}
}
