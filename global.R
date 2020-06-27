## global.R
# read files for shinyapp.io
#
# Kun-Hsing Yu
# July 22, 2017

hppNames <- read.csv("data/hppNames.txt", header = F, stringsAsFactors = F, sep="\t")
hppQueries <- read.csv("data/hpp.txt", header = F, stringsAsFactors = F, sep="\t")
hpp <- cbind(hppNames, hppQueries)

humanSystemNames <- read.csv("data/humanSystemNames.txt", header = F, stringsAsFactors = F, sep="\t")
humanSystemQueries <- read.csv("data/humanSystems.txt", header = F, stringsAsFactors = F, sep="\t")
humanSystems <- cbind(humanSystemNames, humanSystemQueries)

topicsLookup<-rbind(hpp,humanSystems)
colnames(topicsLookup)<-c("Topics","SearchTerms")

humanNextProt <- read.csv("data/nextprot_geneid.txt", header = F, stringsAsFactors = F, sep="\t")
colnames(humanNextProt)[2]<-"GeneID"


phewas_codes <- read.csv("data/PheWAS_code_translation_v1_2.txt", header = T, stringsAsFactors = F, sep="\t")

#uniprot_protein_names <- read.csv("data/uniprotGeneNames.txt", header = F, stringsAsFactors = F, sep="\t")
#uniprot_protein_names_mapping <- read.csv("data/uniprot-proteome%3AUP000005640.tab.tsv", header = T, stringsAsFactors = F, sep="\t")

human_protein_names_mapping <- read.csv("data/geneInfo/humanGeneInfo.txt", header = T, stringsAsFactors = F, sep="\t")
rat_protein_names_mapping <- read.csv("data/geneInfo/ratGeneInfo.txt", header = T, stringsAsFactors = F, sep="\t")
mouse_protein_names_mapping <- read.csv("data/geneInfo/mouseGeneInfo.txt", header = T, stringsAsFactors = F, sep="\t")
fly_protein_names_mapping <- read.csv("data/geneInfo/flyGeneInfo.txt", header = T, stringsAsFactors = F, sep="\t")
worm_protein_names_mapping <- read.csv("data/geneInfo/wormGeneInfo.txt", header = T, stringsAsFactors = F, sep="\t")
yeast_protein_names_mapping <- read.csv("data/geneInfo/yeastGeneInfo.txt", header = T, stringsAsFactors = F, sep="\t")

blacklist <- read.csv("data/blacklist.txt", header = F, stringsAsFactors = F, sep="\t")

progressMsg <- "Loading... It might take a few seconds..."
