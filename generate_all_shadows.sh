#!/bin/sh
cd raw_assets;
gimp -i -b '(shadow "*.png" )' -b '(gimp-quit 0)'
