\name{Pathway-class}
\docType{class}
\alias{Pathway-class}
\alias{class:Pathway}
\alias{Pathway}
\alias{name,Pathway-method}
\alias{name<-,Pathway-method}
\alias{fileName,Pathway-method}
\alias{fileName<-,Pathway-method}
\alias{allRefs,Pathway-method}
\alias{allRefs<-,Pathway-method}
\alias{print,Pathway-method}
\alias{match,Pathway-method}
\alial{getAllRefs}

\title{Class "Pathway"}
\description{A class that represents pathway data. For now a pathway consist of a list of gene products (\code{\link{GeneProduct}}), without any information on the relations between these gene products.} 

\section{Extends}{
   Directly extends class \code{\link{list}}.
}

\section{Creating objects}{
Objects can be created by calls of the form 
\code{Pathway(name, fileName, geneProducts)} where \code{geneProducts} is a \code{\link{list}} of \code{\link{GeneProduct}} objects and the other arguments are slot values (slot allRefs is set automaticly within the constructor)
}

\section{Slots}{
  \describe{
    \item{name}{ the name of the pathway }
    \item{fileName}{ points to the file from which this pathway was created
    (used in Path-Visio to open and visualize the original pathway) }
    \item{allRefs}{ a vector containing all annotation references of all 
    \code{\link{GeneProduct}}s in this pathway. Used internally for \code{\link{match}}}
  }
}

\section{Methods}{
  \describe{
    \item{name and name<-}{
    	\code{signature(x = "Pathway")}:
    	accessor for the slot 'name'
    }
    \item{fileName and fileName<-}{
    	\code{signature(x = "Pathway")}:
    	accessor for the slot 'description'
    }
    \item{allRefs and allRefs<-}{
    	\code{signature(x = "Pathway")}:
    	accessor for the slot 'allRefs'
    }
    \item{print}{
    	\code{signature(x = "Pathway")}:
    	prints this object in readable form
    }
    \item{hasReference}{
    	\code{signature{ref = "ANY", pathway = "Pathway"}}:
    	returns TRUE or FALSE depending on whether this pathway contains the given 
    	\code{\link{GeneProduct}} or reference (\code{character} of the form \code{"code:id"})
    }
    \item{matchReferences}{
    	\code{signature{refs = "ANY", pathway = "Pathway"}}:
    	matchs the given references to the pathway and returns a logical vector indicating for every
    	reference whether it is present in the pathway or not. 
    	Argument refs has to be a list of \code{\link{GeneProduct}s} or vector of references (\code{character} of the form \code{"code:id"})
    }
    \item{inReferences}{
    	\code{signature{pathway = "Pathway", refs = "ANY"}}:
    	Check for every \code{\link{GeneProduct}} in the pathway whether it is in the given list of references. 
      Argument refs has to be a list of \code{\link{GeneProduct}s} or vector of references (\code{character} of the form \code{"code:id"})	
    }
    \item{asEnsembl}{
    	\code{signature(x = "PathwaySet")}:
    	returns a vector containing all Ensembl genes in this pathway-set
    }
   }
} 
\author{Thomas Kelder (BiGCaT)}

\keyword{methods}
\keyword{classes} 