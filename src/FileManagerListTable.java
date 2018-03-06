import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

public class FileManagerListTable extends AbstractTableModel {
	private String columns[] = {"Icon","File"};
	private File files[];
	private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	
	/**
	 * Constructor
	 */
	public FileManagerListTable() {
        this(new File[0]);
	}
	/**
	 * Constructor(file)
	 * @param files
	 */
	public FileManagerListTable(File[] files) {
		this.files = files;
	}
	// getRow
	@Override
	public int getRowCount() {
		return files.length;
	}

	// getColumn
	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	// getColName
	@Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }
	
	
	// Display detail information about a file
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		  File file = files[rowIndex];
	        switch (columnIndex) {
	        
	            case 0:
	                return fileSystemView.getSystemIcon(file);
	            case 1: 
	                return fileSystemView.getSystemDisplayName(file);
	        
	            default:
	                System.err.println("Logic Error");
	        }
	        return "";
	}
	
	// setFiles 
	public void setFiles(File[] files) {
		this.files = files;
		fireTableDataChanged();
	}
	
	// getColumnClass return columns[columnIdx] class
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ImageIcon.class;
        }
        return String.class;
    }
	// getFile
    public File getFile(int row) {
        return files[row];
    }
	

}
