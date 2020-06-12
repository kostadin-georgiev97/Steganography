# Steganography

## Summary
This project provides means to encode files into steganographic images and vice versa, using the Least Significant Bit (LSB) algorithm. The application is written in Java and has a simple command line UI.

## Functionality
* Hide a file of any type (.txt, .docx, and .mp3, etc.) within a 24-bit bitmap cover image using the least significant bit algorithm.
* Extract a file of any type from a bitmap containing a file which has previously been hidden using the same program.

## Command line UI:
| Command | Result |
| - | - |
| `enc <file_path>.<extension> <image_path>.<extension> <new_file_path>` | Encode file in .bmp image |
| `dec <image_path>.<extension> <new_file_path>` | Decode file from .bmp image | 
| `help` | Display list of commands |
| `exit` | Exit the application |
	
## To run the app:
Run ./src/app/Main.java
