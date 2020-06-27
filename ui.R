## ui.R
# proteomics text mining app UI
#
# Kun-Hsing Yu
# July 22, 2017

library(shiny)
library(shinyBS)

shinyUI(fluidPage(
    navbarPage(
      theme="sandstone",
      "PURPOSE: Protein Universal Reference Publication-Originated Search Engine",
      tabPanel("Topic-based Analysis", 
               sidebarLayout(
                 sidebarPanel(
                  tags$head(
                    tags$style(type="text/css", "
                           #loadmessage {
                               position: fixed;
                               top: 0px;
                               left: 0px;
                               width: 100%;
                               padding: 5px 0px 5px 0px;
                               text-align: center;
                               font-weight: bold;
                               font-size: 100%;
                               color: #000000;
                               background-color: #CCFF66;
                               z-index: 10000;
                               }
                               "),
                    tags$style(type="text/css", "select { max-width: 340px; }"),
                    tags$style(type="text/css", ".span4 { max-width: 390px; }"),
                    tags$style(type="text/css", ".well { max-width: 380px; }"),
                    tags$style(type="text/css", ".well { min-width: 340px; }")
                  ),
                  conditionalPanel(condition="$('html').hasClass('shiny-busy')",
                                   tags$div(progressMsg,id="loadmessage")),
                  br(),
                  selectInput("queryType", "Step 1: Select a query type:",
                              c("HPP Targeted Area" = "hpp",
                                "Human Diseases (PheWAS codes)" = "diseases",
                                "Organ Systems" = "organ",
                                "Custom Search" = "custom")),
                  bsTooltip("queryType", "Select categories of terms or perform a customized search", trigger = "hover", options = NULL),
                  uiOutput("selectionORInput"),
                  bsTooltip("query", "Select or enter a search term", trigger = "hover", options = NULL),
                  br(),
                  selectInput("species", "Step 3: Choose a species:",
                              c("Human (Homo sapiens)" = "human",
                                "Mouse (Mus musculus)" = "mouse",
                                "Rat (Rattus norvegicus)" = "rat",
                                "Fly (Drosophila melanogaster)" = "fly",
                                "Worm (Caenorhabditis elegans)" = "worm",
                                "Yeast (Saccharomyces cerevisiae)" = "yeast")),
                  bsTooltip("species", "Filter the serarch results by species", trigger = "hover", options = NULL),
                  strong("Step 4 (Optional):"),br(),
                  checkboxInput("excludeBlacklisted", "Exclude blacklisted proteins*", TRUE),
                  bsTooltip("excludeBlacklisted", "Exclude proteins that are known to be irrelevant to the query term", trigger = "hover", options = NULL),
                  h6("* Blacklisted proteins: proteins that are known to be irrelevant to the query term"),
                  #checkboxInput("citation", "Weighted by the number of citations", TRUE),
                  #bsTooltip("citation", "Weighted the co-publication score by the number of citation", trigger = "hover", options = NULL),
                  br(),
                  strong("Step 5: "),br(),
                  actionButton("run", "Run!", width = "100%"),
                  bsTooltip("run", "Run the analysis", trigger = "hover", options = NULL)
                ),
                mainPanel(
                  tabsetPanel(
                    tabPanel("Prioritized List", 
                             uiOutput("summary"),
                             dataTableOutput("table"),
                             uiOutput("queryDownloadUI"),
                             uiOutput("queryTailUI")),
                    tabPanel("Publication Score Distribution", uiOutput("viewScoreDistribution")),
                    tabPanel("Publication/Citation Numbers", uiOutput("viewPubCit")),
                    tabPanel("DAVID Annotations", uiOutput("viewDAVID")),
                    tabPanel("PPIs", uiOutput("viewPPIs")),
                    tabPanel("View Publications", uiOutput("viewPublications"))
                  )
                )
                )
              ),
      tabPanel("Protein-based Analysis", 
               sidebarLayout(
                 sidebarPanel(
                   tags$head(
                     tags$style(type="text/css", "select { max-width: 340px; }"),
                     tags$style(type="text/css", ".span4 { max-width: 390px; }"),
                     tags$style(type="text/css", ".well { max-width: 380px; }"),
                     tags$style(type="text/css", ".well { min-width: 340px; }")
                   ),
                  br(),
                  selectInput("speciesP", "Step 1: Choose a species:",
                              c("Human (Homo sapiens)" = "human",
                                "Mouse (Mus musculus)" = "mouse",
                                "Rat (Rattus norvegicus)" = "rat",
                                "Fly (Drosophila melanogaster)" = "fly",
                                "Worm (Caenorhabditis elegans)" = "worm",
                                "Yeast (Saccharomyces cerevisiae)" = "yeast")),
                  bsTooltip("speciesP", "Filter the search results by species", trigger = "hover", options = NULL),
                  strong("Step 2: Enter protein name:"),
                  uiOutput("selectionORInputP"),
                  #textInput("queryP", "Protein Query:", "", width = "100%"),
                  #bsTooltip("queryP", "Search the biomedical literature by the input protein name", trigger = "hover", options = NULL),
                  #actionLink("example1", "Example 1"),
                  #actionLink("example2", "Example 2"),
                  br(),
                  selectInput("queryTypeP", "Step 3: Compare within the following categories:",
                              c("22 B/D HPP" = "hpp",
                                "Organ systems" = "humanSystems")),
                  bsTooltip("queryTypeP", "Compare the protein score among the selected categories. 22 B/D HPP = the 22 targeted biology/disease for Human Proteome Project. Organ systems = the known organ systems of organisms.", trigger = "hover", options = NULL),
                  br(),
                  #checkboxInput("excludeBlacklistedP", "Exclude blacklisted proteins", TRUE),
                  #bsTooltip("excludeBlacklistedP", "Exclude proteins that are known to be not related to the query term", trigger = "hover", options = NULL),
                  #checkboxInput("citationP", "Weighted by the number of citations", TRUE),
                  strong("Step 4: "),br(),
                  actionButton("runP", "Run!", width = "100%"),
                  bsTooltip("runP", "Run the analysis", trigger = "hover", options = NULL)
             ),
             mainPanel(
               tabsetPanel(
                 tabPanel("Prioritized List", 
                          uiOutput("summaryP"),
                          dataTableOutput("tableP"),
                          uiOutput("queryOutputUIP")),
                 tabPanel("Score Distribution", uiOutput("viewScoreDistributionP")),
                 tabPanel("View Annotations", uiOutput("viewAnnotationsP")),
                 tabPanel("PPIs", uiOutput("viewPPIsP")),
                 tabPanel("View Publications", uiOutput("viewPublicationsP"))
               )
             )
           )
           ),
      tabPanel("About",
               h4("Summary of Methods:"),
               p("Proteins are prioritized by the protein publication score, defined as"),
               code("(1 + log10(nTP) + log10((Sum(Cit/yr)+1)/10)) * (1 + log10(nU/nT) + log10(nU/nP))"),
               p("where nTP is the number of PubMed publication related to both the protein and topic (TP), Cit/yr is the sum of the annualized number of citation of TP, nU is the number of PubMed publication, nT is the number of publication regarding the topic of interest, and nP is the number of publication regarding the protein of interest."),
               p("The number of PubMed publications and citations are retrieved by eSummary tools, and the platform is implemented in Java and R."),br(),
               h4("Citation:"),
               p("Kun-Hsing Yu, Tsung-Lu Michael Lee, Chi-Shiang Wang, Yu-Ju Chen, Christopher Ré, S. C. Kou, Jung-Hsien Chiang, Isaac S. Kohane, Michael Snyder. Systematic Proteins Prioritization in Organ Systems and Diseases through Literature Mining. Journal of Proteome Research. 2018 Apr 6;17(4):1383-1396. doi: 10.1021/acs.jproteome.7b00772. [Epub ahead of print]"),br(),
               h4("Funding:"),
               p("K.-H. Y. is a Harvard Data Science Fellow. This work was supported in part by grants from National Human Genome Research Institute, National Institutes of Health, grant number 5P50HG007735, National Cancer Institute, National Institutes of Health, grant number 5U24CA160036, the Defense Advanced Research Projects Agency (DARPA) Simplifying Complexity in Scientific Discovery (SIMPLEX) grant number N66001-15-C-4043 and the Data-Driven Discovery of Models contract number FA8750-17-2-0095, and the Ministry of Science and Technology Research Grant, Taiwan, grant number MOST 103-2221-E-006-254-MY2."),br(),
               h4("Acknowledgments:"),
               p("We express appreciation to Professor Jochen Schwenk for his feedback on protein prioritization, Professor Griffin Weber for his insight into citation counts, Mr. Alex Ratner, Dr. Jared Dunnmon, Ms. Paroma Varma, Mr. Chen-Rui Liu, and Dr. Stephen Bach for their valuable advice on literature mining and suggestions on the manuscript, and Dr. Mu-Hung Tsai for pointing out the literature mining resources. We thank the anonymous reviewers for their insightful feedback. We thank the AWS Cloud Credits for Research, Microsoft Azure Research Award, and the NVIDIA GPU Grant Program for their support on the computational infrastructure.")
      )
    )
  )
)

