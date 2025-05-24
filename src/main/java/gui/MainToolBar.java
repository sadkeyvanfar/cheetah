package gui;

import gui.listener.MainToolbarListener;
import utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a>
 */
class MainToolBar extends JToolBar implements ActionListener {

    private final Map<MainToolbarButton, JButton> buttons = new EnumMap<>(MainToolbarButton.class);
    private final Map<JButton, Runnable> actions = new HashMap<>();

    private MainToolbarListener mainToolbarListener;

    public MainToolBar() {
        setBorder(BorderFactory.createEtchedBorder());
        ResourceBundle bundle = ResourceBundle.getBundle("messages/messages");

        for (MainToolbarButton btn : MainToolbarButton.values()) {
            JButton button = createLocalizedButton(bundle, btn.getKey());
            button.addActionListener(this);
            add(button);

            buttons.put(btn, button);
            actions.put(button, () -> {
                if (mainToolbarListener != null) btn.invoke(mainToolbarListener);
            });
        }

        setStateOfButtons();
    }

    private JButton createLocalizedButton(ResourceBundle bundle, String baseKey) {
        JButton button = new JButton(bundle.getString(baseKey + ".label"));
        button.setIcon(Utils.createIcon(bundle.getString(baseKey + ".iconPath")));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setToolTipText(bundle.getString(baseKey + ".toolTip"));
        return button;
    }

    private void setStateOfButtons() {
        setButtonState(MainToolbarButton.RESUME, false);
        setButtonState(MainToolbarButton.PAUSE, false);
        setButtonState(MainToolbarButton.PAUSE_ALL, true);
        setButtonState(MainToolbarButton.CLEAR, false);
        setButtonState(MainToolbarButton.CLEAR_ALL_COMPLETED, true);
        setButtonState(MainToolbarButton.REJOIN, false);
        setButtonState(MainToolbarButton.REDOWNLOAD, false);
        setButtonState(MainToolbarButton.PROPERTIES, false);
        setButtonState(MainToolbarButton.PREFERENCES, true);
    }

    private void setButtonState(MainToolbarButton buttonKey, boolean enabled) {
        JButton button = buttons.get(buttonKey);
        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    public void setMainToolbarListener(MainToolbarListener listener) {
        Objects.requireNonNull(listener, "mainToolbarListener");
        this.mainToolbarListener = listener;
    }

    public void setStateOfButtonsControl(boolean pauseState, boolean resumeState,
            boolean clearState, boolean reJoinState,
            boolean reDownloadState, boolean propertiesState) {
        setButtonState(MainToolbarButton.PAUSE, pauseState);
        setButtonState(MainToolbarButton.RESUME, resumeState);
        setButtonState(MainToolbarButton.CLEAR, clearState);
        setButtonState(MainToolbarButton.REJOIN, reJoinState);
        setButtonState(MainToolbarButton.REDOWNLOAD, reDownloadState);
        setButtonState(MainToolbarButton.PROPERTIES, propertiesState);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Runnable action = actions.get(e.getSource());
        if (action != null) action.run();
    }

    private enum MainToolbarButton {
        NEW_DOWNLOAD("mainToolBar.newDownloadButton", MainToolbarListener::newDownloadEventOccured),
        RESUME("mainToolBar.resumeButton", MainToolbarListener::resumeEventOccured),
        PAUSE("mainToolBar.pauseButton", MainToolbarListener::pauseEventOccured),
        PAUSE_ALL("mainToolBar.pauseAllButton", MainToolbarListener::pauseAllEventOccured),
        CLEAR("mainToolBar.clearButton", MainToolbarListener::clearEventOccured),
        CLEAR_ALL_COMPLETED("mainToolBar.clearAllCompletedButton", MainToolbarListener::clearAllCompletedEventOccured),
        REJOIN("mainToolBar.reJoinButton", MainToolbarListener::reJoinEventOccured),
        REDOWNLOAD("mainToolBar.reDownloadButton", MainToolbarListener::reDownloadEventOccured),
        PROPERTIES("mainToolBar.propertiesButton", MainToolbarListener::propertiesEventOccured),
        PREFERENCES("mainToolBar.preferencesButton", MainToolbarListener::preferencesEventOccured);

        private final String key;
        private final java.util.function.Consumer<MainToolbarListener> action;

        MainToolbarButton(String key, java.util.function.Consumer<MainToolbarListener> action) {
            this.key = key;
            this.action = action;
        }

        public String getKey() {
            return key;
        }

        public void invoke(MainToolbarListener listener) {
            action.accept(listener);
        }
    }
}
