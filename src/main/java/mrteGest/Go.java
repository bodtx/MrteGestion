package mrteGest;

/*
 * Go.java
 *
 * Created on 1 novembre 2004, 18:18
 */

/**
 *
 * @author  BOD
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class Go {

    /** Creates a new instance of Go */
    public Go() {
    }

    public static void main(String s[]) {

        String userHome = System.getProperty("user.home"); //$NON-NLS-1$
        File data = new File(userHome + "/data.txt"); //$NON-NLS-1$
        File config = new File(userHome + "/config.txt"); //$NON-NLS-1$
        File version = new File(userHome + "/version.txt"); //$NON-NLS-1$
        final String login = Messages.getString("Go.login"); //$NON-NLS-1$
        final String password = Messages.getString("Go.password"); //$NON-NLS-1$

        JProgressBar pb = new JProgressBar(0, 100);
        pb.setPreferredSize(new Dimension(175, 20));
        pb.setString("En cours ..."); //$NON-NLS-1$
        pb.setStringPainted(true);
        pb.setValue(0);
        JLabel label = new JLabel("Attendez: "); //$NON-NLS-1$

        JPanel center_panel = new JPanel();
        center_panel.add(label);
        center_panel.add(pb);
        JDialog dialog = new JDialog((JFrame) null, "Attendez"); //$NON-NLS-1$
        dialog.getContentPane().add(center_panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null); // center on screen
        // dialog.setLocation(550,25); // position by coordinates
        dialog.toFront(); // raise above other java windows

        try {
            if (!version.exists())
                version.createNewFile();

            FTPClient f = new FTPClient();
            f.setConnectTimeout(2000);
            try {
                f.connect(Messages.getString("Go.urlFtpDataIntranet"));//on est en local au mrte //$NON-NLS-1$
            } catch (Exception e1) {
                f.connect(Messages.getString("Go.urlFtpDataInternet")); //$NON-NLS-1$
            }

            f.login(login, password); //$NON-NLS-1$ //$NON-NLS-2$
            pb.setValue(25);

            // gestion multi utilisateurs
            FTPFile[] lockFile = f.listFiles("GestionLicence/lock.lck"); //$NON-NLS-1$
            if (lockFile.length != 0) {
                JOptionPane.showMessageDialog(null, "Quelqu'un est en train d'utiliser l'application, vous ne pouvez pas vous y connecter pour le moment .\n" + //$NON-NLS-1$
                        "L'application va s'arreter.\n", "Attention", JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
                f.logout();
                f.disconnect();
                System.exit(0);
            } else {
                byte[] empty = { 1 };
                boolean lockStored = f.storeFile("GestionLicence/lock.lck", new ByteArrayInputStream(empty)); //$NON-NLS-1$
                if (!lockStored)
                    throw (new Exception("Impossible de vérouiller l'application")); //$NON-NLS-1$
            }

            InputStream readVersion = f.retrieveFileStream("GestionLicence/version.txt"); //$NON-NLS-1$
            BufferedReader d = new BufferedReader(new InputStreamReader(readVersion));
            int versionRemote = Integer.parseInt(d.readLine());
            readVersion.close();

            readVersion = new FileInputStream(version);
            d = new BufferedReader(new InputStreamReader(readVersion));
            int versionLocal = -1;
            try {
                versionLocal = Integer.parseInt(d.readLine());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            f.logout();
            f.disconnect();
            f = new FTPClient();
            f.setConnectTimeout(2000);
            try {
                f.connect(Messages.getString("Go.urlFtpDataIntranet"));//on est en local au mrte //$NON-NLS-1$
            } catch (Exception e1) {
                f.connect(Messages.getString("Go.urlFtpDataInternet")); //$NON-NLS-1$
            }
            f.login(login, password); //$NON-NLS-1$ //$NON-NLS-2$
            pb.setValue(50);
            if (versionLocal > versionRemote) {
                Object[] options = { "Oui", "Non" }; //$NON-NLS-1$ //$NON-NLS-2$
                int reponse = JOptionPane.showOptionDialog(null, "Vos données sur le pc sont plus avancées que celle partagées à distance.\n" + //$NON-NLS-1$
                        "Voulez vous tout de même récupérer les données distantes?", //$NON-NLS-1$
                        "Attention", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]); //$NON-NLS-1$ //$NON-NLS-2$
                if (reponse == 0) {
                    FileOutputStream streamData = new FileOutputStream(data);
                    FileOutputStream streamConfig = new FileOutputStream(config);
                    FileOutputStream streamVersion = new FileOutputStream(version);
                    boolean dataOk = f.retrieveFile("GestionLicence/data.txt", streamData); //$NON-NLS-1$
                    pb.setValue(65);
                    boolean configOk = f.retrieveFile("GestionLicence/config.txt", streamConfig); //$NON-NLS-1$
                    pb.setValue(80);
                    boolean versionOk = f.retrieveFile("GestionLicence/version.txt", streamVersion); //$NON-NLS-1$
                    pb.setValue(90);

                    if (!dataOk || !configOk || !versionOk)
                        throw new Exception("Erreur de récupération des données"); //$NON-NLS-1$
                    streamData.flush();
                    streamConfig.flush();
                    streamVersion.flush();
                    streamData.close();
                    streamConfig.close();
                    streamVersion.close();
                    f.logout();
                    f.disconnect();
                } else {
                    // on récupère rien.
                    f.logout();
                    f.disconnect();
                }
            } else {
                FileOutputStream streamData = new FileOutputStream(data);
                FileOutputStream streamConfig = new FileOutputStream(config);
                FileOutputStream streamVersion = new FileOutputStream(version);
                boolean dataOk = f.retrieveFile("GestionLicence/data.txt", streamData); //$NON-NLS-1$
                boolean configOk = f.retrieveFile("GestionLicence/config.txt", streamConfig); //$NON-NLS-1$
                boolean versionOk = f.retrieveFile("GestionLicence/version.txt", streamVersion); //$NON-NLS-1$
                pb.setValue(75);
                if (!dataOk || !configOk || !versionOk)
                    throw new Exception("Erreur de récupération des données"); //$NON-NLS-1$

                streamData.flush();
                streamConfig.flush();
                streamVersion.flush();
                streamData.close();
                streamConfig.close();
                streamVersion.close();
                f.logout();
                f.disconnect();

                BufferedWriter writer = new BufferedWriter(new FileWriter(version));
                writer.write(String.valueOf(versionRemote + 1));
                writer.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Impossible de se connecter aux données partagées .\n" + //$NON-NLS-1$
                    "L'application va s'arreter.\n" + //$NON-NLS-1$
                    e.toString(), "Erreur", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            System.exit(0);

        }

        pb.setValue(100);

        dialog.dispose();
        Gestion.getInstance();

    }
}
