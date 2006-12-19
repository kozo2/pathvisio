# Het uitschrijven van de genormaliseerde data
# De Broodnodige libraries
library ("affy")

# Het laden van de workspace
load("D:\\Project BioInformatica\\gcrma-rdata\\.RData")

# Het uitschrijven van de data naar een Tab-Delimited File: (.txt)
write.table(exprs(data.bg.norm.gcrma.quantiles), sep="\t", eol="\n",file="expr.txt");

# Er staat nu een bestandje in je working directory van 128 MB groot..
