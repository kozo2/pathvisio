#!/bin/sh
# $Id: hs_contrib_convert.sh,v 1.3 2005/12/04 22:33:31 gontran Exp $

# Config me!
# com format..
#OUTDIR=/cygdrive/l/tmp/gm2gmml/out/cp
#INDIR=/cygdrive/l/tmp/gm2gmml/in/cp
# cygwin sh format..
INDIR='L:\\tmp\\gm2gmml\\in\\cp'
OUTDIR='L:\\tmp\\gm2gmml\\out\\cp'

# this script can be used in the following way, once you update you're
# input and output directories:
#
#	$0 name_of_my_mappfile.mapp
#
# where 'name of my mappfile.mapp' is in $INDIR

exec java -jar genmap2gmml.jar ${OUTDIR} ${INDIR}\\${@}
