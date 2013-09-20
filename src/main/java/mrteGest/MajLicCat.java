package mrteGest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.Dimension;
import java.io.*;

public class MajLicCat extends JFrame implements ActionListener
{
	String leTitre, leLibelle;
	Vector lesRangees, lesEnTete, newEnTete, newRangees, rangee;
	JButton bVoir, bImprimer, bFicCvs;
	Container lePanelPrinc;
	Gestion laGestion;
	JFrame fenetre = this;

	public MajLicCat (Gestion gestion, String titre)
	{
		super(titre);
		leTitre = titre;
		laGestion = gestion;
		lesEnTete = laGestion.enTete;
		lesRangees = laGestion.colonnes;
		lePanelPrinc = this.getContentPane();
		lePanelPrinc.setLayout(new FlowLayout());

		JLabel lChoixLicence = new JLabel("Licence :");
		lChoixLicence.setForeground(Color.blue);
		JLabel lNomLicence = new JLabel("Licence :");
		lNomLicence.setForeground(Color.blue);
		JLabel lPrixLicence = new JLabel("Prix :");
		lPrixLicence.setForeground(Color.blue);
		JLabel lRemplacementLicence = new JLabel("Opération :");
		lRemplacementLicence.setForeground(Color.blue);

		bVoir = new JButton("Apercu", new ImageIcon("/APERCU.GIF","Aperçu"));
		//bVoir.setBackground(Color.green);
		bVoir.addActionListener(this);
		bImprimer = new JButton("Imprimer", new ImageIcon("/IMPRIMER.GIF","Imprimer"));
		//bImprimer.setBackground(Color.green);
		bImprimer.addActionListener(this);
		bFicCvs = new JButton("Générer fichier Excel", new ImageIcon("/FICHIER.GIF","Fichier"));
		//bFicCvs.setBackground(Color.green);
		bFicCvs.addActionListener(this);

		JComboBox jLic = new JComboBox(gestion.listeLic);
		String[] actions = {"Supprimer","Remplacer par : "};
		JComboBox jActions = new JComboBox(actions);
		JTextField tNomLic = new JTextField(8);
		JTextField tPrixLic = new JTextField(8);

		lePanelPrinc.add(lChoixLicence);
		lePanelPrinc.add(jLic);
		lePanelPrinc.add(lRemplacementLicence);
		lePanelPrinc.add(jActions);
		lePanelPrinc.add(lNomLicence);
		lePanelPrinc.add(tNomLic);
		lePanelPrinc.add(lPrixLicence);
		lePanelPrinc.add(tPrixLic);
		lePanelPrinc.add(new JButton("Valider"));






		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(300 ,200);


		setIconImage(Toolkit.getDefaultToolkit().createImage(Gestion.class.getResource("/LOGO.GIF")));
		setVisible(true);

		this.setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				fenetre.dispose();
			}
		});



	}

	public void actionPerformed(ActionEvent e)
	{}




}
