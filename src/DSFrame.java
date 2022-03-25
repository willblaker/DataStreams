import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.StandardOpenOption.CREATE;
import javax.swing.JFileChooser;

public class DSFrame extends JFrame {
    JPanel mainPnl, topPnl, midPnl, botPnl;
    JLabel titleTxt;
    JButton loadFileBtn, searchFileBtn, quitBtn;
    JTextArea ogTxt, alterTxt;
    JScrollPane ogScrollTxt, alterScrollTxt;

    private ArrayList<String> fileR = new ArrayList<String>();
    private String result;

    public DSFrame(){
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createTopPnl();
        mainPnl.add(topPnl, BorderLayout.NORTH);

        createMidPnl();
        mainPnl.add(midPnl, BorderLayout.CENTER);

        createBotPnl();
        mainPnl.add(botPnl, BorderLayout.SOUTH);

        add(mainPnl);
        setSize(500,600);
        setVisible(true);
    }

    public void createTopPnl(){
        topPnl = new JPanel();
        titleTxt = new JLabel();

        titleTxt.setText("Data Stream Processing");
        titleTxt.setFont(new Font("Sans", Font.PLAIN, 36));
        titleTxt.setHorizontalTextPosition(JLabel.CENTER);
        topPnl.add(titleTxt);
    }

    public void createMidPnl(){
        midPnl = new JPanel();
        midPnl.setLayout(new GridLayout(1,2));
        ogTxt = new JTextArea("",21,32);
        alterTxt = new JTextArea("",21,32);
        ogScrollTxt = new JScrollPane(ogTxt);
        alterScrollTxt = new JScrollPane(alterTxt);

        midPnl.add(ogScrollTxt);
        midPnl.add(alterScrollTxt);
    }

    public void createBotPnl(){
        botPnl = new JPanel();
        botPnl.setLayout(new GridLayout(1,3));
        loadFileBtn = new JButton("Load File");
        searchFileBtn = new JButton("Search File");
        quitBtn = new JButton("Quit");

        loadFileBtn.addActionListener((ActionEvent ae) ->
                {
                    readFile();
                }
        );

        searchFileBtn.addActionListener((ActionEvent ae) ->
                {
                    String result = (String)JOptionPane.showInputDialog(
                            mainPnl,
                            "Insert Search Word",
                            "Data Stream",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            null
                    );
                    filterString(result);
                }
        );

        quitBtn.addActionListener((ActionEvent ae) ->
                {
                    System.exit(0);
                }
        );

        botPnl.add(loadFileBtn);
        botPnl.add(searchFileBtn);
        botPnl.add(quitBtn);
    }

    public void readFile(){
            JFileChooser chooser = new JFileChooser();
            File selectedFile;
            String rec = "";

            try
            {
                // uses a fixed known path:
                //  Path file = Paths.get("c:\\My Documents\\PersonTestData.txt");

                // use the toolkit to get the current working directory of the IDE
                // Not sure if the toolkit is thread safe...
                File workingDirectory = new File(System.getProperty("user.dir"));

                // Typiacally, we want the user to pick the file so we use a file chooser
                // kind of ugly code to make the chooser work with NIO.
                // Because the chooser is part of Swing it should be thread safe.
                chooser.setCurrentDirectory(workingDirectory);
                // Using the chooser adds some complexity to the code.
                // we have to code the complete program within the conditional return of
                // the filechooser because the user can close it without picking a file

                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    // Typical java pattern of inherited classes
                    // we wrap a BufferedWriter around a lower level BufferedOutputStream
                    InputStream in =
                            new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));

                    // Finally we can read the file LOL!
                    int line = 0;
                    while(reader.ready())
                    {
                        rec = reader.readLine();
                        line++;
                        // echo to screen
                        fileR.add(rec);
                        ogTxt.append(rec + "\n");
                    }
                    reader.close(); // must close the file to seal it and flush buffer
                    System.out.println("\n\nData file read!");
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("File not found!!!");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void filterString(String f){
            fileR.stream()
                    .filter(s -> s.contains(f))
                    .forEach(s -> alterTxt.append(s + "\n"));
        }
}
