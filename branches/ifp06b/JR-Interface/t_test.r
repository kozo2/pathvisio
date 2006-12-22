tvalues <- c()
pos_control <- c(1,3,6,9,10,12) # set the colums with the data from the control group
pos_fedef <- c(2,4,5,7,8,11) # set the colums with the data from the Fe deficient group
count <- 0

for(i in 1 : dim( exprs(eset.gcrma.q) )[1]){
		
		A <- exprs(eset.gcrma.q)[i,pos_control]
		B <- exprs(eset.gcrma.q)[i,pos_fedef]
		
		ttest <- t.test(A,B)[[3]]
		
		tvalues[i] <- ttest
		
		if (ttest < 0.01){		# a p-value of 0.01 is chosen, the number of false positives is 2
			count <- count + 1
		}
}