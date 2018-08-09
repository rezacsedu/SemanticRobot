package se.oru.mpi.ontology;

import java.io.File;
import java.security.CodeSource;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import se.oru.mpi.setting.GeneralSetting;

public class OntologyOWL {
	protected OWLOntology ontology;
	protected OWLReasoner reasoner;
	public OntologyOWL(String ontologyPath, boolean runningFromJar, CodeSource codeSource) {
		
		try {

			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			ontology = manager.loadOntologyFromOntologyDocument(IRI.create(new File(GeneralSetting.getFullPath(ontologyPath, runningFromJar, codeSource))));
			OWLDataFactory factory = manager.getOWLDataFactory();
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
			reasoner.precomputeInferences();

			/*OWLDataProperty p = factory.getOWLDataProperty(IRI.create(base + "hasName"));
			OWLClassExpression ex = 
			                factory.getOWLDataHasValue(p, factory.getOWLLiteral("John"));

			Set<OWLNamedIndividual> result = reasoner.getInstances(ex, true).getFlattened();        
			for (OWLNamedIndividual owlNamedIndividual : result) {
			    System.out.println(owlNamedIndividual);
			}*/

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
