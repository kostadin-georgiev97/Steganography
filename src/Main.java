import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		System.out.println("Use \"enc <file_path>.<extension> <image_path>.<extension> <new_file_path>\" for encoding");
		System.out.println("Use \"dec <image_path>.<extension> <new_file_path>\" for decoding");
		System.out.println("Use \"help\" to display commands list");
		System.out.println("Use \"exit\" to exit application");
		System.out.println("********************************************************************");
		
		boolean isRunning = true;
		Scanner scanner = new Scanner(System.in);
		
		while(isRunning) {
			System.out.println(System.getProperty("user.dir"));
			System.out.print("$ ");
			
			// get user input and clear it for processing
			String in = scanner.nextLine();
			in = in.trim().replaceAll(" +", " ");
			String[] inTokens = in.split(" ");
			
			// checks which command to execute (inTokens[0])
			switch(inTokens[0]) {
				case "": // empty input -> do nothing
					break;
				case "enc": // enc <file_path> <img_path> <new_file_path> -> hides file into image
					if(inTokens.length == 4) {
						String filePath = inTokens[1]; // file_path
						String imgPath = inTokens[2]; // img_path
						String newFilePath = inTokens[3]; // new_file_path

						IEncoder enc = new Encoder( imgPath,filePath, newFilePath);
						
						if(enc.encode()) {
							System.out.println("File encoding is successful!");
						} else {
							System.out.println(enc.getError());
						}
					} else {
						System.out.println("\"enc <file_path> <image_path> <new_file_path>\" should have three parameters");
					}
					break;
				case "dec": // dec <img_path> <new_file_path> -> retrieves hidden file from image
					if(inTokens.length == 3) {
						String imgPath = inTokens[1];
						String newFilePath = inTokens[2];
						
						IDecoder dec = new Decoder(imgPath, newFilePath);
						
						if(dec.decode()) {
							System.out.println("File decoding is successful!");
						} else {
							System.out.println(dec.getError());
						}
					} else {
						System.out.println("\"dec <image_path> <new_file_path>\" should have two parameters");
					}
					break;
				case "help":
					System.out.println("Use \"enc <file_path>.<extension> <image_path>.<extension> <new_file_path>\" for encoding");
					System.out.println("Use \"dec <image_path>.<extension> <new_file_path>\" for decoding");
					System.out.println("Use \"help\" to display commands list");
					System.out.println("Use \"exit\" to exit application");
					System.out.println("********************************************************************");
					break;
				case "exit":
					System.out.println("Exiting..");
					isRunning = false;
					break;
				default:
					System.out.println("Command not found!");
			}
			System.out.println();
		}
		
		scanner.close();

	}

}
