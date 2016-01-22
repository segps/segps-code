/*
 * Created by ASEG at Concordia University.
 * http://aseg.cs.concordia.ca
 * http://aseg.cs.concordia.ca/segps
 * Please see the LICENSE file for details.
 */
package ca.concordia.cs.aseg.segps.ontologies.publisher.raw_data_parser;

import java.util.HashMap;
import java.util.Map;

public class Entry {
	private String affectedProduct;
	private String operatingSystem;
	private String application;
	private String hardware;

	private String cveID;

	private String publishedDatetime;
	private String lastModifiedDatetime;

	private double score;
	private String accessVector;
	private String accessComplexity;
	private String authentication;
	private String confidentialityImpact;
	private String integrityImpact;
	private String availabilityImpact;

	private String cweID;

	private String referenceType;
	private String referenceSource;
	private String refernceURL;

	private String summary;
	
	private Map<String, String> attributes = new HashMap<>();
	
	public void setAffectedProduct(String affectedProduct){
		
		String temp = affectedProduct.replaceAll("\\s+",""); // removes all whitespace and non visible characters such as tab, \n . 
		String[] split = temp.split(":"); // e.g.  cpe:/a:vendor_name:product_name:version.
		
		if(split.length >= 5){
			this.affectedProduct = split[2] + ":" + split[3] + ":" + split[4]; // e.g. vendor_name:product_name:version
			// Classify Affected products into OS or APP
			if (split[1].equals("/o")) {
				this.operatingSystem = this.affectedProduct;
			}else if (split[1].equals("/a")) {
				this.application = this.affectedProduct;
			}
		}
	}
	public String getAffectedProduct(){
		return this.affectedProduct;
	}
	public String getOperatinSystem(){
		return this.operatingSystem;
	}
	public String getApplicaiotn(){
		return this.application;
	}
	
	public void setcveID(String cveID){
		this.cveID = cveID;
	}
	public String getcveID(){
		return this.cveID;
	}
	
	public void setPublishedDatetime(String publishedDatetime){
		this.publishedDatetime = publishedDatetime;
	}
	public String getPublishedDatetime(){
		return this.publishedDatetime;
	}
	
	public void setlastModifiedDatetime(String lastModifiedDatetime){
		this.lastModifiedDatetime = lastModifiedDatetime;
	}
	public String getLastModifiedDatetime(){
		return this.lastModifiedDatetime;
	}
	
	public void setScore(double score){
		this.score = score;
	}
	public double getScore(){
		return this.score;
	}
	
	public void setAccessVector(String accessVector){
		this.accessVector = accessVector;
	}
	public String getAccessVector(){
		return this.accessVector;
	}
	
	public void setAccessComplexity(String accessComplexity){
		this.accessComplexity = accessComplexity;
	}
	public String getAccessComplexity(){
		return this.accessComplexity;
	}
	
	public void setAuthentication(String authentication){
		this.authentication = authentication;
	}
	public String getAuthentication(){
		return this.authentication;
	}
	
	public void setConfidentialityImpact(String confidentialityImpact){
		this.confidentialityImpact = confidentialityImpact;
	}
	public String getConfidentialityImpact(){
		return this.confidentialityImpact;
	}
	
	public void setIntegrityImpact(String integrityImpact){
		this.integrityImpact = integrityImpact;
	}
	public String getIntegrityImpact(){
		return this.integrityImpact;
	}
	
	public void setAvailabilityImpact(String availabilityImpact){
		this.availabilityImpact = availabilityImpact;
	}
	public String getAvailabilityImpact(){
		return this.availabilityImpact;
	}
	
	public void setcweID(String cweID){
		this.cweID = cweID;
	}
	public String getcweID(){
		return this.cweID;
	}
	
	public void setReferenceType(String referenceType){
		this.referenceType = referenceType;
	}
	public String getreferenceType(){
		return this.referenceType;
	}
	
	public void setReferenceSource(String referenceSource){
		this.referenceSource = referenceSource;
	}
	public String getReferenceSource(){
		return this.referenceSource;
	}
	
	public void setRefernceURL(String refernceURL){
		this.refernceURL = refernceURL;
	}
	public String getRefernceURL(){
		return this.refernceURL;
	}
	
	public void setSummary(String summary){
		this.summary = summary;
	}
}

