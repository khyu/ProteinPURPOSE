## server.R
# proteomics text mining app server file
#
# Kun-Hsing Yu
# July 22, 2017

library(shinysky)
library(shinyjs)
library(V8)
library(ggplot2)
library(plotly)
library(plyr)
options(shiny.reactlog=TRUE, shiny.sanitize.errors = FALSE)


#phewas_codes <- read.csv("data/PheWAS_code_translation_v1_2.txt", header = T, stringsAsFactors = F, sep="\t")
disease_autocomplete_list <- phewas_codes[,2]

#protein_autocomplete_list <- human_protein_names_mapping[,1]

jsOpenURLCode <- "shinyjs.openURL = function(param) {window.open (param);}"

createLink <- function(type,protein,species) {
  if (type=="nextprot") {
    sprintf('<a href="https://www.nextprot.org/entry/%s" target="_blank" class="btn btn-primary">neXtProt</a>',protein)
  } else if (type=="uniprot") {
    sprintf('<a href="http://www.uniprot.org/uniprot/?query=%s&sort=score" target="_blank" class="btn btn-primary">Uniprot</a>',protein)
  } else if (type=="genecards") {
    sprintf('<a href="http://www.genecards.org/Search/Keyword?queryString=%s" target="_blank" class="btn btn-primary">Genecards</a>',protein)
  } else if (type=="peptideAtlas") {
    sprintf('<a href="https://db.systemsbiology.net/sbeams/cgi/PeptideAtlas/Search?search_key=%s&build_type_name=%s&action=GO" target="_blank" class="btn btn-primary">Peptide Atlas</a>',protein,species)
  } else if (type=="NCBIProtein") {
    sprintf('<a href="https://www.ncbi.nlm.nih.gov/protein/?term=%s" target="_blank" class="btn btn-primary">NCBI Protein</a>',protein)
  } else if (type=="phosphosite") {
    sprintf('<a href="http://www.phosphosite.org/simpleSearchSubmitAction.action?searchStr=%s" target="_blank" class="btn btn-primary">Phosphosite</a>',protein)
  } else if (type=="PubMed") {
    sprintf('<a href="https://www.ncbi.nlm.nih.gov/pubmed/?term=%s" target="_blank" class="btn btn-primary">%s</a>',protein,protein)
  }
}


aggVotes <- function (geneIds, votes, query, species) {
  return(sum(votes[votes[,1]==query & votes[,2]==species & votes[,3]==geneIds,4]))
}

countVotes <- function (outputTableFileVotes, query, species){
  print("votes")
  votes<<-read.table("data/votes.txt",header=F,stringsAsFactors=F,sep="\t")
  outputTableFileVotes[,dim(outputTableFileVotes)[2]]<-sapply(outputTableFileVotes[,3],aggVotes,votes,query,species)
  print(outputTableFileVotes[1,])
  return(outputTableFileVotes)
}



server <- function(input, output, session) {
  output$selectionORInput = renderUI(
    if (is.null(input$queryType)) {
      return()
    } else if (input$queryType=="hpp") {
      selectInput("query", "Step 2: Select an HPP Targeted Area:",
                  c("Brain" = "brain",
                    "Cancer" = "cancer",
                    "Cardiovascular" = "cardiovascular",
                    "Diabetes" = "diabetes",
                    "Extreme conditions" = "hot+OR+cold+OR+alkaline+condition+OR+acidic+condition+OR+hypersaline+OR+radiation",
                    "EyeOME" = "eye+OR+ocular",
                    "Food and nutrition" = "food+OR+nutrition+OR+nutrients",
                    "Glycoproteomics" = "glycoproteins",
                    "Immune-peptidome" = "immune+OR+immune+system",
                    "Infectious diseases" = "infectious+OR+infection",
                    "Kidney and urine" = "kidney+OR+urine",
                    "Liver" = "liver+OR+hepatic",
                    "Mitochondria" = "mitochondria",
                    "Model organisms" = "rat+OR+mouse",
                    "Musculoskeletal" = "muscle+OR+bone+OR+musculoskeletal",
                    "Pathology" = "pathology",
                    "PediOme" = "pediatric+OR+newborn+OR+infant+OR+toddler+OR+child+OR+adolescent",
                    "Plasma" = "plasma+OR+serum",
                    "Protein aggregation" = "protein+aggregation+NOT+platelet+aggregation",
                    "Rheumatic disorders" = "rheumatic",
                    "Stem cells" = "stem+cells",
                    "Toxicoproteomics"="toxicology+OR+toxic+OR+toxin"),selected = NULL)
                  #c("Brain","Cancer","Cardiovascular","Diabetes","Epigenetic chromatin",
                  #  "Extreme conditions","Eye","Food and Nutrition","Glycoproteomics",
                  #  "Immune","Infectious diseases","Kidney and Urine",
                  #  "Liver","Mitochondria","Model organisms","Musculoskeletal",
                  #  "PediOme","Plasma","Protein aggregation","Respiratory","Stem cells",
                  #  "Toxicoproteomics"),selected = NULL)
    } else if (input$queryType=="diseases") {
      h3("Disease (PheWAS Code):")
      textInput.typeahead(id="query",
                          #width = '100%',
                          placeholder="Step 2: Enter disease here",
                          local=data.frame(name=c(disease_autocomplete_list)),
                          valueKey = "name",
                          tokens=c(1:length(disease_autocomplete_list)),
                          template = HTML("<p class='repo-language'>{{info}}</p> <p class='repo-name'>{{name}}</p>")
      )
    } else if (input$queryType=="organ") {
      selectInput("query", "Step 2: Select an organ system:",
                  c("Cardiovascular / Circulatory system" = "cardiovascular+system+OR+circulatory+system",
                    "Digestive system / Excretory system"="digestive+system+OR+excretory+system",
                    "Endocrine system"="endocrine+system",
                    "Integumentary system"="integumentary+system", 
                    "Lymphatic system / Immune system"="lymphatic+system+OR+immune+system", 
                    "Muscular system"="muscular+system", 
                    "Nervous system"="nervous+system",
                    "Renal system / Urinary system"="renal+system+OR+urinary+system", 
                    "Reproductive system"="reproductive+system", 
                    "Respiratory system"="respiratory+system", 
                    "Skeletal system"="skeletal+system"
                  ),selected = NULL)
    } else if (input$queryType=="custom") {
      textInput("query", "Step 2: Input custom topic query below:", "", width = "100%")
    }
  )
  output$selectionORInputP = renderUI({
    if (is.null(input$speciesP)) {
      return()
    } else {
      if (isolate(input$speciesP)=="human") {
        protein_autocomplete_list<-human_protein_names_mapping[,1]
      } else if (isolate(input$speciesP)=="mouse") {
        protein_autocomplete_list<-mouse_protein_names_mapping[,1]
      } else if (isolate(input$speciesP)=="rat") {
        protein_autocomplete_list<-rat_protein_names_mapping[,1]
      } else if (isolate(input$speciesP)=="fly") {
        protein_autocomplete_list<-fly_protein_names_mapping[,1]
      } else if (isolate(input$speciesP)=="worm") {
        protein_autocomplete_list<-worm_protein_names_mapping[,1]
      } else if (isolate(input$speciesP)=="yeast") {
        protein_autocomplete_list<-yeast_protein_names_mapping[,1]
      }
      textInput.typeahead(id="queryP",
                          #width = '100%',
                          placeholder="Enter protein name here",
                          local=data.frame(name=c(protein_autocomplete_list)),
                          valueKey = "name",
                          tokens=c(1:length(protein_autocomplete_list)),
                          template = HTML("<p class='repo-language'>{{info}}</p> <p class='repo-name'>{{name}}</p>")
      )
    }
  })
  renderOutputTable<-function(outputTableFile){
    #print(outputTableFile[1,])
    #outputTableFile[,2]<-substr(outputTableFile[,2],1,6)
    #outputTable<-outputTableFile[,c(1,7,8,9,3,4,5,6)]
    if (input$species == "human"){
      outputTableFile[,2]<-join(outputTableFile[,2:3], humanNextProt, by = "GeneID", match="first")[,3]
    }
    outputTable<<-outputTableFile[,c(1,4,5,6,7,8,12,2)]
    #print(outputTable[1,])
    colnames(protein_names_mappingSelected)[1]<-"Gene_Name"
    outputTableVotes<<-join(outputTable, protein_names_mappingSelected, by = "Gene_Name")
    colnames(outputTableVotes)<<-c("Rank","Symbol","Score","#P","#TP","#Citation/yr","Votes","UniProtKB_ID","Protein Name")
    print(outputTableVotes[1:2,])
    output$table <- renderDataTable({
      print(outputTableVotes[1:2,])
      if (speciesID=="9606"){
        outputTableVotes$neXtProt <- createLink("nextprot", outputTableVotes$UniProtKB_ID, input$species)
      } else {
        outputTableVotes$uniprot <- createLink("uniprot", outputTableVotes$UniProtKB_ID, input$species)
      }
      outputTableShow<-outputTableVotes[,c(1:2,9,3:6,10,7)]
      outputTableShow[["Actions"]]<-
        paste0('<div class="btn-group" role="group" aria-label="votes">
               <button type="button" class="btn btn-secondary upvote" id=upvote_',1:nrow(outputTableShow),'>Upvote</button>
               <button type="button" class="btn btn-secondary downvote"id=downvote_',1:nrow(outputTableShow),'>Downvote</button></div>')
      return(outputTableShow)}, options=list(rownames = FALSE), escape = FALSE)
  }
  #output$viewPublicationsTable<-renderDataTable({outputTableFile[outputTableFile[,4]==input$viewPublicationsProtein,]})
  output$viewPublicationsTable<-renderDataTable({
    viewPublicationsTop100Show[viewPublicationsTop100Show[,2]==input$viewPublicationsProtein,c(8,3:7)]
    }, escape=FALSE)
  output$downloadPublicationData <- downloadHandler(
    filename = function() { paste(queryTerm, outputTableFile[outputTableFile[,3]==input$viewPublicationsProtein,4],'Publications.csv', sep='_') },
    content = function(file) {
      write.csv(viewPublicationsTop100Show[viewPublicationsTop100Show[,2]==input$viewPublicationsProtein,c(1,3:7)], file, row.names=F)
    }
  )
  output$viewPublicationsTableP<-renderDataTable({
    viewPublicationsTop100PShow[viewPublicationsTop100PShow[,1]==input$viewPublicationsTopic,c(9,4:8)]
    }, options=list(rownames=FALSE), escape=FALSE)
  output$downloadPublicationDataP <- downloadHandler(
    filename = function() { paste(input$queryP, input$viewPublicationsTopic,'Publications.csv', sep='_') },
    content = function(file) {
      write.csv(viewPublicationsTop100PShow[viewPublicationsTop100PShow[,1]==input$viewPublicationsTopic,c(2,4:8)], file, row.names=F)
    }
  )
  observeEvent(input$runPubCit, {
    print(input$pubcitN)
    pubcitPlotTable<-outputTableFile[input$pubcitN[1]:input$pubcitN[2],]
    print(pubcitPlotTable[1:2,])
    yAxis <- list(
      title = "log10(value)"
    )
    pubcitPlotObject<-plot_ly(pubcitPlotTable, x=~Rank, y=~log10(Big_P), name="#P", type="scatter", mode="markers", text=~Gene_Name) %>%
      add_trace(y=~log10(T.P), name="#TP", mode="markers") %>%
      add_trace(y=~log10(Citation), name="#Citations/year", mode="markers") %>%
      layout(yaxis = yAxis)
    output$pubcitPlot <- renderPlotly({ 
      pubcitPlotObject
    })
  })
  observeEvent(input$runDAVID, {
    print(input$davidN)
    urlPart<-outputTableFile[1,3]
    if (input$davidN>1){
      for (i in 2:input$davidN){
        urlPart<-paste(urlPart,outputTableFile[i,3],sep=",")
      }
    }
    js$openURL(paste("http://david.abcc.ncifcrf.gov/api.jsp?type=ENTREZ_GENE_ID&ids=",urlPart,"&tool=summary",sep=""))
    #browseURL(paste("http://david.abcc.ncifcrf.gov/api.jsp?type=ENTREZ_GENE_ID&ids=",urlPart,"&tool=summary",sep=""))
  })
  observeEvent(input$runPPI, {
    print(input$ppiN)
    urlPart<-outputTableFile[1,4]
    if (input$ppiN==1){
      js$openURL(paste("http://string-db.org/newstring_cgi/show_network_section.pl?identifier=",urlPart,"&species=",speciesID,sep=""))
      #browseURL(paste("http://string-db.org/newstring_cgi/show_network_section.pl?identifier=",urlPart,"&species=9606",sep=""))
    } else {
      for (i in 2:input$ppiN){
        urlPart<-paste(urlPart,outputTableFile[i,4],sep="%0D")
      }
      js$openURL(paste("http://string-db.org/newstring_cgi/show_network_section.pl?identifiers=",urlPart,"&species=",speciesID,sep=""))
      #browseURL(paste("http://string-db.org/newstring_cgi/show_network_section.pl?identifiers=",urlPart,"&species=9606",sep=""))
    }
  })
  observeEvent(input$lastClick,{
    print ("XXX")
    print(outputTableFileVotes[1:2,])
    if (substr(input$lastClickId,1,6)=="upvote") {
      row=as.numeric(gsub("upvote_","",input$lastClickId))
      write.table(t(c(queryTerm,input$species,outputTableFile[outputTableFile[,4]==outputTableVotes[row,2],3],"1",as.character(Sys.time()))),sep="\t",append=T,file="data/votes.txt",quote=F,row.names=F,col.names=F)
    } else if (substr(input$lastClickId,1,6)=="downvo") {
      row=as.numeric(gsub("downvote_","",input$lastClickId))
      write.table(t(c(queryTerm,input$species,outputTableFile[outputTableFile[,4]==outputTableVotes[row,2],3],"-1",as.character(Sys.time()))),sep="\t",append=T,file="data/votes.txt",quote=F,row.names=F,col.names=F)
    }
    outputTableFileVotes<<-countVotes(outputTableFileVotes, queryTerm, isolate(input$species))
    renderOutputTable(outputTableFileVotes)
  })
  observeEvent(input$runPPIP, {
    js$openURL(paste("http://string-db.org/newstring_cgi/show_network_section.pl?identifier=",input$queryP,"&species=",speciesID,sep=""))
    #browseURL(paste("http://string-db.org/newstring_cgi/show_network_section.pl?identifier=",input$queryP,"&species=9606",sep=""))
  })
  observeEvent(input$run, {
    queryTerm<<-tolower(isolate(input$query))
    queryTerm<-gsub(" ", "+", queryTerm)
    queryTerm<-gsub("\"", "", queryTerm)
    queryTerm<-gsub("\'", "", queryTerm)
    queryTerm<-gsub("“", "", queryTerm)
    queryTerm<-gsub("”", "", queryTerm)
    dirName<-tolower(paste(queryTerm,"+",isolate(input$species),sep=""))
    print (dirName)
    outputTableFilename<-paste("PPS_Results/",isolate(input$species),"/",queryTerm,"+",isolate(input$species),"_PPS",sep="")
    
    if (!file.exists(outputTableFilename)){
      # run query
      print("running query")
      print(paste("java -Xmx2048m JProteomeText_gene2pubtator",queryTerm, isolate(input$species), sep=" "))
      #progressMsg<<-""
      #logFile<-reactiveFileReader(500,session,filePath=paste("progressLog/",dirName,".txt",sep=""),readLines)
      #progressMsg<<-"Running query... It might take a few seconds..."
      #print(progressMsg)
      system(paste("java -Xmx2048m JProteomeText_gene2pubtator ", queryTerm," ", isolate(input$species)," | tee progressLog/",dirName,".txt", sep=""))
    }
    print("Completed running the java code")
    print(outputTableFilename)
    if (file.exists(outputTableFilename)) {
      outputTableSummary<<-read.table(outputTableFilename,stringsAsFactors=F,sep="\t",nrows=2)
      bigT<<-outputTableSummary[1,2]
      bigF<<-outputTableSummary[2,2]
      outputTableFile<<-read.table(outputTableFilename,header=T,comment.char="",stringsAsFactors=F,sep="\t",fill=T,quote="",skip=2)
      # remove blacklisted proteins
      if (input$excludeBlacklisted == T){
        blacklistThisQuery<-blacklist[(blacklist[,1] == queryTerm & blacklist[,2] == isolate(input$species)),]
        outputTableFile<<-outputTableFile[!outputTableFile[,3] %in% blacklistThisQuery[,3],]
      }
      outputTableFile<<-cbind((1:dim(outputTableFile)[1]),outputTableFile)
      colnames(outputTableFile)[1]<<-"Rank"
      # count votes
      outputTableFileVotes<<-cbind(outputTableFile,rep(0,dim(outputTableFile)[1]))
      colnames(outputTableFileVotes)[dim(outputTableFileVotes)[2]]<-"Votes"
      outputTableFileVotes<-countVotes(outputTableFileVotes, queryTerm, isolate(input$species))
      print("run...")
      print(outputTableFileVotes[1,])
      if (isolate(input$species)=="human") {
        protein_names_mappingSelected<<-human_protein_names_mapping
        speciesID<<-"9606"
      } else if (isolate(input$species)=="mouse") {
        protein_names_mappingSelected<<-mouse_protein_names_mapping
        speciesID<<-"10090"
      } else if (isolate(input$species)=="rat") {
        protein_names_mappingSelected<<-rat_protein_names_mapping
        speciesID<<-"10116"
      } else if (isolate(input$species)=="fly") {
        protein_names_mappingSelected<<-fly_protein_names_mapping
        speciesID<<-"7227"
      } else if (isolate(input$species)=="worm") {
        protein_names_mappingSelected<<-worm_protein_names_mapping
        speciesID<<-"6239"
      } else if (isolate(input$species)=="yeast") {
        protein_names_mappingSelected<<-yeast_protein_names_mapping
        speciesID<<-"4932"
      }
      output$queryValue <- renderText({ paste("Query term: ",queryTerm,"\n",
                                              "Species: ",isolate(input$species),"\n",
                                              "Number of publications on ",queryTerm," in ",isolate(input$species),": ",dim(outputTableFile)[1],"\n",sep="") })
      output$summary <- renderUI({ 
        tagList(
          strong("Query term: "),code(queryTerm),br(),
          strong("Species: "),code(isolate(input$species)),br(),
          #em("***BELOW CONTAINS PLACEHOLDERS***"),br(),code(sample(1:10000,1)),
          strong("Number of publications associated with the query:"),code(bigT),br(),
          strong("Number of publications in the PubMed database associated with any proteins:"),code(bigF),br(),
          strong("Number of proteins associated with the retrieved publications:"),code(dim(outputTableFile)[1]),br(),
          #strong("Number of retrieved publications associated with proteins:"),code(outputTableFile[1,9]),br(),
          #strong("Number of average citations per year of the retrieved publications associated with proteins:"),code(sample(1:10000,1)),br(),
          #strong("Number of publications in the PubMed database:"),code(sample(1:10000,1)),br(),
          br(),br(),br(),h3("Prioritized List",align="center"),
          h6("Descriptions:"),
          h6("#T = the number of paper on this topic; #TP = the number of paper on this topic and this protein; #Citation/yr = the total number of citations per year"),
          h6("Votes: user voting on the relevance of the protein to the queried topic. Please note that infrequently accessed topics may have smaller numbers of votes.")
        )
      })
      print(outputTableFile[1:2,])
      output$scoreScatterPlot <- renderPlotly({ 
        yAxis <- list(
          title = "log10(Publication Score)"
        )
        plot_ly(outputTableFile, x=~Rank, y=~log10(PPS), text=~Gene_Name) %>%
          layout(yaxis = yAxis)
      })
      output$viewScoreDistribution <- renderUI({ 
        tagList(
          h3("View the Distribution of Publication Scores",align = "center"),
          h5("(it may take a few seconds to load the whole figure)",align="center"),
          plotlyOutput("scoreScatterPlot")
        )
      })
      proteinMin<-min(1,dim(outputTableFile)[1])
      proteinMax<-min(1000,dim(outputTableFile)[1])
      proteinDefault<-min(20,dim(outputTableFile)[1])
      output$viewPubCit <- renderUI({ 
        tagList(
          h3("View Publication/Citation Numbers of the Top-ranking Proteins",align = "center"),br(),
          p("This module visualized the number of publication and the number of citations per year for each of the retrieved proteins."),
          sliderInput("pubcitN", "View proteins whose publication score ranking are at the following range:", min=proteinMin, max=proteinMax, step=1, value=c(proteinMin,proteinDefault), width='100%'),br(),
          #sliderInput("pubcitN", "N=", min=1, max=dim(outputTableFile)[1], value=c(1,20)),br(),
          actionButton("runPubCit", "Run", width = "100%"),
          plotlyOutput("pubcitPlot"),
          h6("Legend: #T = the number of paper on this topic; #TP = the number of paper on this topic and this protein; #Citations/year = the total number of citations per year")
        )
      })
      output$viewDAVID <- renderUI({ 
        tagList(
          h3("View the DAVID Functional Annotation Summary of the Top N Proteins",align = "center"),br(),
          p("This module conducts enrichment analysis using the Database for Annotation, Visualization and Integrated Discovery (DAVID)."),
          p("The result page will be shown in a new tab."),
          em("Please check the pop-up blocker of your browser if the results do not show up after clicking 'Run DAVID.'"),
          sliderInput("davidN", "N=", min=proteinMin, max=proteinMax, step=1, value=proteinDefault, width='100%'),br(),
          useShinyjs(),
          extendShinyjs(text = jsOpenURLCode),
          actionButton("runDAVID", "Run DAVID", width = "100%")
        )
      })
      output$viewPPIs <- renderUI({ 
        tagList(
          h3("View Protein-Protein Interactions (PPI) among the Top N Proteins",align = "center"),br(),
          p("This module conducts PPI analysis using the STRING Database."),
          p("The result page will be shown in a new tab."),
          em("Please check the pop-up blocker of your browser if the results do not show up after clicking 'View PPI.'"),
          sliderInput("ppiN", "N=", min=proteinMin, max=proteinMax, step=1, value=proteinDefault, width='100%'),br(),
          useShinyjs(),
          extendShinyjs(text = jsOpenURLCode),
          actionButton("runPPI", "View PPI", width = "100%")
        )
      })
      viewPublicationsTop100FileFlag<-0
      viewPublicationsTop100Filename<-paste("PPS_Results/",isolate(input$species),"/",dirName,"_TOP100",sep="")
      if (file.exists(viewPublicationsTop100Filename)) {
        fileInfo <- file.info(viewPublicationsTop100Filename)
        if (fileInfo$size>0) {
          viewPublicationsTop100FileFlag<-1
          viewPublicationsListProteinID<-outputTableFile[1:100,3]
          names(viewPublicationsListProteinID)<-outputTableFile[1:100,4]
          viewPublicationsTop100<-read.table(viewPublicationsTop100Filename,header=F,comment.char="",stringsAsFactors=F,sep="\t",fill=T,quote="")
          viewPublicationsTop100<-viewPublicationsTop100[order(viewPublicationsTop100[,7],decreasing=T),]
          viewPublicationsTop100[,3]<-paste(viewPublicationsTop100[,3]," et al.",sep="")
          colnames(viewPublicationsTop100)<-c("PMID","ProteinID","Authors","Title","Journal","Year","Total Citations")
          viewPublicationsTop100$PMIDLink<-createLink("PubMed", viewPublicationsTop100$PMID, "")
          viewPublicationsTop100Show<<-viewPublicationsTop100
          output$viewPublications <- renderUI({ 
            tagList(
              h3("View Publications of the Top Proteins",align = "center"),br(),
              p("This module shows the highly-cited (>= 5 citations per year) publications for each of the top topics, sorted by the number of citations."),
              em("The table would be empty if there is no highly-cited publication matching the protein and the topic."),
              #em("***PLACEHOLDERS BELOW***"),br(),
              selectInput("viewPublicationsProtein", "Choose a protein:",
                          viewPublicationsListProteinID),
              dataTableOutput("viewPublicationsTable"),
              downloadButton('downloadPublicationData', 'Download')
            )
          })
        }
      }
      if (viewPublicationsTop100FileFlag==0) {
        output$viewPublications <- renderUI({ 
          tagList(
            h3("View Publications of the Top Proteins",align = "center"),br(),
            p("This module shows the highly-cited (>= 5 citations per year) publications for each of the top topics, sorted by the number of citations.."),
            em("No publication with more than 5 citations per year was found.")
          )
        })
      }
      renderOutputTable(outputTableFileVotes)
      output$queryDownloadUI <- renderUI({
        downloadButton('downloadData', 'Download')
      })
      output$queryTailUI <- renderUI({
        tags$script("$(document).on('click', '#table button', function () {
          Shiny.onInputChange('lastClickId',this.id);
          Shiny.onInputChange('lastClick', Math.random())
                    });")
      })
    }
    outputTableFileDownload<-outputTableFile[,1:8]
    colnames(outputTableFileDownload)<-c("Rank","UniProtKB_ID","GeneID","Protein_Name","PURPOSE_Score","nP","nTP","Citation/year")
    output$downloadData <- downloadHandler(
      filename = function() { paste(queryTerm, '.csv', sep='') },
      content = function(file) {
        write.csv(outputTableFileDownload, file, row.names=F)
      }
    )
  })
  observeEvent(input$example1, {
    updateTextInput(session, "queryP", value = "TP53")
  })
  observeEvent(input$example2, {
    updateTextInput(session, "queryP", value = "CEACAM5")
  })
  observeEvent(input$runP, {
    dirName<-paste(isolate(input$queryTypeP),"+",isolate(input$speciesP),sep="")
    fileName<-input$queryP
    print(dirName)
    print(fileName)
    fileNameExist<-file.exists(paste("protein-based/",dirName,"/",fileName,".txt",sep=""))
    #fileNameExist<-system(paste("if [ -f protein-based/",dirName,"/",fileName,".txt"," ]; then echo '1';else echo '0'; fi",sep=""),intern=T)
    if (isolate(input$speciesP)=="human") {
      speciesID<<-"9606"
    } else if (isolate(input$speciesP)=="mouse") {
      speciesID<<-"10090"
    } else if (isolate(input$speciesP)=="rat") {
      speciesID<<-"10116"
    } else if (isolate(input$speciesP)=="fly") {
      speciesID<<-"7227"
    } else if (isolate(input$speciesP)=="worm") {
      speciesID<<-"6239"
    } else if (isolate(input$speciesP)=="yeast") {
      speciesID<<-"4932"
    }
    if (fileNameExist==0){
      # run query
      print("running query")
      system(paste("bash protein.sh", isolate(input$queryTypeP), isolate(input$queryP), isolate(input$speciesP),sep=" "))
      print(paste("bash protein.sh", isolate(input$queryTypeP), isolate(input$queryP), isolate(input$speciesP), sep=" "))
    }
    fileNameExist<-file.exists(paste("protein-based/",dirName,"/",fileName,".txt",sep=""))
    #fileNameExist<-as.numeric(system(paste("if [ -f protein-based/",dirName,"/",fileName,".txt"," ]; then echo '1';else echo '0'; fi",sep=""),intern=T))
    if (fileNameExist==1){
      outputTableFileP<<-read.table(paste("protein-based/",isolate(input$queryTypeP),"+",isolate(input$speciesP),"/",isolate(input$queryP),".txt",sep=""),header=F,comment.char="",stringsAsFactors=F,sep="\t",fill=T,quote="")
      # remove blacklisted proteins
      #if (input$excludeBlacklisted == T){
        #outputTableFile<<-outputTableFile[!outputTableFile[,3] %in% blacklist[,2],]
        #outputTableFile[,1]<-(1:dim(outputTableFile)[1])
      #}
      output$summaryP <- renderUI({ 
        tagList(
          strong("Query term: "),code(isolate(input$queryP)),br(),
          strong("Species: "),code(isolate(input$speciesP)),br(),
          strong("Number of publications associated with this protein:"),code(outputTableFileP[1,8]),br(),
          strong("Number of publications associated with ANY proteins:"),code(outputTableFileP[1,3]),br(),
          strong("Number of topics in comparison:"),code(dim(outputTableFileP)[1]),br(),
          #em("***BELOW CONTAINS PLACEHOLDERS***"),br(),
          #strong("Number of proteins associated with the retrieved publications:"),code(sample(1:10000,1)),br(),
          #strong("Number of retrieved publications associated with proteins:"),code(sample(1:10000,1)),br(),
          #strong("Number of average citations per year of the retrieved publications associated with proteins:"),code(sample(1:10000,1)),br(),
          br(),br(),br(),h3("Prioritized List",align="center"),
          h6("Descriptions:"),
          h6("#T = the number of paper on this topic; #TP = the number of paper on this topic and this protein; #Cit/yr = the total number of citations per year")
        )
      })
      outputTableFileShowP<<-outputTableFileP[,c(1,7,2,9,10)]
      colnames(outputTableFileShowP)<<-c("SearchTerms","Score","#T","#TP","#Cit/yr")
      outputTableFileShowP<<-merge(outputTableFileShowP,topicsLookup,by='SearchTerms')[,c(6,2:5)]
      outputTableFileShowP<<-outputTableFileShowP[order(outputTableFileShowP[,2],decreasing=T),]
      outputTableFileShowP<<-cbind(1:dim(outputTableFileShowP)[1],outputTableFileShowP)
      colnames(outputTableFileShowP)[1]<<-"Rank"
      #output$tableP <- renderDataTable(iris)
      output$tableP <- renderDataTable(outputTableFileShowP, options=list(rownames = FALSE))
      output$queryOutputUIP <- renderUI({
        downloadButton('downloadDataP', 'Download')
      })
      output$scoreScatterPlotP <- renderPlotly({ 
        yAxis <- list(
          title = "log10(Publication Score)"
        )
        plot_ly(outputTableFileShowP, x=~Rank, y=~log10(Score), text=~Topics) %>%
          layout(yaxis = yAxis)
      })
      output$viewScoreDistributionP <- renderUI({ 
        tagList(
          h3("View the Distribution of Publication Scores",align = "center"),
          h5("(it may take a few seconds to load the whole figure)",align="center"),
          plotlyOutput("scoreScatterPlotP")
        )
      })
      outputAnnotationsP<-as.data.frame(t(c(input$queryP)))
      if (input$speciesP=="human"){
        print(humanNextProt[(humanNextProt[,2]==outputTableFileP[1,5]),1][1])
        outputAnnotationsP$neXtProt <- createLink("nextprot", humanNextProt[(humanNextProt[,2]==outputTableFileP[1,5]),1][1], input$speciesP)
      }
      outputAnnotationsP$Uniprot <- createLink("uniprot", outputTableFileP[1,4], input$speciesP)
      outputAnnotationsP$PeptideAtlas <- createLink("peptideAtlas", outputTableFileP[1,4], input$speciesP)
      outputAnnotationsP$Phosphosite <- createLink("phosphosite", outputTableFileP[1,6], input$speciesP)
      outputAnnotationsP$NCBI_Protein <- createLink("NCBIProtein", outputTableFileP[1,4], input$speciesP)
      outputAnnotationsP$Genecards <- createLink("genecards", outputTableFileP[1,4], input$speciesP)
      colnames(outputAnnotationsP)[1]<-"Protein Name"
      output$viewAnnotationsP <- renderUI({ 
        tagList(
          h3(paste("View the Annotations of ",input$queryP,sep=""),align = "center"),
          p("The result page will be shown in a new tab."),
          em("Please check the pop-up blocker of your browser if the results do not show up after clicking 'View PPI.'"),
          renderDataTable(outputAnnotationsP, option=list(rownames=FALSE), escape=FALSE)
        )
      })
      output$viewPPIsP <- renderUI({ 
        tagList(
          h3(paste("View Protein-Protein Interactions (PPI) of ",input$queryP,sep=""),align = "center"),br(),
          p("This module conducts PPI analysis using the STRING Database."),
          p("The result page will be shown in a new tab."),
          em("Please check the pop-up blocker of your browser if the results do not show up after clicking 'View PPI.'"),
          br(),
          useShinyjs(),
          extendShinyjs(text = jsOpenURLCode),
          actionButton("runPPIP", "View PPI", width = "100%")
        )
      })
      viewPublicationsTop100FilenameP<-paste("protein-based/",isolate(input$queryTypeP),"+",isolate(input$speciesP),"/",isolate(input$queryP),"_publication.txt",sep="")
      #print(viewPublicationsTop100FilenameP)
      if (file.exists(viewPublicationsTop100FilenameP)) {
        viewPublicationsListTopic<-outputTableFileShowP[,2]
        viewPublicationsTop100P<-read.table(viewPublicationsTop100FilenameP,header=F,comment.char="",stringsAsFactors=F,sep="\t",fill=T,quote="")
        viewPublicationsTop100P[,4]<-paste(viewPublicationsTop100P[,4]," et al.",sep="")
        colnames(viewPublicationsTop100P)<-c("SearchTerms","PMID","PeptideID","Authors","Title","Journal","Year","Total Citations")
        viewPublicationsTop100P<-merge(viewPublicationsTop100P,topicsLookup,by='SearchTerms')[,c(9,2:8)]
        colnames(viewPublicationsTop100P)[1]<-"Topics"
        viewPublicationsTop100P<-viewPublicationsTop100P[order(viewPublicationsTop100P[,8],decreasing=T),]
        viewPublicationsTop100P$PMIDLink<-createLink("PubMed", viewPublicationsTop100P$PMID, "")
        viewPublicationsTop100PShow<<-viewPublicationsTop100P
        output$viewPublicationsP <- renderUI({ 
          tagList(
            h3("View Publications of the Top Topics",align = "center"),br(),
            p("This module shows the highly-cited (>= 5 citations per year) publications for each of the top topics, sorted by the number of citations."),br(),
            em("The table would be empty if there is no highly-cited publication matching the protein and the topic."),
            #em("***PLACEHOLDERS BELOW***"),br(),
            selectInput("viewPublicationsTopic", "Choose a topic:",
                        viewPublicationsListTopic),
            dataTableOutput("viewPublicationsTableP"),
            downloadButton('downloadPublicationDataP', 'Download')
          )
        })
      } else {
        output$viewPublicationsP <- renderUI({ 
          tagList(
            h3("View Publications of the Top Topics",align = "center"),br(),
            p("This module shows the highly-cited (>= 5 citations per year) publications for each of the top topics, sorted by the number of citations.."),
            em("No highly-cited publications was found.")
          )
        })
      }
    } else {
      output$summaryP <- renderUI({ 
        tagList(
          strong("Query term: "),code(isolate(input$queryP)),br(),
          strong("Species: "),code(isolate(input$speciesP)),br(),
          em("***No Publication Found. Please Reselect.***")
          #strong("Number of publications associated with this protein:"),code(outputTableFileP[1,6]),br(),
          #strong("Number of publications associated with ANY proteins:"),code(outputTableFileP[1,10]),br(),
          #strong("Number of topics in comparison:"),code(dim(outputTableFileP)[1]),br(),
        )
      })
      output$tableP <- renderUI("")
    }
    outputTableFileDownloadP<<-outputTableFileShowP#cbind(1:dim(outputTableFileP)[1],outputTableFileP[,c(1,5:8)])
    #colnames(outputTableFileDownloadP)<<-c("Rank","Topic","PURPOSE_Score","nP","nTP","Citation/year")
  })
  output$downloadDataP <- downloadHandler(
    filename = function() { paste(input$queryP, '.csv', sep='') },
    content = function(file) {
      #write.csv(iris, file)
      write.csv(outputTableFileDownloadP, file, row.names=F)
    }
  )
}


