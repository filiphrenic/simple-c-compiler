#!/bin/bash

LAN="test.lan"
IN="test.in"
OUT="test.out"
MYOUT="my.out"

for test in tests/*; do
        echo $test
	cd bin
	java -Xss1m GLA 2>&1 >/dev/null < ../$test/$LAN
	cd analizator
	java -Xss1m LA < ../../$test/$IN 2>/dev/null # > ../../$test/$MYOUT
	cd ../..
done

