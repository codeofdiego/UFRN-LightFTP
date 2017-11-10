package com.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;

public class ClientWindow {
    private JFrame frame;
    private JList<String> fileList;
    private DefaultListModel<String> fileListModel;
    public JPanel mainPanel;
    private JButton saveButton;
    private JButton uploadButton;
    private Dimension size = new Dimension(400, 500);

    public ClientWindow () {
        frame = new JFrame("LightFTP Client");
        frame.setSize(size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setSize(size);

        saveButton = new JButton("Save");
        uploadButton = new JButton("Upload");
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setSize(size);

        mainPanel.add(fileList);
        mainPanel.add(saveButton);
        mainPanel.add(uploadButton);

        frame.setContentPane(mainPanel);

        //5. Show it.
        frame.setVisible(true);
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getUploadButton() {
        return uploadButton;
    }

    public JList<String> getFileList() {
        return fileList;
    }

    public void updateList (String list) {

        String[] listItems = list.split(",");
        fileListModel.removeAllElements();

        for (int i = 0; i < listItems.length; i++) {
            fileListModel.add(i, listItems[i]);
        }

        fileList = new JList<>(fileListModel);
        System.out.println(fileList.getModel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
