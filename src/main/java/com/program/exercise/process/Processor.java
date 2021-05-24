package com.program.exercise.process;

import com.program.exercise.constants.Constants;

/**
 * @author Jayashree
 *
 */
public class Processor implements Runnable{
	 private String uri;
	 private String text;
	 private String fileName;

	    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

		public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

		public String getUri() {
	        return uri;
	    }

	    public void setUri(String uri) {
	        this.uri = uri;
	    }

		@Override
		public void run() {			
			Extractor bwc = new Extractor();
	        bwc.getPageLinks(getUri(),Constants.DEPTH);
	        bwc.getArticles(getText());	 
	        bwc.writeToFile(getText());
			
		}
}
