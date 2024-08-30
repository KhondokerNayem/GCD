# GCD
Title: Leveraging Software Metrics for Effective God Class Detection in Java Systems
Project Overview
This thesis project focuses on the detection of God Classes in Java systems using both metric-based and manual validation approaches. God Classes are classes that centralize too much functionality, making them overly complex and difficult to maintain. Detecting and refactoring these classes is crucial for improving software quality, maintainability, and minimizing the risk of defects.

Introduction
God Classes in Java systems present a significant challenge due to their complexity and centralization of multiple functionalities. This project aims to address this issue by developing a methodology for detecting God Classes using a combination of metrics such as Access to Foreign Data (ATFD), Weighted Method Count (WMC), Tight Class Cohesion (TCC), Lines of Code (LOC), and a newly introduced metric, Average Lines of Code per Method (AvgLOC). The effectiveness of this approach is evaluated by comparing the results with manual validation across multiple Java projects.

Methodology
The project uses two primary approaches to identify God Classes:

Metric-based Detection:

Metric Selection: The project utilizes five key metrics (ATFD, WMC, TCC, LOC, and AvgLOC) to detect God Classes. These metrics are calculated for each class within the selected Java projects.
Threshold Definition: Threshold values are defined for the metrics to determine whether a class is classified as a God Class. Five conditions are established, each with different ATFD thresholds while keeping WMC, TCC, and LOC thresholds constant.
Implementation: Java code is developed to calculate the metrics for each class in the project dataset. The code evaluates each class against the defined conditions to classify it as a God Class if it meets the criteria.
Manual Validation:

Validation Process: Manual validation is performed by evaluating the Number of Methods (NOM) and Number of Fields (NOF) for each class. Classes with NOM and NOF exceeding 20 are manually classified as God Classes. The validation process is cross-checked by multiple authors to ensure accuracy.
Results and Analysis
The project demonstrated significant improvements in detecting God Classes across all conditions compared to previous studies. Condition IV achieved the highest average accuracy of 87.97%, while Condition I demonstrated the best overall performance with the highest F1 score of 31.68%. The results provide empirical support for the effectiveness of using calibrated metrics for God Class detection in real-world Java projects.

Conclusion
This thesis successfully leverages existing God Class metrics and introduces a new metric (AvgLOC) to enhance the detection process. The proposed methodology outperforms other approaches, providing practitioners with a more effective tool for improving code quality. Future work will focus on automating the detection process to further improve accuracy, reliability, and ease of use.

Project Structure
Source Code: The Java code developed for metric calculation and God Class detection.
Data: Includes the dataset of Java projects used for analysis.
Documentation: Detailed documentation of the methodology, implementation, and results.
Thesis Paper: The complete thesis paper detailing the research, methodology, results, and conclusion.
Authors
Indrajit Chakraborty
Ahsanullah University of Science and Technology, Dhaka, Bangladesh
Email: indrajit.cse.200104135@aust.edu
ORCID: 0000-0002-0931-1919

Khondoker Jubayer Ahmed Nayem
Ahsanullah University of Science and Technology, Dhaka, Bangladesh
Email: jubayer.cse.200104137@aust.edu

Sabbir Ahmed Salman
Ahsanullah University of Science and Technology, Dhaka, Bangladesh
Email: sabbir.cse.200104146@aust.edu

Tanvir Rahman
Ahsanullah University of Science and Technology, Dhaka, Bangladesh
Email: tanvir.cse.200104136@aust.edu

Md. Masudur Rahman
Ahsanullah University of Science and Technology, Dhaka, Bangladesh
Email: masudur.rahman0413.cse@aust.edu

Acknowledgements
We would like to thank our supervisors, peers, and the Ahsanullah University of Science and Technology for their continuous support and guidance throughout this project.
