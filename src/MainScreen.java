import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    private JButton withBtn;
    private JButton withoutBtn;

    public MainScreen() {
        setDisplay();
        addFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }
        });

        withBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readWithParallelism();
            }
        });

        withoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readWithoutParallelism();
            }
        });
    }//End constructor


    private void setDisplay() {
        JFrame frame = new JFrame("File Processing - Word Counter");
        frame.setSize(900, 900);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(mainPanel);
    }//End setDisplay


    private void uploadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
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

    private void readWithoutParallelism() {
        List<File> fileList = getListOfFiles();
        int totalWords = 0;
        int totalTime = 0;
        long startSequential = System.currentTimeMillis();

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
//            withoutTextArea.append("Time taken to read " + file.getName() + ": " + (endFileRead - startFileRead) + " ms\n");

        }
        long endSequential = System.currentTimeMillis();


        withoutTextArea.append("Total words (Sequential): " + totalWords + "\n");
        withoutTextArea.append("Total Time taken (Sequential): " + (endSequential - startSequential) + " ms\n");

    }//End readWithoutParallelism


    private void readWithParallelism() {
        List<File> fileList = getListOfFiles();
        AtomicInteger totalWords = new AtomicInteger(0); //Use AtomicInteger for thread safety
        ExecutorService executor = Executors.newFixedThreadPool(fileList.size()); //Fixed thread pool consisting of how many files are in the List
        long startExec = System.currentTimeMillis(); //Start time for parallel execution

        //Each file is ran using the executor runnable
        for (File file : fileList) {
            executor.submit(newRunnable(file, totalWords));
        }

        //Shutdown the executor and wait for all tasks to finish
        executor.shutdown();

        //Security measure to ensure all tasks are either completed or stopped after a certain time limit
        try {
            //Wait for a maximum of 1 minute for all tasks to finish - if a task did not complete, shutdown
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow(); //Force shutdown if tasks did not finish
                withTextArea.append("A task did not complete.\n");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt(); //Preserve interrupt status
        }

        long endExec = System.currentTimeMillis(); // End time for parallel execution
        withTextArea.append("Total words (Parallel): " + totalWords.get() + "\n");
        withTextArea.append("Total Time taken (Parallel): " + (endExec - startExec) + " ms\n");
    }//End readWithParallelism

    private Runnable newRunnable(File file, AtomicInteger totalWords) {
        return new Runnable() {
            @Override
            public void run() {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        totalWords.addAndGet(line.split("\\s+").length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }//End newRunnable

}//End class