import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class Encoder implements IEncoder {
    private static final int HEADER_SIZE_OFFSET = 54;
    private static final int DATA_SIZE_OFFSET = 32;
    private static final int EXTENSION_OFFSET = 64;
    private static final int TOTAL_OFFSET = (HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET + EXTENSION_OFFSET);

    private File dataFile;
    private File imageFile;
    private File newFile;
    private String error;

    /**
     * Initializes the Encoder object and all its fields
     * @param imagePath
     * @param dataFilePath
     * @param newFilePath
     */
    public Encoder (String imagePath, String dataFilePath,String newFilePath) {
        this.error = "";
    	dataFile = new File(dataFilePath);
    	imageFile = new File(imagePath);

    	if(getFileExtension(newFilePath).equals( "")){
    	    String newName = newFilePath + ".bmp";
            newFile = new File(newName);
        }
    	else
    	    newFile = new File(newFilePath);
    }
    
    @Override
    public boolean encode() {
        //Check if files exist
        if(!dataFile.exists()){
            this.setError("Data file does not exist");
            return false;
        }

        if(!imageFile.exists()){
            this.setError("Image file does not exist!");
            return false;
        }
        if(newFile.exists()){
            this.setError("File with the same name as the new file already exists.");
            return false;
        }

        //Check image format
        if(!getFileExtension(imageFile.getName()).equals("bmp")){
            this.setError("Invalid image format - only .bmp allowed!");
            return false;
        }

    	//Check sizes before hiding the information
    	if(!checkSizes()) {
    		this.setError("Size of image is not sufficient to hide file!");
    		return false;
    	}
    	
    	try {
                //Open Input/Output Streams
                FileInputStream infoStream = new FileInputStream(dataFile);
                FileInputStream imageStream = new FileInputStream(imageFile);
                FileOutputStream newStream = new FileOutputStream(newFile);
                ArrayList<Integer> imageBytes = new ArrayList<>();
                ArrayList<Integer> dataBits = new ArrayList<>();
                int b;

                while((b = imageStream.read()) != -1 ){
                    imageBytes.add(b);
                }

                while((b = infoStream.read()) != -1){
                    for(int i = 7;i >= 0;i--){
                        int currentBit = getBit(b,i);
                        dataBits.add(currentBit);
                    }
                }
                // Add size of plain text at the start
                encodeSize(imageBytes);

                String extension = getFileExtension(dataFile.getName());
                //Add file extension at the start
                encodeExtension(imageBytes,extension);

                for(int imageBytePos = TOTAL_OFFSET; imageBytePos < (TOTAL_OFFSET + (dataSize() * 8)); imageBytePos ++){
                        int imageByte = imageBytes.get(imageBytePos);
                        int currentDataBit = dataBits.get(imageBytePos - TOTAL_OFFSET);

                        if (currentDataBit == 0x1) {
                            imageByte |= 0x1;
                            imageBytes.set(imageBytePos, imageByte );
                        }
                        else {
                            imageByte &= ~0x1;
                            imageBytes.set(imageBytePos, imageByte);
                        }
                }

                newFile.createNewFile();

                for(int cnt = 0; cnt<imageBytes.size();cnt ++){
                    newStream.write(imageBytes.get(cnt));
                }
                //Close i/o streams
                infoStream.close();
                imageStream.close();
                newStream.close();
            }
            catch(IOException e) {
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
		return this.error;
	}

    /**
     * Returns the value of "imageFile" in bytes
     * @return
     */
    private long imageSize() {
        return imageFile.length();
    }

    /**
     * Returns the value of "dataFile" in bytes
     * @return
     */
    private long dataSize(){
        return dataFile.length();
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

    /**
     *  Checks if the size of the image is sufficient to hide the plaintext
     * @return
     */
    private boolean checkSizes(){
        return (imageSize() - TOTAL_OFFSET) * 0.125 > dataSize();
    }

    /**
     *  Returns a String representing the file extension of 'file'
     * @param fileName
     * @return
     */
    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return fileName.substring(lastIndexOf+1);
    }

    /**
     * Sets the value of the field "error"
     * @param err
     */
	private void setError(String err) {
		this.error = err;
	}

    /**
     * Encodes the size of the plain text in the image
     * @param imageBytes
     */
	private void encodeSize(ArrayList<Integer> imageBytes){
        int size = (int) dataSize();

        for(int bits = 0; bits < DATA_SIZE_OFFSET ; bits ++){
            int imageByte = imageBytes.get(HEADER_SIZE_OFFSET + bits + 1);
            int currentBit = getBit(size,(DATA_SIZE_OFFSET - bits -1 ));
            if(currentBit == 0x1) {
                imageByte |=  0x1;
                imageBytes.set((HEADER_SIZE_OFFSET + bits), imageByte );
            }
            else {
                imageByte &= ~ 0x1;
                imageBytes.set((HEADER_SIZE_OFFSET + bits), imageByte);
            }
        }
    }

    /**
     *  Ecnodes the extension of the data file in the image.
     * @param imageBytes
     * @param extension
     */
	private void encodeExtension(ArrayList<Integer> imageBytes, String extension){

        //Convert extension bits to a string
        String extBits = new BigInteger(extension.getBytes()).toString(2);
        int numOfBytesNeeded = extBits.length();
        //Pad extra bytes on the left with zeros
        for(int bytePos = 0; bytePos < (EXTENSION_OFFSET - numOfBytesNeeded) ; bytePos++) {
                //Add 54 + 32 because of header and size information
                int imageByte = imageBytes.get(bytePos + HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET);
                imageByte = imageByte & ~0x01;
                imageBytes.set(bytePos + HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET, imageByte);
        }
            //Hide the extension in the remaining bits
            for(int extBit = 0; extBit< extBits.length();extBit++) {
                int currentImageByte = imageBytes.get(extBit + HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET );
                int currentBit = Character.getNumericValue(extBits.charAt(extBit));
                if(currentBit == 1)
                    imageBytes.set(extBit + HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET + (EXTENSION_OFFSET - numOfBytesNeeded) ,currentImageByte | currentBit);
                else
                    imageBytes.set(extBit + HEADER_SIZE_OFFSET + DATA_SIZE_OFFSET + (EXTENSION_OFFSET-numOfBytesNeeded), currentImageByte & ~currentBit);
        }
    }
}