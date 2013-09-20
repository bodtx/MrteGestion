package mrteGest;

import java.awt.print.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.Dimension;
import java.io.*;

import java.text.MessageFormat;

public class FenPref extends JFrame implements ActionListener
{
    String leTitre, leLibelle;
    Vector lesRangees, lesEnTete, newEnTete, newRangees, rangee;
    JButton bVoir, bImprimer, bFicCvs;
    JCheckBox cNumLicense, cNom, cPrenom,cDatN, cAdresse , cCodP, cVille, cTel, cTypLic, cPrixLic, cAdhePrix,cCat, cNouvAnc, cInscrit, cAssurance, cDateEdition ;
    Container lePanelPrinc;
    Tableau pTableView;
    Gestion laGestion;
    File dSelection;
    
    private static FenPref _instance;
    
    public static synchronized FenPref getInstance(Gestion gestion, String titre, String libelle)
    {
        if (_instance==null)
            _instance = new FenPref(gestion, titre, libelle);
        return _instance;
    }
    
    private FenPref(Gestion gestion, String titre, String libelle)
    {
        super(titre);
        leTitre = titre;
        leLibelle = libelle;
        laGestion = gestion;
        lesEnTete = laGestion.enTete;
        lesRangees = laGestion.colonnes;
        lePanelPrinc = this.getContentPane();
        lePanelPrinc.setLayout(new BorderLayout());
        JPanel lePanelH = new JPanel(new GridLayout(10,4));
        //lePanelH.setBackground(Color.green);
        
        JLabel lTitreCGG = new JLabel("Colonnes du tableau");
        lTitreCGG.setForeground(Color.blue);
        JLabel lTitreCDG = new JLabel("Cocher pour inclure");
        lTitreCDG.setForeground(Color.blue);
        JLabel lTitreCGD = new JLabel("Colonnes du tableau");
        lTitreCGD.setForeground(Color.blue);
        JLabel lTitreCDD = new JLabel("Cocher pour inclure");
        lTitreCDD.setForeground(Color.blue);
        JLabel lNumLicense = new JLabel("Numéro de license");
        JLabel lNom = new JLabel("Nom");
        JLabel lPrenom = new JLabel("Prénom");
        JLabel lDatN = new JLabel("Date de naissance");
        JLabel lAdresse = new JLabel("Adresse");
        JLabel lCodP = new JLabel("Code postal");
        JLabel lVille = new JLabel("Ville");
        JLabel lTel = new JLabel("Téléphone");
        JLabel lTypLic = new JLabel("Type de license");
        JLabel lPrixLic = new JLabel("Prix de la license");
        JLabel lAdhePrix = new JLabel("Prix de l'adhésion");
        JLabel lCat = new JLabel("Catégories");
        JLabel lNouvAnc = new JLabel("Nouveau");
        JLabel lInscrit = new JLabel("Inscrit");
        JLabel lAssurance = new JLabel("Assurance");
        JLabel lDateEdition = new JLabel("Date d'édition");
        
        cNumLicense = new JCheckBox();
        //cNumLicense.setBackground(Color.green);
        cNom = new JCheckBox();
        //cNom.setBackground(Color.green);
        cPrenom = new JCheckBox();
        //cPrenom.setBackground(Color.green);
        cDatN = new JCheckBox();
        //cDatN.setBackground(Color.green);
        cAdresse = new JCheckBox();
        //cAdresse.setBackground(Color.green);
        cCodP = new JCheckBox();
        //cCodP.setBackground(Color.green);
        cVille = new JCheckBox();
        cTel = new JCheckBox();
        //cVille.setBackground(Color.green);
        cTypLic = new JCheckBox();
        //cTypLic.setBackground(Color.green);
        cPrixLic = new JCheckBox();
        //cPrixLic.setBackground(Color.green);
        cAdhePrix = new JCheckBox();
        //cAdhePrix.setBackground(Color.green);
        cCat = new JCheckBox();
        ///cCat.setBackground(Color.green);
        cNouvAnc = new JCheckBox();
        //cNouvAnc.setBackground(Color.green);
        cInscrit = new JCheckBox();
        //cInscrit.setBackground(Color.green);
        cAssurance = new JCheckBox();
        cDateEdition = new JCheckBox();
        
        bVoir = new JButton("Apercu", new ImageIcon(getClass().getResource("APERCU.GIF"),"Aperçu"));
        //bVoir.setBackground(Color.green);
        bVoir.addActionListener(this);
        bImprimer = new JButton("Imprimer", new ImageIcon(getClass().getResource("IMPRIMER.GIF"),"Imprimer"));
        //bImprimer.setBackground(Color.green);
        bImprimer.addActionListener(this);
        bFicCvs = new JButton("Générer fichier Excel", new ImageIcon(getClass().getResource("FICHIER.GIF"),"Fichier"));
        //bFicCvs.setBackground(Color.green);
        bFicCvs.addActionListener(this);
        
        lePanelH.add(lTitreCGG);
        lePanelH.add(lTitreCDG);
        lePanelH.add(lTitreCGD);
        lePanelH.add(lTitreCDD);
        lePanelH.add(lNumLicense);
        lePanelH.add(cNumLicense);
        lePanelH.add(lNom);
        lePanelH.add(cNom);
        lePanelH.add(lPrenom);
        lePanelH.add(cPrenom);
        lePanelH.add(lDatN);
        lePanelH.add(cDatN);
        lePanelH.add(lAdresse);
        lePanelH.add(cAdresse);
        lePanelH.add(lCodP);
        lePanelH.add(cCodP);
        lePanelH.add(lVille);
        lePanelH.add(cVille);
        lePanelH.add(lTel);
        lePanelH.add(cTel);
        lePanelH.add(lTypLic);
        lePanelH.add(cTypLic);
        lePanelH.add(lPrixLic);
        lePanelH.add(cPrixLic);
        lePanelH.add(lAdhePrix);
        lePanelH.add(cAdhePrix);
        lePanelH.add(lCat);
        lePanelH.add(cCat);
        lePanelH.add(lNouvAnc);
        lePanelH.add(cNouvAnc);
        lePanelH.add(lInscrit);
        lePanelH.add(cInscrit);
        lePanelH.add(lAssurance);
        lePanelH.add(cAssurance);
        lePanelH.add(lDateEdition);
        lePanelH.add(cDateEdition);
        //lePanelH.add(new JPanel());
        //lePanelH.add(new JPanel());
        lePanelH.add(bVoir);
        lePanelH.add(bImprimer);
        lePanelH.add(bFicCvs);
        
        
        
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenDim.width ,screenDim.height);
        lePanelPrinc.add(lePanelH,BorderLayout.NORTH);
        
        setIconImage(Toolkit.getDefaultToolkit().createImage(Gestion.class.getResource("LOGO.GIF")));
        setVisible(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        this.setVisible(true);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
                removeAll();
                _instance=null;
            }
        });
        
        
        
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == bVoir)
        {
            newEnTete = (Vector) lesEnTete.clone();
            newRangees = (Vector) lesRangees.clone();
            rangee = null;
            int i,j;
            j = 0;
            
            if(!cNumLicense.isSelected())
            {
                newEnTete.remove(0);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(0);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cNom.isSelected())
            {
                newEnTete.remove(1-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(1-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            
            if(!cPrenom.isSelected())
            {
                newEnTete.remove(2-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(2-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cDatN.isSelected())
            {
                newEnTete.remove(3-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(3-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
                
            }
            if(!cAdresse.isSelected())
            {
                newEnTete.remove(4-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(4-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cCodP.isSelected())
            {
                newEnTete.remove(5-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(5-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cVille.isSelected())
            {
                newEnTete.remove(6-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(6-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cTel.isSelected())
            {
                newEnTete.remove(7-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(7-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cTypLic.isSelected())
            {
                newEnTete.remove(8-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(8-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cPrixLic.isSelected())
            {
                newEnTete.remove(9-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(9-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cAdhePrix.isSelected())
            {
                newEnTete.remove(10-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(10-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cCat.isSelected())
            {
                newEnTete.remove(11-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(11-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cDateEdition.isSelected())
            {
                newEnTete.remove(12-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(12-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cNouvAnc.isSelected())
            {
                newEnTete.remove(13-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(13-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cInscrit.isSelected())
            {
                newEnTete.remove(14-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(14-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            if(!cAssurance.isSelected())
            {
                newEnTete.remove(15-j);
                for(i = 0; i < newRangees.size(); i++)
                {
                    rangee = (Vector) ((Vector) newRangees.elementAt(i)).clone();
                    rangee.remove(15-j);
                    newRangees.remove(i);
                    newRangees.insertElementAt(rangee,i);
                }
                j++;
            }
            
            if(pTableView != null)
                lePanelPrinc.remove(pTableView);
            pTableView = new Tableau(laGestion, newEnTete, newRangees, false);
            lePanelPrinc.add(pTableView,BorderLayout.CENTER);
            this.validate();
            
            
        }
        else if(e.getSource() == bImprimer)
        {
            if(newEnTete == null || newEnTete.size() == 0)
                JOptionPane.showMessageDialog(null, "Il faut effectuer l'apercu d'au moins une colonne avant de pouvoir imprimer.", "Attention", JOptionPane.WARNING_MESSAGE);
            else
            {
                try
                {
                    pTableView.tableView.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat("MRTE CLUB N°20"), new MessageFormat("Page {0} / "+newRangees.size()+" licences = "+laGestion.prixTotalLic+"€" ) );
                }
                catch(java.awt.print.PrinterException pE)
                {
                    JOptionPane.showMessageDialog(null, "Erreur lors de l'impression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if(e.getSource() == bFicCvs)
        {
            
            if(newEnTete == null || newEnTete.size() == 0)
                JOptionPane.showMessageDialog(null, "Il faut effectuer l'apercu d'au moins une colonne avant de pouvoir générer un fichier Excel.", "Attention", JOptionPane.WARNING_MESSAGE);
            else
            {
                JFileChooser fc = new JFileChooser();
                fc.setApproveButtonText("Sélectionner");
                fc.setDialogTitle("Chemin du fichier à enregistrer");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                if(JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this) )
                {
                    dSelection = fc.getSelectedFile();
                    File csv = new File(dSelection.getAbsolutePath()+"/mrte.xls");
                    try
                    {
                        
                        csv.createNewFile();
                        
                        
                        FileWriter ecrivain = new FileWriter(csv);
                        String attribut = null;
                        for(int i = 0;i < newEnTete.size();i++)
                        {
                            if(i != 0)
                                ecrivain.write('\t');
                            attribut = (String) newEnTete.elementAt(i);
                            ecrivain.write(attribut,0,attribut.length());
                        }
                        ecrivain.write(13);
                        ecrivain.write(10);
                        for(int i = 0;i < newRangees.size();i++)
                        {
                            rangee = (Vector) newRangees.elementAt(i);
                            for(int j = 0; j < rangee.size(); j++)
                            {
                                if(j != 0)
                                    ecrivain.write('\t');
                                if( rangee.elementAt(j).getClass() == Boolean.class)
                                    if( ( (Boolean) rangee.elementAt(j) ).booleanValue() )
                                        attribut = "oui";
                                    else
                                        attribut = "non";
                                else
                                    attribut = (String) rangee.elementAt(j);
                                ecrivain.write(attribut,0,attribut.length());
                            }
                            ecrivain.write(13);
                            ecrivain.write(10);
                        }
                        
                        ecrivain.flush();
                        ecrivain.close();
                        JOptionPane.showMessageDialog(null, "Fichier Excel généré.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }//try
                    
                    catch(IOException eIo)
                    {
                        JOptionPane.showMessageDialog(null, "Impossible de générer le fichier Excel.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            
        }
        
    }
}
