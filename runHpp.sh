while read species; do
  echo $species
  while read hppTopic; do
    echo $hppTopic
    java JProteomeText_gene2pubtator $hppTopic $species
  done < data/hpp.txt
done < data/species.txt
