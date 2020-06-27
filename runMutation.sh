java JProteomeText_gene2pubtator mutation cancer human
while read mutationTopic; do
  echo $mutationTopic
  java JProteomeText_gene2pubtator $mutationTopic human
done < data/mutations.txt
