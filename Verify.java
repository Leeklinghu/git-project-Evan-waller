import java.io.IOException;
import java.util.ArrayList;

public class Verify {
    public static void main(String[] args) throws IOException {
        System.out.println(GitInitialize.init(false, false, false, false));

        // GitInitialize.createBlob("hello.txt");
        // GitInitialize.hashFile("Sha1Test.txt");
        // GitInitialize.updateIndexFile("Sha1Test.txt");
        // System.out.println(GitInitialize.hashFile("Sha1Test.txt"));

        // ArrayList<String> workingList = new ArrayList<String>();
        // workingList.add("blob 45ttusjlk534lkj5 docs/Hello.txt");
        // workingList.add("blob 74hjk6546j docs/World.txt");
        // workingList.add("blob 4j6354jl README.md");
        // String hash = GitInitialize.createTree(workingList, "docs");
        // System.out.println(hash);

        GitInitialize.updateIndexFile("Folder/Stuff/Hello.txt");
        GitInitialize.updateIndexFile("Folder/Hello.txt");
   

        System.out.println(GitInitialize.createTreeFromIndex());

        // System.out.println(GitInitialize.updateIndexFile("folder/Hello.txt"));
        // System.out.println(GitInitialize.updateIndexFile("folder/Hello.txt"));
        // System.out.println(GitInitialize.updateIndexFile("folder/Stuff/Hello.txt"));
        // System.out.println(GitInitialize.updateIndexFile("folder/Hello2.txt"));

    }
}
