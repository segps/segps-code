/*
 * Created by ASEG at Concordia University.
 * http://aseg.cs.concordia.ca
 * http://aseg.cs.concordia.ca/segps
 * Please see the LICENSE file for details.
 */

package ca.concordia.cs.aseg.segps.ontologies.urigenerator.domain_specific.abox;

import ca.concordia.cs.aseg.segps.ontologies.urigenerator.namespace.NamespaceFactory;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.registry.NamespaceRegistry;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.registry.OntologyRegistry;

public class SecurityDBsABox {
	
	public static String VulnerabilityID(String VulnerabilityID) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ VulnerabilityID;
		return uri;
	}
	
	public static String VulnerabilityURI(String VulnerabilityID) {
		String uri = "https://web.nvd.nist.gov/view/vuln/detail?vulnId="+VulnerabilityID;
		return uri;
	}
	
	public static String WeaknessID(String WeaknessID) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ WeaknessID;
		return uri;
	}
	
	public static String WeaknessURI(String WeaknessID) {
		String[] splits = WeaknessID.split("-");
//		System.out.println(splits[1]);
		String uri = "http://cwe.mitre.org/data/definitions/"+splits[1]+".html";
		return uri;
	}
	
	public static String AffectedProduct(String AffectedProduct) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ AffectedProduct;
		return uri;
	}
	public static String AffectedRelease(String AffectedRelease) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ AffectedRelease;
		return uri;
	}
	public static String OtherReleases(String OtherReleases){
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ OtherReleases;
		return uri;
	}
	public static String Application(String Application) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ Application;
		return uri;
	}
	public static String OperatingSystem(String OperatingSystem) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ OperatingSystem;
		return uri;
	}
	
	public static String Score(String cve, String score) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ cve+":socre:"+score;
		return uri;
	}
	
	public static String Summary(String cve_summary) {
		String uri = NamespaceFactory.createAboxNamespace(NamespaceRegistry.theAboxNameSpace, OntologyRegistry.securityDBs)
				+ cve_summary+"_Summary";
		return uri;
	}
	
//	public static void main(String[] args) {
//		String vulnerability = WeaknessURI("CWE-199");
//		System.out.println(vulnerability);
//	}

}
