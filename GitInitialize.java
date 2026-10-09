import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GitInitialize {

    public static void main(String[] args) {
        // System.out.println("Testing init()");
        // System.out.println(init(false, false, false, false));

        // System.out.println();

        // System.out.println("Testing hashFile()");
        // try {
        // System.out.println(hashFile("Hello.txt"));
        // } catch (IOException e) {
        // System.out.println("File error: " + e.getMessage());
        // }

        // System.out.println();

        // System.out.println("Testing createBlob()");
        // System.out.println(createBlob("Hello.txt"));

        // System.out.println();

        // System.out.println("Testing updateIndexFile()");
        // System.out.println(updateIndexFile("Hello.txt"));
        // System.out.println(updateIndexFile("Hello1.txt"));

    }

    // This init method does four things:
    // 1. Create a git/ directory if it doesn't already exist
    // 2. Create an objects/ directory inside of git/ if it doesn't already exist
    // 3. Create a file named index inside git/ if it does not already exist, with no extension
    // 4. Create a file named HEAD inside git/ if it does not already exist, with no extension
    // all these prarmeters represent whether each file/directory already exists in ., they will be
    // checked throughout the method and all start as false
    public static String init(boolean gitExists, boolean objectsExists, boolean indexExists,
            boolean HEADExists) {

        try {

            // initialize the git directory by first creating a File object called git
            File git = new File("git");

            // make sure a directory named git doesn't already exist
            // if it doesn't exist set gitExists to false
            // otherwise set gitExists to true
            if (!git.exists()) {
                gitExists = false;
                git.mkdir();
            } else {
                gitExists = true;
            }

            // initialize the objects directory inside of git by first creating a File Object called
            // objects
            File objects = new File(git, "objects");

            // make sure a directory named objects doesn't already exist
            // if it doesn't set objectsExists to false
            // otherwise set objectsExists to true
            if (!objects.exists()) {
                objectsExists = true;
                objects.mkdir();
            } else {
                objectsExists = true;
            }

            // initialize the index file by first creating a File object inside of git called index
            File index = new File(git, "index");

            // make sure a file named index doesn't already exist
            // if it doesn't exist set indexExists to false
            // otherwise set indexExists to true
            if (!index.exists()) {
                indexExists = false;
                index.createNewFile();
            } else {
                indexExists = true;
            }

            // initialize the HEAD file by first creating a File object inside of git called HEAD
            File HEAD = new File(git, "HEAD");

            // make sure a file named HEAD doesn't already exist
            // if it doesn't exist set HEADExists to false
            // otherwise set HEADExists to true
            if (!HEAD.exists()) {
                HEADExists = false;
                HEAD.createNewFile();
            } else {
                HEADExists = true;
            }

            // if all four of the boolean parameters are true return: Git Repository Already Exists
            // otherwise return: Git Repository Created
            if (gitExists == true && objectsExists == true && indexExists == true
                    && HEADExists == true) {
                return "Git Repository Already Exists";
            } else {
                return "Git Repository Created";
            }

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return "";
        }
    }

    /**
     * Reads the file at filePath and returns its SHA-256 hash as a lowercase 64-character
     * hexadecimal string.
     */
    public static String hashFile(String filePath) throws IOException {

        // Create the SHA-1 calculator
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-1 algorithm is not available");
            return "";
        }

        // create the file reader for filePath
        FileReader reader = new FileReader(filePath);

        int character;
        // while the current character being read is valid, convert it into a byte and digest that
        // specific byte
        while ((character = reader.read()) != -1) {
            digest.update((byte) character);
        }

        // close the reader
        reader.close();

        // finish hash calculations
        byte[] hashBytes = digest.digest();

        String hash = "";

        // write each hash byte into a String
        for (int i = 0; i < hashBytes.length; i++) {
            hash += String.format("%02x", hashBytes[i]);
        }

        return hash;
    }

    // This method that creates a blob for a given file
    // It takes in the file path of an existing file
    // It computes the SHA-1 hash of the file's contents
    // Then creates a new file in git/objects/ with that hash as its filename
    // Then it writes the original file's content into the blob, byte for byte, unchanged
    // It returns the hashed file name of the new file created inside of git/objects/
    public static String createBlob(String filePath) {

        try {
            // create the reader for the file we want to copy
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));

            // Instantiate the file's contents as an empty StringBuilder (This is the blob)
            StringBuilder fileContents = new StringBuilder();

            // Instantiate the String variable line: this represents the current line in the file
            // we're reading
            String currLine;

            // Add every line from the file we're reading to fileContents
            while ((currLine = fileReader.readLine()) != null) {
                fileContents.append(currLine);
            }

            fileReader.close();

            // Instantiate the name of newFile as the hash of filePath
            String newFileName = hashFile(filePath);

            // create the new file
            File newFile = new File("git/objects/", newFileName);

            // create the writer for newFile and write the blob into it
            FileWriter newFileWriter = new FileWriter(newFile);
            newFileWriter.write(fileContents.toString());

            newFileWriter.close();

            // return the name of the newly created file
            return newFileName;

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return "";
        }
    }

    // This method adds a file's entry to git/index
    // It takes in a String parameter filePath that represents, u guessed it!, the path of the file
    // we're staging
    // Each entry follows this format: Each line is the SHA-1 hash, one space, and the path relative
    // to the project root. Nothing else goes on the line.
    // It returns the entry added to index
    public static String updateIndexFile(String filePath) {

        try {

            // turn index into a Java File Object
            File index = new File("git/index");

            StringBuilder entry = new StringBuilder();

            // the first part of the entry is the file's has
            String hash = hashFile(filePath);

            // append hash to entry
            entry.append(hash);

            // append a space to entry
            entry.append(" ");

            // append the path to entry
            entry.append(filePath);



            // this is the contents in the index file before we write to it
            // we'll want to read everything in file, attach it to this variable, write this
            // variable into file, write a new line into file, then write entry into file
            StringBuilder indexContents = new StringBuilder();

            String line;

            BufferedReader fileReader = new BufferedReader(new FileReader("git/index"));

            // Add every line from the file we're reading to fileContents
            while ((line = fileReader.readLine()) != null) {
                indexContents.append(line);
            }



            // create the fileWriter for index
            FileWriter indexWriter = new FileWriter(index);

            // write indexContents into index
            // if there is something already inside the file write a new line to it before entry
            if (indexContents.length() > 0) {
                indexWriter.write(indexContents.toString() + "\n" + entry.toString());
            } else {
                indexWriter.write(indexContents.toString() + entry.toString());
            }


            // close the file reader
            fileReader.close();

            // close indexWriter
            indexWriter.close();


            // write entry into index
            // indexWriter.write(entry.toString());

            // return entry.toString()
            return entry.toString();

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return "";
        }
    }
}
