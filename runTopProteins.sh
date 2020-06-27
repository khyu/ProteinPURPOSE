while read proteinName; do
  echo $proteinName
  bash protein.sh hpp $proteinName human
done < data/topProteins.txt
