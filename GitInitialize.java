import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;

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
    public static String init(boolean gitDirectoryExists, boolean objectsDirectoryExists,
            boolean indexFileExists, boolean HEADExists) {

        try {

            // initialize the git directory by first creating a File object called git
            File gitDirectory = new File("git");

            // make sure a directory named git doesn't already exist
            // if it doesn't exist set gitDirectoryExists to false
            // otherwise set gitDirectoryExists to true
            if (!gitDirectory.exists()) {
                gitDirectoryExists = false;
                gitDirectory.mkdir();
            } else {
                gitDirectoryExists = true;
            }

            // initialize the objects directory inside of git by first creating a File Object called
            // objects
            File objects = new File(gitDirectory, "objects");

            // make sure a directory named objects doesn't already exist
            // if it doesn't set objectsDirectoryExists to false
            // otherwise set objectsDirectoryExists to true
            if (!objects.exists()) {
                objectsDirectoryExists = false;
                objects.mkdir();
            } else {
                objectsDirectoryExists = true;
            }

            // initialize the index file by first creating a File object inside of git called index
            File index = new File(gitDirectory, "index");

            // make sure a file named index doesn't already exist
            // if it doesn't exist set indexFileExists to false
            // otherwise set indexFileExists to true
            if (!index.exists()) {
                indexFileExists = false;
                index.createNewFile();
            } else {
                indexFileExists = true;
            }

            // initialize the HEAD file by first creating a File object inside of git called HEAD
            File HEAD = new File(gitDirectory, "HEAD");

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
            if (gitDirectoryExists == true && objectsDirectoryExists == true
                    && indexFileExists == true && HEADExists == true) {
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
        FileInputStream reader = new FileInputStream(filePath);

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
            int c;

            // Add every line from the file we're reading to fileContents
            while ((c = fileReader.read()) != -1) {
                fileContents.append((char) c);
            }

            fileReader.close();

            // Instantiate the name of newFile as the hash of filePath
            String blobHash = hashFile(filePath);

            if (blobHash.isEmpty()) {
                return "";
            }

            // create the new file
            File blobFile = new File("git/objects/", blobHash);

            // create the writer for blobFile and write the blob into it
            FileWriter newFileWriter = new FileWriter(blobFile);
            newFileWriter.write(fileContents.toString());

            newFileWriter.close();

            // return the name of the newly created file
            return blobHash;

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
            File indexFile = new File("git/index");

            Path root = new File("").toPath().toAbsolutePath().normalize();
            Path file = new File(filePath).toPath().toAbsolutePath().normalize();

            if (!file.startsWith(root)) {
                System.out.println("File must be inside the project.");
                return "";
            }

            String relativePath = root.relativize(file).toString();
            relativePath = relativePath.replace(File.separatorChar, '/');

            StringBuilder entry = new StringBuilder();

            // the first part of the entry is the file's has
            String hash = hashFile(filePath);

            // append hash to entry
            entry.append(hash);

            // append a space to entry
            entry.append(" ");

            // append the path to entry
            entry.append(relativePath);



            // this is the contents in the index file before we write to it
            // we'll want to read everything in file, attach it to this variable, write this
            // variable into file, write a new line into file, then write entry into file
            StringBuilder indexContents = new StringBuilder();
            boolean found = false;
            String line;

            BufferedReader fileReader = new BufferedReader(new FileReader("git/index"));

            // Add every line from the file we're reading to fileContents
            while ((line = fileReader.readLine()) != null) {
                String[] parts = line.split(" ", 2);

                if (parts.length == 2 && parts[1].equals(relativePath)) {
                    if (parts[0].equals(hash)) {
                        fileReader.close();
                        return entry.toString();
                    }

                    indexContents.append(entry.toString());
                    found = true;
                } else {
                    indexContents.append(line);
                }

                indexContents.append("\n");
            }

            fileReader.close();

            if (!found) {
                indexContents.append(entry.toString());
                indexContents.append("\n");
            }

            String blobHash = createBlob(filePath);

            if (blobHash.isEmpty()) {
                return "";
            }

            // create the fileWriter for index
            FileWriter indexWriter = new FileWriter(indexFile);

            // write indexContents into index
            // if there is something already inside the file write a new line to it before entry
            indexWriter.write(indexContents.toString());

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

    public static String createTree(ArrayList<String> workingList, String dirPath)
            throws IOException {
        StringBuilder treeContents = new StringBuilder();

        for (int i = 0; i < workingList.size(); i++) {
            String[] parts = workingList.get(i).split(" ", 3);
            String type = parts[0];
            String hash = parts[1];
            String path = parts[2];

            int lastSlash = -1;
            for (int j = path.length() - 1; j >= 0; j--) {
                if (path.substring(j, j + 1).equals("/")) {
                    lastSlash = j;
                    break;
                }
            }
            String directory = "";
            String name = path;

            if (lastSlash != -1) {
                directory = path.substring(0, lastSlash);
                name = path.substring(lastSlash + 1);
            }

            if (directory.equals(dirPath)) {
                treeContents.append(type + " " + hash + " " + name + "\n");
            }
        }

        File tempfile = new File("git/objects/newTree.txt");
        FileWriter writer = new FileWriter(tempfile, StandardCharsets.UTF_8);
        writer.write(treeContents.toString());
        writer.close();

        String treeHash = hashFile(tempfile.getPath());

        if (!tempfile.delete()) {
            throw new IOException("Could not delete newTree.txt");
        }

        File treeFile = new File("git/objects/" + treeHash);
        FileWriter treeWriter = new FileWriter(treeFile, StandardCharsets.UTF_8);
        treeWriter.write(treeContents.toString());
        treeWriter.close();

        return treeHash;
    }

    public static String createTreeFromIndex() throws IOException {
        ArrayList<String> workingList = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new FileReader("git/index"));
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.isBlank()) {
                workingList.add("blob " + line);
            }
        }

        reader.close();

        while (true) {
            for (int i = 0; i < workingList.size() - 1; i++) {
                int smallestIndex = i;

                for (int j = i + 1; j < workingList.size(); j++) {
                    String currentPath = workingList.get(j).split(" ", 3)[2];
                    String smallestPath = workingList.get(smallestIndex).split(" ", 3)[2];

                    if (currentPath.compareTo(smallestPath) < 0) {
                        smallestIndex = j;
                    }
                }

                String temp = workingList.get(i);
                workingList.set(i, workingList.get(smallestIndex));
                workingList.set(smallestIndex, temp);
            }

            String deepestDirectory = "";
            int greatestDepth = 0;

            for (int i = 0; i < workingList.size(); i++) {
                String path = workingList.get(i).split(" ", 3)[2];
                int depth = 0;

                for (int j = 0; j < path.length(); j++) {
                    if (path.substring(j, j + 1).equals("/")) {
                        depth++;
                    }
                }

                if (depth > greatestDepth) {
                    greatestDepth = depth;
                    int lastSlash = path.lastIndexOf("/");
                    deepestDirectory = path.substring(0, lastSlash);
                }
            }

            if (greatestDepth == 0) {
                if (workingList.size() == 1) {
                    String[] parts = workingList.get(0).split(" ", 3);

                    if (parts[0].equals("tree")) {
                        return parts[1];
                    }
                }

                return createTree(workingList, "");
            }

            String treeHash = createTree(workingList, deepestDirectory);

            for (int i = workingList.size() - 1; i >= 0; i--) {
                String path = workingList.get(i).split(" ", 3)[2];
                int lastSlash = path.lastIndexOf("/");
                String directory = "";

                if (lastSlash != -1) {
                    directory = path.substring(0, lastSlash);
                }

                if (directory.equals(deepestDirectory)) {
                    workingList.remove(i);
                }
            }

            workingList.add("tree " + treeHash + " " + deepestDirectory);
        }
    }
}
