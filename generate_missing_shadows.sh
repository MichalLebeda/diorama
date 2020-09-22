#!/bin/bash

cd "$(dirname "$0")"

Dir1="raw_assets/"
Dir2="raw_shadows/"

find "$Dir1/" "$Dir2/" -printf '%P\n' | grep -v -e '^$' | sort | uniq -d | while read fname; do
if ( [ "$Dir1/$fname" -nt "$Dir2/$fname" ] );
then
    printf "creating shadow for updated file %s %s\n" "$Dir1$fname" "$Dir2$fname"
    ./generate_single_shadow.sh "$Dir1$fname" "$Dir2$fname"
fi
done

find "$Dir1/" "$Dir2/" "$Dir2/" -printf '%P\n' | grep -v -e '^$' | sort | uniq -u | while read fname; do
    printf "creating shadow for not yet generated file %s %s\n" "$Dir1$fname2" "$Dir2$fname2"
    ./generate_single_shadow.sh "$Dir1$fname" "$Dir2$fname"
done
