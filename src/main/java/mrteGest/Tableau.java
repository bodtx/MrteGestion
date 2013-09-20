package mrteGest;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.JTable;
import javax.swing.border.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Color;
import java.awt.*;
import java.awt.print.*;
import java.util.*;
import java.awt.geom.*;



public class Tableau extends JPanel
{

	public JTable tableView;
	Gestion laGestion;
	Vector lEnTete;
	Vector lesRangees;

    public Tableau(Gestion telleGestion, Vector telleEnTete, Vector telleRangee, boolean selection)
    {
        laGestion = telleGestion;
        lEnTete = telleEnTete;
            lesRangees = telleRangee;

            JLabel compteur = new JLabel( "Nombre d'enregistrements trouvés: "+String.valueOf(lesRangees.size()) );
            compteur.setForeground(Color.blue);


        // Create a model of the data.
          TableModel dataModel = new AbstractTableModel()
        {
            // These methods always need to be implemented.
            public int getColumnCount() { return lEnTete.size(); }
            public int getRowCount() { return lesRangees.size();}
            public Object getValueAt(int row, int col){return ((Vector)lesRangees.elementAt(row)).elementAt(col);}
            // The default implementations of these methods in
            // AbstractTableModel would work, but we can refine them.
            public String getColumnName(int column) {return (String)lEnTete.elementAt(column);}
            public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
            public boolean isCellEditable(int row, int col) {return false;}
            public void setValueAt(Object aValue, int row, int column) {
                ((Vector)lesRangees.elementAt(row)).setElementAt(aValue,column);


            }
         };

		//remplacement des num type -xx par des blancs pour l'impression
		Vector laRangee;
		if(!selection)
		{
			for(int i = 0;i < telleRangee.size();i++)
			{
				laRangee = (Vector)lesRangees.elementAt(i);
				if( !((String)laRangee.elementAt(0)).equals("") && ((String)laRangee.elementAt(0)).charAt(0) == '-' )
				{
					laRangee.remove(0);
					laRangee.add(0,"");
				}
			}
		}
		// Create the table
		tableView = new JTable(telleRangee,telleEnTete);
		tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		TableSorter sorter = new TableSorter(dataModel); //ADDED THIS
		tableView.setModel(sorter);             //NEW
		sorter.addMouseListenerToHeaderInTable(tableView); //ADDED THIS
        //tableView.setModel(dataModel);


       DefaultTableCellRenderer inscritColumnRenderer = new DefaultTableCellRenderer()
	   {
	  		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocused, int row, int column)
	   		{
	   			Component component=super.getTableCellRendererComponent(table,value,isSelected,isFocused,row,column);
	   			boolean inscris = false;

				if(table.getColumnCount() == 16)
	   			{
	   				inscris = ((Boolean)table.getValueAt(row,14)).booleanValue();
			   		if (inscris)
					   component.setBackground(Color.pink);
			   		else
					   component.setBackground(Color.white);
				}
				if (table.getColumnName(column).equals("Inscrit") || table.getColumnName(column).equals("Nouveau") || table.getColumnName(column).equals("Assurance"))
				{
					boolean check = ((Boolean)table.getValueAt(row,column)).booleanValue();
					component = new JCheckBox("",check);
					if (inscris)
						component.setBackground(Color.pink);
					else
						component.setBackground(Color.white);
				}
				return component;
			}
	   };//end of class


		tableView.setDefaultRenderer(Boolean.class,inscritColumnRenderer);
		tableView.setDefaultRenderer(String.class,inscritColumnRenderer);
		if(selection)
		{
			TableColumn column = null;
			column = tableView.getColumnModel().getColumn(4);
			column.setMinWidth(200);
			column = tableView.getColumnModel().getColumn(1);
			column.setMinWidth(75);
			column = tableView.getColumnModel().getColumn(2);
			column.setMinWidth(75);
			column = tableView.getColumnModel().getColumn(3);
			column.setMinWidth(75);
		}




		// Disable auto resizing
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Add data here

		// Pack the second column of the table
		int margin = 2;
		for(int i = 0;i < lEnTete.size();i++)
			packColumn(tableView, i, margin);





        // Finish setting up the table.
        JScrollPane scrollpane = new JScrollPane(tableView);
		scrollpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        //scrollpane.setPreferredSize(new Dimension(800, 300));

        setLayout(new BorderLayout());
        add(scrollpane,BorderLayout.CENTER);
        add(compteur,BorderLayout.SOUTH);

		if(selection)
		{
			ListSelectionModel rowSM = tableView.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					//Ignore extra messages.
					if (e.getValueIsAdjusting())
						return;
					ListSelectionModel lsm = (ListSelectionModel)e.getSource();
					if (lsm.isSelectionEmpty())
					{
						//System.out.println("//no rows are selected");
					}
					else
					{
						int selectedRow = lsm.getMinSelectionIndex();
						int leNumLic = 0;
						try
						{
							leNumLic = Integer.parseInt((String)tableView.getValueAt(selectedRow,0));
							if(leNumLic < 0)
							{
								laGestion.tNumLicense.setEnabled(true);
								laGestion.oldNumLic = (String)tableView.getValueAt(selectedRow,0);
							}
							else
								laGestion.tNumLicense.setEnabled(false);
							laGestion.tDateEdition.setEnabled(false);
						}
						catch(Exception ne)
						{
							laGestion.tNumLicense.setEnabled(true);
							laGestion.oldNumLic = (String)tableView.getValueAt(selectedRow,0);
						}

						laGestion.modeModifSupp = true;
						laGestion.tNumLicense.setText((String)tableView.getValueAt(selectedRow,0));
						laGestion.tNom.setText((String)tableView.getValueAt(selectedRow,1));
						laGestion.tPrenom.setText((String)tableView.getValueAt(selectedRow,2));
						laGestion.tDatN.setText((String)tableView.getValueAt(selectedRow,3));
						laGestion.tAdresse.setText((String)tableView.getValueAt(selectedRow,4));
						laGestion.tCodP.setText((String)tableView.getValueAt(selectedRow,5));
						laGestion.tVille.setText((String)tableView.getValueAt(selectedRow,6));
						laGestion.tTel.setText((String)tableView.getValueAt(selectedRow,7));
						laGestion.jLic.setSelectedItem((String)tableView.getValueAt(selectedRow,8));
						laGestion.tPrixLic.setText((String)tableView.getValueAt(selectedRow,9));
						laGestion.tAdhePrix.setText((String)tableView.getValueAt(selectedRow,10));
						laGestion.jCategorie.setSelectedItem((String)tableView.getValueAt(selectedRow,11));
						laGestion.tDateEdition.setText((String)tableView.getValueAt(selectedRow,12));
						if(((Boolean)tableView.getValueAt(selectedRow,13)).booleanValue() )
							laGestion.rNouvAncN.doClick();
						else
							laGestion.rNouvAncA.doClick();
						if(((Boolean)tableView.getValueAt(selectedRow,14)).booleanValue())
							laGestion.rInscritI.doClick();
						else
							laGestion.rInscritN.doClick();
                                                if(((Boolean)tableView.getValueAt(selectedRow,15)).booleanValue())
							laGestion.rAssuranceO.doClick();
						else
							laGestion.rAssuranceN.doClick();
						lsm.clearSelection();


					}
				}
			});
		}//if

        setOpaque(true);
        setVisible(true);
    }

	public void packColumns(JTable table, int margin)
	{
		for (int c=0; c<table.getColumnCount(); c++)
		{
			packColumn(table, c, 2);
		}
	}

	// Sets the preferred width of the visible column specified
	// by vColIndex. The column will be just wide enough
	// to show the column head and the widest cell in the column.
	// margin pixels are added to the left and right
	// (resulting in an additional width of 2*margin pixels).
	public void packColumn(JTable table, int vColIndex, int margin)
	{
		TableModel model = table.getModel();
		DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null)
		{
			renderer = table.getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// Get maximum width of column data
		for (int r=0; r<table.getRowCount(); r++)
		{
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(
				table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2*margin;

		// Set the width
		col.setPreferredWidth(width);
	}
}


