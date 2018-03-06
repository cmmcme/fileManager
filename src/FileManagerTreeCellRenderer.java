
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

// TreeCellRendeerder
public class FileManagerTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	
	private JLabel label;

	/**
	 * Constructor init label
	 */
    FileManagerTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
    }
    
    /**
     * getTreeCellRendererComponent
     * return file's label
     * setIcon & settext
     * selected At tree => highlight Back,ForeGround
     */
    @Override
    public Component getTreeCellRendererComponent(
    		JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        File file = (File)node.getUserObject();
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }

}
