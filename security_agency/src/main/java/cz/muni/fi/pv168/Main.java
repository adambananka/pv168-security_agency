package cz.muni.fi.pv168;

import cz.muni.fi.pv168.frontend.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Adam on 08-May-17.
 */
public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainWindow app = new MainWindow();
        });
    }
}
