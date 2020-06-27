while read species; do
  echo $species
  mkdir protein-based/hpp+${species}
  mkdir protein-based/humanSystems+${species}
done < data/species.txt
