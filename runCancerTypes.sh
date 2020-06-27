while read cancerType; do
  echo $cancerType
  java JProteomeText_gene2pubtator $cancerType human
done < data/mutationCancerTypes.txt
