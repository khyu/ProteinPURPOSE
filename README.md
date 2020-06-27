# README #

### Summary ###
* App Name: Protein Universal Reference Publication-Originated Search Engine (PURPOSE): A cloud-based protein prioritization tool.
* Version: 1.0.0


### Dependencies ###
* R version 3 or above
* R packages: shiny, shinyjs, shinyBS, shinysky, V8, ggplot2, plotly.


### Summary of Methods ###
* Proteins are prioritized by the Protein Universal Reference Publication-Originated Search Engine (PURPOSE), defined as
(1 + log10(nTP) + log10((Sum(Cit/yr)+1)/10)) * (1 + log10(nU/nT) + log10(nU/nP)),
where nTP is the number of PubMed publication related to both the protein and topic (TP), Cit/yr is the sum of the annualized number of citation of TP, nU is the number of PubMed publication, nT is the number of publication regarding the topic of interest, and nP is the number of publication regarding the protein of interest.

* The number of PubMed publications and citations are retrieved by eSummary tools, and the platform is implemented in Java and R.


### The Running Instance ###
* http://rebrand.ly/proteinpurpose


### Citation ###
* Kun-Hsing Yu, Tsung-Lu Michael Lee, Chi-Shiang Wang, Yu-Ju Chen, Christopher Ré, S. C. Kou, Jung-Hsien Chiang, Isaac S. Kohane, Michael Snyder. Systematic Proteins Prioritization in Organ Systems and Diseases through Literature Mining. Journal of Proteome Research. 2018 Mar 5. doi: 10.1021/acs.jproteome.7b00772. [Epub ahead of print]


### Funding ###
* K.-H. Y. is a Harvard Data Science Fellow. This work was supported in part by grants from National Human Genome Research Institute, National Institutes of Health, grant number 5P50HG007735, National Cancer Institute, National Institutes of Health, grant number 5U24CA160036, the Defense Advanced Research Projects Agency (DARPA) Simplifying Complexity in Scientific Discovery (SIMPLEX) grant number N66001-15-C-4043 and the Data-Driven Discovery of Models contract number FA8750-17-2-0095, and the Ministry of Science and Technology Research Grant, Taiwan, grant number MOST 103-2221-E-006-254-MY2.


### Acknowledgments ###
* We express appreciation to Professor Jochen Schwenk for his feedback on protein prioritization, Professor Griffin Weber for his insight into citation counts, Mr. Alex Ratner, Dr. Jared Dunnmon, Ms. Paroma Varma, Mr. Chen-Rui Liu, and Dr. Stephen Bach for their valuable advice on literature mining and suggestions on the manuscript, and Dr. Mu-Hung Tsai for pointing out the literature mining resources. We thank the anonymous reviewers for their insightful feedback. We thank the AWS Cloud Credits for Research, Microsoft Azure Research Award, and the NVIDIA GPU Grant Program for their support on the computational infrastructure.


### Contact ###
* Kun-Hsing Yu (Kun [hyphen] Hsing [underscore] Yu [at] hms dot harvard dot edu)
