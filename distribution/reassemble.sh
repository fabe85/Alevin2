#!/bin/bash

if [ ! -n $1 ]; then
	echo "Usage: bash reassemble.sh TestOut_Prefix_before_number"
fi

cat $1* > $1_out.txt
