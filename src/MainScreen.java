import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainScreen {
    private JPanel mainPanel;
    private JPanel filePanel;
    private JPanel noParallelismPanel;
    private JPanel parallelismPanel;
    private JButton withParallelismBtn;
    private JButton withoutParallelismBtn;
    private JButton addFileBtn;
    private JTextArea fileTextArea;


    public MainScreen(){
        setDisplay();
        addFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadPDF();
            }
        });


        withParallelismBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }//End constructor


    private void setDisplay() {
        JFrame frame = new JFrame("File Processing - Word Counter");
        frame.setSize(800,800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setContentPane(mainPanel);
    }//End setDisplay


    private void uploadPDF(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileTextArea.append(selectedFile.getAbsolutePath() + "\n");
        } else {
            JOptionPane.showMessageDialog(null, "Please Select a File");
        }
    }//End uploadPDF

    private List<File> getListOfFiles() {
        List<File> uploadedFilesList = new ArrayList<>();

        //Get the text from the JTextArea
        String text = fileTextArea.getText();
        //Split the text into lines
        String[] filePaths = text.split("\\n");

        //Convert each line to a File object and add to the list
        for (String filePath : filePaths) {
            if (!filePath.trim().isEmpty()) { // Ensure it's not an empty line
                uploadedFilesList.add(new File(filePath.trim()));
            }
        }
        return uploadedFilesList;
    }//End getListOfFiles


    private void readWithParallelism(){
        List<File> fileList = getListOfFiles();


    }//End readWithParallelism

    private void readWithoutParallelism(){

    }//End readWithoutParallelism

    private int countWords(String text){
        if(text == null || text.isEmpty()){
            return 0;
        }
        String[] words = text.split("\\s+");
        return words.length;
    }//End countWords







}//End class
