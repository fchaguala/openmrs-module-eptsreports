/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineForAdultsAndChildrenCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.RoutineForBreastfeedingAndPregnantWomenCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.TxPvlsQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Defines all of the TxNew Cohort Definition instances we want to expose for EPTS
 */
@Component
public class TxPvlsCohortQueries {
	
	@Autowired
	private GenericCohortQueries genericCohortQueries;
	
	@Autowired
	private HivCohortQueries hivCohortQueries;
	
	@Autowired
	private TxNewCohortQueries txNewCohortQueries;
	
	/**
	 * Breast feeding women with viral load suppression
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomenWithViralSuppression")
	public CohortDefinition getBreastfeedingWomenWhoHaveViralSuppression() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral suppression");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("suppression", EptsReportUtils.map(getPatientsWithViralLoadSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeeding AND suppression");
		
		return cd;
	}
	
	/**
	 * Breast feeding women with viral load suppression
	 * 
	 * @return CohortDefinition
	 */
	@DocumentedDefinition(value = "breastfeedingWomenWithViralLoadResults")
	public CohortDefinition getBreastfeedingWomenWhoHaveViralLoadResults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral results");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeeding", EptsReportUtils.map(txNewCohortQueries.getTxNewBreastfeedingComposition(),
		    "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
		cd.addSearch("results",
		    EptsReportUtils.map(getPatientsWithViralLoadResults(), "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("breastfeeding AND results");
		
		return cd;
	}
	
	/**
	 * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU, transferred
	 * out, stopped ART
	 */
	public CohortDefinition getPatientsWithViralLoadSuppression() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
		cd.addSearch("baseCohort", EptsReportUtils.map(genericCohortQueries.getBaseCohort(), mappings));
		cd.setCompositionString("supp AND baseCohort");
		return cd;
	}
	
	/**
	 * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred out,
	 * stopped ARTtxNewCohortQueries
	 */
	public CohortDefinition getPatientsWithViralLoadResults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
		cd.addSearch("baseCohort", EptsReportUtils.map(genericCohortQueries.getBaseCohort(), mappings));
		cd.setCompositionString("results AND baseCohort");
		return cd;
	}
	
	/**
	 * Get patients with viral load suppression with age bracket
	 */
	public CohortDefinition getPatientsWithViralLoadSuppressionWithinAgeBracket(int min, int max) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients with suppression within age");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("age", EptsReportUtils.map(findPatientsBetweenAgeBracketsInYears(min, max), mappings));
		cd.setCompositionString("supp AND age");
		return cd;
	}
	
	/**
	 * Get patients with viral load results with age bracket
	 */
	public CohortDefinition getPatientsWithViralLoadResultsWithinAgeBracket(int min, int max) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients with viral load results within age");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		cd.addSearch("age", EptsReportUtils.map(findPatientsBetweenAgeBracketsInYears(min, max), mappings));
		cd.setCompositionString("results AND age");
		return cd;
	}
	
	/**
	 * Get patients with viral load suppression with age below
	 */
	public CohortDefinition getPatientsWithViralLoadSuppressionAgeBelow(int age) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients with suppression aged below");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("age", EptsReportUtils.map(findPatientsagedBelowInYears(age), mappings));
		cd.setCompositionString("supp AND age");
		return cd;
	}
	
	/**
	 * Get patients with viral load results with age below
	 */
	public CohortDefinition getPatientsWithViralLoadResultsWithAgeBelow(int age) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Patients with viral load results aged below");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
		cd.addSearch("age", EptsReportUtils.map(findPatientsagedBelowInYears(age), mappings));
		cd.setCompositionString("results AND age");
		return cd;
	}
	
	/**
	 * Get patients who are aged between age bracket
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public CohortDefinition findPatientsBetweenAgeBracketsInYears(int min, int max) {
		return genericCohortQueries.generalSql("aged between age brackets",
		    TxPvlsQueries.getPatientsBetweenAgeBracketsInYears(min, max));
	}
	
	/**
	 * Find patients who are aged below
	 * 
	 * @param age
	 * @return
	 */
	public CohortDefinition findPatientsagedBelowInYears(int age) {
		return genericCohortQueries.generalSql("aged between age brackets", TxPvlsQueries.getPatientsWhoAreBelowXyears(age));
	}
	
	/**
	 * Get adults and children patients who are on routine
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getpatientsOnRoutineAdultsAndChildren(int monthsOnArt, int artLowerLimit1, int artUpperLimit1,
	        int artLowerLimit2, int artUpperLimit2) {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("criteria1", new RoutineForAdultsAndChildrenCalculation());
		cd.setName("Routine for adults and children");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		cd.addCalculationParameter("monthsOnArt", monthsOnArt);
		cd.addCalculationParameter("artLowerLimit1", artLowerLimit1);
		cd.addCalculationParameter("artUpperLimit1", artUpperLimit1);
		cd.addCalculationParameter("artLowerLimit2", artLowerLimit2);
		cd.addCalculationParameter("artUpperLimit2", artUpperLimit2);
		return cd;
		
	}
	
	/**
	 * Get breastfeeding and pregnant women on routine
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantAndBreastfeedingWomenOnRoutine() {
		CalculationCohortDefinition cd = new CalculationCohortDefinition("criteria2",
		        new RoutineForBreastfeedingAndPregnantWomenCalculation());
		cd.setName("Routine for breastfeeding and pregnant");
		cd.addParameter(new Parameter("onDate", "On Date", Date.class));
		return cd;
	}
	
	/**
	 * Get patients having viral load suppression and routine for adults and children - Numerator
	 * 
	 * @retrun CohortDefinition
	 */
	public CohortDefinition getPatientWithViralSuppressionAndOnRoutineAdultsAndChildren() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Suppression and on routine adult and children");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getpatientsOnRoutineAdultsAndChildren(6, 6, 9, 12, 15), "onDate=${endDate}"));
		cd.setCompositionString("supp AND routine");
		return cd;
	}
	
	/**
	 * Get patients having viral load suppression and not documented for adults and children - Numerator
	 * 
	 * @retrun CohortDefinition
	 */
	public CohortDefinition getPatientWithViralSuppressionAndNotDocumentedForAdultsAndChildren() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("suppression and not documented adults and children");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("supp", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getpatientsOnRoutineAdultsAndChildren(6, 6, 9, 12, 15), "onDate=${endDate}"));
		cd.setCompositionString("supp AND NOT routine");
		return cd;
	}
	
	/**
	 * Get patients with viral load results and on routine - Denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWithViralLoadREsultsAndOnRoutineForChildrenAndAdults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Viral load results with routine for children and adults denominator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getpatientsOnRoutineAdultsAndChildren(6, 6, 9, 12, 15), "onDate=${endDate}"));
		cd.setCompositionString("results AND routine");
		return cd;
	}
	
	/**
	 * Get patients with viral load results and NOT documented
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPatientsWithViralLoadREsultsAndNotDocumenetdForChildrenAndAdults() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Viral load results with not documentation for children and adults denominator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getpatientsOnRoutineAdultsAndChildren(6, 6, 9, 12, 15), "onDate=${endDate}"));
		cd.setCompositionString("results AND NOT routine");
		return cd;
	}
	
	// breastfeeding and pregnant women
	// breast feeding Numerator.
	/**
	 * Get breastfeding women on routine Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastFeedingWomenOnRoutineNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral results and on routine");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeedingVl", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("breastfeedingVl AND routine");
		
		return cd;
	}
	
	/**
	 * Get breastfeeding women NOT documented Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastFeedingWomenNotDocumentedNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding with viral results and NOT documented");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("breastfeedingVl", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralSuppression(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("breastfeedingVl AND NOT routine");
		
		return cd;
	}
	
	/**
	 * Get pregnant women Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenWithViralLoadSuppressionNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load suppression");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("suppression", EptsReportUtils.map(getPatientsWithViralLoadSuppression(), mappings));
		cd.addSearch("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(), mappings));
		cd.setCompositionString("suppression AND pregnant");
		return cd;
	}
	
	/**
	 * Get pregnant women Denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load results denominator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("results", EptsReportUtils.map(getPatientsWithViralLoadResults(), mappings));
		cd.addSearch("pregnant", EptsReportUtils.map(txNewCohortQueries.getPatientsPregnantEnrolledOnART(), mappings));
		cd.setCompositionString("results AND pregnant");
		return cd;
	}
	
	/**
	 * Get pregnant, has viral load suppression, and on routine Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantAndOnRoutineNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant and on Routine Numerator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadSuppressionNumerator(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("pregnant AND routine");
		return cd;
	}
	
	/**
	 * Get pregnant, has viral load, and NOT documented Numerator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantAndNotDocumentedNumerator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Pregnant and NOT documented Numerator");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadSuppressionNumerator(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("pregnant AND NOT routine");
		return cd;
	}
	
	// Pregnant and breastfeeding denominator
	// Breastfeeding
	/**
	 * Patients who have viral load results, breastfeeding and on routine denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastfeedingWomenOnRoutineWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding women on routine and have Viral load results");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("vlandBreastfeeding", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("vlandBreastfeeding AND routine");
		return cd;
	}
	
	/**
	 * Patients who have virial load results and NOT documented and are breastfeeding denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getBreastfeedingWomenAndNotDocumentedWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Breastfeeding women NOT documented and have Viral load results");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("vlandBreastfeeding", EptsReportUtils.map(getBreastfeedingWomenWhoHaveViralLoadResults(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("vlandBreastfeeding AND NOT routine");
		return cd;
	}
	
	// Pregnant
	/**
	 * Patients who are pregnant, have viral load results and on routine denominator
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenAndOnRoutineWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load results Not documented");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadResultsDenominator(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("pregnant AND routine");
		return cd;
	}
	
	/**
	 * Patients who are pregnant, have viral load and NOT Documented
	 * 
	 * @return CohortDefinition
	 */
	public CohortDefinition getPregnantWomenAndNotDocumentedWithViralLoadResultsDenominator() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Get pregnant women with viral load results Not documented");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		cd.addSearch("pregnant", EptsReportUtils.map(getPregnantWomenWithViralLoadResultsDenominator(), mappings));
		cd.addSearch("routine", EptsReportUtils.map(getPregnantAndBreastfeedingWomenOnRoutine(), "onDate=${endDate}"));
		cd.setCompositionString("pregnant AND NOT routine");
		return cd;
	}
}
