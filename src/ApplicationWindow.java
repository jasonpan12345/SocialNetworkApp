import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ApplicationWindow implements ActionListener {

    JFrame frame = new JFrame();
    private JButton choice1Button;
    private JPanel panel1;
    private JButton choice2Button;
    private JButton choice3Button;
    private JButton choice4Button;
    private JButton choice5Button;
    private JButton quitBtn;

    public ApplicationWindow() {
        // Set frame properties
        frame.add(panel1);

        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);


        choice1Button.addActionListener(this);
        choice2Button.addActionListener(this);
        choice3Button.addActionListener(this);
        choice4Button.addActionListener(this);
        choice5Button.addActionListener(this);

        quitBtn.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) { // detects when user chooses an action

        if(e.getSource() == choice1Button) { // TODO: implement choice 1
            frame.dispose();
            new Choice1Window();
        }
        if(e.getSource() == choice2Button) {
            frame.dispose();
            new Choice2Window();
        }
        if(e.getSource() == choice3Button) { // TODO: implement choice 3
            frame.dispose();
            new Choice3Window();
        }
        if(e.getSource() == choice4Button) { // TODO: implement choice 4
            frame.dispose();
            new Choice4Window();
        }
        if(e.getSource() == choice5Button) { // TODO: implement choice 5
            frame.dispose();
            new Choice5Window();
        }
        if(e.getSource() == quitBtn) {
            System.exit(0);
        }
    }
}
