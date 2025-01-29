package com.recruitment.controller;

import com.recruitment.enums.JobStatus;
import com.recruitment.enums.JobType;
import com.recruitment.enums.Source;
import com.recruitment.enums.WorkExperience;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class JobTypeController {


    @Operation(summary = "Get all job types", description = "Retrieve all available job types")
    @GetMapping("/job-types")
    public ResponseEntity<List<JobType>> getAllJobTypes() {
        return ResponseEntity.ok(Arrays.asList(JobType.values()));
    }
    @Operation(summary = "Get all job status", description = "Retrieve all available job Status")
    @GetMapping("/job-status")
    public ResponseEntity<List<JobStatus>> getAllJobStatus() {
        return ResponseEntity.ok(Arrays.asList(JobStatus.values()));
    }


    @GetMapping("/industry")
    public ResponseEntity<List<String>> getAllCountries() {
        List<String> countries =
                Arrays.asList(  "None", "Administration", "Advertising", "Agriculture", "Architecture & Construction", "Arts & Graphics", "Airline - Aviation", "Accounting", "Automotive", "Banking", "Biotechnology", "Broadcasting", "Business Management", "Charity", "Catering", "Customer Service", "Chemicals", "Construction", "Communications", "Consulting", "Computer", "Consumer", "Cosmetics", "Design", "Defence", "Education", "Electronics", "Engineering", "Energy and Utilities", "Entertainment", "Employment - Recruiting - Staffing", "Environmental", "Exercise - Fitness", "Export/Import", "Financial Services", "Fashion", "FMCG/Foods/Beverage", "Fertilizers/Pesticides", "Furniture", "Grocery", "Gas", "Government", "Government/Military", "Government & Public Sector", "Gems & Jewellery", "Health Care", "Human Resources", "Hospitality", "Hotels and Lodging", "HVAC", "Hardware", "Insurance", "Installation", "IT Services", "Industrial", "Internet Services", "Import - Export", "Legal", "Logistics", "Landscaping", "Leisure and Sport", "Library Science", "Marketing", "Manufacturing", "Management", "Merchandising", "Medical", "Media", "Metals", "Mining", "Military", "Mortgage", "Marine", "Maritime", "Nonprofit Charitable Organizations", "NGO/Social Services", "Newspaper", "Oil & Gas", "Other", "Other/Not Classified", "Pharma", "Polymer / Plastic / Rubber", "Pharma/Biotech/Clinical Research", "Public Sector and Government", "Printing/Packaging/Publishing", "Personal and Household Services", "Property & Real Estate", "Paper", "Pet Store", "Public Relations", "Real Estate", "Retail", "Retail & Wholesale", "Recreation", "Real Estate and Property", "Recruitment/Employment Firm", "Real Estate/Property Management", "Restaurant/Food Services", "Rental Services", "Research & Development", "Repair / Maintenance Services", "Services", "Sales - Marketing", "Science & Technology", "Security/Law Enforcement", "Shipping/Marine", "Security and Surveillance", "Sports and Physical Recreation", "Staffing/Employment Agencies", "Social Services", "Sports Leisure & Lifestyle", "Semiconductor", "Technology", "Services - Corporate B2B", "Travel", "Training", "Transportation", "Telecommunications", "Trade and Services", "Travel and Tourism", "Textiles/Garments/Accessories", "Tyres", "Utilities", "Wireless", "Wood / Fibre / Paper", "Waste Management", "Wholesale Trade/Import-Export"); return ResponseEntity.ok(countries); }



    @GetMapping("/work-experience")
    public ResponseEntity<List<WorkExperience>> getAllWorkExperiences() {
        return ResponseEntity.ok(
                Arrays.asList(
                        WorkExperience.values()
                ));
    }

    @GetMapping("/all-job-references")
//    public ResponseEntity<List<Source>> getAllJobRefSources() {
//        return ResponseEntity.ok(
//                Arrays.asList(
//                        Source.values()
//                ));
//    }
    public List<String> getAllSources() {
        // Return all Source names as a list in sentence case
        return Arrays.stream(Source.values())
                .map(Source::toSentenceCase)  // Convert to sentence case for human-readable format
                .collect(Collectors.toList());
    }


}