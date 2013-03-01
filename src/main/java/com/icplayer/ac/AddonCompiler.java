package com.icplayer.ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class AddonCompiler extends Task{

	private String destPath;
	private String sourcePath;
	private String addonId;

	// The method executing the task
	public void execute() throws BuildException {
		
		String addonPath = sourcePath + "/" + addonId + "/src/";
		String destAddonPath = destPath + "/" + addonId + ".xml";
		
		Document addonDoc = loadAddon(addonPath + "addon.xml");
		addElementFromFile(addonDoc, addonPath + "style.css", "css");
		addElementFromFile(addonDoc, addonPath + "view.html", "view");
		addElementFromFile(addonDoc, addonPath + "preview.html", "preview");
		addElementFromFile(addonDoc, addonPath + "presenter.js", "presenter");
		saveAddon(destAddonPath, addonDoc);
	}

	public void setSrc(String path) {
		this.sourcePath = path;
	}

	public void setDest(String path) {
		this.destPath = path;
	}

	public void setAddon(String id) {
		this.addonId = id;
	}
	

	/**
	 * Load addon.xml
	 * @param path
	 * @return 
	 */
	private Document loadAddon(String path){
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document xmlDoc = null;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			xmlDoc = dBuilder.parse(path);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return xmlDoc;
	}
	
	
	private void addElementFromFile(Document addonDoc, String filePath, String elementName){
		String content = loadFileContent(filePath);
		Element root = addonDoc.getDocumentElement();
		Element presenterElement = addonDoc.createElement(elementName);
		Text textNode = addonDoc.createTextNode(content);
		presenterElement.appendChild(textNode);
		root.appendChild(presenterElement);
	}

	
	private String loadFileContent(String filePath){
	    StringBuilder contents = new StringBuilder();
	    
	    try {
	    	BufferedReader input =  new BufferedReader(new FileReader(filePath));
	    	try {
	    		String line = null;
	    		while (( line = input.readLine()) != null){
	    			contents.append(line);
	    			contents.append(System.getProperty("line.separator"));
	    		}
	    	}
	    	finally {
	    		input.close();
	    	}
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    return contents.toString();
	}

	private void saveAddon(String path, Document xml){
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xml);
			StreamResult result = new StreamResult(new File(path));
			transformer.transform(source, result);		
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		AddonCompiler compiler = new AddonCompiler();
		compiler.setSrc("addons");
		compiler.setDest("output");
		compiler.setAddon("YouTube_Addon");
		compiler.execute();
	}
}
