
import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


//FileManager class
public class FileManager {

	
	private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	
	
	//get root function
	
	public DefaultTreeModel getRootChilds() {
		
		//create DefaultMutableTreeNode, DefaultTreeModel
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		File[] roots = fileSystemView.getRoots(); 
		for(File rootFolder : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootFolder);
			root.add(node);//Adds nodes to the root.
			addChilds(node);
		}
		
		return treeModel;
	}
	
	//addChilds function  
	public void addChilds(DefaultMutableTreeNode node) {
		File parent = new File(node.toString());
		File[] childs = fileSystemView.getFiles(parent, true);
		for(File child : childs) {
			if(child.isDirectory()){
				node.add(new DefaultMutableTreeNode(child));//Adds childe to the directory
			}
		}
	}
}