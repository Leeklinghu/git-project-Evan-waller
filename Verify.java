import java.io.IOException;

public class Verify {
    public static void main(String[] args) throws IOException {
        // System.out.println(GitInitialize.init(false, false, false, false));

        GitInitialize.updateIndexFile("test3.txt");
        // GitInitialize.hashFile("Sha1Test.txt");
        // GitInitialize.updateIndexFile("Sha1Test.txt");
        // System.out.println(GitInitialize.hashFile("Sha1Test.txt"));
    }
}
