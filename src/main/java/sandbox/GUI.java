package sandbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class GUI {
    public static void main(String[] args) {
        Frame frame = new Frame("Close me!");
        frame.addWindowListener(new FrameCloser(frame));

        Button button = new Button("Click me to see the current date!");
        button.addActionListener(new ButtonUpdater(button));
        frame.add(button);

        frame.pack();
        frame.show();
    }
}

class FrameCloser extends WindowAdapter {
    private final Frame frame;

    FrameCloser(Frame frame) {
        this.frame = frame;
    }

    public void windowClosing(WindowEvent event) {
        frame.dispose();
    }
}

class ButtonUpdater implements ActionListener {
    private final Button button;

    ButtonUpdater(Button button) {
        this.button = button;
    }

    public void actionPerformed(ActionEvent event) {
        button.setLabel(new Date().toString());
    }
}
