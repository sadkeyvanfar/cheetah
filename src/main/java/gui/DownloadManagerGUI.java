/*
 * Cheetah - A Free Fast Downloader
 *
 * Copyright © 2015 Saeed Kayvanfar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gui;

import concurrent.DownloadExecutor;
import controller.DialogAuthenticator;
import enums.ConnectionType;
import enums.DownloadStatus;
import enums.ProtocolType;
import gui.controller.DownloadController;
import gui.controller.DownloadControllerImpl;
import gui.listener.*;
import gui.preference.PreferenceDialog;
import model.Download;
import model.dto.*;
import model.httpImpl.HttpDownload;
import model.httpsImpl.HttpsDownload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import utils.*;
import utils.LookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.net.Authenticator;
import java.util.*;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/10/2015
 */

// implements Observer
public class DownloadManagerGUI extends JFrame implements ActionListener {

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Logger messageLogger = Logger.getLogger("message");

    private final ResourceBundle messagesBundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N
    private final ResourceBundle defaultPreferencesBundle = java.util.ResourceBundle.getBundle("defaultPreferences"); // NOI18N

    private MainToolBar mainToolbar;
    private CategoryPanel categoryPanel;
    private DownloadPanel downloadPanel;
    private MessagePanel messagePanel;
    private JSplitPane mainSplitPane;
    private StatusPanel statusPanel;
    private AddNewDownloadDialog addNewDownloadDialog;
    private PreferenceDialog preferenceDialog;
    private Preferences preferences;
    private AboutDialog aboutDialog;

    // Menu Items
    private JMenuItem exportDataItem;
    private JMenuItem importDataItem;
    private JMenuItem exitItem;
    private JMenuItem prefsItem;

    private JMenuItem newDownloadItem;
    private JMenuItem openItem;
    private JMenuItem openFolderItem;
    private JMenuItem pauseItem;
    private JMenuItem pauseAllItem;
    private JMenuItem resumeItem;
    private JMenuItem clearItem;
    private JMenuItem clearAllCompletedItem;
    private JMenuItem reJoinItem;
    private JMenuItem reDownloadItem;
    private JMenuItem moveToQueueItem;
    private JMenuItem removeFromQueueItem;
    private JMenuItem propertiesItem;

    private JMenuItem aboutItem;

    // Constructor for download Manager.
    public DownloadManagerGUI(String name) {
        super(name);

        setLayout(new BorderLayout());

        preferences = Preferences.userRoot().node("db");
        final PreferencesDTO preferencesDTO = getPreferences();

        LookAndFeel.setLaf(preferencesDTO.getPreferencesInterfaceDTO().getLookAndFeelName());

        createFileHierarchy();

        mainToolbar = new MainToolBar();
        categoryPanel = new CategoryPanel(preferencesDTO.getPreferencesSaveDTO().getPreferencesDirectoryCategoryDTOs());
        DownloadController downloadController = new DownloadControllerImpl(preferencesDTO.getPreferencesSaveDTO().getDatabasePath(), preferencesDTO.getPreferencesConnectionDTO().getConnectionTimeOut(),
                preferencesDTO.getPreferencesConnectionDTO().getReadTimeOut());
        downloadPanel = new DownloadPanel(this, downloadController);
        messagePanel = new MessagePanel();
        JTabbedPane mainTabPane = new JTabbedPane();
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryPanel, mainTabPane);
        mainSplitPane.setOneTouchExpandable(true);
        statusPanel = new StatusPanel();
        addNewDownloadDialog = new AddNewDownloadDialog(this);

        mainTabPane.addTab(messagesBundle.getString("downloadManagerGUI.mainTabPane.downloadPanel"), downloadPanel);
        mainTabPane.addTab(messagesBundle.getString("downloadManagerGUI.mainTabPane.messagePanel"), messagePanel);

        preferenceDialog = new PreferenceDialog(this, preferencesDTO);

        aboutDialog = new AboutDialog(this);

        categoryPanel.setCategoryPanelListener((fileExtensions, downloadCategory) -> downloadPanel.setDownloadsByDownloadPath(fileExtensions, downloadCategory));

        addNewDownloadDialog.setAddNewDownloadListener(textUrl -> {
            Objects.requireNonNull(textUrl, "textUrl");
            if (textUrl.equals(""))
                throw new IllegalArgumentException("textUrl is empty");

            String downloadName;
            try {
                downloadName = ConnectionUtil.getRealFileName(textUrl);
            } catch (IOException e) {
                logger.error("Can't get real name of file that you want to download." + textUrl);
                messageLogger.error("Can't get real name of file that you want to download." + textUrl);
                downloadName = ConnectionUtil.getFileName(textUrl);
            }
            String fileExtension =  FilenameUtils.getExtension(downloadName);
            File downloadPathFile = new File(preferencesDTO.getPreferencesSaveDTO().getPathByFileExtension(fileExtension));
            File downloadRangeFile = new File(preferencesDTO.getPreferencesSaveDTO().getTempDirectory());
            int maxNum = preferencesDTO.getPreferencesConnectionDTO().getMaxConnectionNumber();

            Download download = null;

            List<Download> downloads = downloadPanel.getDownloadList();
            String properDownloadName = getProperNameForDownload(downloadName, downloads, downloadPathFile);

            // todo must set stretegy pattern
            switch (ProtocolType.valueOfByDesc(textUrl.getProtocol())) {
                case HTTP:
                    download = new HttpDownload(downloadPanel.getNextDownloadID(), textUrl, properDownloadName, maxNum,
                            downloadPathFile, downloadRangeFile, ProtocolType.HTTP);
                    break;
                case FTP:
                    // todo must be created ...
                    break;
                case HTTPS:
                    download = new HttpsDownload(downloadPanel.getNextDownloadID(), textUrl, properDownloadName, maxNum,
                            downloadPathFile, downloadRangeFile, ProtocolType.HTTPS);
                    break;
            }

            downloadPanel.addDownload(download);
        });

        // Add panels to display.
        add(mainToolbar, BorderLayout.PAGE_START);
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.PAGE_END);

        setJMenuBar(initMenuBar());


        mainToolbar.setMainToolbarListener(new MainToolbarListener() {
            @Override
            public void newDownloadEventOccured() {
                addNewDownloadDialog.setVisible(true);
                addNewDownloadDialog.onPaste();
            }

            @Override
            public void pauseEventOccured() {
                downloadPanel.actionPause();
                mainToolbar.setStateOfButtonsControl(false, false, false, false, false, true); // canceled
            }

            @Override
            public void resumeEventOccured() {
                downloadPanel.actionResume();
            }

            @Override
            public void pauseAllEventOccured() {
                downloadPanel.actionPauseAll();
            }

            @Override
            public void clearEventOccured() {
                downloadPanel.actionClear();
            }

            @Override
            public void clearAllCompletedEventOccured() {
                downloadPanel.actionClearAllCompleted();
            }

            @Override
            public void reJoinEventOccured() {
                downloadPanel.actionReJoinFileParts();
            }

            @Override
            public void reDownloadEventOccured() {
                downloadPanel.actionReDownload();
            }

            @Override
            public void propertiesEventOccured() {
                downloadPanel.actionProperties();
            }

            @Override
            public void preferencesEventOccured() {
                preferenceDialog.setVisible(true);
            }
        });

        downloadPanel.setDownloadPanelListener(new DownloadPanelListener() {
            @Override
            public void stateChangedEventOccured(DownloadStatus downloadState) {
                updateButtons(downloadState);
            }

            @Override
            public void downloadSelected(Download download) {
                statusPanel.setStatus(download.getDownloadName());
            }
        });

        preferenceDialog.setPreferencesListener(new PreferencesListener() {
            @Override
            public void preferencesSet(PreferencesDTO preferenceDTO) {
                setPreferencesOS(preferenceDTO);
            }

            @Override
            public void preferenceReset() {
                PreferencesDTO resetPreferencesDTO = getPreferences();
                preferenceDialog.setPreferencesDTO(resetPreferencesDTO);
                categoryPanel.setTreeModel(resetPreferencesDTO.getPreferencesSaveDTO().getPreferencesDirectoryCategoryDTOs());
            }

            @Override
            public void preferenceDefaults() {
                PreferencesDTO defaultPreferenceDTO = new PreferencesDTO();
                resetAndSetPreferencesDTOFromConf(defaultPreferenceDTO);
                preferenceDialog.setPreferencesDTO(defaultPreferenceDTO);
                categoryPanel.setTreeModel(defaultPreferenceDTO.getPreferencesSaveDTO().getPreferencesDirectoryCategoryDTOs());
            }
        });

        // Handle window closing events.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int action = JOptionPane.showConfirmDialog(DownloadManagerGUI.this,
                        "Do you realy want to exit the application?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION) {
                    logger.info("Window Closing");
                    downloadPanel.actionPauseAll();

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        AddNewDownloadDialog.shutdownThreads();
                        DownloadExecutor.shutdown();
                    }));
                    dispose();
                    System.gc();
                }
            }
        });

        Authenticator.setDefault(new DialogAuthenticator(this));

        setIconImage(Utils.createIcon(messagesBundle.getString("downloadManagerGUI.mainFrame.iconPath")).getImage());

        setMinimumSize(new Dimension(640, 480));
        // Set window size.
        pack();
        setSize(900, 580);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                downloadPanel.actionPauseAll();
                AddNewDownloadDialog.shutdownThreads();
            }
        });
    }

    /**
     * Specify real name for file to download that was not repeated in download manger and download folder
     * @return
     */
    private String getProperNameForDownload(String name, List<Download> downloads, File downloadPathFile) {
        List<String> fileNames = downloads.stream().map(download -> download.getDownloadName()).collect(Collectors.toList());
        String fileName = FileUtil.countableFileName(name, fileNames);
        String finalFileName = FileUtil.outputFile(new File(downloadPathFile + File.separator + fileName));
        if (!fileNames.contains(finalFileName))
            return finalFileName;
        else
            return getProperNameForDownload(finalFileName, downloads, downloadPathFile);
    }

    private void setStateOfMenuItems() {
        openItem.setEnabled(false);
        openFolderItem.setEnabled(false);
        resumeItem.setEnabled(false);
        pauseItem.setEnabled(false);
        clearItem.setEnabled(false);
        reJoinItem.setEnabled(false);
        reDownloadItem.setEnabled(false);
        moveToQueueItem.setEnabled(false);
        removeFromQueueItem.setEnabled(false);
        propertiesItem.setEnabled(false);
    }

    private PreferencesDTO getPreferences() {
        Optional<PreferencesDTO> preferencesDTOOptional = getPreferencesOS();
        PreferencesDTO preferencesDTO;
        if (!preferencesDTOOptional.isPresent()) {
            preferencesDTO = new PreferencesDTO();
            resetAndSetPreferencesDTOFromConf(preferencesDTO);
            setPreferencesOS(preferencesDTO);
        } else
            preferencesDTO = preferencesDTOOptional.get();

        return preferencesDTO;
    }

    private void resetAndSetPreferencesDTOFromConf(PreferencesDTO preferencesDTO) {
        String homeDir = System.getProperty("user.home");

        String path = homeDir + File.separator + "Downloads" + File.separator + "Cheetah Downloaded Files" + File.separator;

        PreferencesGeneralDTO preferencesGeneralDTO = preferencesDTO.getPreferencesGeneralDTO();
        preferencesGeneralDTO.setLaunchOnsStartup(Boolean.valueOf(defaultPreferencesBundle.getString("launchOnsStartup")));

        // PreferencesConnectionDTO
        PreferencesConnectionDTO preferencesConnectionDTO = preferencesDTO.getPreferencesConnectionDTO();
        preferencesConnectionDTO.setConnectionType(ConnectionType.valueOfByDesc(defaultPreferencesBundle.getString("connectionType")));
        preferencesConnectionDTO.setMaxConnectionNumber(Integer.parseInt(defaultPreferencesBundle.getString("maxConnectionNumber")));
        preferencesConnectionDTO.setTimeBetweenAttempts(Integer.parseInt(defaultPreferencesBundle.getString("timeBetweenAttempts")));
        preferencesConnectionDTO.setMaxNumberAttempts(Integer.parseInt(defaultPreferencesBundle.getString("maxNumberAttempts")));
        preferencesConnectionDTO.setConnectionTimeOut(Integer.parseInt(defaultPreferencesBundle.getString("connectionTimeOut")));
        preferencesConnectionDTO.setReadTimeOut(Integer.parseInt(defaultPreferencesBundle.getString("readTimeOut")));

        // preferencesSaveDTO
        PreferencesSaveDTO preferencesSaveDTO = preferencesDTO.getPreferencesSaveDTO();

        List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs = new ArrayList<>();

        // preferencesCompressedDirCategoryDTO
        PreferencesDirectoryCategoryDTO preferencesCompressedDirCategoryDTO = new PreferencesDirectoryCategoryDTO();

        preferencesCompressedDirCategoryDTO.setDirectoryName(defaultPreferencesBundle.getString("compressedDirectoryCategory.directoryName"));
        preferencesCompressedDirCategoryDTO.setPath(path + defaultPreferencesBundle.getString("compressedDirectoryCategory.path"));
        String [] fileCompressedExtensions = defaultPreferencesBundle.getString("compressedDirectoryCategory.fileExtensions").split(" ");
        preferencesCompressedDirCategoryDTO.setFileExtensions(fileCompressedExtensions);
        preferencesCompressedDirCategoryDTO.setIconPath(defaultPreferencesBundle.getString("compressedDirectoryCategory.icon"));
        preferencesDirectoryCategoryDTOs.add(preferencesCompressedDirCategoryDTO);

        PreferencesDirectoryCategoryDTO preferencesDocumentDirCategoryDTO = new PreferencesDirectoryCategoryDTO();

        preferencesDocumentDirCategoryDTO.setDirectoryName(defaultPreferencesBundle.getString("documentDirectoryCategory.directoryName"));
        preferencesDocumentDirCategoryDTO.setPath(path + defaultPreferencesBundle.getString("documentDirectoryCategory.path"));
        String [] fileDocumentExtensions = defaultPreferencesBundle.getString("documentDirectoryCategory.fileExtensions").split(" ");
        preferencesDocumentDirCategoryDTO.setFileExtensions(fileDocumentExtensions);
        preferencesDocumentDirCategoryDTO.setIconPath(defaultPreferencesBundle.getString("documentDirectoryCategory.icon"));
        preferencesDirectoryCategoryDTOs.add(preferencesDocumentDirCategoryDTO);

        PreferencesDirectoryCategoryDTO preferencesMusicDirCategoryDTO = new PreferencesDirectoryCategoryDTO();

        preferencesMusicDirCategoryDTO.setDirectoryName(defaultPreferencesBundle.getString("musicDirectoryCategory.directoryName"));
        preferencesMusicDirCategoryDTO.setPath(path + defaultPreferencesBundle.getString("musicDirectoryCategory.path"));
        String [] fileMusicExtensions = defaultPreferencesBundle.getString("musicDirectoryCategory.fileExtensions").split(" ");
        preferencesMusicDirCategoryDTO.setFileExtensions(fileMusicExtensions);
        preferencesMusicDirCategoryDTO.setIconPath(defaultPreferencesBundle.getString("musicDirectoryCategory.icon"));
        preferencesDirectoryCategoryDTOs.add(preferencesMusicDirCategoryDTO);


        PreferencesDirectoryCategoryDTO preferencesProgramDirCategoryDTO = new PreferencesDirectoryCategoryDTO();

        preferencesProgramDirCategoryDTO.setDirectoryName(defaultPreferencesBundle.getString("programDirectoryCategory.directoryName"));
        preferencesProgramDirCategoryDTO.setPath(path + defaultPreferencesBundle.getString("programDirectoryCategory.path"));
        String [] fileProgramExtensions = defaultPreferencesBundle.getString("programDirectoryCategory.fileExtensions").split(" ");
        preferencesProgramDirCategoryDTO.setFileExtensions(fileProgramExtensions);
        preferencesProgramDirCategoryDTO.setIconPath(defaultPreferencesBundle.getString("programDirectoryCategory.icon"));
        preferencesDirectoryCategoryDTOs.add(preferencesProgramDirCategoryDTO);

        PreferencesDirectoryCategoryDTO preferencesVideoDirCategoryDTO = new PreferencesDirectoryCategoryDTO();

        preferencesVideoDirCategoryDTO.setDirectoryName(defaultPreferencesBundle.getString("videoDirectoryCategory.directoryName"));
        preferencesVideoDirCategoryDTO.setPath(path + defaultPreferencesBundle.getString("videoDirectoryCategory.path"));
        String [] fileVideoExtensions = defaultPreferencesBundle.getString("videoDirectoryCategory.fileExtensions").split(" ");
        preferencesVideoDirCategoryDTO.setFileExtensions(fileVideoExtensions);
        preferencesVideoDirCategoryDTO.setIconPath(defaultPreferencesBundle.getString("videoDirectoryCategory.icon"));
        preferencesDirectoryCategoryDTOs.add(preferencesVideoDirCategoryDTO);

        PreferencesDirectoryCategoryDTO preferencesImageDirCategoryDTO = new PreferencesDirectoryCategoryDTO();

        preferencesImageDirCategoryDTO.setDirectoryName(defaultPreferencesBundle.getString("imageDirectoryCategory.directoryName"));
        preferencesImageDirCategoryDTO.setPath(path + defaultPreferencesBundle.getString("imageDirectoryCategory.path"));
        String [] fileImageExtensions = defaultPreferencesBundle.getString("imageDirectoryCategory.fileExtensions").split(" ");
        preferencesImageDirCategoryDTO.setFileExtensions(fileImageExtensions);
        preferencesImageDirCategoryDTO.setIconPath(defaultPreferencesBundle.getString("imageDirectoryCategory.icon"));
        preferencesDirectoryCategoryDTOs.add(preferencesImageDirCategoryDTO);

        preferencesSaveDTO.setPreferencesDirectoryCategoryDTOs(preferencesDirectoryCategoryDTOs);
        preferencesSaveDTO.setTempDirectory(path + defaultPreferencesBundle.getString("tempDirectory"));
        preferencesSaveDTO.setDatabasePath(path + defaultPreferencesBundle.getString("databasePath"));
        preferencesSaveDTO.setLogPath(path + defaultPreferencesBundle.getString("logPath"));

        // PreferenceProxyDTO
        PreferencesProxyDTO preferencesProxyDTO = preferencesDTO.getPreferencesProxyDTO();
        preferencesProxyDTO.setProxySettingType(Integer.parseInt(defaultPreferencesBundle.getString("proxySettingType")));
        preferencesProxyDTO.setUseProxyNotSocks(Boolean.parseBoolean(defaultPreferencesBundle.getString("useProxyNotSocks")));
        preferencesProxyDTO.setHttpProxyAddress(defaultPreferencesBundle.getString("httpProxyAddress"));
        preferencesProxyDTO.setHttpProxyPort(Integer.parseInt(defaultPreferencesBundle.getString("httpProxyPort")));
        preferencesProxyDTO.setHttpProxyUserName(defaultPreferencesBundle.getString("httpProxyUserName"));
        preferencesProxyDTO.setHttpProxyPassword(defaultPreferencesBundle.getString("httpProxyPassword"));
        preferencesProxyDTO.setHttpsProxyAddress(defaultPreferencesBundle.getString("httpsProxyAddress"));
        preferencesProxyDTO.setHttpsProxyPort(Integer.parseInt(defaultPreferencesBundle.getString("httpsProxyPort")));
        preferencesProxyDTO.setHttpsProxyUserName(defaultPreferencesBundle.getString("httpsProxyUserName"));
        preferencesProxyDTO.setHttpsProxyPassword(defaultPreferencesBundle.getString("httpsProxyPassword"));
        preferencesProxyDTO.setFtpProxyAddress(defaultPreferencesBundle.getString("ftpProxyAddress"));
        preferencesProxyDTO.setFtpProxyPort(Integer.parseInt(defaultPreferencesBundle.getString("ftpProxyPort")));
        preferencesProxyDTO.setFtpProxyUserName(defaultPreferencesBundle.getString("ftpProxyUserName"));
        preferencesProxyDTO.setFtpProxyPassword(defaultPreferencesBundle.getString("ftpProxyPassword"));

        // PreferenceInterfaceDTO
        PreferencesInterfaceDTO preferencesInterfaceDTO = preferencesDTO.getPreferencesInterfaceDTO();
        preferencesInterfaceDTO.setLookAndFeelName(defaultPreferencesBundle.getString("lookAndFeelName"));
        preferencesInterfaceDTO.setLocalName(getLocale().getLanguage());

        // add all Main preferences objects
        preferencesDTO.setPreferencesGeneralDTO(preferencesGeneralDTO);
        preferencesDTO.setPreferencesConnectionDTO(preferencesConnectionDTO);
        preferencesDTO.setPreferencesSaveDTO(preferencesSaveDTO);
        preferencesDTO.setPreferencesProxyDTO(preferencesProxyDTO);
        preferencesDTO.setPreferencesInterfaceDTO(preferencesInterfaceDTO);
    }

    private Optional<PreferencesDTO> getPreferencesOS() {
        PreferencesDTO preferencesDTO = null;
        try {
            preferencesDTO = (PreferencesDTO) PrefObj.getObject(preferences, "preferenceDTO"); // todo must find a way to delete preferenceDTO from OS
        } catch(IOException | BackingStoreException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(preferencesDTO);
    }

    private void setPreferencesOS(PreferencesDTO preferenceDTO) {
        try {
            PrefObj.putObject(preferences, "preferenceDTO", preferenceDTO);
        } catch (IOException | BackingStoreException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* Update each button's state based off of the
       currently selected download's status. */
    private void updateButtons(DownloadStatus state) {
        if (state != null) {
            switch (state) {
                case DOWNLOADING:
                    mainToolbar.setStateOfButtonsControl(true, false, false, false, false, true); // Toolbar Buttons
                    downloadPanel.setStateOfButtonsControl(false, false, true, false, false, false, false, true); // Toolbar Buttons
                    setStateOfMenuItemsControl(false, false, true, false, false, false, false, true); // MenuItems
                    break;
                case PAUSED:
                    mainToolbar.setStateOfButtonsControl(false, true, true, false, false, true); // last false
                    downloadPanel.setStateOfButtonsControl(false, false, false, true, true, false, false, true); // Toolbar Buttons
                    setStateOfMenuItemsControl(false, false, false, true, true, false, false, true);
                    break;
                case ERROR:
                    mainToolbar.setStateOfButtonsControl(false, true, true, false, false, true);
                    downloadPanel.setStateOfButtonsControl(false, false, false, true, true, false, false, true); // Toolbar Buttons
                    setStateOfMenuItemsControl(false, false, false, true, true, false, false, true);
                    break;
                case CANCELLED:
                    mainToolbar.setStateOfButtonsControl(false, true, true, false, false, true);
                    downloadPanel.setStateOfButtonsControl(false, false, false, true, true, false, false, true); // Toolbar Buttons
                    setStateOfMenuItemsControl(false, false, false, true, true, false, false, true);
                    break;
                default: // COMPLETE
                    mainToolbar.setStateOfButtonsControl(false, false, true, true, true, true);
                    downloadPanel.setStateOfButtonsControl(true, true, false, false, true, true, true, true); // Toolbar Buttons
                    setStateOfMenuItemsControl(true, true, false, false, true, true, true, true);
            }
        } else {
            // No download is selected in table.
            mainToolbar.setStateOfButtonsControl(false, false, false, false, false, false);
            downloadPanel.setStateOfButtonsControl(false, false, false, false, false, false, false, false); // Toolbar Buttons
            setStateOfMenuItemsControl(false, false, false, false, false, false, false, false);
        }
    }

    private JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        /////////////////////////////////////////////////////////////////////////
        JMenu fileMenu = new JMenu(messagesBundle.getString("downloadManagerGUI.fileMenu.name"));
        exportDataItem = new JMenuItem("Export Data...");
        importDataItem = new JMenuItem("Import Data...");
        exitItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.exitItem.name"));
        exitItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.exitItem.iconPath")))); // NOI18N

        exportDataItem.setEnabled(false);
        importDataItem.setEnabled(false);

        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        /////////////////////////////////////////////////////////////////////////
        JMenu windowMenu = new JMenu(messagesBundle.getString("downloadManagerGUI.windowMenu.name"));
        JMenu showMenu = new JMenu(messagesBundle.getString("downloadManagerGUI.showMenu.name"));
        prefsItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.prefsItem.name"));
        prefsItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.prefsItem.iconPath"))));


        JCheckBoxMenuItem showFormItem = new JCheckBoxMenuItem(messagesBundle.getString("downloadManagerGUI.showFormItem.name"));
        showFormItem.setSelected(true);

        showMenu.add(showFormItem);
        windowMenu.add(showMenu);
        windowMenu.add(prefsItem);

        exportDataItem.addActionListener(this);
        importDataItem.addActionListener(this);
        exitItem.addActionListener(this);
        prefsItem.addActionListener(this);

        /////////////////////////////////////////////////////////////////////////
        JMenu downloadsMenu = new JMenu(messagesBundle.getString("downloadManagerGUI.downloadsMenu.name"));
        newDownloadItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.newDownloadItem.name"));
        newDownloadItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.newDownloadItem.iconPath"))));
        openItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.openItem.name"));
        openFolderItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.openFolderItem.name"));
        resumeItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.resumeItem.name"));
        resumeItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.resumeItem.iconPath"))));
        pauseItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.pauseItem.name"));
        pauseItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.pauseItem.iconPath"))));
        pauseAllItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.pauseAllItem.name"));
        pauseAllItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.pauseAllItem.iconPath"))));
        clearItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.clearItem.name"));
        clearItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.clearItem.iconPath"))));
        clearAllCompletedItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.clearAllCompletedItem.name"));
        clearAllCompletedItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.clearAllCompletedItem.iconPath"))));
        reJoinItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.reJoinItem.name"));
        reJoinItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.reJoinItem.iconPath"))));
        reDownloadItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.reDownloadItem.name"));
        reDownloadItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.reDownloadItem.iconPath"))));

        moveToQueueItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.moveToQueueItem.name"));

        removeFromQueueItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.removeFromQueueItem.name"));
        propertiesItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.propertiesItem.name"));
        propertiesItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.propertiesItem.iconPath"))));

        downloadsMenu.add(newDownloadItem);
        downloadsMenu.add(new JSeparator());
        downloadsMenu.add(openItem);
        downloadsMenu.add(openFolderItem);
        downloadsMenu.add(new JSeparator());
        downloadsMenu.add(resumeItem);
        downloadsMenu.add(pauseItem);
        downloadsMenu.add(pauseAllItem);
        downloadsMenu.add(new JSeparator());
        downloadsMenu.add(clearItem);
        downloadsMenu.add(clearAllCompletedItem);
        downloadsMenu.add(new JSeparator());
        downloadsMenu.add(reJoinItem);
        downloadsMenu.add(reDownloadItem);
        downloadsMenu.add(new JSeparator());
        downloadsMenu.add(moveToQueueItem);
        downloadsMenu.add(removeFromQueueItem);
        downloadsMenu.add(new JSeparator());
        downloadsMenu.add(propertiesItem);

        newDownloadItem.addActionListener(this);
        openItem.addActionListener(this);
        openFolderItem.addActionListener(this);
        resumeItem.addActionListener(this);
        pauseItem.addActionListener(this);
        pauseAllItem.addActionListener(this);
        clearItem.addActionListener(this);
        clearAllCompletedItem.addActionListener(this);
        reDownloadItem.addActionListener(this);
        moveToQueueItem.addActionListener(this);
        removeFromQueueItem.addActionListener(this);
        propertiesItem.addActionListener(this);

        setStateOfMenuItems();

        /////////////////////////////////////////////////////////////////////////
        JMenu helpMenu = new JMenu(messagesBundle.getString("downloadManagerGUI.helpMenu.name"));
        aboutItem = new JMenuItem(messagesBundle.getString("downloadManagerGUI.aboutItem.name"));
        aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(messagesBundle.getString("downloadManagerGUI.aboutItem.iconPath"))));

        helpMenu.add(aboutItem);

        aboutItem.addActionListener(this);

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);
        menuBar.add(downloadsMenu);
        menuBar.add(helpMenu);

        showFormItem.addActionListener(ev -> {
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)ev.getSource();

            if (menuItem.isSelected()) {
                mainSplitPane.setDividerLocation((int) categoryPanel.getMinimumSize().getWidth());
            }
            categoryPanel.setVisible(menuItem.isSelected());
        });

        fileMenu.setMnemonic(KeyEvent.VK_F);
        exitItem.setMnemonic(KeyEvent.VK_X);

        prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));

        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

        importDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));

        return menuBar;
    }

    private void createFileHierarchy() {
        PreferencesDTO preferencesDTO;
        try {
            preferencesDTO = (PreferencesDTO) PrefObj.getObject(preferences, "preferenceDTO");

            List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs = preferencesDTO.getPreferencesSaveDTO().getPreferencesDirectoryCategoryDTOs();
            for (PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO : preferencesDirectoryCategoryDTOs) {
                File dirFile = new File(preferencesDirectoryCategoryDTO.getPath());
                if (!dirFile.exists())
                    dirFile.mkdirs();
            }
            String tempDirectory = preferencesDTO.getPreferencesSaveDTO().getTempDirectory();
            File tempDirFile = new File(tempDirectory);
            if (!tempDirFile.exists())
                tempDirFile.mkdirs();
            String databasePath = preferencesDTO.getPreferencesSaveDTO().getDatabasePath();
            File databasePathFile = new File(databasePath);
            if (!databasePathFile.exists())
                databasePathFile.mkdirs();
            String logPath = preferencesDTO.getPreferencesSaveDTO().getLogPath();
            File logPathFile = new File(logPath);
            if (!logPathFile.exists())
                logPathFile.mkdirs();
        } catch (InvalidClassException | NullPointerException e) {
            try {
                PrefObj.putObject(preferences, "preferenceDTO", new PreferencesDTO());
            } catch (IOException | BackingStoreException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            JOptionPane.showMessageDialog(DownloadManagerGUI.this, "Please run program again", "Just one time", JOptionPane.ERROR_MESSAGE);
        } catch (IOException | BackingStoreException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException se) {
            JOptionPane.showMessageDialog(DownloadManagerGUI.this, "Unable to create necessary directories. may be not have write permission, please restart program", "Directories creation problem", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setStateOfMenuItemsControl(boolean openState, boolean openFolderState, boolean pauseState, boolean resumeState, boolean clearState, boolean reJoinState, boolean reDownloadState, boolean propertiesState) {
        openItem.setEnabled(openState);
        openFolderItem.setEnabled(openFolderState);
        pauseItem.setEnabled(pauseState);
        resumeItem.setEnabled(resumeState);
        clearItem.setEnabled(clearState);
        reJoinItem.setEnabled(reJoinState);
        reDownloadItem.setEnabled(reDownloadState);
        propertiesItem.setEnabled(propertiesState);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem clicked= (JMenuItem) e.getSource();

        if (clicked == exportDataItem) {
            //    addNewDownloadDialog.setVisible(true);
        } else if (clicked == importDataItem) {
            //    addNewDownloadDialog.setVisible(true);
        } else if (clicked == exitItem) {
            WindowListener[]  listeners = getWindowListeners();
            for (WindowListener listener : listeners)
                listener.windowClosing(new WindowEvent(DownloadManagerGUI.this, 0));
        } else if (clicked == prefsItem) {
            preferenceDialog.setVisible(true);
        } else if (clicked == newDownloadItem) {
            addNewDownloadDialog.setVisible(true);
            addNewDownloadDialog.onPaste();
        } else if (clicked == openItem) {
            downloadPanel.actionOpenFile();
        } else if (clicked == openFolderItem) {
            downloadPanel.actionOpenFolder();
        } else if (clicked == resumeItem) {
            downloadPanel.actionResume();
        } else if (clicked == pauseItem) {
            downloadPanel.actionPause();
            mainToolbar.setStateOfButtonsControl(false, false, false, false, false, true); // canceled
        } else if (clicked == pauseAllItem) {
            int action = JOptionPane.showConfirmDialog(DownloadManagerGUI.this, "Do you realy want to pause all downloads?", "Confirm pause all", JOptionPane.OK_CANCEL_OPTION); ////***********
            if (action == JOptionPane.OK_OPTION) {
                downloadPanel.actionPauseAll();
            }
        } else if (clicked == clearItem) {
            downloadPanel.actionClear();
        } else if (clicked == clearAllCompletedItem) {
            downloadPanel.actionClearAllCompleted();
        } else if (clicked == reJoinItem) {
            downloadPanel.actionReJoinFileParts();
        } else if (clicked == reDownloadItem) {
            downloadPanel.actionReDownload();
        } else if (clicked == moveToQueueItem) {
        } else if (clicked == removeFromQueueItem) {
        } else if (clicked == propertiesItem) {
            downloadPanel.actionProperties();
        } else if (clicked == aboutItem) {
            if (!aboutDialog.isVisible())
                aboutDialog.setVisible(true);
        }
    }
}
