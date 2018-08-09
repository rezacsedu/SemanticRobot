package se.oru.mpi.ontology;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.CodeSource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

import se.oru.mpi.setting.GeneralSetting;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class Ontology {
		
	protected OntModel model;
	private File ontoFile = null;

	public Ontology(String ontologyPath, boolean runningFromJar, CodeSource codeSource) {
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
		ontoFile = 	new File(GeneralSetting.getFullPath(ontologyPath, runningFromJar, codeSource));
		
		model = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_DL_MEM));
		
		model.read(getOntologyInputStream(), null);
					
    }
	
	public Ontology(String ontologyPath) {
		
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
		ontoFile = 	new File(ontologyPath);
		
		model = ModelFactory.createOntologyModel(new OntModelSpec(OntModelSpec.OWL_DL_MEM));
		
		model.read(getOntologyInputStream(), null);
					
    }

	public OntModel getOntologyModel() {
		return model;
	}
	
	protected void savetoFile() {
		
		try{
			StringWriter sw = new StringWriter();
			model.write(sw, "RDF/XML-ABBREV");
			String owlCode = sw.toString();
		    FileWriter fw = new FileWriter(ontoFile);
		    fw.write(owlCode);
		    fw.close();
		} catch(Exception ioe){
		    ioe.printStackTrace();
		}
	}


	public OntClass getClass(String uri) {
		return model.getOntClass(uri);
	}
	
	public StmtIterator getStatements(OntResource subject, OntProperty predicate, OntResource object) {
			return model.listStatements(subject, predicate, object);
	}
	
	public ObjectProperty getObjectProperty(String uri) {
		return model.getObjectProperty(uri);
	}
	
	public DatatypeProperty getDatatypeProperty(String uri) {
		return model.getDatatypeProperty(uri);
	}
	
	public Individual getIndividual(String uri) {
		return model.getIndividual(uri);
	}
	
	public ExtendedIterator<RDFNode> getObjectsOfProperty(String uri) {
		return model.listObjectsOfProperty(getObjectProperty(uri));
	}
	
	public ResIterator getSubjectsOfProperty(String propertyURI, RDFNode object) {
		return model.listSubjectsWithProperty(getObjectProperty(propertyURI), object);
	}
	
	public Set<OntClass> getClassInRestrictionSomeValues(OntClass ontClass, OntProperty ontProperty ) {
		Set<OntClass> classes =  new HashSet<>();
		if (ontClass.isRestriction()) {
			Restriction restriction = ontClass.asRestriction();
			if (restriction.getOnProperty().equals(ontProperty)) {
				Resource resource = restriction.asSomeValuesFromRestriction().getSomeValuesFrom();
				if (resource.canAs( OntClass.class )) {
					classes.add(resource.as( OntClass.class ));    
				}
			}
		}
		
		return classes;
	}
	
	public Set<OntClass> getSuperClassesInRestrictionSomeValues(OntClass ontClass, OntProperty ontProperty ) {
		Set<OntClass> classes =  new HashSet<>();
		ExtendedIterator<OntClass> supers = ontClass.listSuperClasses();
		while (supers.hasNext()) {
			OntClass superClass = supers.next();
			classes.addAll(getClassInRestrictionSomeValues(superClass, ontProperty));
		}
		return classes;
	}
	
	public void addSubclass(String classURL, String subClassURI) {
		OntClass subClass = model.createClass(subClassURI);
		model.getOntClass(classURL).addSubClass(subClass);
	}
	
	protected void addIndividual (String classURI, String individualURI) {

		OntClass ontClass = model.getOntClass(classURI);
		ontClass.createIndividual(individualURI);
	}
	
	protected void addDataPropertyAxiom (String individualURI, String dataPropertyURI, Date value) {
				
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDdateTime); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		
		model.add(statement);
	}
	
	protected void removeDataPropertyAxiom (String individualURI, String dataPropertyURI, Date value) {
		
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDdateTime); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		model.remove(statement);
		
	}
	
	protected void addDataPropertyAxiom (String individualURI, String dataPropertyURI, long value) {
		
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDlong); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		model.add(statement);
		
	}

	protected void removeDataPropertyAxiom (String individualURI, String dataPropertyURI, long value) {
		
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDlong); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		model.remove(statement);
	
	}
	
    protected void addDataPropertyAxiom (String individualURI, String dataPropertyURI, double value) {
		
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDdouble); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		model.add(statement);
		
	}

	protected void removeDataPropertyAxiom (String individualURI, String dataPropertyURI, double value) {
		
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDdouble); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		model.remove(statement);
	
	}

	protected void addDataPropertyAxiom (String individualURI, String dataPropertyURI, String value) {
		Individual indiv = getIndividual(individualURI);
		DatatypeProperty dataProp = getDatatypeProperty(dataPropertyURI);
		Literal propertyValue = model.createTypedLiteral(value, XSDDatatype.XSDstring); 
		Statement statement = model.createStatement(indiv, dataProp, propertyValue);
		model.add(statement);
	}
	
	protected ObjectProperty getObjectProperty (String objectPropertyURI, String superPropertyURI) {
		ObjectProperty objProp = getObjectProperty(objectPropertyURI);
		if (objProp == null) {
			ObjectProperty objSuperProperty = getObjectProperty(superPropertyURI);
			objProp = model.createObjectProperty(objectPropertyURI);
			model.add(objProp, RDFS.subPropertyOf, objSuperProperty);
		}
		return objProp;
	}
	
	protected void addObjectPropertyAxiom (String individual1URI, String individual2URI, String objectPropertyURI) {
		Individual indiv1 = getIndividual(individual1URI);
		Individual indiv2 = getIndividual(individual2URI);
		ObjectProperty objProp = getObjectProperty(objectPropertyURI);
		Statement statement = model.createStatement(indiv1, objProp, indiv2);
		model.add(statement);
		
	}
	
	protected void addObjectPropertyAxiom (String individual1URI, String individual2URI, ObjectProperty objProp) {
		
		Individual indiv1 = getIndividual(individual1URI);
		Individual indiv2 = getIndividual(individual2URI);
		Statement statement = model.createStatement(indiv1, objProp, indiv2);
		model.add(statement);
		
	}
	
	private InputStream getOntologyInputStream () {

		try {
			return new FileInputStream(ontoFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}