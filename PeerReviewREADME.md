updateIndexFile(filePath): Adds or updates a file’s entry in git/index
 - Checks that the file is inside the project and gets its relative path and hash.
 - Leaves the index unchanged if the same path and hash are already listed.
 - Otherwise, creates a blob and adds or replaces the entry using the format “hash path.”

createTree(workingList, dirPath): Creates a tree file for a directory
 - Finds entries in workingList that belong directly to dirPath.
 - Lists each entry’s type, hash, and name in the tree.
 - Saves the tree in git/objects using its hash as the filename with  returns the hash.

createTreeFromIndex(): Builds trees from the files listed in git/index
 - Reads the index file into an Arraylist and sorts entries by path.
 - Creates trees for the deepest directories first, replacing their entries with tree entries.
 - Continues working upward and returns the final tree hash.