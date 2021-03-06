package ca.concordia.cs.aseg.segps.ontologies.publisher.security;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ca.concordia.cs.aseg.segps.ontologies.publisher.history.CVSPublisher;
import ca.concordia.cs.aseg.segps.ontologies.publisher.util.NtriplesWriter;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.domain_specific.abox.SecurityDBsABox;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.domain_specific.tbox.SecurityDBsTBox;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.general.abox.MainABox;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.general.tbox.MainTBox;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.general.tbox.RDF;
import ca.concordia.cs.aseg.segps.ontologies.urigenerator.system_specific.tbox.SecurityDBs_nvdTBox;

public class InstancesLinker {

	private Entry currentEntry = new Entry();
	private NtriplesWriter writer;

	// file location and previous versions patterns, regualr expertions
	private String file_location_regex = "(([a-zA-Z]*||[a-zA-Z]*:)(?:/[\\w\\s]+)*/[\\w\\s]+\\.\\w+)";
	private String previous_version_regex1 = "((?:before)?(?:Before)?\\s(?:[\\d.])+[\\w-]+)\\b";
	private String previous_version_regex2 = "((?:and earlier))";

	private boolean validateStringContent(String content) {
		if (!content.isEmpty())
			return true;
		else
			return false;
	}

	private void generalLayer(Entry currentEntry) {
	}

	private void domainSpanningLayer(Entry currentEntry) {
	}

	private void domainSpecificLayer(Entry currentEntry) {

		try {
			// ABox instances
			String cve = SecurityDBsABox.VulnerabilityURI(currentEntry.getcveID());

			// TBox instances
			writer.addDeclarationTriple(cve, RDF.type(), SecurityDBsTBox.Vulnerability(), false);
			writer.addIndividualTriple(cve, SecurityDBsTBox.hasVulnerabilityId(), currentEntry.getcveID(), true);

			/**
			 * Add creation date, publication(disclosure) date and modification
			 * date
			 **/
			String creationDate = getCreationDate(currentEntry.getcveID());
			if (creationDate != null && !creationDate.isEmpty()) {
				writer.addIndividualTriple(cve, MainTBox.hasCreationDate(), creationDate, true);
			}
			writer.addIndividualTriple(cve, SecurityDBsTBox.hasPublishedDate(), currentEntry.getPublishedDatetime(),
					true);
			writer.addIndividualTriple(cve, SecurityDBsTBox.hasModifiedDate(), currentEntry.getLastModifiedDatetime(),
					true);

			// Check if the vulnerability has weakness type.
			if (currentEntry.getcweID() != null) {
				// ABox instances
				String cwe = SecurityDBsABox.WeaknessURI(currentEntry.getcweID());
				// TBox instances
				writer.addDeclarationTriple(cwe, RDF.type(), SecurityDBsTBox.Weakness(), false);
				writer.addIndividualTriple(cve, SecurityDBsTBox.hasWeakness(), cwe, false);
				writer.addIndividualTriple(cve, SecurityDBsTBox.hasWeaknessId(), currentEntry.getcweID(), true);
			}

			// Check if the vulnerability has external sources *e.g. references*
			if (currentEntry.getReferencesList() != null) {
				ArrayList<String> rfl = currentEntry.getReferencesList();
				// rfl contain [Reference type (e.g. Unknown, Patch, ...etc),
				// Reference Source, Reference Location (URL), ...]
				for (int i = 0; i < rfl.size();) {
					// ABox instances

					String ReferenceURI = rfl.get(i);
					if (validateStringContent(ReferenceURI) && ReferenceURI.contains("http")) {
						// TBox instances
						writer.addDeclarationTriple(ReferenceURI, RDF.type(), SecurityDBsTBox.Reference(), false);
						writer.addIndividualTriple(cve, SecurityDBsTBox.hasReferenceURI(), ReferenceURI, false);
					}
					i += 3;
				}
			}

			// Mapping the vulnerable products facts into the ontology concepts
			// and properties.
			ArrayList<String> affectedProducts = currentEntry.getAffectedProductList();
			boolean onePass = true;
			for (int i = 0; i < affectedProducts.size(); i++) {
				// ABox instances
				String affectedRelease = "";
				String versionID = "";
				/**removes all whitespace and non visible characters such as tab, \n.**/
				String temp = affectedProducts.get(i).replaceAll("\\s+", "");
				String[] split = temp.split(":"); // e.g.
													// cpe:/a:vendor_name:product_name:version:quantifier.
				if (split.length > 5) {
					affectedRelease = SecurityDBsABox
							.AffectedRelease(split[2] + ":" + split[3] + ":" + split[4] + "-" + split[5]); // e.g.
																											// vendor_name:product_name:version-quantifier
					versionID = split[4] + "-" + split[5];
				} else {
					affectedRelease = SecurityDBsABox.AffectedRelease(split[2] + ":" + split[3] + ":" + split[4]); // e.g.
																													// vendor_name:product_name:version
					versionID = split[4];
				}
				String organizationName = MainABox.Organization(split[2]);
				String procutName = MainABox.Product(split[2] + ":" + split[3]);

				// TBox instances
				// Classify Affected products into OS or APP
				if (split[1].equals("/o")) {
					writer.addDeclarationTriple(procutName, RDF.type(), SecurityDBsTBox.OperatingSystem(), false);
					writer.addIndividualTriple(procutName, MainTBox.belongsToOrganization(), organizationName, false);
				} else if (split[1].equals("/a")) {
					writer.addDeclarationTriple(procutName, RDF.type(), SecurityDBsTBox.Application(), false);
					writer.addIndividualTriple(procutName, MainTBox.belongsToOrganization(), organizationName, false);
				}

				writer.addDeclarationTriple(affectedRelease, RDF.type(), SecurityDBsTBox.AffectedRelease(), false);
				writer.addDeclarationTriple(organizationName, RDF.type(), MainTBox.Organization(), false);
				writer.addIndividualTriple(cve, SecurityDBsTBox.affectRelease(), affectedRelease, false);
				writer.addIndividualTriple(cve, SecurityDBsTBox.affectProduct(), procutName, false);

				writer.addIndividualTriple(procutName, MainTBox.hasRelease(), affectedRelease, false);

				// We might delete 1 and 2 if the reasoner will be able to infer
				// the inverse relations
				// hasVulnerability inverse of affectProduct
				/** 1 **/
				writer.addIndividualTriple(affectedRelease, SecurityDBsTBox.hasVulnerability(), cve, false);
				/** 2 **/
				writer.addIndividualTriple(procutName, SecurityDBsTBox.hasVulnerability(), cve, false);
				writer.addIndividualTriple(affectedRelease, SecurityDBsTBox.hasAffectedReleaseVersion(), versionID,
						true);
				writer.addIndividualTriple(affectedRelease, SecurityDBsTBox.hasAffectedReleaseName(), split[3], true);

				/**
				 * This if-condition executed only one time for each CVE. It
				 * modeled the previous versions of this affected release if it
				 * is mentioned in CVE summary text.
				 */
				if (currentEntry.getSummary() != null && onePass) {
					Matcher pvMatcher1 = Pattern.compile(previous_version_regex1).matcher(currentEntry.getSummary());
					Matcher pvMatcher2 = Pattern.compile(previous_version_regex2).matcher(currentEntry.getSummary());
					// first pattern: previous version such as before 1.2.x
					while (pvMatcher1.find()) {
						String pv = pvMatcher1.group(1).replaceAll("\\s+", "_");
						// ABox instances
						String otherReleases = SecurityDBsABox.OtherReleases(split[2] + ":" + split[3] + ":" + pv);
						// TBox instances
						writer.addDeclarationTriple(otherReleases, RDF.type(), SecurityDBsTBox.OtherReleases(), false);
						writer.addIndividualTriple(cve, SecurityDBsTBox.affectOtherReleases(), otherReleases, false);
					}
					// second pattern: previous version such as 1.2.x and
					// earlier
					while (pvMatcher2.find()) {
						String pv = pvMatcher2.group(1).replaceAll("\\s+", "_");
						// ABox instances
						String otherReleases = SecurityDBsABox
								.OtherReleases(split[2] + ":" + split[3] + ":" + split[4] + "_" + pv);
						// TBox instances
						writer.addDeclarationTriple(otherReleases, RDF.type(), SecurityDBsTBox.OtherReleases(), false);
						writer.addIndividualTriple(cve, SecurityDBsTBox.affectOtherReleases(), otherReleases, false);
					}
					onePass = false;
				}

			}

			if (currentEntry.getSummary() != null) {
				String summaryURI = SecurityDBsABox.Summary(currentEntry.getcveID());
				writer.addIndividualTriple(summaryURI, RDF.type(), SecurityDBsTBox.Summary(), false);
				writer.addIndividualTriple(cve, SecurityDBsTBox.hasSummary(), summaryURI, false);
				/**we can use the sub-data property 'vulnerabilityDescrption' **/
				writer.addIndividualTriple(summaryURI, MainTBox.hasDescription(), currentEntry.getSummary(), true); 
				/** Extracting the affected artifacts (file/folder locations) from cve summary (if any) **/
				Matcher flMatcher = Pattern.compile(file_location_regex).matcher(currentEntry.getSummary());
				while (flMatcher.find()) {
					String fl = flMatcher.group(1).replaceAll("/", "_");
					// ABox instance
					String flURI = MainABox.File(fl);
					// TBox instance
					writer.addDeclarationTriple(flURI, RDF.type(), MainTBox.File(), false);
					writer.addIndividualTriple(cve, SecurityDBsTBox.isLocatedIn(), flURI, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void nvd_SystemSpecificLayer(Entry currentEntry) {
		String cve = SecurityDBsABox.VulnerabilityURI(currentEntry.getcveID());
		// Check references sources, types, and location.
		try {
			if (currentEntry.getReferencesList() != null) {
				ArrayList<String> rfl = currentEntry.getReferencesList();
				/**
				 * if(rfl.size()%3>0){ System.out.println(rfl.toString()); }
				 *
				 * rfl contain [Reference type (e.g. Unknown, Patch, ...etc),
				 * Reference Source, Reference Location (URL), ...]**/
				for (int index = 0; index < rfl.size();) {

					String ReferenceType = "", ReferenceSource = "", ReferenceURL = "";
					/**
					 * this is temporary solution for the ArrayLists indexes out
					 * of bounds. Sometimes ArrayList include references with
					 * incomplete information such as type, source or URL.
					 */
					// if (((index + 2) / 3) == 0) {
					ReferenceType = rfl.get(index);
					ReferenceSource = rfl.get(index + 1);
					ReferenceURL = rfl.get(index + 2);
					// } else {
					// ReferenceType = rfl.get(index);
					// ReferenceURL = rfl.get(index + 1);
					// }
					// TBox instances
					String processedLink = CVSPublisher.processLink(ReferenceURL);
					if (!processedLink.isEmpty()) {
						ReferenceURL = processedLink;
					}
					if (ReferenceType.equalsIgnoreCase("PATCH")) {
						writer.addDeclarationTriple(ReferenceURL, RDF.type(), SecurityDBs_nvdTBox.PatchReference(),
								false);
						writer.addIndividualTriple(ReferenceURL, SecurityDBs_nvdTBox.hasPatchSource(), ReferenceSource,
								true);
						writer.addIndividualTriple(cve, SecurityDBsTBox.hasStatus(), SecurityDBs_nvdTBox.Patched(),
								false);
					} else if (ReferenceType.equalsIgnoreCase("UNKNOWN")) {
						writer.addDeclarationTriple(ReferenceURL, RDF.type(), SecurityDBs_nvdTBox.UnknownReference(),
								false);
						writer.addIndividualTriple(ReferenceURL, SecurityDBs_nvdTBox.hasUnknownSource(),
								ReferenceSource, true);
						/**
						 * trying to optimize the vulnerability status, so far,
						 * we can capture the patched and detected status, and
						 * if non of these status happen, then the vulnerability
						 * status should be still
						 * unkown.writer.addIndividualTriple(cve,
						 * SecurityDBsTBox.hasStatus(),
						 * SecurityDBs_nvdTBox.Unknown(), false);
						 */
					} else if (ReferenceType.equalsIgnoreCase("VENDOR_ADVISORY")
							|| ReferenceSource.equalsIgnoreCase("CONFIRM")) {
						writer.addDeclarationTriple(ReferenceURL, RDF.type(),
								SecurityDBs_nvdTBox.VendorAdvisoryReference(), false);
						writer.addIndividualTriple(ReferenceURL, SecurityDBs_nvdTBox.hasVendorAdvisorySource(),
								ReferenceSource, true);
						writer.addIndividualTriple(cve, SecurityDBsTBox.hasStatus(), SecurityDBs_nvdTBox.Detected(),
								false);
					}
					index += 3;

				}
			}

			double score = currentEntry.getScore();
			if (score != -1) {
				// ABox instances
				String scoreURI = SecurityDBsABox.Score(currentEntry.getcveID(), String.valueOf(score));
				writer.addDeclarationTriple(scoreURI, RDF.type(), SecurityDBsTBox.BaseScoreMertrics(), false);
				String severityLevel = null;
				/**
				 * 1. Vulnerabilities are labelled "Low" severity if they have a
				 * score of 0.0-3.9. 2. Vulnerabilities will be labelled
				 * "Medium" severity if they have a score of 4.0-6.9. 3.
				 * Vulnerabilities will be labelled "High" severity if they have
				 * a score of 7.0-10.0.
				 */
				if (score <= 3.9) {
					severityLevel = SecurityDBs_nvdTBox.Low();
					writer.addIndividualTriple(cve, SecurityDBsTBox.hasSeverity(), severityLevel, false);
				} else if (score > 4 && score < 6.9) {
					severityLevel = SecurityDBs_nvdTBox.Medium();
					writer.addIndividualTriple(cve, SecurityDBsTBox.hasSeverity(), severityLevel, false);
				} else if (score >= 7.0) {
					severityLevel = SecurityDBs_nvdTBox.High();
					writer.addIndividualTriple(cve, SecurityDBsTBox.hasSeverity(), severityLevel, false);
				}
				writer.addDeclarationTriple(severityLevel, RDF.type(), SecurityDBsTBox.VulnerabilitySeverity(), false);
				writer.addIndividualTriple(severityLevel, SecurityDBsTBox.hasSeverityScore(), scoreURI, false);
				writer.addIndividualTriple(scoreURI, SecurityDBsTBox.hasAccessComplexity(),
						currentEntry.getAccessComplexity(), true);
				writer.addIndividualTriple(scoreURI, SecurityDBsTBox.hasAccessVector(), currentEntry.getAccessVector(),
						true);
				writer.addIndividualTriple(scoreURI, SecurityDBsTBox.hasAuthentication(),
						currentEntry.getAuthentication(), true);
				writer.addIndividualTriple(scoreURI, SecurityDBsTBox.hasAvailabilityImpact(),
						currentEntry.getAvailabilityImpact(), true);
				writer.addIndividualTriple(scoreURI, SecurityDBsTBox.hasIntegrityImpact(),
						currentEntry.getIntegrityImpact(), true);
				writer.addIndividualTriple(scoreURI, SecurityDBsTBox.hasConfidentialityImpact(),
						currentEntry.getConfidentialityImpact(), true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void distributer(Entry currentEntry) {

		this.currentEntry = currentEntry;
		// writer = new
		// NtriplesWriter("C:/Users/umroot/workspace/data/triples/sevont.nt",
		// 100000, 500000);
		writer = new NtriplesWriter("C:/Users/umroot/workspace/data/triples/sevont.nt", 100000, 500000);

		// System.out.println("Mapping "+ this.currentEntry.getcveID()+" facts
		// into SEVONT layers");

		// Populate triples for General layer
		generalLayer(this.currentEntry);
		// Populate triples for Domain-Spanning layer
		domainSpanningLayer(this.currentEntry);
		// Populate triples for Domain-Specific layer
		domainSpecificLayer(this.currentEntry);
		// Populate triples for System-Specific layer
		nvd_SystemSpecificLayer(this.currentEntry);
		try {
			writer.flushAndClose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getCreationDate(String cveID) throws ParseException {
		String result=null;
		String url ="http://www.cve.mitre.org/cgi-bin/cvename.cgi?name="+cveID;
		try {
			Document doc = Jsoup.connect(url).get();
			Elements trElements = doc.select("tr");
			for (Element elem : trElements) {
				// System.out.println(elem);
				if (elem.text().contains("Date Entry Created")) {
					Element nextElem = elem.nextElementSibling();
					String dateStr =nextElem.getElementsByTag("td").get(0).text();
					
					
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
					Date fmtDate = dateFormat.parse(dateStr);
					//System.out.println(fmtDate);
					DateTime dateTime = new DateTime(fmtDate);
					result=dateTime.toString();
				}
			}

		} catch (IOException e) {
			System.out.println("Exception caught on: " + url);
			e.printStackTrace();
		}
		return result;
	}

	/*public static void main(String[] args) {
		try {
			System.out.println(new InstancesLinker().getCreationDate("CVE-2008-4582"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}*/
}
