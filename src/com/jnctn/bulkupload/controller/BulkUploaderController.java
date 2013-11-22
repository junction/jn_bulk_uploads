package com.jnctn.bulkupload.controller;

import java.io.File;
import java.awt.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import com.jnctn.bulkupload.model.*;
import com.jnctn.bulkupload.model.processors.*;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * @author martin
 */
public class BulkUploaderController {

    Logger logger = Logger.getLogger(com.jnctn.bulkupload.controller.BulkUploaderController.class);

    BaseProcessor processor = null;

    public void runInSwingThread(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
	} else {
	    SwingUtilities.invokeLater(r);
	}
    }

   /**
    * Opens opens the given fileChooser.
    *
    * @param parent Parent component
    * @param fileChooser File chooser.
    */
    public void loadFileChooser (final Component parent, final JFileChooser fileChooser) {
        if (fileChooser == null) {
	    throw new IllegalArgumentException ("File chooser is null");
	}

	runInSwingThread(new Runnable() {
	    @Override
	    public void run() {
	        FileSystemView fsView = FileSystemView.getFileSystemView();
		File[] roots          = fsView.getRoots();
		File currentDir       = new File("/");

		if (roots != null && roots.length > 0) {
		  currentDir = roots[0];
		}

		fileChooser.setCurrentDirectory(currentDir);
		if (fileChooser.getChoosableFileFilters() != null && fileChooser.getChoosableFileFilters().length > 0) {
		    fileChooser.resetChoosableFileFilters();
		}

		fileChooser.setFileFilter(new FileFilter() {

		@Override
		public boolean accept(File f) {
		    return f.isDirectory() || f.getPath().toLowerCase().endsWith(".csv");
		}

		@Override
		public String getDescription() {
		    return "Comma-separated values (csv) files.";
		}
	    });

            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    fileChooser.setDialogTitle("Choose CSV file for upload.");
	    fileChooser.showOpenDialog(parent);

	    }
        });
    }

    /**
     * Delegates to the webservice controller for parsing and uploading the data.
     *
     * @param progress
     * @param user the admin user "bob@example.onsip.com"
     * @param password the admin password
     * @param domain the user's domain
     * @param csvFile csv file to parse
     *
     * @throws Exception
     */
    public void startUpload(ProgressController progress, String user,
        char[] password, String domain, File csvFile) throws Exception {

        CsvParser parser = new CsvParser(csvFile);

        processor = parser.getProcessor(progress, user, new String(password), domain);
        processor.upload();
    }

    @Override
    public String toString() {
	return processor.toString();
    }

    /**
     * Returns true/false depending on if the input is valid.
     *
     * @param errors List of errors is stored here. It is cleared before the input is processed.
     * @param user the admin user "bob@example.onsip.com"
     * @param password the admin password
     * @param domain the domain "example.onsip.com"
     * @param csvFile CSV file to process
     *
     * @return true if no errors
    */
    public boolean validateInput(List<String> errors,  String user, char[] password,
        String domain, File csvFile) {
	errors.clear();
	try {
	    if (StringUtils.isEmpty(user)) {
		errors.add("Username is blank.");
	    }
	    if (StringUtils.isEmpty(domain)) {
		errors.add("Domain is blank.");
	    }
	    if (password == null || password.length == 0 || StringUtils.isEmpty(new String(password))) {
		errors.add("Password is blank.");
	    }
	    if (csvFile == null || !csvFile.canRead()) {
		errors.add("CSV file not readable.");
	    }
	} catch (Exception e) {
	    errors.add("Unknown error: " + e.toString());
	}
	return errors.isEmpty();
    }
}
