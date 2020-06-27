proteinID="000"
topicFileName="$1"
echo $topicFileName

while read topic; do
    echo $topic
    proteinName="$2"
    species="$3"
    #echo $proteinName
    #echo $species
    #grep $proteinName hpp/$topic+$species/$topic+$species_gene2pubtator_matched_Pair_Citation_PS_Result.txt
    scoreFilename=PPS_Results/$species/${topic}+${species}_PPS
    outputScoreFilename=protein-based/${topicFileName}+${species}/${proteinName}.txt
    publicationFilename=PPS_Results/$species/${topic}+${species}_TOP100
    outputPublicationFilename=protein-based/${topicFileName}+${species}/${proteinName}_publication.txt
    #rm $outputFilename
    #rm $outputPublicationFilename
    #touch $outputFilename
    echo $scoreFilename
    echo $outputScoreFilename
    nT=$(head -1 $scoreFilename | cut -f 2)
    nF=$(head -2 $scoreFilename | tail -1 | cut -f 2)
    awk -v topic="$topic" -v proteinName="$proteinName" -v outputScoreFilename="$outputScoreFilename" -v nT="$nT" -v nF="$nF" '{if ($3 == proteinName) {print topic "\t" nT "\t" nF "\t" $0 >> outputScoreFilename}}' $scoreFilename
    # get protein ID to query Top 100 publication list
    if [ $proteinID -eq "000" ]; then
    	echo $proteinID
		proteinID=$(awk -v topic="$topic" -v proteinName="$proteinName" -v outputScoreFilename="$outputScoreFilename" '{if ($3 == proteinName) {print $2}}' $scoreFilename)
	fi
	echo $proteinID
    awk -v topic="$topic" -v proteinID="$proteinID" -v outputPublicationFilename="$outputPublicationFilename" '{if ($2 == proteinID) {print topic "\t" $0 >> outputPublicationFilename}}' $publicationFilename
done < data/${topicFileName}.txt

