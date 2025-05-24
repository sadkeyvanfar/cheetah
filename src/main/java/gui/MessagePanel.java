package gui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import service.StatusMessageAppender;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a>
 */
class MessagePanel extends JPanel {

    private static final Logger logger = Logger.getLogger(MessagePanel.class);
    private static final Logger messageLogger = Logger.getLogger("message");

    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages/messages");

    private final JTextArea messageTextArea = new JTextArea();
    private final JButton clearButton = new JButton(bundle.getString("messagePanel.clearButton.name"));

    public MessagePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initClearButton();
        initMessageArea();
        initLogging();
    }

    private void initClearButton() {
        clearButton.addActionListener(e -> messageTextArea.setText(""));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(createEmptyTitledBorder());

        topPanel.add(clearButton);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initMessageArea() {
        messageTextArea.setEditable(false);
        messageTextArea.setFont(new Font("Dialog", Font.PLAIN, 16));
        messageTextArea.setBorder(createCompoundBorder("Logs"));

        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setBorder(createEmptyTitledBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void initLogging() {
        PatternLayout layout = new PatternLayout("%d %-5p [%c{1}] %m%n");
        StatusMessageAppender appender = new StatusMessageAppender(messageTextArea);
        appender.setLayout(layout);
        appender.activateOptions();
        messageLogger.addAppender(appender);
    }

    private Border createCompoundBorder(String title) {
        Border inner = BorderFactory.createTitledBorder(title);
        Border outer = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        return BorderFactory.createCompoundBorder(outer, inner);
    }

    private Border createEmptyTitledBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                BorderFactory.createTitledBorder("")
        );
    }
}
