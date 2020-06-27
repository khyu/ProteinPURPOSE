while read species; do
  echo $species
  while read topic; do
    echo $topic
    java JProteomeText_gene2pubtator $topic $species
  done < data/humanSystems.txt
done < data/species.txt
