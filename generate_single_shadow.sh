#!/bin/sh
#todo remove
#cd "$(dirname "$0")"
echo "generating shadow from: $1 to: $2"
gimp -i -b "(shadow \"$1\" \"$2\" )" -b '(gimp-quit 0)'

