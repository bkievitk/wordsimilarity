package relations;

import gui.simple.GUISimple;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import relations.beagle.WordRelatorBEAGLE;
import relations.helpers.WordRelationCrystalized;
import relations.helpers.WordRelatorAverage;
import relations.helpers.WordRelatorFull;
import relations.holoc.WordRelatorContinuousHolographic;
import relations.ngram.WordRelatorNGram;
import relations.sspace.*;
import relations.wordnet.WordRelatorWordNet;
import wizard.Wizard;
import wizard.WizardPanel;

public class RelationLoader {

	
	// List of types of relations.
	@SuppressWarnings("rawtypes")
	private Vector<Class> relationTypes = new Vector<Class>();
	
	// List of objects to update when the list of relations is changed.
	public Vector<ChangeListener> changeListeners = new Vector<ChangeListener>();

	public RelationLoader() {
		relationTypes.add(WordRelatorBEAGLE.class);
		relationTypes.add(WordRelationCrystalized.class);
		relationTypes.add(WordRelatorFull.class);
		relationTypes.add(WordRelatorNGram.class);
		relationTypes.add(WordRelatorWordNet.class);
		relationTypes.add(WordRelatorESA.class);
		relationTypes.add(WordRelatorFDTRI.class);
		relationTypes.add(WordRelatorHAL.class);
		relationTypes.add(WordRelatorLSA.class);
		relationTypes.add(WordRelatorRandomIndexing.class);
		relationTypes.add(WordRelatorContinuousHolographic.class);
		relationTypes.add(WordRelatorAverage.class);
	}
	
	public void addRelationType(@SuppressWarnings("rawtypes") Class relationType) {
		relationTypes.add(relationType);
		changeEvent();
	}
	
	private void changeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener listener : changeListeners) {
			listener.stateChanged(e);
		}
	}
	
	public int getRelationCount() {
		return relationTypes.size();
	}
	
	public WordRelator createRelator(int type) {
		try {
			return (WordRelator)relationTypes.get(type).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getDescription(int type) {
		try {
			Field f = relationTypes.get(type).getField("description");
			return (String)f.get(null);
		} catch (Exception e) {
			return "[No description found.]";
		}
	}
	
	public String getName(int type) {
		try {
			Field f = relationTypes.get(type).getField("typeName");
			return (String)f.get(null);
		} catch (Exception e) {
			return "[No name found.]";
		}
	}
	
	public Icon getIcon(int type) {
		try {
			Field f = relationTypes.get(type).getField("icon");
			return GUISimple.loadIcon((String)f.get(null));
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public WizardPanel getWizardPanel(final int type, final Wizard wizard) {
		try {
			Field f = relationTypes.get(type).getField("wizardPanel");
			Class panel = (Class)f.get(null);
			Constructor[] constructors = panel.getConstructors();
			final Constructor constructor = constructors[0];
						
			return (WizardPanel)constructor.newInstance(wizard);
						
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error with [" + relationTypes.get(type) + "]");
			return null;
		}
	}
}
