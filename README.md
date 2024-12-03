Parallism-Word-Counter is a user-friendly GUI application designed to count words across multiple text files efficiently. Users can upload any number of text files and choose between two processing modes: sequential, where files are processed one at a time, or parallel, where each file is processed simultaneously using one thread per file. The output provides the total word count across all files and the total execution time. This program demonstrates the advantages of parallel processing in improving execution efficiency compared to sequential execution, highlighting how parallelism can significantly reduce processing time for large or numerous files.

How to Run:
1) Clone or Download this project and open it in your choice IDE
   
2) Run the Program and a GUI will open.
![Screenshot 2024-12-02 151010](https://github.com/user-attachments/assets/2ce62b16-e4d6-48bd-b967-c10342d511ad)

3) Find the text files in the project's directory or download the text files in the link below to use for testing.
    [Text Files to Use For Testing] (https://github.com/kylercantu/parallelism-word-counter/tree/main/testable-text-files)
   
4) After obtaining the text files to use for testing, click the "ADD FILE" button which will open up a second screen that will allow you to navigate and find those files. After clicking the file that you want to use, the path will be displayed in the box above the button.

5) Once the text files are uploaded, choose either the button "RUN PARALLELISM" to run the program using parallelism or the button "RUN SEQUENTIAL" to run the program sequentially.
   
6) Depending on which button you clicked, the total words and total execution time will be displayed in their respected boxes above the button.
