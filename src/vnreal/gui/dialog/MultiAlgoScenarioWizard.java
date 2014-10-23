package vnreal.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import mulavito.gui.dialogs.AbstractButtonDialog;
import vnreal.Scenario;
import vnreal.generators.AlFaresGenerator;
import vnreal.generators.ExponentialGenerator;
import vnreal.generators.FatTreeGenerator;
import vnreal.generators.INetworkGenerator;
import vnreal.generators.GridGenerator;
import vnreal.generators.LocalityGenerator;
import vnreal.generators.RandomEdgeGenerator;
import vnreal.generators.RingGenerator;
import vnreal.generators.StarGenerator;
import vnreal.generators.TransitStubGenerator;
import vnreal.generators.WaxmanGenerator;
import vnreal.gui.GUI;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * This class implements a scenario wizard which allows the use of a different
 * {@link INetworkGenerator} for each network.
 * 
 * @author Philip Huppert
 */
public class MultiAlgoScenarioWizard extends AbstractButtonDialog {

	/**
	 * Generators supported by the wizard.
	 * TODO maybe automatically populate using reflection
	 */
	private static final String[] GENERATORS = new String[] {
		WaxmanGenerator.class.getName(),
		RingGenerator.class.getName(),
		StarGenerator.class.getName(),
		FatTreeGenerator.class.getName(),
		AlFaresGenerator.class.getName(),
		GridGenerator.class.getName(),
		RandomEdgeGenerator.class.getName(),
		ExponentialGenerator.class.getName(),
		LocalityGenerator.class.getName(),
		TransitStubGenerator.class.getName()
	};

	// Table constants
	private static final String[] COLUMNS = new String[] {"Network", "Generator"};
	private static final String VIRTUAL_LBL = "Virtual network %d";
	private static final String SUBSTRATE_LBL = "Substrate network";
	private static final String TABLE_TIP = "Right-click to configure/change generator.";

	// Button constants
	private static final String REMOVE_ACTN = "remove";
	private static final String REMOVE_TIP = "Remove the selected generator.";
	private static final String REMOVE_LBL = "-";

	private static final String ADD_ACTN = "add";
	private static final String ADD_TIP = "Add a generator.";
	private static final String ADD_LBL = "+";

	private static final String DOWN_ACTN = "moveDown";
	private static final String DOWN_TIP = "Move the selected generator down by one level.";
	private static final String DOWN_LBL = "\u2193";

	private static final String UP_ACTN = "moveUp";
	private static final String UP_TIP = "Move the selected generator up by one level.";
	private static final String UP_LBL = "\u2191";

	// Message box constants
	private static final String GENERATOR_MISMATCH_TITLE = "Generator mismatch";
	private static final String GENERATOR_MISMATCH_TEXT = "Replace %s with %s?";
	private static final String CONFIRM_SCENARIO_OVERWRITE_TEXT = "By generating a new scenario you will lose any unsaved changes.\nContinue?";
	private static final String CONFIRM_SCENARIO_OVERWRITE_TITLE = "Scenario Wizard";

	// Dropdown constants
	private static final String CHANGE_GENERATOR_ACTN = "changeGenerator";
	private static final String CONFIGURE_GENERATOR_ACTN = "configureGenerator";
	private static final String COPY_CONFIGURATION_ACTN = "copyConfiguration";
	private static final String PASTE_CONFIGURATION_ACTN = "pasteConfiguration";
	private static final String PASTE_CONFIGURATION_LBL = "Paste configuration";
	private static final String COPY_CONFIGURATION_LBL = "Copy configuration";
	private static final String CONFIGURE_GENERATOR_LBL = "Configure generator";
	private static final String CHANGE_GENERATOR_LBL = "Change generator";

	// Dialog constants
	private static final Dimension DIALOG_DIMENSION = new Dimension(600, 300);
	private static final String DIALOG_ACCEPT = "Create";
	private static final String DIALOG_TITLE = "Scenario Wizard (multiple algorithms)";
	private static final int PADDING = 4;

	// Generator change dialog constants
	private static final String CHGDIAG_ACCEPT = "Change";
	private static final String CHGDIAG_TITLE = "Change generator";
	private static final Dimension CHGDIAG_DIMENSION = new Dimension(250, 100);

	private static final long serialVersionUID = 1L;

	private JTable networkTable;
	private WizardModel model;
	private JButton moveUp, moveDown, add, remove;
	private JMenuItem paste;
	private Scenario scenario;

	public MultiAlgoScenarioWizard(Scenario scenario) {
		super(
				GUI.getInstance(),
				DIALOG_TITLE,
				DIALOG_ACCEPT,
				DIALOG_DIMENSION);
		this.scenario = scenario;
		//this.pack();
		this.setVisible(true);
	}

	@Override
	protected JPanel createContent() {
		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(PADDING, PADDING, 0, PADDING));
		content.setLayout(new BorderLayout(PADDING, 0));

		// createTable enables/disables buttons, therefore createButtons must be called first
		content.add(this.createButtons(), BorderLayout.EAST);
		content.add(this.createTable(), BorderLayout.CENTER);

		return content;
	}

	@Override
	protected void doAction() {
		if (JOptionPane.showConfirmDialog(
				this,
				CONFIRM_SCENARIO_OVERWRITE_TEXT,
				CONFIRM_SCENARIO_OVERWRITE_TITLE,
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return;
		}

		INetworkGenerator[] generators = this.model.getGenerators();
		SubstrateNetwork substrate = null;
		List<VirtualNetwork> vns = new ArrayList<>(generators.length - 1);

		for (int i = 0; i < generators.length; i++) {
			if (i == 0) {
				substrate = generators[i].generateSubstrateNetwork(false);
			} else {
				vns.add(generators[i].generateVirtualNetwork(i));
			}
		}

		this.scenario.setNetworkStack(new NetworkStack(substrate, vns));
	}

	/**
	 * @return a {@link JPanel} containing the network table.
	 */
	private JPanel createTable() {
		// Initialize table and its model.
		this.model = new WizardModel();
		this.networkTable = new JTable(this.model);

		// Add listener to enable/disable entry manipulation buttons based on current selection.
		ButtonActivator ba = new ButtonActivator();
		this.networkTable.getSelectionModel().addListSelectionListener(ba);
		ba.valueChanged(null);

		// Add listener for context menu.
		NetworkTableMouseAdapter ma =
				new NetworkTableMouseAdapter(this.createContextMenu());
		this.networkTable.addMouseListener(ma);

		// Misc. table configuration.
		this.networkTable.setToolTipText(TABLE_TIP);
		this.networkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.networkTable.doLayout();

		// Create container for table and header.
		JPanel tableContainer = new JPanel();
		tableContainer.setLayout(new BorderLayout());

		// Add header.
		tableContainer.add(this.networkTable.getTableHeader(), BorderLayout.PAGE_START);

		// Make table scrollable.
		this.networkTable.setFillsViewportHeight(true);
		JScrollPane tableScroller = new JScrollPane(this.networkTable);
		tableContainer.add(tableScroller, BorderLayout.CENTER);

		return tableContainer;
	}

	/**
	 * @return {@link JPopupMenu} for manipulating an entry in the network
	 *         table.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu menu = new JPopupMenu();
		ActionListener listener = new ContextHandler(this);
		String[] menuItems = {
				CHANGE_GENERATOR_LBL,		CHANGE_GENERATOR_ACTN,
				CONFIGURE_GENERATOR_LBL,	CONFIGURE_GENERATOR_ACTN,
				COPY_CONFIGURATION_LBL,		COPY_CONFIGURATION_ACTN,
				PASTE_CONFIGURATION_LBL,	PASTE_CONFIGURATION_ACTN
		};

		for (int i = 0; i < menuItems.length / 2; i++) {
			JMenuItem item = new JMenuItem(menuItems[i*2]);
			item.setActionCommand(menuItems[i*2+1]);
			item.addActionListener(listener);
			menu.add(item);

			if (item.getText().equals(PASTE_CONFIGURATION_LBL)) {
				this.paste = item;
				item.setEnabled(false);
			}
		}

		return menu;
	}

	/**
	 * @return a {@link JPanel} with {@link JButton}s to manipulate the network
	 *         table: Move up, move down, add, remove.
	 */
	private JPanel createButtons() {
		JPanel bpanel = new JPanel();
		bpanel.setLayout(new BoxLayout(bpanel, BoxLayout.PAGE_AXIS));

		ActionListener listener = new ButtonHandler();
		JButton[] jbuttons = new JButton[4];
		String[] buttons = {
				UP_LBL,		UP_TIP, 	UP_ACTN,
				DOWN_LBL,	DOWN_TIP,	DOWN_ACTN,
				ADD_LBL,	ADD_TIP,	ADD_ACTN,
				REMOVE_LBL,	REMOVE_TIP,	REMOVE_ACTN
		};

		for (int i = 0; i < jbuttons.length; i++) {
			JButton button = new JButton();

			button.setText(buttons[i*3]);
			button.setToolTipText(buttons[i*3+1]);
			button.setActionCommand(buttons[i*3+2]);

			button.addActionListener(listener);
			button.setMaximumSize(new Dimension(
					Short.MAX_VALUE, button.getPreferredSize().height));

			bpanel.add(button);
			jbuttons[i] = button;

			if (i != jbuttons.length - 1) {
				bpanel.add(Box.createVerticalStrut(PADDING));
			}
		}

		this.moveUp = jbuttons[0];
		this.moveDown = jbuttons[1];
		this.add = jbuttons[2];
		this.remove = jbuttons[3];

		return bpanel;
	}

	private void showConfigurationDialog(INetworkGenerator generator) {
		JDialog dialog = new JDialog();
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setLayout(new BorderLayout());
		dialog.setTitle(generator.getName());
		dialog.add(generator.getConfigurationDialog(), BorderLayout.CENTER);

		Rectangle tbounds = this.getBounds();
		Rectangle bounds = new Rectangle();
		bounds.setSize(generator.getConfigurationDialog().getPreferredSize());
		bounds.x = (int) (tbounds.x + 0.5 * tbounds.width - 0.5 * bounds.width);
		bounds.y = (int) (tbounds.y + 0.5 * tbounds.height - 0.5 * bounds.height);
		dialog.setBounds(bounds);
		dialog.setResizable(false);

		dialog.setVisible(true);
	}

	/**
	 * This class implements the model that holds the data that is shown or
	 * manipulated using {@link MultiAlgoScenarioWizard}, i.e. the
	 * {@link INetworkGenerator}s for the different networks.
	 * 
	 * @author Philip Huppert
	 */
	private class WizardModel extends AbstractTableModel {
	
		private static final long serialVersionUID = 1L;
	
		private List<INetworkGenerator> generators;
	
		public WizardModel() {
			super();
			this.generators = new LinkedList<INetworkGenerator>();
			this.generators.add(new WaxmanGenerator());
		}
	
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}
	
		@Override
		public int getColumnCount() {
			return COLUMNS.length;
		}
	
		@Override
		public String getColumnName(int columnIndex) {
			return COLUMNS[columnIndex];
		}
	
		@Override
		public int getRowCount() {
			return generators.size();
		}
	
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				if (rowIndex == 0) {
					return SUBSTRATE_LBL;
				} else {
					return String.format(VIRTUAL_LBL, rowIndex);
				}
			} else {
				return generators.get(rowIndex).toString();
			}
		}
	
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	
		/**
		 * Move the generator in the specified row up by one row.
		 * 
		 * @param row
		 *            to move.
		 */
		public void moveGeneratorUp(int row) {
			if (this.canMoveGeneratorUp(row)) {
				Collections.swap(this.generators, row, row - 1);
				this.fireTableRowsUpdated(row - 1, row);
			}
		}
	
		/**
		 * @param row
		 *            to move.
		 * @return {@code true} if the specified row can be moved up by one row.
		 */
		public boolean canMoveGeneratorUp(int row) {
			return row > 0 && row <= this.generators.size() - 1;
		}
	
		/**
		 * Move the generator in the specified row down by one row.
		 * 
		 * @param row
		 *            to move.
		 */
		public void moveGeneratorDown(int row) {
			if (this.canMoveGeneratorDown(row)) {
				Collections.swap(this.generators, row, row + 1);
				this.fireTableRowsUpdated(row, row + 1);
			}
		}
	
		/**
		 * @param row
		 *            to move.
		 * @return {@code true} if the specified row can be moved down by one
		 *         row.
		 */
		public boolean canMoveGeneratorDown(int i) {
			return i >= 0 && i < this.generators.size() - 1;
		}
	
		/**
		 * Remove the generator in the specified row.
		 * 
		 * @param row
		 *            to remove.
		 */
		public void removeGenerator(int row) {
			if (this.canRemoveGenerator(row)) {
				this.generators.remove(row);
				this.fireTableRowsDeleted(row, row);
			}
		}
	
		/**
		 * @param row
		 *            to remove.
		 * @return {@code true} if the specified row can be removed.
		 */
		public boolean canRemoveGenerator(int i) {
			return this.generators.size() > 1 && i >= 0;
		}
	
		/**
		 * Add a new generator in the specified row.
		 * 
		 * @param row
		 *            to add a new generator in.
		 */
		public void addGenerator(int i) {
			if (this.canAddGenerator(i)) {
				if (i < 0 || i > this.generators.size()) {
					i = this.generators.size();
				} else {
					i++;
				}

				INetworkGenerator gen = new WaxmanGenerator();
				this.generators.add(i, gen);
				this.fireTableRowsInserted(i, i);
			}
		}
	
		/**
		 * @param row
		 *            to add a new generator in.
		 * @return {@code true} if a new generator can be added in the specified
		 *         row.
		 */
		public boolean canAddGenerator(int i) {
			return true;
		}
	
		/**
		 * @return the list of {@link INetworkGenerator}s configured by the
		 *         user.
		 */
		public INetworkGenerator[] getGenerators() {
			return this.generators.toArray(new INetworkGenerator[0]);
		}

		public void setGenerator(int row, INetworkGenerator generator) {
			this.generators.set(row, generator);
			this.fireTableRowsUpdated(row, row);
		}

		public INetworkGenerator getGenerator(int row) {
			return this.generators.get(row);
		}
	
	}

	/**
	 * This class implements a {@link ListSelectionListener} to enable or
	 * disable the row manipulation buttons according to the current selection.
	 * 
	 * @author Philip Huppert
	 */
	private class ButtonActivator implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int row = networkTable.getSelectedRow();
			moveDown.setEnabled(model.canMoveGeneratorDown(row));
			moveUp.setEnabled(model.canMoveGeneratorUp(row));
			remove.setEnabled(model.canRemoveGenerator(row));
			add.setEnabled(model.canAddGenerator(row));
		}
	}

	/**
	 * This class implements an {@link ActionListener} which receives any button
	 * presses from the user and calls the corresponding method in the
	 * {@link WizardModel}.
	 * 
	 * @author Philip Huppert
	 */
	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			int row = networkTable.getSelectedRow();
	
			// Move generator up.
			if (cmd.equals(UP_ACTN) && model.canMoveGeneratorUp(row)) {
				model.moveGeneratorUp(row);
				this.selectRow(row - 1); // Keep moved row selected.

			// Move generator down.
			} else if (cmd.equals(DOWN_ACTN) && model.canMoveGeneratorDown(row)) {
				model.moveGeneratorDown(row);
				this.selectRow(row + 1); // Keep moved row selected.
	
			// Add generator.
			} else if (cmd.equals(ADD_ACTN) && model.canAddGenerator(row)) {
				model.addGenerator(row);
				// Select added row.
				if (row > 0) {
					this.selectRow(row + 1);
				} else {
					// If the row was added to the end, select the last row.
					this.selectRow(networkTable.getRowCount() - 1);
				}
	
			// Remove generator.
			} else if (cmd.equals(REMOVE_ACTN) && model.canRemoveGenerator(row)) {
				model.removeGenerator(row);
				// Keep selection in same row.
				if (row < networkTable.getRowCount()) {
					this.selectRow(row);
				} else {
					// If the last row was deleted, move selection up.
					this.selectRow(row - 1);
				}
			}
		}

		private void selectRow(int row) {
			networkTable.setRowSelectionInterval(row, row);
		}
	}
	
	/**
	 * This class implements an {@link ActionListener} which receives any
	 * context menu selections by the user and calls the corresponding method in
	 * the {@link WizardModel}.
	 * 
	 * @author Philip Huppert
	 */
	private class ContextHandler implements ActionListener {
		private INetworkGenerator clipboard = null;
		private Component parent;

		public ContextHandler(Component parent) {
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			int row = networkTable.getSelectedRow();
			if (row < 0) {
				return;
			}

			if (cmd.equals(CHANGE_GENERATOR_ACTN)) {
				INetworkGenerator generator = model.getGenerator(row);
				GeneratorChange chgdiag = new GeneratorChange(generator);
				generator = chgdiag.getGenerator();
				model.setGenerator(row, generator);

			// Show generator configuration dialog.
			} else if (cmd.equals(CONFIGURE_GENERATOR_ACTN)) {
				INetworkGenerator generator = model.getGenerator(row);
				showConfigurationDialog(generator);
				model.setGenerator(row, generator); // cause row update

			// Copy configuration to clipboard.
			} else if (cmd.equals(COPY_CONFIGURATION_ACTN)) {
				this.clipboard = model.getGenerator(row).clone();
				paste.setEnabled(this.clipboard != null);

			// Paste the configuration.
			} else if (cmd.equals(PASTE_CONFIGURATION_ACTN) && this.clipboard != null) {
				INetworkGenerator current = model.getGenerator(row);

				// Warn user if the generator will change.
				if (!this.clipboard.getClass().equals(current.getClass())) {
					int res = JOptionPane.showConfirmDialog(
							this.parent,
							String.format(
									GENERATOR_MISMATCH_TEXT,
									current.getName(),
									this.clipboard.getName()),
							GENERATOR_MISMATCH_TITLE,
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);

					if (res != JOptionPane.YES_OPTION) {
						return;
					}
				}

				// Replace generator.
				model.setGenerator(row, this.clipboard.clone());
			}
		}
	}

	/**
	 * This class implements a {@link MouseAdapter} and registers any clicks on
	 * the network table. It is responsible for selecting the row that was
	 * clicked on and to show a context menu.
	 * 
	 * @author Philip Huppert
	 */
	private class NetworkTableMouseAdapter extends MouseAdapter {
		private JPopupMenu menu;

		public NetworkTableMouseAdapter(JPopupMenu menu) {
			this.menu = menu;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.mouseEvent(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			this.mouseEvent(e);
		}

		private void mouseEvent(MouseEvent e) {
			this.updateSelection(e);
			this.showPopup(e);
		}

		private void showPopup(MouseEvent e) {
			if (networkTable.getSelectedRow() >= 0
					&& e.isPopupTrigger()
					&& e.getComponent() instanceof JTable) {
				this.menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private void updateSelection(MouseEvent e) {
			int row = networkTable.rowAtPoint(e.getPoint());
			if (row < 0) {
				networkTable.clearSelection();
			} else {
				networkTable.setRowSelectionInterval(row, row);
			}
		}
	}

	/**
	 * This class implements a {@link AbstractButtonDialog} to allow the user to
	 * change the {@link INetworkGenerator} associated with a network in the
	 * table.
	 * 
	 * @author Philip Huppert
	 */
	private class GeneratorChange extends AbstractButtonDialog {
		private static final long serialVersionUID = 1L;

		private INetworkGenerator generator;
		private Map<String, String> classnames;
		private JComboBox<String> dropdown;
		private JPanel panel;
	
		public GeneratorChange(INetworkGenerator current) {
			super(
					GUI.getInstance(),
					CHGDIAG_TITLE,
					CHGDIAG_ACCEPT,
					CHGDIAG_DIMENSION);

			this.generator = current;

			// Map: generator name -> generator fully qualified name
			this.classnames = new HashMap<String, String>();
			for (String classname : GENERATORS) {
				INetworkGenerator generator = this.constructGenerator(classname);
				if (generator != null) {
					this.classnames.put(generator.getName(), classname);
				}
			}

			this.initGUI();
			//this.pack();
			this.setVisible(true);
		}

		/**
		 * @return the {@link INetworkGenerator} selected by the user.
		 */
		public INetworkGenerator getGenerator() {
			return this.generator;
		}

		private INetworkGenerator constructGenerator(String classname) {
			try {
				Class<?> clazz = Class.forName(classname);
				Constructor<?> constr = clazz.getConstructor();
				Object inst = constr.newInstance(new Object[0]);
				return (INetworkGenerator) inst;

			} catch (Exception e) {
				System.err.printf(
						"Failed to instantiate network generator: %s%n",
						classname);
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected JPanel createContent() {
			// Lazy initialization, because we need to run our constructor first.
			this.panel = new JPanel();
			return this.panel;
		}

		private void initGUI() {
			// Fill dropdown.
			String[] items = this.classnames.keySet().toArray(new String[0]);
			this.dropdown = new JComboBox<String>(items);
	
			// Select current generator in dropdown.
			for(int i = 0; i < items.length; i++) {
				if (this.generator.getName().equals(items[i])) {
					this.dropdown.setSelectedIndex(i);
				}
			}

			// Create label.
			JLabel dropdownLabel = new JLabel("Generator:");
			dropdownLabel.setLabelFor(this.dropdown);
			dropdownLabel.setHorizontalAlignment(JLabel.TRAILING);
	
			// Initialize layout and add components to GUI.
			GroupLayout layout = new GroupLayout(this.panel);
			this.panel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			layout.setHorizontalGroup(layout
					.createSequentialGroup()
					.addComponent(dropdownLabel)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(this.dropdown)
			);
			layout.setVerticalGroup(layout
					.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(dropdownLabel)
					.addComponent(this.dropdown)
			);
		}

		@Override
		protected void doAction() {
			String name = (String) this.dropdown.getSelectedItem();

			if (name.equals(this.generator.getName())) {
				// Keep old generator and its configuration.
				return;
			}

			String classname = this.classnames.get(name);
			this.generator = this.constructGenerator(classname);
		}
	}
}
