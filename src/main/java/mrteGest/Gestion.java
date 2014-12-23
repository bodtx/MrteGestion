package mrteGest;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTPClient;

public class Gestion extends JFrame implements ActionListener, FocusListener {
    private static Gestion _instance;
    Tableau PtableView;
    Container panelPrincipal;
    JButton bRecherche, bModif, bAjout, bRaz, bImprimer, bSuppression, bEnregistrer, bChangeAnnee, bMajLic, bMajCat;

    JTextField tNumLicense, tNom, tPrenom, tDatN, tAdresse, tCodP, tVille, tTel, tPrixLic, tAdhePrix, tDateEdition, tMail, tAdresse2;

    JRadioButton rNouvAncT, rNouvAncN, rNouvAncA, rInscritT, rInscritI, rInscritN, rAgeTexte, rAgeInter, rAssuranceT, rAssuranceO, rAssuranceN;

    ButtonGroup gNouvAnc, gInscrit, gAge, gAssurance;

    JComboBox jAgeDeb, jAgeFin, jCategorie, jLic, jCompDate;

    File data, config, version;
    ArrayList listeLigne;
    ArrayList listeAttributs;
    ArrayList listePrixLic;
    Vector listeCategorie;
    Vector listeLic;
    Vector enTete, colonnes;
    SimpleDateFormat formatDate, formatDateFic;
    Date dateN, dToday;
    boolean modeModifSupp = false;
    String oldNumLic;
    double prixTotalLic;

    JFrame dialog = new JFrame("Attendez");
    JProgressBar pb = new JProgressBar(0, 100);

    public static synchronized Gestion getInstance() {
        if (_instance == null)
            _instance = new Gestion();
        return _instance;
    }

    public Gestion() {
        super("gestion abonné");

        // Chargement du fichier
        FileReader lecteur = null;
        int carLu = 0;
        listeLigne = new ArrayList();
        listeAttributs = new ArrayList();
        String attribut = "";
        int numAttribut = 0;
        int numLigne = 0;
        dToday = new Date();
        String userHome = System.getProperty("user.home");
        data = new File(userHome + "/data.txt");
        config = new File(userHome + "/config.txt");
        version = new File(userHome + "/version.txt");
        formatDate = new SimpleDateFormat("dd/MM/yyyy");
        formatDateFic = new SimpleDateFormat("dd-MM-yyyy");
        jCompDate = new JComboBox();
        jCompDate.addItem("=");
        jCompDate.addItem(">");
        jCompDate.addItem("<");
        try {
            data.createNewFile();

            lecteur = new FileReader(data);
            while ((carLu = lecteur.read()) != -1) {
                switch (carLu) {
                case '$':
                    if (numAttribut == 3 || numAttribut == 12) {
                        if (!attribut.equals("")) {
                            try {
                                listeAttributs.add(formatDate.parse(attribut));
                            } catch (ParseException pe) {
                                JOptionPane.showMessageDialog(null, "La date de la ligne " + String.valueOf(numAttribut)
                                        + " du fichier n'est pas au format JJ/MM/YYYY (" + attribut + ").", "Erreur", JOptionPane.ERROR_MESSAGE);
                                System.exit(0);
                            }
                        } else
                            listeAttributs.add(dateN);

                    } else
                        listeAttributs.add(attribut);
                    attribut = "";
                    numAttribut++;
                    break;
                case 10:
                    listeLigne.add(listeAttributs);
                    listeAttributs = new ArrayList();
                    attribut = "";
                    numAttribut = 0;
                    break;
                case 13:
                    break;
                default:
                    attribut += (char) carLu;
                }// switch
            }// while
            lecteur.close();
            attribut = "";

            // ////////////////////////////Fichier config
            jCategorie = new JComboBox();
            boolean prixLic = false;
            listePrixLic = new ArrayList();
            listeCategorie = new Vector();
            listeLic = new Vector();
            jCategorie.addItem("");
            jLic = new JComboBox();
            jLic.addItem("");
            jLic.addActionListener(this);
            config.createNewFile();
            lecteur = new FileReader(config);
            while ((carLu = lecteur.read()) != -1) {
                switch (carLu) {
                case '$':
                    if (numLigne == 0) {
                        jCategorie.addItem(attribut);
                        listeCategorie.add(attribut);
                    } else if (numLigne == 1 && prixLic == false) {
                        jLic.addItem(attribut);
                        listeLic.add(attribut);
                        prixLic = true;
                    } else if (numLigne == 1 && prixLic == true) {
                        listePrixLic.add(attribut);
                        prixLic = false;
                    }
                    attribut = "";
                    break;
                case 10:
                    attribut = "";
                    numLigne++;
                    break;
                case 13:
                    break;
                default:
                    attribut += (char) carLu;
                }// switch
            }// while
            lecteur.close();
            attribut = null;
        }// try

        catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Une erreur s'est produite pendant la lecture du fichier de sauvegarde.", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // declaration des colonnes du tableau
        enTete = new Vector();
        enTete.addElement(new String("Num lic"));
        enTete.addElement(new String("Nom"));
        enTete.addElement(new String("Prénom"));
        enTete.addElement(new String("Date naiss"));
        enTete.addElement(new String("Adresse"));
        enTete.addElement(new String("Code p"));
        enTete.addElement(new String("Ville"));
        enTete.addElement(new String("Tel"));
        enTete.addElement(new String("Type lic"));
        enTete.addElement(new String("Prix lic"));
        enTete.addElement(new String("Prix adh"));
        enTete.addElement(new String("Catégories"));
        enTete.addElement(new String("Date édition"));
        enTete.addElement(new String("Nouveau"));
        enTete.addElement(new String("Inscrit"));
        enTete.addElement(new String("Assurance"));
        enTete.addElement(new String("Cpl Addr"));
        enTete.addElement(new String("Mail"));

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenDim.width, screenDim.height);

        JLabel lNumLicense, lNom, lPrenom, lDatN, lAdresse, lCodP, lVille, lTel, lTypLic, lPrixLic, lAdhePrix, lCat, lDateEdition, lNouvAnc, lInscrit, lAssurance, lMail, lAdresse2;

        lNumLicense = new JLabel("Numéro de licence");
        lNom = new JLabel("Nom");
        lPrenom = new JLabel("Prénom");
        lDatN = new JLabel("Date de naissance");
        lAdresse = new JLabel("Adresse");
        lCodP = new JLabel("Code postal");
        lVille = new JLabel("Ville");
        lTel = new JLabel("Téléphone");
        lTypLic = new JLabel("Type de license");
        lPrixLic = new JLabel("Prix de la licence");
        lAdhePrix = new JLabel("Prix de l'adhésion");
        lCat = new JLabel("Catégories");
        lDateEdition = new JLabel("Date édition");
        lNouvAnc = new JLabel("Nouveau");
        lInscrit = new JLabel("Inscrit");
        lAssurance = new JLabel("Assurance");
        lMail = new JLabel("Mail");
        lAdresse2 = new JLabel("Complément d'adresse");

        tNumLicense = new JTextField(15);
        tNom = new JTextField(15);
        tPrenom = new JTextField(15);
        tDatN = new JTextField(7);
        tAdresse = new JTextField(15);
        tCodP = new JTextField(15);
        tVille = new JTextField(15);
        tTel = new JTextField(15);
        tPrixLic = new JTextField(15);
        tAdhePrix = new JTextField(15);
        tDateEdition = new JTextField(15);
        tAdresse2 = new JTextField(15);
        tMail = new JTextField(15);

        tNumLicense.addFocusListener(this);
        tNom.addFocusListener(this);
        tPrenom.addFocusListener(this);
        tDatN.addFocusListener(this);
        tAdresse.addFocusListener(this);
        tCodP.addFocusListener(this);
        tVille.addFocusListener(this);
        tTel.addFocusListener(this);
        tPrixLic.addFocusListener(this);
        tAdhePrix.addFocusListener(this);
        tDateEdition.addFocusListener(this);
        tAdresse2.addFocusListener(this);
        tMail.addFocusListener(this);

        jAgeDeb = new JComboBox();
        jAgeFin = new JComboBox();
        for (int i = 0; i < 120; i++) {
            jAgeDeb.addItem(String.valueOf(i));
            jAgeFin.addItem(String.valueOf(i));
        }

        rNouvAncT = new JRadioButton("Tous", true);
        rNouvAncN = new JRadioButton("Oui", false);
        rNouvAncA = new JRadioButton("Non", false);
        gNouvAnc = new ButtonGroup();
        gNouvAnc.add(rNouvAncT);
        gNouvAnc.add(rNouvAncN);
        gNouvAnc.add(rNouvAncA);

        rInscritT = new JRadioButton("Tous", true);
        rInscritI = new JRadioButton("Oui", false);
        rInscritN = new JRadioButton("Non", false);
        gInscrit = new ButtonGroup();
        gInscrit.add(rInscritT);
        gInscrit.add(rInscritI);
        gInscrit.add(rInscritN);

        rAgeTexte = new JRadioButton("", true);
        rAgeInter = new JRadioButton("", false);
        gAge = new ButtonGroup();
        gAge.add(rAgeTexte);
        gAge.add(rAgeInter);

        rAssuranceT = new JRadioButton("Tous", true);
        rAssuranceO = new JRadioButton("Oui", false);
        rAssuranceN = new JRadioButton("Non", false);
        gAssurance = new ButtonGroup();
        gAssurance.add(rAssuranceT);
        gAssurance.add(rAssuranceO);
        gAssurance.add(rAssuranceN);

        bRecherche = new JButton("Recherche", new ImageIcon(getClass().getResource("/SEARCH.GIF"), "Rechercher"));
        bRecherche.setMnemonic(KeyEvent.VK_ENTER);
        bRecherche.addActionListener(this);
        bModif = new JButton("Modifier", new ImageIcon(getClass().getResource("/MODIFIER.GIF"), "Modifier"));
        bModif.addActionListener(this);
        bAjout = new JButton("Ajouter", new ImageIcon(getClass().getResource("/AJOUTER.GIF"), "Ajouter"));
        bAjout.addActionListener(this);
        bRaz = new JButton("RAZ", new ImageIcon(getClass().getResource("/RAZ.GIF"), "RAZ"));
        bRaz.addActionListener(this);
        bImprimer = new JButton("Imprimer/Excel", new ImageIcon(getClass().getResource("/IMPRIMER.GIF"), "Imprimer"));
        bImprimer.addActionListener(this);
        bSuppression = new JButton("Supprimer", new ImageIcon(getClass().getResource("/SUPPRIMER.GIF"), "Supprimer"));
        bSuppression.addActionListener(this);
        bEnregistrer = new JButton("Sauvegarder", new ImageIcon(getClass().getResource("/SAUVEGARDER.GIF"), "Sauvegarder"));
        bEnregistrer.addActionListener(this);
        bChangeAnnee = new JButton("Changer d'année", new ImageIcon(getClass().getResource("/ANNEE.GIF"), "Année"));
        bChangeAnnee.addActionListener(this);
        bMajLic = new JButton("Maj");
        bMajLic.setPreferredSize(new Dimension(75, 20));
        bMajLic.addActionListener(this);
        bMajCat = new JButton("Maj");
        bMajCat.setPreferredSize(new Dimension(75, 20));
        bMajCat.addActionListener(this);

        panelPrincipal = getContentPane();
        panelPrincipal.setLayout(new BorderLayout());

        PtableView = new Tableau(this, enTete, new Vector(), true);
        panelPrincipal.add(PtableView, BorderLayout.CENTER);

        JPanel panelHaut = new JPanel();
        JPanel panelHautG = new JPanel();
        JPanel panelHautD = new JPanel();
        JPanel panelHautB = new JPanel();
        JPanel panelNouvAnc = new JPanel();
        JPanel panelInscrit = new JPanel();
        JPanel panelAssurance = new JPanel();
        JPanel panelAge = new JPanel();
        JPanel panelLic = new JPanel();
        JPanel panelCat = new JPanel();

        panelHaut.setLayout(new BorderLayout());

        panelPrincipal.add(panelHaut, BorderLayout.NORTH);
        panelHaut.add(panelHautD, BorderLayout.CENTER);
        panelHaut.add(panelHautB, BorderLayout.SOUTH);

        panelHautD.setLayout(new GridLayout(9, 2, 2, 2));
        panelHautB.setLayout(new GridLayout(2, 4));
        panelAge.setLayout(new FlowLayout());
        panelLic.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelCat.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelNouvAnc.setLayout(new GridLayout(1, 3));
        panelInscrit.setLayout(new GridLayout(1, 3));
        panelAssurance.setLayout(new GridLayout(1, 3));

        panelNouvAnc.add(rNouvAncT);
        panelNouvAnc.add(rNouvAncN);
        panelNouvAnc.add(rNouvAncA);

        panelInscrit.add(rInscritT);
        panelInscrit.add(rInscritI);
        panelInscrit.add(rInscritN);

        panelAssurance.add(rAssuranceT);
        panelAssurance.add(rAssuranceO);
        panelAssurance.add(rAssuranceN);

        panelAge.add(rAgeTexte);
        panelAge.add(tDatN);
        panelAge.add(rAgeInter);
        panelAge.add(jAgeDeb);
        panelAge.add(jAgeFin);

        panelLic.add(lTypLic);
        panelLic.add(bMajLic);

        panelCat.add(lCat);
        panelCat.add(bMajCat);

        panelHautB.add(bRecherche);
        panelHautB.add(bModif);
        panelHautB.add(bAjout);
        panelHautB.add(bRaz);
        panelHautB.add(bImprimer);
        panelHautB.add(bSuppression);
        panelHautB.add(bEnregistrer);
        panelHautB.add(bChangeAnnee);

        panelHautD.add(lNumLicense);
        panelHautD.add(tNumLicense);
        panelHautD.add(lNom);
        panelHautD.add(tNom);
        panelHautD.add(lPrenom);
        panelHautD.add(tPrenom);
        panelHautD.add(lDatN);
        panelHautD.add(panelAge);
        panelHautD.add(lAdresse);
        panelHautD.add(tAdresse);
        panelHautD.add(lAdresse2);
        panelHautD.add(tAdresse2);
        panelHautD.add(lMail);
        panelHautD.add(tMail);
        panelHautD.add(lCodP);
        panelHautD.add(tCodP);
        panelHautD.add(lVille);
        panelHautD.add(tVille);
        panelHautD.add(lTel);
        panelHautD.add(tTel);
        panelHautD.add(panelLic);
        panelHautD.add(jLic);
        panelHautD.add(lPrixLic);
        panelHautD.add(tPrixLic);
        panelHautD.add(lAdhePrix);
        panelHautD.add(tAdhePrix);
        panelHautD.add(panelCat);
        panelHautD.add(jCategorie);
        panelHautD.add(lDateEdition);
        JPanel dateEdi = new JPanel(new FlowLayout());
        dateEdi.add(jCompDate);
        dateEdi.add(tDateEdition);
        panelHautD.add(dateEdi);
        panelHautD.add(lNouvAnc);
        panelHautD.add(panelNouvAnc);
        panelHautD.add(lInscrit);
        panelHautD.add(panelInscrit);
        panelHautD.add(lAssurance);
        panelHautD.add(panelAssurance);

        pb.setPreferredSize(new Dimension(250, 30));
        pb.setString("Sauvegarde en cours...");
        pb.setStringPainted(true);
        pb.setValue(0);
        JLabel label = new JLabel("Attendez");

        JPanel center_panel = new JPanel();
        center_panel.add(label);
        center_panel.add(pb);

        dialog.getContentPane().add(center_panel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(null); // center on screen
        // dialog.setLocation(550,25); // position by coordinates

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialog.toFront(); // raise above other java windows
                dialog.setVisible(true);
                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        enregistrerFic(data);
                        // sauvegarde distante (on sait jamais)
                        int reponse = 0;
                        while (reponse == 0) {
                            try {
                                FileInputStream streamData = new FileInputStream(data);
                                FileInputStream streamConfig = new FileInputStream(config);
                                FileInputStream streamVersion = new FileInputStream(version);
                                FTPClient f = new FTPClient();
                                f.setConnectTimeout(2000);
                                try {
                                    f.connect(Messages.getString("Go.urlFtpDataIntranet"));// on est en local au mrte
                                } catch (Exception e1) {
                                    f.connect(Messages.getString("Go.urlFtpDataInternet"));
                                }
                                f.login(Messages.getString("Go.login"), Messages.getString("Go.password"));
                                pb.setValue(25);
                                boolean dataStored = f.storeFile("GestionLicence/data.txt", streamData);
                                pb.setValue(50);
                                boolean configStored = f.storeFile("GestionLicence/config.txt", streamConfig);
                                pb.setValue(75);
                                boolean versionStored = f.storeFile("GestionLicence/version.txt", streamVersion);
                                pb.setValue(90);
                                boolean lockDeleted = f.deleteFile("GestionLicence/lock.lck");
                                pb.setValue(100);
                                if (!dataStored || !configStored || !versionStored || !lockDeleted)
                                    throw new Exception("Erreur de publication de donnée");
                                streamData.close();
                                streamConfig.close();
                                streamVersion.close();
                                f.disconnect();
                                break;
                            } catch (Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                                Object[] options = { "Oui", "Non" };
                                reponse = JOptionPane.showOptionDialog(null, "Echec de sauvegarde des données modifiées, voulez vous réessayer?\n"
                                        + "Si oui attendez 30 secondes avant de réessayer, le serveur est peut être en train de dormir...", "Attention",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                            }
                        }
                        dialog.dispose();
                        System.exit(0);
                        return null;
                    }
                };
                worker.execute();

            }
        });

        setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/LOGO.GIF")));
        setVisible(true);

    }

    public void focusLost(FocusEvent e) {
        if (e.getSource().getClass() == JTextField.class) {

            JTextField textField = (JTextField) e.getSource();
            String content = textField.getText();
            if (content.indexOf("$") != -1) {
                JOptionPane.showMessageDialog(null, "Vous ne pouvez pas saisir le caractère '$'.\nIl est réservé pour le logiciel.", "Attention",
                        JOptionPane.WARNING_MESSAGE);
                textField.requestFocus();
            }
        }
        if (e.getSource() == tCodP) {
            boolean trouve = false;
            for (int i = 0; i < listeLigne.size() && !trouve; i++) {
                listeAttributs = (ArrayList) listeLigne.get(i);
                if (((String) listeAttributs.get(5)).equals(tCodP.getText())) {
                    trouve = true;
                    tVille.setText((String) listeAttributs.get(6));
                }
            }// for
            if (!trouve)
                tVille.setText("");
        }
        if (e.getSource() == tNom) {
            int nbTrouve = 0;
            String adresse = "", codP = "", ville = "", tel = "";
            for (int i = 0; i < listeLigne.size() && nbTrouve < 2; i++) {
                listeAttributs = (ArrayList) listeLigne.get(i);
                if ((((String) listeAttributs.get(1)).toUpperCase()).equals((tNom.getText()).toUpperCase())) {

                    adresse = (String) listeAttributs.get(4);
                    codP = (String) listeAttributs.get(5);
                    ville = (String) listeAttributs.get(6);
                    tel = (String) listeAttributs.get(7);
                    nbTrouve++;
                }
            }// for

            if (nbTrouve == 1) {
                tAdresse.setText(adresse);
                tCodP.setText(codP);
                tVille.setText(ville);
                tTel.setText(tel);
            }
        }
    }

    public void focusGained(FocusEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bImprimer) {
            // FenPref laFenPref;
            if (colonnes != null && colonnes.size() != 0)
                // laFenPref = new FenPref(this,"test", "essai");
                FenPref.getInstance(this, "test", "essai");
            else
                JOptionPane.showMessageDialog(null, "Vous ne pouvez pas imprimer ou générer un tableau vide.", "Attention", JOptionPane.WARNING_MESSAGE);

        } else if (e.getSource() == bRaz) {
            tNumLicense.setText("");
            tNom.setText("");
            tPrenom.setText("");
            tDatN.setText("");
            tAdresse.setText("");
            tAdresse2.setText("");
            tMail.setText("");
            tCodP.setText("");
            tVille.setText("");
            tTel.setText("");
            jLic.setSelectedItem("");
            tPrixLic.setText("");
            tAdhePrix.setText("");
            jCategorie.setSelectedItem("");
            jCompDate.setSelectedItem("=");
            tDateEdition.setText("");
            rNouvAncT.doClick();
            rInscritT.doClick();
            rAssuranceT.doClick();
            rAgeTexte.doClick();
            modeModifSupp = false;
            tNumLicense.setEnabled(true);
            tDateEdition.setEnabled(true);
        } else if (e.getSource() == bRecherche) {
            try {
                colonnes = rechercher();
            } catch (ParseException e1) {
                // TODO Auto-generated catch block
                JOptionPane.showMessageDialog(null, "La date n'est pas au format JJ/MM/YYYY.", "Erreur", JOptionPane.WARNING_MESSAGE);
            }
            panelPrincipal.remove(PtableView);
            PtableView = new Tableau(this, enTete, colonnes, true);
            panelPrincipal.add(PtableView);
            this.validate();
            // if(colonnes.size() == 0)
            // JOptionPane.showMessageDialog(null, "Aucun membre trouvé.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == bAjout) {
            ArrayList listeAttributs = null;
            boolean dejaPresent = false;
            if (rNouvAncT.isSelected() || rInscritT.isSelected())
                JOptionPane.showMessageDialog(null, "Positionnez les boutons sur 'Oui' ou 'Non'\npas sur 'Tous'.", "Attention", JOptionPane.WARNING_MESSAGE);
            else {
                if (rAgeInter.isSelected())
                    JOptionPane.showMessageDialog(null, "Pour l'ajout, il faut positionner le bouton sur la zone de texte de l'année de naissance.",
                            "Attention", JOptionPane.WARNING_MESSAGE);
                else {
                    if (!tNumLicense.getText().equals(""))
                        for (int i = 0; i < listeLigne.size() && !dejaPresent; i++) {
                            listeAttributs = (ArrayList) listeLigne.get(i);
                            if (((String) listeAttributs.get(0)).equals((tNumLicense.getText()).trim())) {
                                JOptionPane.showMessageDialog(null, "Cet enregistrement existe déjà.\nSéléctionnez le si vous voulez le modifier.",
                                        "Attention", JOptionPane.WARNING_MESSAGE);
                                dejaPresent = true;
                            }
                        }
                    else {
                        Object[] options = { "Oui", "Non" };
                        int reponse, i;
                        int fauxNum = -1;
                        int leNumLic = 0;
                        reponse = JOptionPane
                                .showOptionDialog(
                                        null,
                                        "Si vous ne rentrez pas de numéro de licence, le programme va en genérer un faux automatiquement.\nVous pourrez le modifier à tous moments.\nVoulez vous continuer?",
                                        "Attention", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                        if (reponse == 0) {
                            for (int g = 0; g < listeLigne.size() && !dejaPresent; g++) {
                                listeAttributs = (ArrayList) listeLigne.get(g);
                                try {
                                    leNumLic = Integer.parseInt((String) listeAttributs.get(0));
                                } catch (Exception ne) {
                                }
                                if (leNumLic <= fauxNum)
                                    fauxNum = leNumLic - 1;
                            }
                            JOptionPane.showMessageDialog(null, "Voici le numéro généré automatiquement: " + String.valueOf(fauxNum)
                                    + "\nNotez le afin de pouvoir retrouver ce membre plus facilement.", "Attention", JOptionPane.INFORMATION_MESSAGE);
                            tNumLicense.setText(String.valueOf(fauxNum));
                        } else
                            dejaPresent = true;// il est pas là mais c pour pas l'inserer
                    }
                    try {
                        if (!dejaPresent) {
                            tDateEdition.setText(formatDate.format(new Date()));
                            listeAttributs = new ArrayList();
                            listeAttributs.add(tNumLicense.getText());
                            listeAttributs.add(tNom.getText());
                            listeAttributs.add(tPrenom.getText());
                            if (tDatN.getText().equals(""))
                                dateN = null;
                            else
                                dateN = formatDate.parse(tDatN.getText());
                            listeAttributs.add(dateN);
                            listeAttributs.add(tAdresse.getText());

                            listeAttributs.add(tCodP.getText());
                            listeAttributs.add(tVille.getText());
                            listeAttributs.add(tTel.getText());
                            listeAttributs.add((String) jLic.getSelectedItem());
                            listeAttributs.add(tPrixLic.getText());
                            listeAttributs.add(tAdhePrix.getText());
                            listeAttributs.add((String) jCategorie.getSelectedItem());
                            if (tDateEdition.getText().equals(""))
                                dToday = null;
                            else
                                dToday = formatDate.parse(tDateEdition.getText());
                            listeAttributs.add(dToday);
                            if (rNouvAncN.isSelected())
                                listeAttributs.add("oui");
                            else
                                listeAttributs.add("non");
                            if (rInscritI.isSelected())
                                listeAttributs.add("oui");
                            else
                                listeAttributs.add("non");
                            if (rAssuranceO.isSelected())
                                listeAttributs.add("oui");
                            else
                                listeAttributs.add("non");
                            listeAttributs.add(tAdresse2.getText());
                            listeAttributs.add(tMail.getText());
                            listeLigne.add(listeAttributs);
                            bRecherche.doClick();
                        }
                    }// try
                    catch (ParseException pe) {
                        JOptionPane.showMessageDialog(null, "La date n'est pas au format JJ/MM/YYYY.", "Erreur", JOptionPane.WARNING_MESSAGE);
                    }
                }// else erreur année
            }// else erreur

        } else if (e.getSource() == bModif) {
            ArrayList listeAttributs = null;
            boolean trouve = false;
            boolean dejaPresent = false;
            int i = 0;
            String numLicComp;
            // if(!tNumLicense.isEnabled())
            if (modeModifSupp) {
                if (rNouvAncT.isSelected() || rInscritT.isSelected() || rAssuranceT.isSelected())
                    JOptionPane
                            .showMessageDialog(null, "Positionnez les boutons sur 'Oui' ou 'Non'\npas sur 'Tous'.", "Attention", JOptionPane.WARNING_MESSAGE);
                else {
                    if (rAgeInter.isSelected())
                        JOptionPane.showMessageDialog(null, "Pour l'ajout, il faut positionner le bouton sur la zone de texte de l'année de naissance.",
                                "Attention", JOptionPane.WARNING_MESSAGE);
                    else {
                        try {
                            if (tNumLicense.isEnabled()) {
                                numLicComp = oldNumLic;
                                if (tNumLicense.getText().charAt(0) != '-')
                                    for (int k = 0; k < listeLigne.size() && !dejaPresent; k++) {
                                        listeAttributs = (ArrayList) listeLigne.get(k);
                                        if (((String) listeAttributs.get(0)).equals((tNumLicense.getText()).trim())) {
                                            JOptionPane.showMessageDialog(null, "Il existe déjà un membre avec ce numéro de licence.", "Attention",
                                                    JOptionPane.WARNING_MESSAGE);
                                            dejaPresent = true;
                                        }
                                    }
                            } else
                                numLicComp = tNumLicense.getText();
                            for (i = 0; i < listeLigne.size() && !trouve; i++) {
                                listeAttributs = (ArrayList) listeLigne.get(i);
                                if (((String) listeAttributs.get(0)).equals(numLicComp))
                                    trouve = true;
                            }
                            if (trouve && !dejaPresent) {
                                tDateEdition.setText(formatDate.format(new Date()));
                                if (tDatN.getText().equals(""))
                                    dateN = null;
                                else
                                    dateN = formatDate.parse(tDatN.getText());
                                if (tDateEdition.getText().equals(""))
                                    dToday = null;
                                else
                                    dToday = formatDate.parse(tDateEdition.getText());
                                listeLigne.remove(i - 1);
                                listeAttributs = new ArrayList();
                                listeAttributs.add(tNumLicense.getText());
                                listeAttributs.add(tNom.getText());
                                listeAttributs.add(tPrenom.getText());
                                listeAttributs.add(dateN);
                                listeAttributs.add(tAdresse.getText());
                                listeAttributs.add(tCodP.getText());
                                listeAttributs.add(tVille.getText());
                                listeAttributs.add(tTel.getText());
                                listeAttributs.add((String) jLic.getSelectedItem());
                                listeAttributs.add(tPrixLic.getText());
                                listeAttributs.add(tAdhePrix.getText());
                                listeAttributs.add((String) jCategorie.getSelectedItem());
                                listeAttributs.add(dToday);
                                if (rNouvAncN.isSelected())
                                    listeAttributs.add("oui");
                                else
                                    listeAttributs.add("non");
                                if (rInscritI.isSelected())
                                    listeAttributs.add("oui");
                                else
                                    listeAttributs.add("non");
                                if (rAssuranceO.isSelected())
                                    listeAttributs.add("oui");
                                else
                                    listeAttributs.add("non");
                                listeAttributs.add(tAdresse2.getText());
                                listeAttributs.add(tMail.getText());
                                listeLigne.add(listeAttributs);
                                JOptionPane.showMessageDialog(null, "Modification efféctuée.", "Info", JOptionPane.INFORMATION_MESSAGE);

                                panelPrincipal.remove(PtableView);
                                PtableView = new Tableau(this, enTete, rechercher(), true);
                            }
                            panelPrincipal.add(PtableView);
                            this.validate();
                        }// try
                        catch (ParseException pe) {
                            JOptionPane.showMessageDialog(null, "La date n'est pas au format JJ/MM/YYYY.", "Erreur", JOptionPane.WARNING_MESSAGE);
                        }
                    }// erreur date
                }
            } else
                JOptionPane.showMessageDialog(null, "Il faut séléctionner un membre dans le tableau\navant de modifier.", "Attention",
                        JOptionPane.WARNING_MESSAGE);
        } else if (e.getSource() == bSuppression) {
            Object[] options = { "Oui", "Non" };
            int reponse, i;
            boolean trouve = false;
            ArrayList listeAttributs = new ArrayList();
            // if(!tNumLicense.isEnabled())
            if (modeModifSupp) {
                reponse = JOptionPane.showOptionDialog(null, "Etes vous certain de vouloir supprimer cet enregistrement?", "Attention",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (reponse == 0) {
                    for (i = 0; i < listeLigne.size() && !trouve; i++) {
                        listeAttributs = (ArrayList) listeLigne.get(i);
                        if (((String) listeAttributs.get(0)).equals(tNumLicense.getText()))
                            trouve = true;
                    }
                    if (trouve) {
                        listeLigne.remove(i - 1);
                        JOptionPane.showMessageDialog(null, "Suppression du membre N°" + (String) listeAttributs.get(0) + " effectuée.", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                        bRaz.doClick();
                        bRecherche.doClick();
                    }
                }// if oui
            } else
                JOptionPane.showMessageDialog(null, "Il faut séléctionner un membre dans le tableau\navant de pouvoir supprimer.", "Attention",
                        JOptionPane.WARNING_MESSAGE);

        } else if (e.getSource() == bEnregistrer) {
            enregistrerFic(data);
        } else if (e.getSource() == bChangeAnnee) {
            Object[] options = { "Oui", "Non" };
            int reponse, i;
            reponse = JOptionPane.showOptionDialog(null,
                    "Tous les nouveaux membres vont devenir ancien\net tous les inscrit vont être désinscrit?\nL'ancien fichier de données sera sauvegardé sous le nom de : "
                            + "data_" + formatDateFic.format(new Date()) + ".txt", "Attention", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                    options, options[0]);
            if (reponse == 0) {
                enregistrerFic(new File("data_" + formatDateFic.format(new Date()) + ".txt"));
                for (i = 0; i < listeLigne.size(); i++) {
                    listeAttributs = (ArrayList) listeLigne.get(i);
                    listeAttributs.remove(13);
                    listeAttributs.add(13, "non");
                    listeAttributs.remove(14);
                    listeAttributs.add(14, "non");
                }
                JOptionPane.showMessageDialog(null, "Changement d'année effectué.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }// if oui
        } else if (e.getSource() == jLic) {
            if (jLic.getSelectedIndex() != 0) {
                tPrixLic.setText((String) listePrixLic.get(jLic.getSelectedIndex() - 1));
            } else
                tPrixLic.setText("");
        } else if (e.getSource() == bMajLic) {
            // Singleton
            ConfigLicence.getInstance(this);
        } else if (e.getSource() == bMajCat) {
            // Singleton
            ConfigCategorie.getInstance(this);
        }

    }

    public Vector rechercher() throws ParseException {

        Vector rangeeCourante = null;
        Vector toutesRangees = new Vector();
        Vector toutesRangeesFalse = new Vector();
        ArrayList listeAttributs;
        boolean ageOk = false;
        Date dateActuelle = new Date();
        Calendar calendrier = Calendar.getInstance();
        String dateAttribut = null;
        prixTotalLic = 0;

        for (int i = 0; i < listeLigne.size(); i++) {
            ageOk = false;
            listeAttributs = (ArrayList) listeLigne.get(i);
            rangeeCourante = new Vector();
            if ((tNumLicense.getText()).equals("") || ((String) listeAttributs.get(0)).toUpperCase().indexOf(tNumLicense.getText().toUpperCase()) != -1)
                if ((tNom.getText()).equals("") || ((String) listeAttributs.get(1)).toUpperCase().indexOf(tNom.getText().toUpperCase()) != -1)
                    if ((tPrenom.getText()).equals("") || ((String) listeAttributs.get(2)).toUpperCase().indexOf(tPrenom.getText().toUpperCase()) != -1) {
                        if ((Date) listeAttributs.get(3) != null)
                            dateAttribut = formatDate.format((Date) listeAttributs.get(3));
                        else
                            dateAttribut = "";
                        if (rAgeTexte.isSelected()) {

                            if ((tDatN.getText()).equals("") || dateAttribut.toUpperCase().indexOf(tDatN.getText().toUpperCase()) != -1)
                                ageOk = true;
                        } else {
                            if (!dateAttribut.equals("")) {
                                calendrier.setTime((Date) listeAttributs.get(3));
                                calendrier.add(Calendar.YEAR, Integer.parseInt((String) jAgeDeb.getSelectedItem()));
                                if (dateActuelle.compareTo(calendrier.getTime()) > 0) {

                                    calendrier.setTime((Date) listeAttributs.get(3));
                                    calendrier.add(Calendar.YEAR, Integer.parseInt((String) jAgeFin.getSelectedItem()) + 1);
                                    if (dateActuelle.compareTo(calendrier.getTime()) < 0)
                                        ageOk = true;
                                }
                            }

                        }
                        if (ageOk == true)
                            if ((tAdresse.getText()).equals("")
                                    || ((String) listeAttributs.get(4)).toUpperCase().indexOf(tAdresse.getText().toUpperCase()) != -1)
                                if ((tCodP.getText()).equals("") || ((String) listeAttributs.get(5)).toUpperCase().indexOf(tCodP.getText().toUpperCase()) != -1)
                                    if ((tVille.getText()).equals("")
                                            || ((String) listeAttributs.get(6)).toUpperCase().indexOf(tVille.getText().toUpperCase()) != -1)
                                        if ((tTel.getText()).equals("")
                                                || ((String) listeAttributs.get(7)).toUpperCase().indexOf(tTel.getText().toUpperCase()) != -1)
                                            if (((String) jLic.getSelectedItem()).equals("")
                                                    || ((String) listeAttributs.get(8)).toUpperCase().indexOf(((String) jLic.getSelectedItem()).toUpperCase()) != -1)
                                                if ((tPrixLic.getText()).equals("")
                                                        || ((String) listeAttributs.get(9)).toUpperCase().indexOf(tPrixLic.getText().toUpperCase()) != -1)
                                                    if ((tAdhePrix.getText()).equals("")
                                                            || ((String) listeAttributs.get(10)).toUpperCase().indexOf(tAdhePrix.getText().toUpperCase()) != -1) {
                                                        if ((Date) listeAttributs.get(12) != null)
                                                            dateAttribut = formatDate.format((Date) listeAttributs.get(12));
                                                        else
                                                            dateAttribut = "";
                                                        if ((tDateEdition.getText()).equals("")
                                                                || (Date) listeAttributs.get(12) != null
                                                                && ((jCompDate.getSelectedItem().equals("=") && dateAttribut.toUpperCase().indexOf(
                                                                        tDateEdition.getText().toUpperCase()) != -1)
                                                                        || (jCompDate.getSelectedItem().equals(">") && ((Date) listeAttributs.get(12))
                                                                                .after(formatDate.parse(tDateEdition.getText()))) || (jCompDate
                                                                        .getSelectedItem().equals("<") && ((Date) listeAttributs.get(12)).before(formatDate
                                                                        .parse(tDateEdition.getText())))))
                                                            if (((String) jCategorie.getSelectedItem()).equals("")
                                                                    || ((String) listeAttributs.get(11)).toUpperCase().indexOf(
                                                                            ((String) jCategorie.getSelectedItem()).toUpperCase()) != -1)
                                                                if (rNouvAncT.isSelected()
                                                                        || (((String) listeAttributs.get(13)).equals("oui") && rNouvAncN.isSelected())
                                                                        || (((String) listeAttributs.get(13)).equals("non") && rNouvAncA.isSelected()))
                                                                    if (rInscritT.isSelected()
                                                                            || (((String) listeAttributs.get(14)).equals("oui") && rInscritI.isSelected())
                                                                            || (((String) listeAttributs.get(14)).equals("non") && rInscritN.isSelected()))
                                                                        if (rAssuranceT.isSelected()
                                                                                || (((String) listeAttributs.get(15)).equals("oui") && rAssuranceO.isSelected())
                                                                                || (((String) listeAttributs.get(15)).equals("non") && rAssuranceN.isSelected()))
                                                                            if ((tAdresse2.getText()).equals("")
                                                                                    || ((String) listeAttributs.get(16)).toUpperCase().indexOf(
                                                                                            tAdresse2.getText().toUpperCase()) != -1)
                                                                                if ((tMail.getText()).equals("")
                                                                                        || ((String) listeAttributs.get(17)).toUpperCase().indexOf(
                                                                                                tMail.getText().toUpperCase()) != -1) {
                                                                                    rangeeCourante.addElement(listeAttributs.get(0));
                                                                                    rangeeCourante.addElement(listeAttributs.get(1));
                                                                                    rangeeCourante.addElement(listeAttributs.get(2));
                                                                                    if (listeAttributs.get(3) != null)
                                                                                        rangeeCourante.addElement(formatDate.format(listeAttributs.get(3)));
                                                                                    else
                                                                                        rangeeCourante.addElement("");
                                                                                    rangeeCourante.addElement(listeAttributs.get(4));
                                                                                    rangeeCourante.addElement(listeAttributs.get(5));
                                                                                    rangeeCourante.addElement(listeAttributs.get(6));
                                                                                    rangeeCourante.addElement(listeAttributs.get(7));
                                                                                    rangeeCourante.addElement(listeAttributs.get(8));
                                                                                    rangeeCourante.addElement(listeAttributs.get(9));

                                                                                    try {
                                                                                        prixTotalLic += Double.valueOf((String) listeAttributs.get(9));

                                                                                    } catch (Exception ei) {
                                                                                    }
                                                                                    rangeeCourante.addElement(listeAttributs.get(10));
                                                                                    rangeeCourante.addElement(listeAttributs.get(11));
                                                                                    if (listeAttributs.get(12) != null)
                                                                                        rangeeCourante.addElement(formatDate.format(listeAttributs.get(12)));
                                                                                    else
                                                                                        rangeeCourante.addElement("");
                                                                                    if (((String) listeAttributs.get(13)).equals("oui"))
                                                                                        rangeeCourante.addElement(new Boolean(true));
                                                                                    else
                                                                                        rangeeCourante.addElement(new Boolean(false));
                                                                                    if (((String) listeAttributs.get(14)).equals("oui"))
                                                                                        rangeeCourante.addElement(new Boolean(true));
                                                                                    else
                                                                                        rangeeCourante.addElement(new Boolean(false));
                                                                                    if (((String) listeAttributs.get(15)).equals("oui"))
                                                                                        rangeeCourante.addElement(new Boolean(true));
                                                                                    else
                                                                                        rangeeCourante.addElement(new Boolean(false));
                                                                                    rangeeCourante.addElement(listeAttributs.get(16));
                                                                                    rangeeCourante.addElement(listeAttributs.get(17));
                                                                                    // lignes roses VS lignes blanches
                                                                                    if (((String) listeAttributs.get(14)).equals("oui"))
                                                                                        toutesRangees.addElement(rangeeCourante);
                                                                                    else
                                                                                        toutesRangeesFalse.addElement(rangeeCourante);

                                                                                }
                                                    }// tadh
                    }// if dateN
        }

        for (int i = 0; i < toutesRangeesFalse.size(); i++)
            toutesRangees.add((Vector) toutesRangeesFalse.elementAt(i));
        return toutesRangees;

    }// rechercher

    public void enregistrerFic(File fichier) {
        try {
            fichier.createNewFile();

            FileWriter ecrivain = new FileWriter(fichier);
            String attribut = null;
            for (int i = 0; i < listeLigne.size(); i++) {
                listeAttributs = (ArrayList) listeLigne.get(i);
                for (int j = 0; j < listeAttributs.size(); j++) {
                    if (j == 3 || j == 12)
                        if ((Date) listeAttributs.get(j) != null)
                            attribut = formatDate.format((Date) listeAttributs.get(j));
                        else
                            attribut = "";
                    else
                        attribut = (String) listeAttributs.get(j);
                    ecrivain.write(attribut + "$", 0, attribut.length() + 1);
                }
                ecrivain.write(10);
            }

            ecrivain.flush();
            ecrivain.close();

        }// try

        catch (IOException eIo) {
            JOptionPane.showMessageDialog(null, "Impossible d'écrire sur le fichier de sauvegarde.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

    }

}
