/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.dimensions;

import java.util.Date;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.cohorts.AgeCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri2MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.Eri4MonthsCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenderCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.GenericCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxNewCohortQueries;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TxPvlsCohortQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.indicator.dimension.CohortDefinitionDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EptsCommonDimension {

  @Autowired private GenderCohortQueries genderCohortQueries;

  @Autowired private AgeCohortQueries ageCohortQueries;

  @Autowired private TxNewCohortQueries txNewCohortQueries;

  @Autowired private TxPvlsCohortQueries txPvlsCohortQueries;

  @Autowired private GenericCohortQueries genericCohortQueries;

  @Autowired private HivMetadata hivMetadata;

  @Autowired private Eri4MonthsCohortQueries eri4MonthsCohortQueries;

  @Autowired private Eri2MonthsCohortQueries eri2MonthsCohortQueries;

  /**
   * Gender dimension
   *
   * @return the {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension gender() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("gender");
    dim.addCohortDefinition("M", EptsReportUtils.map(genderCohortQueries.MaleCohort(), ""));
    dim.addCohortDefinition("F", EptsReportUtils.map(genderCohortQueries.FemaleCohort(), ""));
    return dim;
  }

  /**
   * Age range dimension 10-14, 15-19, 20-24, 25-29, 30-34, 35-39, 40-44, 45-49, >=50
   *
   * @return {@link org.openmrs.module.reporting.indicator.dimension.CohortDimension}
   */
  public CohortDefinitionDimension pvlsAges() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "Location", Location.class));
    dim.setName("pvls ages");

    dim.addCohortDefinition(
        "UK",
        EptsReportUtils.map(
            ageCohortQueries.getPatientsWithUnknownAge(),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "<1",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsagedBelowInYears(1),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "1-4",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(1, 4),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(5, 9),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "10-14",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(10, 14),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "15-19",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(15, 19),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "20-24",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(20, 24),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "25-29",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(25, 29),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "30-34",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(30, 34),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "35-39",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(35, 39),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "40-44",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(40, 44),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "45-49",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(45, 49),
            "endDate=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "50+",
        EptsReportUtils.map(
            txPvlsCohortQueries.findPatientsBetweenAgeBracketsInYears(50, 200),
            "endDate=${endDate},location=${location}"));
    return dim;
  }

  public CohortDefinitionDimension txNewAges() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.setName("age dimension");
    dim.addParameter(new Parameter("startDate", "startDate", Date.class));
    dim.addParameter(new Parameter("endDate", "endDate", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    String mappings = "location=${location},onOrAfter=${startDate},onOrBefore=${endDate}";
    dim.addCohortDefinition(
        "<1",
        EptsReportUtils.map(txNewCohortQueries.createBelowXAgeOnArtStartDateCohort(1), mappings));
    dim.addCohortDefinition(
        "1-4",
        EptsReportUtils.map(txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(1, 4), mappings));
    dim.addCohortDefinition(
        "5-9",
        EptsReportUtils.map(txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(5, 9), mappings));
    dim.addCohortDefinition(
        "10-14",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(10, 14), mappings));
    dim.addCohortDefinition(
        "15-19",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(15, 19), mappings));
    dim.addCohortDefinition(
        "20-24",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(20, 24), mappings));
    dim.addCohortDefinition(
        "25-29",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(25, 29), mappings));
    dim.addCohortDefinition(
        "30-34",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(30, 34), mappings));
    dim.addCohortDefinition(
        "35-39",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(35, 39), mappings));
    dim.addCohortDefinition(
        "40-44",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(40, 44), mappings));
    dim.addCohortDefinition(
        "45-49",
        EptsReportUtils.map(
            txNewCohortQueries.createXtoYAgeOnArtStartDateCohort(45, 49), mappings));
    dim.addCohortDefinition(
        "50+",
        EptsReportUtils.map(txNewCohortQueries.createOverXAgeOnArtStartDateCohort(50), mappings));
    dim.addCohortDefinition(
        "unknown", EptsReportUtils.map(genericCohortQueries.getUnknownAgeCohort(), ""));
    return dim;
  }

  /** @return CohortDefinitionDimension */
  public CohortDefinitionDimension maternityDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Maternity Dimension");

    dim.addCohortDefinition(
        "breastfeeding",
        EptsReportUtils.map(
            txNewCohortQueries.getTxNewBreastfeedingComposition(),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    dim.addCohortDefinition(
        "pregnant",
        EptsReportUtils.map(
            txNewCohortQueries.getPatientsPregnantEnrolledOnART(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri4MonthsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("startDate", "Start Date", Date.class));
    dim.addParameter(new Parameter("endDate", "End Date", Date.class));
    dim.addParameter(new Parameter("reportEndDate", "Report End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patient states");
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    String endOfReportingPeriodMappings =
        "startDate=${startDate},endDate=${reportEndDate},location=${location}";
    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoInitiatedArtLessTransferIns(), mappings));

    dim.addCohortDefinition(
        "AIT",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoAreAliveAndOnTreatment(), endOfReportingPeriodMappings));

    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(genericCohortQueries.getDeceasedPatients(), endOfReportingPeriodMappings));

    dim.addCohortDefinition(
        "LTFU",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getAllPatientsWhoAreLostToFollowUpDuringPeriod(),
            endOfReportingPeriodMappings));

    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata
                    .getTransferredOutToAnotherHealthFacilityWorkflowState()
                    .getProgramWorkflowStateId()),
            endOfReportingPeriodMappings));

    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            genericCohortQueries.getPatientsBasedOnPatientStates(
                hivMetadata.getARTProgram().getProgramId(),
                hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId()),
            endOfReportingPeriodMappings));

    dim.addCohortDefinition(
        "ANIT",
        EptsReportUtils.map(
            eri4MonthsCohortQueries.getPatientsWhoAreAliveAndNotOnTreatment(),
            endOfReportingPeriodMappings));
    return dim;
  }

  /**
   * Get the dimensions based on the patient states for ERI-4 months
   *
   * @return CohortDefinitionDimension
   */
  public CohortDefinitionDimension getEri2MonthsDimension() {
    CohortDefinitionDimension dim = new CohortDefinitionDimension();
    dim.addParameter(new Parameter("cohortStartDate", "Cohort Start Date", Date.class));
    dim.addParameter(new Parameter("cohortEndDate", "Cohort End Date", Date.class));
    dim.addParameter(new Parameter("reportingEndDate", "Reporting End Date", Date.class));
    dim.addParameter(new Parameter("location", "location", Location.class));
    dim.setName("Get patients dimensions for Eri2Months");
    dim.addCohortDefinition(
        "IART",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getAllPatientsWhoInitiatedArt(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},location=${location}"));
    dim.addCohortDefinition(
        "DNPUD",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoDidNotPickDrugsOnTheirSecondVisit(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    dim.addCohortDefinition(
        "PUD",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoPickedUpDrugsOnTheirSecondVisit(),
            "startDate=${cohortStartDate},endDate=${cohortEndDate},location=${location}"));
    dim.addCohortDefinition(
        "DP",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoInitiatedArtAndDead(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "TOP",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoInitiatedArtButTransferredOut(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    dim.addCohortDefinition(
        "STP",
        EptsReportUtils.map(
            eri2MonthsCohortQueries.getPatientsWhoInitiatedArtButSuspendedTreatment(),
            "cohortStartDate=${cohortStartDate},cohortEndDate=${cohortEndDate},reportingEndDate=${reportingEndDate},location=${location}"));
    return dim;
  }
}
