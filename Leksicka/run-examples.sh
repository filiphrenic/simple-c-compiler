#!/bin/bash

LAN="test.lan"
IN="test.in"
OUT="test.out"

for test in tests/*; do
	cd bin
	java -Xss1m GLA 2>&1 >/dev/null < ../$test/$LAN
	cd analizator
	java -Xss1m LA < ../../$test/$IN 2>/dev/null | diff -u -- - ../../$test/$OUT
	cd ../..
done

