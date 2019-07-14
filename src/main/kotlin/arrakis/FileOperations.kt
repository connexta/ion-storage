package arrakis

interface FileOperations {

    fun getFileInfo(folderName: String, filename: String) {}

    fun newFile(folderName: String, file: kotlin.Any) {}

    fun delteFile(folderName: String, file: kotlin.Any) {}

    fun UpdateFile(folderName: String, file: kotlin.Any) {}
    
}