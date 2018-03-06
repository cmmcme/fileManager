
//import library
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.List;
import java.util.spi.CurrencyNameProvider;
import java.io.*;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.events.MouseEvent;

import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class FileManagerGUI extends JFrame {

	// variable
	private JPanel entirePane;
	private static JMenuBar menuBar;
	private static JMenu mnFile;
	private static JMenu mnEdit;
	private static JMenu mnView;
	private static JMenuItem mntmOpenFolder;
	private static JMenuItem mntmOpenFile;
	private static JMenuItem mntmNewFolder;
	private static JMenuItem mntmFile;
	private static JMenuItem mntmDelete;
	private static JMenuItem mntmExit;
	private static JMenuItem mntmRename;
	private static JMenuItem mntmCopy;
	private static JMenuItem mntmCut;
	private static JMenuItem mntmPaste;
	private static JMenuItem mntmDetail;
	private static JMenuItem mntmList;
	private static JMenuItem mntmIcons;
	private static JMenuItem mntmTitles;
	private static final String VIEW_TYPE_LIST = "viewTypeList";
	private static JMenu mnNewMenu_4;
	private final JFileChooser chooser;
	private JSplitPane splitPane;
	private JScrollPane treePane;
	private JPanel fileViewPane;
	private JTree tree;
	private JScrollPane fileViewScrollPane;
	private JTable fileViewTable;
	private JTable fileViewTable2;

	private JLabel lblList;
	private JLabel lblListShortcut;
	private JLabel lblListDescription;
	private JPanel fileInfoPane;
	private JPanel fileLeftInfoPane;
	private JPanel fileRightInfoPane;
	private JLabel lblName;
	private JLabel lblPath;
	private JLabel lblLeftLastModified;
	private JTextField txtPath;
	private JTextField txtName;
	private JLabel lblRightLastModified;
	private JPanel panel_4;
	private JLabel lblFileSize;
	private JLabel lblRealFileSize;
	private JProgressBar progressBar;
	private JMenuItem mntmHelp;
	private Desktop desktop; // Used OpenFile
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private ListSelectionListener listSelectionListener;
	private File currentFile;
	private FileManagerTableModel fileManagerTableModel;
	private FileManagerListTable fileManagerListTable;
	private FileManager fileManager = new FileManager();
	private boolean setColSize = false;
	private DefaultMutableTreeNode selectedNode;
	private File orgFile;
	private File newFile;
	private JPanel panel;
	private JLabel lblLocation;
	private JTextField txtLocation;
	private File selectedTableFile;
	private boolean copyFlag = false;
	private boolean cutFlag = false;
	private JFrame frame = this;
	private boolean detailFlag = true;
	private boolean listFlag = false;

	// private
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// create FileManagerGUI type frame
					FileManagerGUI frame = new FileManagerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FileManagerGUI() {

		// create pane
		chooser = new JFileChooser();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		entirePane = new JPanel();
		entirePane.setBorder(new EmptyBorder(5, 5, 5, 5));
		entirePane.setLayout(new BorderLayout(0, 0));
		setContentPane(entirePane);
		splitPane = new JSplitPane();
		entirePane.add(splitPane, BorderLayout.CENTER);

		// create DefaultTreeModel
		DefaultTreeModel treeModel = fileManager.getRootChilds();
		tree = new JTree(treeModel);
		tree.setRootVisible(false);

		// Loads a list of existing files in Tree format
		tree.setCellRenderer(new FileManagerTreeCellRenderer());
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
				selectedNode = node;
				// tree update
				if (detailFlag == true)
					addChildeNode(selectedNode);

				if (listFlag == true)
					addChildeNodeList(selectedNode);

				txtLocation.setText(((File) selectedNode.getUserObject()).getAbsolutePath());

			}
		});

		treePane = new JScrollPane(tree);

		// set treePane's Size
		Dimension preferredSize = treePane.getPreferredSize();
		Dimension widePreferred = new Dimension(150, (int) preferredSize.getHeight());
		treePane.setPreferredSize(widePreferred);
		splitPane.setLeftComponent(treePane);

		fileViewPane = new JPanel();
		splitPane.setRightComponent(fileViewPane);
		fileViewPane.setLayout(new BorderLayout(0, 0));

		fileViewTable = new JTable();
		FileManagerTableModel a = new FileManagerTableModel();
		FileManagerListTable b = new FileManagerListTable();

		// Table list Selection Listener
		listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {

				int row = fileViewTable.getSelectionModel().getLeadSelectionIndex();
				if (detailFlag == true)
					selectedTableFile = ((FileManagerTableModel) fileViewTable.getModel()).getFile(row);

				if (listFlag == true)
					selectedTableFile = ((FileManagerListTable) fileViewTable.getModel()).getFile(row);

				setFileDetails(selectedTableFile);
				fileViewTable.getColumnModel().getColumn(1).setCellEditor(new FileChooserCellEditor());

			}
		};

		// fileViewTable add addListSelectionListener
		fileViewTable.getSelectionModel().addListSelectionListener(listSelectionListener);

		/*
		 * set Layout
		 */
		fileViewScrollPane = new JScrollPane(fileViewTable);
		fileViewPane.add(fileViewScrollPane);

		fileInfoPane = new JPanel();
		fileViewPane.add(fileInfoPane, BorderLayout.SOUTH);
		fileInfoPane.setLayout(new BorderLayout(0, 0));

		fileLeftInfoPane = new JPanel();
		fileLeftInfoPane.setPreferredSize(new Dimension(100, 100));
		fileLeftInfoPane.setMinimumSize(new Dimension(20, 20));
		fileInfoPane.add(fileLeftInfoPane, BorderLayout.WEST);
		fileLeftInfoPane.setLayout(new GridLayout(0, 1, 0, 0));

		lblName = new JLabel("Name : ");
		lblName.setHorizontalAlignment(SwingConstants.TRAILING);
		fileLeftInfoPane.add(lblName);

		lblPath = new JLabel("Path : ");
		lblPath.setHorizontalAlignment(SwingConstants.TRAILING);
		fileLeftInfoPane.add(lblPath);

		lblLeftLastModified = new JLabel("Last Modified : ");
		lblLeftLastModified.setHorizontalAlignment(SwingConstants.TRAILING);
		fileLeftInfoPane.add(lblLeftLastModified);

		fileRightInfoPane = new JPanel();
		fileInfoPane.add(fileRightInfoPane, BorderLayout.CENTER);
		fileRightInfoPane.setLayout(new GridLayout(0, 1, 0, 0));

		txtName = new JTextField();
		fileRightInfoPane.add(txtName);
		txtName.setColumns(10);

		txtPath = new JTextField();
		fileRightInfoPane.add(txtPath);
		txtPath.setColumns(10);

		lblRightLastModified = new JLabel("");
		lblRightLastModified.setHorizontalAlignment(SwingConstants.LEFT);
		fileRightInfoPane.add(lblRightLastModified);

		panel_4 = new JPanel();
		fileInfoPane.add(panel_4, BorderLayout.SOUTH);
		panel_4.setLayout(new GridLayout(0, 3, 0, 0));

		lblFileSize = new JLabel("File Size");
		panel_4.add(lblFileSize);

		lblRealFileSize = new JLabel("");
		panel_4.add(lblRealFileSize);

		progressBar = new JProgressBar();
		panel_4.add(progressBar);

		desktop = Desktop.getDesktop();
		fileSystemView = FileSystemView.getFileSystemView();

		fileViewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileViewTable.setAutoCreateRowSorter(true);
		fileViewTable.setShowVerticalLines(false);

		panel = new JPanel();
		entirePane.add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 247, 61, 0, 130, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 26, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		lblLocation = new JLabel("Location : ");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.anchor = GridBagConstraints.WEST;
		gbc_lblLocation.insets = new Insets(0, 0, 0, 5);
		gbc_lblLocation.gridx = 0;
		gbc_lblLocation.gridy = 0;
		panel.add(lblLocation, gbc_lblLocation);

		txtLocation = new JTextField();
		txtLocation.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_txtLocation = new GridBagConstraints();
		gbc_txtLocation.fill = GridBagConstraints.BOTH;
		gbc_txtLocation.gridwidth = 12;
		gbc_txtLocation.anchor = GridBagConstraints.WEST;
		gbc_txtLocation.gridx = 1;
		gbc_txtLocation.gridy = 0;
		panel.add(txtLocation, gbc_txtLocation);
		txtLocation.setColumns(10);
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		////////////////////////////////////////////////////////////////////////
		JFileChooser fileChooser = new JFileChooser();
		// If you click on a file, the file opens with the default application.
		fileChooser.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				if (e.getClickCount() == 2)// if user doublie click the file,
				{
					try {
						// oepn file at desktop
						java.awt.Desktop.getDesktop().open(new File(fileChooser.getSelectedFile().getAbsolutePath()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		// Open Folder function
		mntmOpenFolder = new JMenuItem("Open Folder");

		// add shortcut
		mntmOpenFolder.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));

		// Open Folder function actionlistener
		mntmOpenFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				// user can open folder, but DIRECTORIES_ONLY.
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

				int result = fileChooser.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					// save selecteFolder
					File selecteFolder = fileChooser.getCurrentDirectory();
					try {
						// open selecteFolder
						desktop.open(selecteFolder);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		mnFile.add(mntmOpenFolder);

		/*
		 * Open file function
		 * 
		 */

		mntmOpenFile = new JMenuItem("Open File");

		// add shortcut
		mntmOpenFile.setAccelerator(KeyStroke.getKeyStroke('O', (Event.CTRL_MASK | Event.SHIFT_MASK))); // Ctrl
																										// +
																										// N

		// add Open file action listener
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				int result = fileChooser.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					try {
						desktop.open(selectedFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		mnFile.add(mntmOpenFile);

		// tree update

		/*
		 * New Folder function
		 */
		mntmNewFolder = new JMenuItem("New Folder");

		// add shortcut
		mntmNewFolder.setAccelerator(KeyStroke.getKeyStroke('N', Event.CTRL_MASK)); // Ctrl
																					// +
																					// N

		/*
		 * New Folder add ActionListener
		 */
		mntmNewFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = (File) selectedNode.getUserObject();
				Path p = Paths.get(file.getAbsolutePath(), "new Folder");

				File newfile = new File(p.toString());
				if (newfile.mkdir()) {

					// tree update
					treeModel.nodeStructureChanged(selectedNode);
					selectedNode.add(new DefaultMutableTreeNode(newfile));
					treeModel.reload(selectedNode);

				} else {

					// if created folder's name is alreadt exists,
					txtName.setText("This folder already exists.");
				}
				// tree update
				if (detailFlag == true)
					addChildeNode(selectedNode);

				if (listFlag == true)
					addChildeNodeList(selectedNode);

				treeModel.nodeStructureChanged(selectedNode);
				treeModel.reload(selectedNode);

			}
		});
		mnFile.add(mntmNewFolder);

		/*
		 * Create txt File
		 */
		mntmFile = new JMenuItem("New File");
		mntmFile.setAccelerator(KeyStroke.getKeyStroke('N', (Event.CTRL_MASK | Event.SHIFT_MASK))); // Ctrl
																									// +
																									// N

		mntmFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = (File) selectedNode.getUserObject();
				Path p = Paths.get(file.getAbsolutePath(), "/New File.txt");
				File newfile = new File(p.toString());
				try {
					if (newfile.createNewFile()) {
						// if create new file successed,
						System.out.println("success");
					} else {
						// if created file's name is alreadt exists,
						txtName.setText("This file already exists.");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// tree update
				if (detailFlag == true)
					addChildeNode(selectedNode);

				if (listFlag == true)
					addChildeNodeList(selectedNode);

				treeModel.nodeStructureChanged(selectedNode);
				selectedNode.add(new DefaultMutableTreeNode(file));
				treeModel.reload(selectedNode);

			}

		});
		mnFile.add(mntmFile);

		// Delete File or Folder
		mntmDelete = new JMenuItem("Delete");

		// add shortcut
		mntmDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)); // Ctrl
																					// +
																					// N

		// delete addActionListener
		mntmDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JDialog.setDefaultLookAndFeelDecorated(true);
				try {
					// create dialog
					int response = JOptionPane.showConfirmDialog(null,
							"Do you really want to delete '" + selectedTableFile.getName() + "'", "Confirm",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.NO_OPTION) {
						System.out.println("No button clicked");
					} else if (response == JOptionPane.YES_OPTION) {
						if (selectedTableFile.isFile())// if yes is selected,
														// and selectedTable
														// file is file,
						{
							selectedTableFile.delete();// delete the file

							// tree update
							if (detailFlag == true)
								addChildeNode(selectedNode);

							if (listFlag == true)
								addChildeNodeList(selectedNode);
							treeModel.nodeChanged(selectedNode);
							treeModel.nodeStructureChanged(selectedNode);
							treeModel.reload(selectedNode);
						} else if (selectedTableFile.isDirectory())// if yes is
																	// selected,
																	// and
																	// selectedTable
																	// file is
																	// folder,
							try {
								deleteFolder(selectedTableFile);// delete the
																// folder
								// selectedNode.remove(new
								// DefaultMutableTreeNode());

								// tree update
								if (detailFlag == true)
									addChildeNode(selectedNode);

								if (listFlag == true)
									addChildeNodeList(selectedNode);

								treeModel.nodeChanged(selectedNode);
								treeModel.nodeStructureChanged(selectedNode);
								treeModel.reload(selectedNode);

							} catch (Exception deleteEx) {
								deleteEx.printStackTrace();
							}
						System.out.println("Yes button clicked");
					} else if (response == JOptionPane.CLOSED_OPTION) {// if
																		// user
																		// selecte
																		// close
																		// option,
						System.out.println("JOptionPane closed");
					}
				} catch (Exception delE) {
					JOptionPane.showMessageDialog(panel,
							"Can't Delete! \n You must click on the folder or file in FileSystemView that you want to delete.",
							"Warning", JOptionPane.ERROR_MESSAGE);
				}

				// tree update
				if (detailFlag == true)
					addChildeNode(selectedNode);

				if (listFlag == true)
					addChildeNodeList(selectedNode);

				treeModel.nodeChanged(selectedNode);
				treeModel.nodeStructureChanged(selectedNode);
				treeModel.reload(selectedNode);
			}
		});
		mnFile.add(mntmDelete);

		/*
		 * Exit function
		 */
		mntmExit = new JMenuItem("Exit");

		// add shorrcut + extra
		mntmExit.setAccelerator(KeyStroke.getKeyStroke('Q', Event.CTRL_MASK)); // Ctrl
																				// +
																				// Q
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);// filemanager GUI is close.
			}
		});
		mnFile.add(mntmExit);

		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		/*
		 * rename function
		 */
		mntmRename = new JMenuItem("Rename");

		// add shortcut
		mntmRename.setAccelerator(KeyStroke.getKeyStroke('R', Event.CTRL_MASK)); // Ctrl
																					// +
																					// N

		// add rename ActionListener
		mntmRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// create dialog
				String renameTo = JOptionPane.showInputDialog("Please enter a new name");
				if (renameTo != null) {

					// if user write something,
					boolean renamed = selectedTableFile.renameTo(new File(selectedTableFile.getParentFile(), renameTo));

					// rename, and tree update
					// tree update
					if (detailFlag == true)
						addChildeNode(selectedNode);

					if (listFlag == true)
						addChildeNodeList(selectedNode);

					treeModel.nodeChanged(selectedNode);
					treeModel.nodeStructureChanged(selectedNode);

					treeModel.reload(selectedNode);

				}
				// If cancel button is pressed
				else
					JOptionPane.getRootFrame().dispose();

			}
		});

		mnEdit.add(mntmRename);

		/*
		 * copy function
		 */
		mntmCopy = new JMenuItem("Copy");

		// add shortcut
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke('C', Event.CTRL_MASK)); // Ctrl
																				// +
																				// N

		// add copy ActionListener
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				orgFile = new File(selectedTableFile.getAbsolutePath());
				copyFlag = true;
				cutFlag = false;

			}
		});
		mnEdit.add(mntmCopy);

		/*
		 * cut function
		 */
		mntmCut = new JMenuItem("Cut");

		// add shortcut
		mntmCut.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK)); // Ctrl
																				// +
																				// N

		// add cut ActionListener
		mntmCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				orgFile = new File(selectedTableFile.getAbsolutePath());
				cutFlag = true;
				copyFlag = false;

			}
		});
		mnEdit.add(mntmCut);

		/*
		 * paste function
		 */

		mntmPaste = new JMenuItem("Paste");

		// add shortcut
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke('V', Event.CTRL_MASK)); // Ctrl
																				// +
																				// N

		// add paste ActionListener
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
					public Void doInBackground() {
						File file = (File) selectedNode.getUserObject();
						newFile = new File(file.getAbsolutePath() + "/" + orgFile.getName());

						try {
							if ((copyFlag == true && cutFlag == true) || ((copyFlag == false) && cutFlag == false))
								JOptionPane.showMessageDialog(frame, "Paste is only available after cut/copy");

							if (copyFlag == true) {// copyflag is true, copy
													// func run
								copyFolder(orgFile, newFile);
								copyFlag = false;
							}
							if (cutFlag == true && !orgFile.renameTo(newFile)) {// cutflag
																				// is
																				// true,
																				// cut
																				// func
																				// run
								cutFile(orgFile, newFile);
							}

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						// tree update
						// tree update
						if (detailFlag == true)
							addChildeNode(selectedNode);

						if (listFlag == true)
							addChildeNodeList(selectedNode);

						treeModel.nodeChanged(selectedNode);
						treeModel.nodeStructureChanged(selectedNode);
						treeModel.reload(selectedNode);

						return null;
					}
				};
				// use thread.
				worker.execute();
			}
		});
		mnEdit.add(mntmPaste);

		mnView = new JMenu("View");
		menuBar.add(mnView);

		// default view
		mntmDetail = new JMenuItem("Detail");
		mntmDetail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				detailFlag = true;
				listFlag = true;
				addChildeNode(selectedNode);
			}
		});
		mnView.add(mntmDetail);

		// unimplement.
		mntmList = new JMenuItem("List");
		mntmList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listFlag = true;
				detailFlag = false;
				addChildeNodeList(selectedNode);
			}
		});
		mnView.add(mntmList);

		mntmList.setAccelerator(KeyStroke.getKeyStroke('L', Event.CTRL_MASK)); // Ctrl
																				// +
																				// N

		mntmIcons = new JMenuItem("Icons");
		mntmIcons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		mnView.add(mntmIcons);

		mntmTitles = new JMenuItem("Titles");
		mntmTitles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Action details = fileChooser.getActionMap().get("viewTypeDetails");
				details.actionPerformed(null);

				// Find the JTable on the file chooser panel and manually do the
				// sort

				// JTable table = table.getDescendantsOfType(JTable.class,
				// fileChooser).get(0);
				// table.getRowSorter().toggleSortOrder(3);

			}
		});
		mnView.add(mntmTitles);

		////////////////////////////////////////

		/*
		 * help function
		 */
		mntmHelp = new JMenuItem("Help(CTRL+I)");

		// add shortcut
		mntmHelp.setAccelerator(KeyStroke.getKeyStroke('I', Event.CTRL_MASK)); // Ctrl
																				// +
																				// N
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// create dialog
				HelpDialog helpDialog = new HelpDialog();
				System.gc();
				helpDialog.setVisible(true);
			}
		});
		menuBar.add(mntmHelp);

	}

	// addChildeNode - DetailModel func is used tree update.
	private void addChildeNode(DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override

			// thread works InBackground.
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {// file is directory,
					File[] files = fileSystemView.getFiles(file, true);
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);// call process function with
												// child parameter
							}
						}
					}
					setTableData(files);
				}
				return null;
			}

			@Override
			// add childe at node
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			// worker is done, done function run.
			protected void done() {
				// progressBar.setIndeterminate(false);
				// progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();// use thread.
	}

	// addChildeNode - ListModel func is used tree update.
	private void addChildeNodeList(DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override

			// thread works InBackground.
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {// file is directory,
					File[] files = fileSystemView.getFiles(file, true);
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);// call process function with
												// child parameter
							}
						}
					}
					setTableListData(files);
				}
				return null;
			}

			@Override
			// add childe at node
			protected void process(List<File> chunks) {
				for (File child : chunks) {
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			@Override
			// worker is done, done function run.
			protected void done() {
				// progressBar.setIndeterminate(false);
				// progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();// use thread.
	}

	/**
	 * setListTableModel
	 * 
	 * @param files
	 */
	private void setTableListData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileManagerListTable == null) {
					fileManagerListTable = new FileManagerListTable();// if
																		// fileManagerListTable
																		// is
																		// null,
																		// create
																		// fileManagerTableModel.
					fileViewTable.setModel(fileManagerListTable);// and set
																	// fileManagerListTable
																	// in
																	// fileViewTable

				}

				// removeListSelectionListener
				fileViewTable.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileManagerListTable.setFiles(files);
				fileViewTable.getSelectionModel().addListSelectionListener(listSelectionListener);

				if (!setColSize) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// size adjustment to better account for icons
					fileViewTable.setRowHeight(icon.getIconHeight());

					setColumnWidth(0, 60);
					fileViewTable.getColumnModel().getColumn(1).setMaxWidth(20);
					fileViewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					fileViewTable.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {// if user
																// doublie
																// click,
							if (((java.awt.event.MouseEvent) e).getClickCount() == 2) {
								int selectedRow = fileViewTable.getSelectedRow();
								try {
									// The file selected by the user will be
									// opened as the default application.
									Desktop.getDesktop()
											.open(new File((String) fileViewTable.getValueAt(selectedRow, 2)));
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					});
				}

				setColSize = true;

			}
		});
	}

	/*
	 * setTableData. use thread.
	 */
	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (fileManagerTableModel == null) {
					fileManagerTableModel = new FileManagerTableModel();// if
																		// fileManagerTableModel
																		// is
																		// null,
																		// create
																		// fileManagerTableModel.
					fileViewTable.setModel(fileManagerTableModel);// and set
																	// fileManagerTableModel
																	// in
																	// fileViewTable

				}

				// removeListSelectionListener
				fileViewTable.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileManagerTableModel.setFiles(files);
				fileViewTable.getSelectionModel().addListSelectionListener(listSelectionListener);

				if (!setColSize) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// size adjustment to better account for icons
					fileViewTable.setRowHeight(icon.getIconHeight() + 5);

					setColumnWidth(0, 60);
					fileViewTable.getColumnModel().getColumn(3).setMaxWidth(120);
					setColumnWidth(3, -1);
					setColumnWidth(4, -1);
					fileViewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					fileViewTable.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {// if user
																// doublie
																// click,
							if (((java.awt.event.MouseEvent) e).getClickCount() == 2) {
								int selectedRow = fileViewTable.getSelectedRow();
								try {
									// The file selected by the user will be
									// opened as the default application.
									Desktop.getDesktop()
											.open(new File((String) fileViewTable.getValueAt(selectedRow, 2)));
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					});
				}

				setColSize = true;

			}
		});
	}

	// setColumnWidth function
	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = fileViewTable.getColumnModel().getColumn(column);
		if (width < 0) {
			// use the preferred width of the header..
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();

			width = (int) preferred.getWidth() + 14;
		}

		// table update.
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);

	}

	// detail is defualt
	private void setFileDetails(File file) {

		txtName.setText(fileSystemView.getSystemDisplayName(file));
		txtPath.setText(file.getPath());
		lblRightLastModified.setText(new Date(file.lastModified()).toString());
		lblRealFileSize.setText(file.length() + "");
		txtLocation.setText(((File) selectedNode.getUserObject()).getAbsolutePath());
	}

	// updateGUI function
	public void updateGUI() {
		revalidate();
		repaint();
	}

	// delete Folder function.
	public boolean deleteFolder(File targetFolder) {

		// if delete function is clicked, Everything in the folder should
		// disappear.
		File[] childFile = targetFolder.listFiles();
		boolean confirm = false;
		int size = childFile.length;

		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (childFile[i].isFile()) {
					confirm = childFile[i].delete();
					System.out.println(childFile[i] + ":" + confirm + " 삭제");
				} else {
					deleteFolder(childFile[i]);
				}
			}
		}

		targetFolder.delete();

		System.out.println(targetFolder + " 폴더삭제됨삭제");
		System.out.println(targetFolder + ":" + confirm + " 삭제");

		return (!targetFolder.exists());

	}// deleteFolder

	// copy folder function. if user click the copy menu, copy all of thing.
	public static void copyFolder(File source, File destination) {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}
			String files[] = source.list();

			for (String file : files) {
				File srcFile = new File(source, file);
				File destFile = new File(destination, file);

				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = null;
			OutputStream out = null;

			try {
				// use InputStream, OutputStream
				in = new FileInputStream(source);
				out = new FileOutputStream(destination);

				byte[] buffer = new byte[1024];

				int length;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/*
	 * cut file function. if user cut file, and paste. original file is deleted
	 * and create new same file
	 */

	public static void cutFile(File source, File destination) {
		byte[] buf = new byte[1024];
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {

			// use FileInputStream, FileOutputStream. and create new same file
			fin = new FileInputStream(source);
			fout = new FileOutputStream(destination);

			int read = 0;
			while ((read = fin.read(buf, 0, buf.length)) != -1)
				fout.write(buf, 0, read);

			fin.close();
			fout.close();

			source.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// AbstractTreeModelListener. this function is listen tree's change.
	public abstract class AbstractTreeModelListener implements TreeModelListener {
		private Object root;

		protected AbstractTreeModelListener(TreeModel data) {
			root = data.getRoot();
		}

		public final void treeNodesChanged(TreeModelEvent e) {
			if (e.getChildren() == null)
				rootChanged(e);
			else
				childrenChanged(e);
		}

		public final void treeNodesInserted(TreeModelEvent e) {
			childrenAdded(e);
		}

		public final void treeNodesRemoved(TreeModelEvent e) {
			childrenRemoved(e);
		}

		public final void treeStructureChanged(TreeModelEvent e) {
			TreePath path = e.getTreePath();

			if (path == null)
				newRoot(null);
			else if (path.getPathCount() == 1) {
				Object newRoot = path.getLastPathComponent();

				/* newRoot cannot be null (is part of TreePath) */

				if (!newRoot.equals(root)) {
					root = newRoot;
					newRoot(newRoot);
				} else
					structureChanged(path);
			} else
				structureChanged(path);
		}

		/**
		 * Invoked when the root has changed (i.e. properties of the root node).
		 */
		protected abstract void rootChanged(TreeModelEvent e);

		/** Invoked when children of the event's tree path have changed. */
		protected abstract void childrenChanged(TreeModelEvent e);

		/** Invoked when children were added to the event's tree path. */
		protected abstract void childrenAdded(TreeModelEvent e);

		/** Invoked when children were removed from the event's tree path. */
		protected abstract void childrenRemoved(TreeModelEvent e);

		/** Invoked when there is a new root (i.e. a new object). */
		protected abstract void newRoot(Object newRoot);

		/** Invoked when the structure below the event's tree path changed. */
		protected abstract void structureChanged(TreePath parentPath);
	}

	public DefaultMutableTreeNode addObject(Object child) {
		DefaultMutableTreeNode parentNode = null;
		TreePath parentPath = tree.getSelectionPath();

		return addObject(parentNode);
	}

}
