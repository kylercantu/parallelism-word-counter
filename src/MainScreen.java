import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainScreen {
    private JPanel mainPanel;
    private JPanel filePanel;
    private JPanel withParallelismPanel;
    private JPanel withoutParallelismPanel;
    private JButton addFileBtn;
    private JTextArea fileTextArea;
    private JTextArea withoutTextArea;
    public JTextArea withTextArea;
    private JButton runBtn;

    //AtomicInteger for thread-safe counting when Multithreading
    private static AtomicInteger totalWords = new AtomicInteger(0);
    private static AtomicInteger totalTime = new AtomicInteger(0);


    public MainScreen(){
        setDisplay();
        addFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }
        });

        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readWithoutParallelism();
                readWithParallelism();
            }
        });
    }//End constructor


    private void setDisplay() {
        JFrame frame = new JFrame("File Processing - Word Counter");
        frame.setSize(900,900);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(mainPanel);
    }//End setDisplay


    private void uploadFile(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileTextArea.append(selectedFile.getAbsolutePath() + "\n");
        } else {
            JOptionPane.showMessageDialog(null, "Please Select a File");
        }
    }//End uploadPDF

    public List<File> getListOfFiles() {
        List<File> uploadedFilesList = new ArrayList<>();

        //Get the text from the JTextArea
        String text = fileTextArea.getText();
        //Split the text into lines
        String[] filePaths = text.split("\\n");

        //Convert each line to a File object and add to the list
        for (String filePath : filePaths) {
            if (!filePath.trim().isEmpty()) { //Ensure it's not an empty line
                uploadedFilesList.add(new File(filePath.trim()));
            }
        }
        return uploadedFilesList;
    }//End getListOfFiles


    private void readWithoutParallelism(){
        List<File> fileList = getListOfFiles();
        int totalWords = 0;
        int totalTime = 0;
//        long startSequential = System.currentTimeMillis();

        for (File file : fileList) {
            long startFileRead = System.currentTimeMillis();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalWords += line.split("\\s+").length;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endFileRead = System.currentTimeMillis();
            totalTime += (endFileRead - startFileRead);
            withoutTextArea.append("Time taken to read " + file.getName() + ": " + (endFileRead - startFileRead) + " ms\n");

        }
//        long endSequential = System.currentTimeMillis();


        withoutTextArea.append("Total words (Sequential): " + totalWords + "\n");
        withoutTextArea.append("Total Time taken (Sequential): " + totalTime + " ms");

    }//End readWithoutParallelism

    public void readWithParallelism(){

        List<File> fileList = getListOfFiles();
        List<Thread> threadList = new ArrayList<>();
        totalWords.set(0);
        totalTime.set(0);


        for(File file: fileList){
            Multithread multithread = new Multithread(file);
            threadList.add(multithread);
            multithread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        withTextArea.append("Total words (Parallel): " + totalWords.get() + "\n");
        withTextArea.append("Total Time taken (Parallel): " + totalTime.get() + " ms\n");


    }//End readWithParallelism

    class Multithread extends Thread {

        private File file;

        public Multithread(File file){
            this.file = file;
        }

        //When thread.start() is called, the instructions in run() executes
        @Override
        public void run(){
            List<File> fileList = getListOfFiles();
            int words = 0;
            int time = 0;
            long startFileRead = System.currentTimeMillis();


            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    words += line.split("\\s+").length;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long endFileRead = System.currentTimeMillis();
            time += (endFileRead - startFileRead);

            totalTime.addAndGet(time);
            totalWords.addAndGet(words);

            withTextArea.append("Time taken to read " + file.getName() + ": " + (endFileRead - startFileRead) + " ms\n");

        }//End run


    }//End Multithread class


}//End class
